package com.hyu.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hyu.common.domain.LoginBody;
import com.hyu.common.domain.LoginUser;
import com.hyu.common.domain.RegisterBody;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.exception.BusinessException;
import com.hyu.common.utils.CaptchaUtils;
import com.hyu.common.utils.JwtUtils;
import com.hyu.common.utils.PasswordUtils;
import com.hyu.common.utils.RedisUtils;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.IAuthService;
import com.hyu.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 认证Service业务层处理
 *
 * @author hyu
 */
@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private HttpServletRequest request;

    /**
     * Redis前缀
     */
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    @Override
    public AjaxResult login(LoginBody loginBody) {
        // 验证码校验
        if (StringUtils.hasText(loginBody.getCaptcha())) {
            validateCaptcha(loginBody.getCaptcha());
        }

        // 用户认证
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginBody.getUsername(),
                            loginBody.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new BusinessException("用户名或密码错误", 1001);
        } catch (Exception e) {
            log.error("用户认证失败", e);
            throw new BusinessException("认证失败", 1001);
        }

        // 获取用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = userService.getById(loginUser.getUserId());

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用", 1002);
        }

        // 生成token
        String token = jwtUtils.generateToken(
                user.getUserId(),
                user.getUsername(),
                user.getRealName(),
                user.getUserType(),
                null // 数据库中不存在deptId字段，传入null
        );

        // 生成刷新token
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId());

        // 存储刷新token到Redis
        redisUtils.set(REFRESH_TOKEN_PREFIX + user.getUserId(), refreshToken, 7 * 24 * 60 * 60);

        // 更新最后登录信息 - 数据库中不存在这些字段，暂时注释掉
        // user.setLastLoginTime(LocalDateTime.now());
        // user.setLastLoginIp(getClientIP());
        // userService.updateById(user);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("refreshToken", refreshToken);
        result.put("expiresIn", jwtUtils.getExpirationTime(token));
        result.put("userInfo", buildUserInfo(user));

        return AjaxResult.success("登录成功", result);
    }

    @Override
    public AjaxResult register(RegisterBody registerBody) {
        // 校验验证码
        String captchaKey = CAPTCHA_PREFIX + registerBody.getCaptchaKey();
        String storedCaptcha = (String) redisUtils.get(captchaKey);
        if (!CaptchaUtils.validateCaptcha(registerBody.getCaptchaKey(), registerBody.getCaptchaCode(), storedCaptcha)) {
            throw new BusinessException("验证码错误", 1201);
        }
        redisUtils.delete(captchaKey);

        // 校验两次密码是否一致
        if (!registerBody.getPassword().equals(registerBody.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致", 1202);
        }

        // 检查用户名是否已存在
        SysUser existUser = userService.selectUserByUsername(registerBody.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在", 1001);
        }

        // 检查手机号是否已存在
        existUser = userService.selectUserByPhone(registerBody.getPhone());
        if (existUser != null) {
            throw new BusinessException("手机号已存在", 1001);
        }

        // 创建用户
        SysUser user = new SysUser();
        user.setUsername(registerBody.getUsername());
        user.setPassword(PasswordUtils.encode(registerBody.getPassword()));
        user.setRealName(registerBody.getRealName());
        user.setPhone(registerBody.getPhone());
        user.setUserType(3); // 默认为业主
        user.setStatus(1);   // 默认正常

        if (!userService.save(user)) {
            throw new BusinessException("注册失败，请稍后重试", 1401);
        }

        return AjaxResult.success("注册成功");
    }

    @Override
    public AjaxResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 将token加入黑名单
            String token = getTokenFromRequest();
            if (StringUtils.hasText(token)) {
                redisUtils.set(TOKEN_BLACKLIST_PREFIX + token, "1", jwtUtils.getExpirationTime(token));
            }

            // 删除刷新token
            redisUtils.delete(REFRESH_TOKEN_PREFIX + loginUser.getUserId());

            // 清除上下文
            SecurityContextHolder.clearContext();
        }

        return AjaxResult.success("登出成功");
    }

    @Override
    public AjaxResult refreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new BusinessException("刷新令牌不能为空", 1201);
        }

        try {
            // 验证刷新token
            if (!jwtUtils.isRefreshToken(refreshToken)) {
                throw new BusinessException("无效的刷新令牌", 1005);
            }

            // 获取用户ID
            Long userId = jwtUtils.getUserIdFromToken(refreshToken);

            // 验证Redis中的刷新token
            String storedRefreshToken = (String) redisUtils.get(REFRESH_TOKEN_PREFIX + userId);
            if (!refreshToken.equals(storedRefreshToken)) {
                throw new BusinessException("刷新令牌已失效", 1005);
            }

            // 获取用户信息
            SysUser user = userService.getById(userId);
            if (user == null) {
                throw new BusinessException("用户不存在", 1301);
            }

            if (user.getStatus() == 0) {
                throw new BusinessException("账号已被禁用", 1002);
            }

            // 生成新的token
            String newToken = jwtUtils.generateToken(
                    user.getUserId(),
                    user.getUsername(),
                    user.getRealName(),
                    user.getUserType(),
                    null // 数据库中不存在deptId字段，传入null
            );

            // 生成新的刷新token
            String newRefreshToken = jwtUtils.generateRefreshToken(user.getUserId());

            // 更新Redis中的刷新token
            redisUtils.set(REFRESH_TOKEN_PREFIX + userId, newRefreshToken, 7 * 24 * 60 * 60);

            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", newToken);
            result.put("refreshToken", newRefreshToken);
            result.put("expiresIn", jwtUtils.getExpirationTime(newToken));

            return AjaxResult.success("刷新成功", result);
        } catch (Exception e) {
            log.error("刷新token失败", e);
            throw new BusinessException("刷新token失败", 1005);
        }
    }

    @Override
    public AjaxResult getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException("用户未登录", 1006);
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = userService.getById(loginUser.getUserId());
        if (user == null) {
            throw new BusinessException("用户不存在", 1301);
        }

        return AjaxResult.success("查询成功", buildUserInfo(user));
    }

    @Override
    public AjaxResult updatePassword(String oldPassword, String newPassword, String confirmPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException("用户未登录", 1006);
        }

        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword) || !StringUtils.hasText(confirmPassword)) {
            throw new BusinessException("参数不能为空", 1201);
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException("两次输入的密码不一致", 1202);
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        try {
            boolean result = userService.updateUserPassword(loginUser.getUserId(), oldPassword, newPassword);
            if (result) {
                return AjaxResult.success("密码修改成功");
            } else {
                throw new BusinessException("密码修改失败", 1401);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改密码失败", e);
            throw new BusinessException("密码修改失败", 1401);
        }
    }

    @Override
    public AjaxResult updateProfile(SysUser user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException("用户未登录", 1006);
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        user.setUserId(loginUser.getUserId());
        user.setUsername(null); // 不允许修改用户名
        user.setPassword(null); // 不允许修改密码
        user.setUserType(null); // 不允许修改用户类型
        user.setStatus(null);   // 不允许修改状态

        try {
            boolean result = userService.updateUserInfo(user);
            if (result) {
                // 返回更新后的用户信息
                SysUser updatedUser = userService.getById(loginUser.getUserId());
                return AjaxResult.success("信息修改成功", buildUserInfo(updatedUser));
            } else {
                throw new BusinessException("信息修改失败", 1401);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("修改个人信息失败", e);
            throw new BusinessException("信息修改失败", 1401);
        }
    }

    @Override
    public AjaxResult generateCaptcha() {
        CaptchaUtils.CaptchaResult captcha = CaptchaUtils.generateCaptcha();

        // 将验证码存储到Redis
        redisUtils.set(CAPTCHA_PREFIX + captcha.getCaptchaId(), captcha.getAnswer(), 300);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("captchaId", captcha.getCaptchaId());
        result.put("captchaImage", captcha.getCaptchaImage());
        result.put("expireTime", captcha.getExpireTime());

        return AjaxResult.success("获取成功", result);
    }

    @Override
    public AjaxResult verifyToken(String token) {
        if (!StringUtils.hasText(token)) {
            return AjaxResult.error(1005, "Token不能为空");
        }

        try {
            // 检查token是否在黑名单中
            if (redisUtils.hasKey(TOKEN_BLACKLIST_PREFIX + token)) {
                return AjaxResult.error(1005, "Token已失效");
            }

            // 验证token
            Long userId = jwtUtils.getUserIdFromToken(token);
            String username = jwtUtils.getUsernameFromToken(token);

            // 检查用户是否存在且状态正常
            SysUser user = userService.selectUserByUsername(username);
            if (user == null || user.getStatus() == 0) {
                return AjaxResult.error(1002, "用户不存在或已被禁用");
            }

            // 检查token是否过期
            if (jwtUtils.isTokenExpired(token)) {
                return AjaxResult.error(1004, "Token已过期");
            }

            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("userId", userId);
            result.put("username", username);
            result.put("expireTime", System.currentTimeMillis() + jwtUtils.getExpirationTime(token) * 1000);

            return AjaxResult.success("Token有效", result);
        } catch (Exception e) {
            log.error("验证token失败", e);
            return AjaxResult.error(1005, "Token无效");
        }
    }

    /**
     * 校验验证码
     *
     * @param captcha 验证码
     */
    private void validateCaptcha(String captcha) {
        if (!StringUtils.hasText(captcha)) {
            throw new BusinessException("验证码不能为空", 1201);
        }

        // 这里可以根据实际需求实现验证码校验逻辑
        // 例如：从Redis中获取存储的验证码进行比较
    }

    /**
     * 构建用户信息
     *
     * @param user 用户
     * @return 用户信息
     */
    private Map<String, Object> buildUserInfo(SysUser user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("username", user.getUsername());
        userInfo.put("realName", user.getRealName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("userType", user.getUserType());
        userInfo.put("avatar", user.getAvatar());
        // userInfo.put("lastLoginTime", user.getLastLoginTime()); // 数据库中不存在此字段
        // userInfo.put("lastLoginIp", user.getLastLoginIp()); // 数据库中不存在此字段
        userInfo.put("createTime", user.getCreateTime());
        userInfo.put("updateTime", user.getUpdateTime());

        // 根据用户类型设置角色和权限信息
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        switch (user.getUserType()) {
            case 1: // 管理员
                roles.add("admin");
                // 管理员拥有所有权限
                permissions.addAll(getAllPermissions());
                break;
            case 2: // 物业管理员
                roles.add("property_manager");
                permissions.addAll(getPropertyManagerPermissions());
                break;
            case 3: // 业主
                roles.add("owner");
                permissions.addAll(getOwnerPermissions());
                break;
            case 4: // 维修人员
                roles.add("worker");
                permissions.addAll(getWorkerPermissions());
                break;
        }

        userInfo.put("roles", roles);
        userInfo.put("permissions", permissions);

        return userInfo;
    }

    /**
     * 获取所有权限
     */
    private Set<String> getAllPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("property:building:view");
        permissions.add("property:building:add");
        permissions.add("property:building:edit");
        permissions.add("property:building:delete");
        permissions.add("property:unit:view");
        permissions.add("property:unit:add");
        permissions.add("property:unit:edit");
        permissions.add("property:unit:delete");
        permissions.add("property:house:view");
        permissions.add("property:house:add");
        permissions.add("property:house:edit");
        permissions.add("property:house:delete");
        permissions.add("property:resident:view");
        permissions.add("property:resident:add");
        permissions.add("property:resident:edit");
        permissions.add("property:resident:delete");
        permissions.add("property:fee:type:view");
        permissions.add("property:fee:type:add");
        permissions.add("property:fee:type:edit");
        permissions.add("property:fee:type:delete");
        permissions.add("property:bill:view");
        permissions.add("property:bill:add");
        permissions.add("property:bill:edit");
        permissions.add("property:bill:delete");
        permissions.add("property:bill:generate");
        permissions.add("property:bill:pay");
        permissions.add("property:wallet:view");
        permissions.add("property:wallet:recharge");
        permissions.add("property:wallet:freeze");
        permissions.add("property:wallet:resetPassword");
        permissions.add("property:transaction:list");
        permissions.add("property:complaint:view");
        permissions.add("property:complaint:add");
        permissions.add("property:complaint:edit");
        permissions.add("property:complaint:delete");
        permissions.add("property:complaint:assign");
        permissions.add("property:complaint:handle");
        permissions.add("property:complaint:rate");
        permissions.add("property:repair:view");
        permissions.add("property:repair:add");
        permissions.add("property:repair:edit");
        permissions.add("property:repair:delete");
        permissions.add("property:repair:assign");
        permissions.add("property:repair:handle");
        permissions.add("property:repair:accept");
        permissions.add("property:parking:view");
        permissions.add("property:parking:add");
        permissions.add("property:parking:edit");
        permissions.add("property:parking:delete");
        permissions.add("property:parking:rent");
        permissions.add("property:parking:audit");
        permissions.add("property:parking:renew");
        permissions.add("property:parking:return");
        permissions.add("property:notice:view");
        permissions.add("property:notice:add");
        permissions.add("property:notice:edit");
        permissions.add("property:notice:delete");
        permissions.add("property:notice:publish");
        permissions.add("property:notice:withdraw");
        permissions.add("analytics:view");
        permissions.add("analytics:export");
        permissions.add("system:config:view");
        permissions.add("system:config:edit");
        permissions.add("system:log:view");
        permissions.add("system:log:export");
        permissions.add("system:dict:view");
        permissions.add("system:dict:add");
        permissions.add("system:dict:edit");
        permissions.add("system:dict:delete");
        permissions.add("system:dict:refresh");
        return permissions;
    }

    /**
     * 获取物业管理员权限
     */
    private Set<String> getPropertyManagerPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("property:building:view");
        permissions.add("property:building:add");
        permissions.add("property:building:edit");
        permissions.add("property:building:delete");
        permissions.add("property:unit:view");
        permissions.add("property:unit:add");
        permissions.add("property:unit:edit");
        permissions.add("property:unit:delete");
        permissions.add("property:house:view");
        permissions.add("property:house:add");
        permissions.add("property:house:edit");
        permissions.add("property:house:delete");
        permissions.add("property:resident:view");
        permissions.add("property:resident:add");
        permissions.add("property:resident:edit");
        permissions.add("property:resident:delete");
        permissions.add("property:fee:type:view");
        permissions.add("property:fee:type:add");
        permissions.add("property:fee:type:edit");
        permissions.add("property:fee:type:delete");
        permissions.add("property:bill:view");
        permissions.add("property:bill:add");
        permissions.add("property:bill:edit");
        permissions.add("property:bill:delete");
        permissions.add("property:bill:generate");
        permissions.add("property:wallet:view");
        permissions.add("property:wallet:recharge");
        permissions.add("property:wallet:freeze");
        permissions.add("property:wallet:resetPassword");
        permissions.add("property:transaction:list");
        permissions.add("property:complaint:view");
        permissions.add("property:complaint:add");
        permissions.add("property:complaint:edit");
        permissions.add("property:complaint:delete");
        permissions.add("property:complaint:assign");
        permissions.add("property:complaint:handle");
        permissions.add("property:repair:view");
        permissions.add("property:repair:add");
        permissions.add("property:repair:edit");
        permissions.add("property:repair:delete");
        permissions.add("property:repair:assign");
        permissions.add("property:repair:handle");
        permissions.add("property:parking:view");
        permissions.add("property:parking:add");
        permissions.add("property:parking:edit");
        permissions.add("property:parking:delete");
        permissions.add("property:parking:audit");
        permissions.add("property:notice:view");
        permissions.add("property:notice:add");
        permissions.add("property:notice:edit");
        permissions.add("property:notice:delete");
        permissions.add("property:notice:publish");
        permissions.add("analytics:view");
        permissions.add("system:config:view");
        permissions.add("system:dict:view");
        return permissions;
    }

    /**
     * 获取业主权限
     */
    private Set<String> getOwnerPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("property:owner:list");
        permissions.add("property:bill:view");
        permissions.add("property:bill:pay");
        permissions.add("property:wallet:view");
        permissions.add("property:wallet:recharge");
        permissions.add("property:transaction:list");
        permissions.add("property:complaint:add");
        permissions.add("property:complaint:rate");
        permissions.add("property:repair:add");
        permissions.add("property:repair:accept");
        permissions.add("property:notice:view");
        permissions.add("portal:view");
        permissions.add("portal:dashboard:view");
        return permissions;
    }

    /**
     * 获取维修人员权限
     */
    private Set<String> getWorkerPermissions() {
        Set<String> permissions = new HashSet<>();
        permissions.add("property:repair:view");
        permissions.add("property:repair:handle");
        permissions.add("property:repair:accept");
        permissions.add("property:notice:view");
        return permissions;
    }

    /**
     * 获取客户端IP
     *
     * @return IP地址
     */
    private String getClientIP() {
        String ip = request.getHeader("x-forwarded-for");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 从请求中获取token
     *
     * @return token
     */
    private String getTokenFromRequest() {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
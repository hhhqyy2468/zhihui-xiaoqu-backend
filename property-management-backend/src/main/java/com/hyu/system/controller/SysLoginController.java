package com.hyu.system.controller;

import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.JwtUtils;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录认证控制器
 */
@RestController
@RequestMapping("/auth")
public class SysLoginController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginBody) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");

        // 验证用户名密码
        SysUser user = userService.lambdaQuery()
                .eq(SysUser::getUsername, username)
                .one();

        if (user == null) {
            return AjaxResult.error("用户不存在");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return AjaxResult.error("密码错误");
        }

        if (user.getStatus() != 1) {
            return AjaxResult.error("用户已被禁用，请联系管理员");
        }

        // 生成token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("userType", user.getUserType());
        String token = jwtUtils.generateToken(claims);

        // 返回登录结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);

        return AjaxResult.success("登录成功", result);
    }

    /**
     * 获取用户信息
     */
    @PostMapping("/userInfo")
    public AjaxResult getUserInfo() {
        // 这里可以从SecurityContext中获取当前登录用户信息
        // 暂时返回模拟数据
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", "admin");
        userInfo.put("realName", "系统管理员");
        userInfo.put("userType", 1);
        return AjaxResult.success(userInfo);
    }

    /**
     * 登出接口
     */
    @PostMapping("/logout")
    public AjaxResult logout() {
        return AjaxResult.success("退出成功");
    }

    /**
     * 刷新token
     */
    @PostMapping("/refresh")
    public AjaxResult refreshToken() {
        // 这里可以实现token刷新逻辑
        return AjaxResult.error("暂不支持token刷新");
    }
}
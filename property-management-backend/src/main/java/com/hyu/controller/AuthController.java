package com.hyu.controller;

import com.hyu.common.domain.LoginBody;
import com.hyu.common.domain.RegisterBody;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.JwtUtils;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.IAuthService;
import com.hyu.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * 认证控制器
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ISysUserService userService;

    @Value("${file.upload.base-path:./uploads}")
    private String uploadBasePath;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AjaxResult login(@Valid @RequestBody LoginBody loginBody) {
        log.info("用户登录：{}", loginBody.getUsername());
        return authService.login(loginBody);
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AjaxResult register(@Valid @RequestBody RegisterBody registerBody) {
        log.info("用户注册：{}", registerBody.getUsername());
        return authService.register(registerBody);
    }

    /**
     * 刷新Token
     */
    @ApiOperation("刷新Token")
    @PostMapping("/refresh")
    public AjaxResult refreshToken(@RequestBody Map<String, String> params) {
        String refreshToken = params.get("refreshToken");
        log.info("刷新Token");
        return authService.refreshToken(refreshToken);
    }

    /**
     * 用户登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public AjaxResult logout() {
        log.info("用户登出");
        return authService.logout();
    }

    /**
     * 获取当前用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public AjaxResult getUserInfo() {
        log.info("获取用户信息");
        return authService.getUserInfo();
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public AjaxResult updatePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        String confirmPassword = params.get("confirmPassword");

        log.info("修改密码");
        return authService.updatePassword(oldPassword, newPassword, confirmPassword);
    }

    /**
     * 修改个人信息
     */
    @ApiOperation("修改个人信息")
    @PutMapping("/profile")
    public AjaxResult updateProfile(@RequestBody SysUser user) {
        log.info("修改个人信息");
        return authService.updateProfile(user);
    }

    /**
     * 获取验证码
     */
    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public AjaxResult getCaptcha() {
        log.info("获取验证码");
        return authService.generateCaptcha();
    }

    /**
     * 验证token有效性
     */
    @ApiOperation("验证Token有效性")
    @GetMapping("/verify")
    public AjaxResult verifyToken(@RequestHeader("Authorization") String authorization) {
        String token = null;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
        }

        log.info("验证Token");
        return authService.verifyToken(token);
    }

    /**
     * 上传头像
     */
    @ApiOperation("上传头像")
    @PostMapping("/avatar")
    public AjaxResult uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("请选择图片");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return AjaxResult.error("只允许上传图片文件");
        }
        if (file.getSize() > 2 * 1024 * 1024) {
            return AjaxResult.error("图片大小不能超过2MB");
        }
        try {
            String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
                    ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."))
                    : ".png";
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;
            File destDir = new File(uploadBasePath + "/avatar").toPath().toAbsolutePath().toFile();
            if (!destDir.exists()) destDir.mkdirs();
            File destFile = new File(destDir, fileName);
            file.transferTo(destFile);
            String avatarUrl = "/avatar/" + fileName;
            // 更新数据库头像字段
            Long userId = SecurityUtils.getUserId();
            SysUser user = new SysUser();
            user.setUserId(userId);
            user.setAvatar(avatarUrl);
            userService.updateUserInfo(user);
            return AjaxResult.success(avatarUrl);
        } catch (IOException e) {
            log.error("头像上传失败", e);
            return AjaxResult.error("头像上传失败");
        }
    }

    /**
     * 测试接口
     */
    @ApiOperation("认证测试接口")
    @GetMapping("/test")
    public AjaxResult test() {
        log.info("认证测试接口");
        return AjaxResult.success("认证成功");
    }
}
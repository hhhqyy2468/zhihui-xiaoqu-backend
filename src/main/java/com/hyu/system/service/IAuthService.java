package com.hyu.system.service;

import com.hyu.common.domain.LoginBody;
import com.hyu.common.domain.RegisterBody;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.system.domain.SysUser;

/**
 * 认证Service接口
 *
 * @author hyu
 */
public interface IAuthService {

    /**
     * 用户登录
     *
     * @param loginBody 登录参数
     * @return 登录结果
     */
    AjaxResult login(LoginBody loginBody);

    /**
     * 用户注册
     *
     * @param registerBody 注册参数
     * @return 注册结果
     */
    AjaxResult register(RegisterBody registerBody);

    /**
     * 退出登录
     *
     * @return 退出结果
     */
    AjaxResult logout();

    /**
     * 刷新token
     *
     * @param refreshToken 刷新令牌
     * @return 刷新结果
     */
    AjaxResult refreshToken(String refreshToken);

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    AjaxResult getUserInfo();

    /**
     * 修改密码
     *
     * @param oldPassword     旧密码
     * @param newPassword     新密码
     * @param confirmPassword 确认新密码
     * @return 修改结果
     */
    AjaxResult updatePassword(String oldPassword, String newPassword, String confirmPassword);

    /**
     * 修改个人信息
     *
     * @param user 用户信息
     * @return 修改结果
     */
    AjaxResult updateProfile(SysUser user);

    /**
     * 生成验证码
     *
     * @return 验证码
     */
    AjaxResult generateCaptcha();

    /**
     * 验证token有效性
     *
     * @param token token
     * @return 验证结果
     */
    AjaxResult verifyToken(String token);
}
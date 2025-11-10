package com.hyu.framework.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    // 这里暂时返回null，后续需要注入用户服务
    // @Autowired
    // private ISysUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // TODO: 实现用户查询逻辑
        // SysUser user = userService.selectUserByUserName(username);
        // if (StringUtils.isNull(user)) {
        //     log.info("登录用户：{} 不存在.", username);
        //     throw new UsernameNotFoundException("登录用户：" + username + " 不存在");
        // }

        // 这里需要根据用户权限创建UserDetails对象
        // return createLoginUser(user);

        // 暂时返回异常，等待用户服务实现
        throw new UsernameNotFoundException("用户服务尚未实现");
    }

    // private UserDetails createLoginUser(SysUser user) {
    //     return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    // }
}
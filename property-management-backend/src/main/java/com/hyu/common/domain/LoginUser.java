package com.hyu.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hyu.system.domain.SysUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户身份权限
 *
 * @author hyu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录密码
     */
    @JsonIgnore
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户类型 1:管理员 2:物业管理员 3:业主 4:维修人员
     */
    private Integer userType;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 用户状态
     */
    private Integer status;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录时间
     */
    private java.time.LocalDateTime loginTime;

    /**
     * 权限列表
     */
    private Set<String> permissions;

    /**
     * 角色列表
     */
    private Set<String> roles;

    /**
     * 用户唯一标识
     */
    private String userKey;

    public LoginUser(SysUser user, Set<String> permissions) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.realName = user.getRealName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.userType = user.getUserType();
        this.avatar = user.getAvatar();
        // this.deptId = user.getDeptId(); // 数据库中不存在此字段，注释掉
        // this.deptName = user.getDeptName(); // 项目不需要部门管理，注释掉
        this.status = user.getStatus();
        this.permissions = permissions;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 将权限字符串转换为GrantedAuthority对象
        if (permissions != null && !permissions.isEmpty()) {
            return permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }
}
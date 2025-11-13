package com.hyu.framework.security.filter;

import com.hyu.common.domain.LoginUser;
import com.hyu.common.utils.JwtUtils;
import com.hyu.common.utils.RedisUtils;
import com.hyu.framework.security.service.UserDetailsServiceImpl;
import com.hyu.system.domain.SysUser;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;

/**
 * JWT认证过滤器
 *
 * @author hyu
 */
@Slf4j
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        LoginUser loginUser = null;
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            try {
                // 检查token是否在黑名单中
                if (redisUtils.hasKey("token:blacklist:" + token)) {
                    log.warn("Token已在黑名单中：{}", token);
                    filterChain.doFilter(request, response);
                    return;
                }

                // 验证token
                if (!jwtUtils.isTokenExpired(token)) {
                    String username = jwtUtils.getUsernameFromToken(token);

                    // 如果上下文中没有认证信息，则进行认证
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        // loadUserByUsername返回的是LoginUser，直接使用
                        loginUser = (LoginUser) userDetailsService.loadUserByUsername(username);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            loginUser, null, loginUser.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                log.error("JWT验证失败：{}", e.getMessage());
                // 这里不抛出异常，让过滤器继续执行
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取token
     *
     * @param request 请求
     * @return token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
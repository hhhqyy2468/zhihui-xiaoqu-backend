package com.hyu.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author hyu
 */
@Slf4j
@Component
public class JwtUtils {

    /**
     * 密钥
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 过期时间（毫秒）
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    /**
     * 用户标识
     */
    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String CLAIM_KEY_USERNAME = "username";
    private static final String CLAIM_KEY_REAL_NAME = "realName";
    private static final String CLAIM_KEY_USER_TYPE = "userType";
    private static final String CLAIM_KEY_DEPT_ID = "deptId";

    /**
     * 获取密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token
     *
     * @param userId   用户ID
     * @param username 用户名
     * @param realName 真实姓名
     * @param userType 用户类型
     * @param deptId   部门ID
     * @return token
     */
    public String generateToken(Long userId, String username, String realName, Integer userType, Long deptId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_REAL_NAME, realName);
        claims.put(CLAIM_KEY_USER_TYPE, userType);
        claims.put(CLAIM_KEY_DEPT_ID, deptId);
        return generateToken(claims);
    }

    /**
     * 生成token
     *
     * @param claims 载荷信息
     * @return token
     */
    public String generateToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put("type", "refresh");

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从token中获取Claims
     *
     * @param token token
     * @return Claims
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw new RuntimeException("Token已过期");
        } catch (UnsupportedJwtException e) {
            log.warn("Token格式不支持: {}", e.getMessage());
            throw new RuntimeException("Token格式不支持");
        } catch (MalformedJwtException e) {
            log.warn("Token格式错误: {}", e.getMessage());
            throw new RuntimeException("Token格式错误");
        } catch (SecurityException e) {
            log.warn("Token签名验证失败: {}", e.getMessage());
            throw new RuntimeException("Token签名验证失败");
        } catch (IllegalArgumentException e) {
            log.warn("Token参数错误: {}", e.getMessage());
            throw new RuntimeException("Token参数错误");
        }
    }

    /**
     * 从token中获取用户ID
     *
     * @param token token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.valueOf(claims.get(CLAIM_KEY_USER_ID).toString());
    }

    /**
     * 从token中获取用户名
     *
     * @param token token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(CLAIM_KEY_USERNAME).toString();
    }

    /**
     * 从token中获取真实姓名
     *
     * @param token token
     * @return 真实姓名
     */
    public String getRealNameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get(CLAIM_KEY_REAL_NAME).toString();
    }

    /**
     * 从token中获取用户类型
     *
     * @param token token
     * @return 用户类型
     */
    public Integer getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Integer.valueOf(claims.get(CLAIM_KEY_USER_TYPE).toString());
    }

    /**
     * 从token中获取部门ID
     *
     * @param token token
     * @return 部门ID
     */
    public Long getDeptIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object deptId = claims.get(CLAIM_KEY_DEPT_ID);
        return deptId != null ? Long.valueOf(deptId.toString()) : null;
    }

    /**
     * 从token中获取过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 验证token
     *
     * @param token    token
     * @param username 用户名
     * @return 是否有效
     */
    public Boolean validateToken(String token, String username) {
        try {
            String tokenUsername = getUsernameFromToken(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否为刷新令牌
     *
     * @param token token
     * @return 是否为刷新令牌
     */
    public Boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取token剩余有效时间（秒）
     *
     * @param token token
     * @return 剩余有效时间
     */
    public Long getExpirationTime(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return (expiration.getTime() - System.currentTimeMillis()) / 1000;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    public String refreshToken(String refreshToken) {
        try {
            Claims claims = getClaimsFromToken(refreshToken);
            if (!isRefreshToken(refreshToken)) {
                throw new RuntimeException("无效的刷新令牌");
            }

            Long userId = Long.valueOf(claims.get(CLAIM_KEY_USER_ID).toString());
            String username = claims.get(CLAIM_KEY_USERNAME).toString();
            String realName = claims.get(CLAIM_KEY_REAL_NAME).toString();
            Integer userType = Integer.valueOf(claims.get(CLAIM_KEY_USER_TYPE).toString());
            Long deptId = Long.valueOf(claims.get(CLAIM_KEY_DEPT_ID).toString());

            return generateToken(userId, username, realName, userType, deptId);
        } catch (Exception e) {
            log.error("刷新token失败", e);
            throw new RuntimeException("刷新token失败");
        }
    }
}
package com.hyu.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.system.domain.SysUser;
import com.hyu.system.mapper.SysUserMapper;
import com.hyu.system.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * 用户表 Service 实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
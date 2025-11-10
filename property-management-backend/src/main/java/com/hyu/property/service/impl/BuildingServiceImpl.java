package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.property.domain.Building;
import com.hyu.property.mapper.BuildingMapper;
import com.hyu.property.service.IBuildingService;
import org.springframework.stereotype.Service;

/**
 * 楼栋表 Service 实现类
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements IBuildingService {

}
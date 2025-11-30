package com.hyu.property.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Complaint;
import com.hyu.property.mapper.ComplaintMapper;
import com.hyu.property.mapper.HouseMapper;
import com.hyu.property.service.IComplaintService;
import com.hyu.system.domain.SysUser;
import com.hyu.system.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 投诉Service业务层处理
 *
 * @author system
 * @date 2025-11-20
 */
@Slf4j
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements IComplaintService {

    @Autowired
    private HouseMapper houseMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 分页查询投诉列表
     *
     * @param page 分页参数
     * @param complaint 投诉信息
     * @return 投诉分页数据
     */
    @Override
    public Page<Complaint> selectComplaintPage(Page<Complaint> page, Complaint complaint) {
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();

        // 过滤已删除的记录
        queryWrapper.eq("deleted", 0);

        // 精确匹配条件
        queryWrapper.eq(StringUtils.isNotEmpty(complaint.getComplaintNo()), "complaint_no", complaint.getComplaintNo())
                   .eq(complaint.getUserId() != null, "user_id", complaint.getUserId())
                   .eq(complaint.getHouseId() != null, "house_id", complaint.getHouseId())
                   .eq(StringUtils.isNotEmpty(complaint.getComplaintType()), "complaint_type", complaint.getComplaintType())
                   .eq(complaint.getUrgencyLevel() != null, "urgency_level", complaint.getUrgencyLevel())
                   .eq(complaint.getComplaintStatus() != null, "complaint_status", complaint.getComplaintStatus())
                   .eq(complaint.getHandlerId() != null, "handler_id", complaint.getHandlerId())
                   .eq(StringUtils.isNotEmpty(complaint.getPhone()), "phone", complaint.getPhone());

        // 模糊匹配条件
        queryWrapper.like(StringUtils.isNotEmpty(complaint.getUserName()), "user_name", complaint.getUserName())
                   .like(StringUtils.isNotEmpty(complaint.getHouseNo()), "house_no", complaint.getHouseNo())
                   .like(StringUtils.isNotEmpty(complaint.getHandlerName()), "handler_name", complaint.getHandlerName())
                   .like(StringUtils.isNotEmpty(complaint.getComplaintContent()), "complaint_content", complaint.getComplaintContent());

        // 日期范围查询 - 创建时间
        if (complaint.getParams() != null) {
            Map<String, Object> params = complaint.getParams();
            if (params.get("beginTime") != null && StringUtils.isNotEmpty(params.get("beginTime").toString())) {
                queryWrapper.ge("create_time", params.get("beginTime"));
            }
            if (params.get("endTime") != null && StringUtils.isNotEmpty(params.get("endTime").toString())) {
                queryWrapper.le("create_time", params.get("endTime"));
            }
        }

        // 按创建时间倒序排序
        queryWrapper.orderByDesc("create_time");

        Page<Complaint> resultPage = page(page, queryWrapper);

        // 设置字典值对应的名称和格式化用户信息
        resultPage.getRecords().forEach(this::setDictionaryNames);

        return resultPage;
    }

    /**
     * 校验投诉单号是否唯一
     *
     * @param complaint 投诉信息
     * @return 结果 true唯一 false不唯一
     */
    @Override
    public boolean checkComplaintNoUnique(Complaint complaint) {
        Long id = StringUtils.isNull(complaint.getId()) ? -1L : complaint.getId();
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("complaint_no", complaint.getComplaintNo())
                   .eq("deleted", 0);
        Complaint info = getOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getId().longValue() != id.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 查询投诉详情
     *
     * @param id 投诉主键
     * @return 投诉
     */
    @Override
    public Complaint selectComplaintById(Long id) {
        Complaint complaint = getById(id);
        if (complaint != null) {
            // 设置字典值对应的名称
            setDictionaryNames(complaint);
        }
        return complaint;
    }

    /**
     * 根据用户ID查询投诉列表
     *
     * @param userId 用户ID
     * @return 投诉集合
     */
    @Override
    public List<Complaint> selectComplaintsByUserId(Long userId) {
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("deleted", 0)
                   .orderByDesc("create_time");

        List<Complaint> complaints = list(queryWrapper);
        // 设置字典值对应的名称
        complaints.forEach(this::setDictionaryNames);

        return complaints;
    }

    /**
     * 根据处理人ID查询投诉列表
     *
     * @param handlerId 处理人ID
     * @return 投诉集合
     */
    @Override
    public List<Complaint> selectComplaintsByHandlerId(Long handlerId) {
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("handler_id", handlerId)
                   .eq("deleted", 0)
                   .orderByDesc("create_time");

        List<Complaint> complaints = list(queryWrapper);
        // 设置字典值对应的名称
        complaints.forEach(this::setDictionaryNames);

        return complaints;
    }

    /**
     * 新增投诉
     *
     * @param complaint 投诉信息
     * @return 结果
     */
    @Override
    public int insertComplaint(Complaint complaint) {
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());
        complaint.setDeleted(0);
        complaint.setComplaintStatus(1); // 初始状态为待处理

        // 生成投诉单号
        if (StringUtils.isEmpty(complaint.getComplaintNo())) {
            complaint.setComplaintNo(generateComplaintNo());
        }

        // 根据房间编号获取房屋ID
        if (StringUtils.isNotEmpty(complaint.getHouseNo())) {
            Long houseId = houseMapper.selectHouseIdByHouseNo(complaint.getHouseNo());
            if (houseId != null) {
                complaint.setHouseId(houseId);
                log.info("根据房间编号 {} 找到房屋ID: {}", complaint.getHouseNo(), houseId);
            } else {
                log.warn("未找到房间编号 {} 对应的房屋ID", complaint.getHouseNo());
            }
        }

        // 格式化投诉人信息为"真实姓名(用户名)"格式
        if (complaint.getUserId() != null) {
            try {
                SysUser user = sysUserMapper.selectById(complaint.getUserId());
                if (user != null) {
                    String realName = user.getRealName();
                    String userName = user.getUsername();
                    if (StringUtils.isNotEmpty(realName) && StringUtils.isNotEmpty(userName)) {
                        // 格式：真实姓名(用户名)
                        complaint.setUserName(realName + "(" + userName + ")");
                    } else if (StringUtils.isNotEmpty(realName)) {
                        complaint.setUserName(realName);
                    } else {
                        complaint.setUserName(userName);
                    }
                }
            } catch (Exception e) {
                log.warn("查询用户信息失败: {}", e.getMessage());
                // 保持原有的userName值
            }
        }

        return save(complaint) ? 1 : 0;
    }

    /**
     * 修改投诉
     *
     * @param complaint 投诉信息
     * @return 结果
     */
    @Override
    public int updateComplaint(Complaint complaint) {
        complaint.setUpdateTime(LocalDateTime.now());
        return updateById(complaint) ? 1 : 0;
    }

    /**
     * 批量删除投诉
     *
     * @param ids 需要删除的投诉主键集合
     * @return 结果
     */
    @Override
    public int deleteComplaintByIds(Long[] ids) {
        // 逻辑删除
        int count = 0;
        for (Long id : ids) {
            Complaint complaint = new Complaint();
            complaint.setId(id);
            complaint.setDeleted(1);
            complaint.setUpdateTime(LocalDateTime.now());
            if (updateById(complaint)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 删除投诉信息
     *
     * @param id 投诉主键
     * @return 结果
     */
    @Override
    public int deleteComplaintById(Long id) {
        Complaint complaint = new Complaint();
        complaint.setId(id);
        complaint.setDeleted(1);
        complaint.setUpdateTime(LocalDateTime.now());
        return updateById(complaint) ? 1 : 0;
    }

    /**
     * 处理投诉
     *
     * @param id 投诉ID
     * @param params 处理参数
     * @return 结果
     */
    @Override
    public int handleComplaint(Long id, Map<String, Object> params) {
        Complaint complaint = getById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return 0;
        }

        // 设置处理信息
        complaint.setHandleContent((String) params.get("handleContent"));
        complaint.setHandleImageUrls((String) params.get("handleImageUrls"));
        complaint.setHandleTime(LocalDateTime.now());
        complaint.setComplaintStatus(3); // 已处理
        complaint.setUpdateTime(LocalDateTime.now());

        return updateById(complaint) ? 1 : 0;
    }

    /**
     * 评价投诉
     *
     * @param id 投诉ID
     * @param params 评价参数
     * @return 结果
     */
    @Override
    public int rateComplaint(Long id, Map<String, Object> params) {
        Complaint complaint = getById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return 0;
        }

        // 设置评价信息
        complaint.setRating((Integer) params.get("rating"));
        complaint.setRatingContent((String) params.get("comment")); // 使用comment字段
        complaint.setRatingTime(LocalDateTime.now());

        // 评价完成后将状态设为已关闭
        complaint.setComplaintStatus(4);
        complaint.setUpdateTime(LocalDateTime.now());

        return updateById(complaint) ? 1 : 0;
    }

    /**
     * 关闭投诉
     *
     * @param id 投诉ID
     * @return 结果
     */
    @Override
    public int closeComplaint(Long id) {
        Complaint complaint = getById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return 0;
        }

        complaint.setComplaintStatus(4); // 已关闭
        complaint.setAutoCloseTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());

        return updateById(complaint) ? 1 : 0;
    }

    /**
     * 派单处理
     *
     * @param id 投诉ID
     * @param handlerId 处理人ID

     * @return 结果
     */
    @Override
    public int assignComplaint(Long id, Long handlerId, String remark) {
        Complaint complaint = getById(id);
        if (complaint == null || complaint.getDeleted() == 1) {
            return 0;
        }

        // 根据handlerId查询处理人姓名
        String handlerName = null;
        if (handlerId != null) {
            try {
                SysUser handler = sysUserMapper.selectById(handlerId);
                if (handler != null) {
                    handlerName = handler.getRealName();
                    log.info("查询到处理人信息: ID={}, 姓名={}", handlerId, handlerName);
                } else {
                    log.warn("未找到处理人信息, ID={}", handlerId);
                }
            } catch (Exception e) {
                log.error("查询处理人信息失败: {}", e.getMessage());
            }
        }

        complaint.setHandlerId(handlerId);
        complaint.setHandlerName(handlerName);
        complaint.setComplaintStatus(2); // 处理中
        complaint.setUpdateTime(LocalDateTime.now());

        return updateById(complaint) ? 1 : 0;
    }

    /**
     * 生成投诉单号
     *
     * @return 投诉单号
     */
    @Override
    public String generateComplaintNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Random random = new Random();
        int randomNum = 1000 + random.nextInt(9000);
        return "CO" + timestamp + "_" + randomNum;
    }

    /**
     * 统计投诉数量
     *
     * @param complaint 投诉信息
     * @return 统计结果
     */
    @Override
    public Map<String, Object> countComplaintStats(Complaint complaint) {
        Map<String, Object> result = new HashMap<>();

        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);

        if (complaint != null && complaint.getUserId() != null) {
            queryWrapper.eq("user_id", complaint.getUserId());
        }

        // 总投诉数
        result.put("total", count(queryWrapper));

        // 待处理数
        queryWrapper.eq("complaint_status", 1);
        result.put("pending", count(queryWrapper));

        // 处理中数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        if (complaint != null && complaint.getUserId() != null) {
            queryWrapper.eq("user_id", complaint.getUserId());
        }
        queryWrapper.eq("complaint_status", 2);
        result.put("processing", count(queryWrapper));

        // 已处理数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        if (complaint != null && complaint.getUserId() != null) {
            queryWrapper.eq("user_id", complaint.getUserId());
        }
        queryWrapper.eq("complaint_status", 3);
        result.put("handled", count(queryWrapper));

        // 已关闭数
        queryWrapper.clear();
        queryWrapper.eq("deleted", 0);
        if (complaint != null && complaint.getUserId() != null) {
            queryWrapper.eq("user_id", complaint.getUserId());
        }
        queryWrapper.eq("complaint_status", 4);
        result.put("closed", count(queryWrapper));

        return result;
    }

    /**
     * 设置字典值对应的名称
     *
     * @param complaint 投诉对象
     */
    private void setDictionaryNames(Complaint complaint) {
        if (complaint == null) {
            return;
        }

        // 如果userName为空，则尝试格式化用户信息
        if (StringUtils.isEmpty(complaint.getUserName()) && complaint.getUserId() != null) {
            try {
                SysUser user = sysUserMapper.selectById(complaint.getUserId());
                if (user != null) {
                    String realName = user.getRealName();
                    String userName = user.getUsername();
                    if (StringUtils.isNotEmpty(realName) && StringUtils.isNotEmpty(userName)) {
                        // 格式：真实姓名(用户名)
                        complaint.setUserName(realName + "(" + userName + ")");
                    } else if (StringUtils.isNotEmpty(realName)) {
                        complaint.setUserName(realName);
                    } else {
                        complaint.setUserName(userName);
                    }
                }
            } catch (Exception e) {
                log.warn("查询用户信息失败: {}", e.getMessage());
                // 保持原有值
            }
        }

        // 设置紧急程度名称
        if (complaint.getUrgencyLevel() != null) {
            complaint.setUrgencyLevelName(complaint.getUrgencyLevel() == 1 ? "普通" : "紧急");
        }

        // 设置评价等级名称
        if (complaint.getRating() != null) {
            String[] ratingNames = {"", "满意", "一般", "不满意"};
            if (complaint.getRating() >= 1 && complaint.getRating() <= 3) {
                complaint.setRatingName(ratingNames[complaint.getRating()]);
            }
        }
    }

    @Override
    public List<Map<String, Object>> getAvailableHandlers() {
        return baseMapper.selectAvailableHandlers();
    }
}
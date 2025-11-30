package com.hyu.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.RepairOrder;
import com.hyu.property.service.IRepairOrderService;
import com.hyu.property.service.IUserHouseService;
import com.hyu.property.domain.UserHouse;
import com.hyu.system.domain.SysUser;
import com.hyu.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 维修工单管理
 *
 * @author hyu
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/repair")
@Validated
public class RepairOrderController {

    @Autowired
    private IRepairOrderService repairOrderService;

    @Autowired
    private IUserHouseService userHouseService;

    @Autowired
    private ISysUserService userService;

    @Value("${repair.upload.path:./uploads/images}")
    private String uploadPath;

    /**
     * 分页查询维修工单列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String orderNo,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) String userName,
                          @RequestParam(required = false) Long houseId,
                          @RequestParam(required = false) String houseNo,
                          @RequestParam(required = false) String repairType,
                          @RequestParam(required = false) Integer urgencyLevel,
                          @RequestParam(required = false) Integer orderStatus,
                          @RequestParam(required = false) Long workerId,
                          @RequestParam(required = false) String workerName,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String faultDescription,
                          @RequestParam(required = false) String beginTime,
                          @RequestParam(required = false) String endTime,
                          @RequestParam(required = false) String beginAppointmentTime,
                          @RequestParam(required = false) String endAppointmentTime,
                          @RequestParam(required = false) String beginFinishTime,
                          @RequestParam(required = false) String endFinishTime,
                          @RequestParam(required = false) BigDecimal minRepairCost,
                          @RequestParam(required = false) BigDecimal maxRepairCost) {
        log.info("分页查询维修工单列表, pageNum: {}, pageSize: {}, orderNo: {}, userId: {}, userName: {}, houseId: {}, houseNo: {}, repairType: {}, urgencyLevel: {}, orderStatus: {}, workerId: {}, workerName: {}, phone: {}, beginTime: {}, endTime: {}",
                pageNum, pageSize, orderNo, userId, userName, houseId, houseNo, repairType, urgencyLevel, orderStatus, workerId, workerName, phone, beginTime, endTime);

        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setOrderNo(orderNo);
        repairOrder.setUserId(userId);
        repairOrder.setUserName(userName);
        repairOrder.setHouseId(houseId);
        repairOrder.setHouseNo(houseNo);
        repairOrder.setRepairType(repairType);
        repairOrder.setUrgencyLevel(urgencyLevel);
        repairOrder.setOrderStatus(orderStatus);
        repairOrder.setWorkerId(workerId);
        repairOrder.setWorkerName(workerName);
        repairOrder.setPhone(phone);
        repairOrder.setFaultDescription(faultDescription);

        // 设置查询参数
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        if (beginTime != null && !beginTime.trim().isEmpty()) {
            params.put("beginTime", beginTime);
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            params.put("endTime", endTime);
        }
        if (beginAppointmentTime != null && !beginAppointmentTime.trim().isEmpty()) {
            params.put("beginAppointmentTime", beginAppointmentTime);
        }
        if (endAppointmentTime != null && !endAppointmentTime.trim().isEmpty()) {
            params.put("endAppointmentTime", endAppointmentTime);
        }
        if (beginFinishTime != null && !beginFinishTime.trim().isEmpty()) {
            params.put("beginFinishTime", beginFinishTime);
        }
        if (endFinishTime != null && !endFinishTime.trim().isEmpty()) {
            params.put("endFinishTime", endFinishTime);
        }
        if (minRepairCost != null) {
            params.put("minRepairCost", minRepairCost);
        }
        if (maxRepairCost != null) {
            params.put("maxRepairCost", maxRepairCost);
        }
        repairOrder.setParams(params);

        Page<RepairOrder> result = repairOrderService.selectRepairOrderPage(page, repairOrder);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取维修工单详细信息
     */
    @GetMapping(value = "/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    public AjaxResult getInfo(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id) {
        log.info("获取维修工单详细信息, id: {}", id);
        return AjaxResult.success(repairOrderService.getById(id));
    }

    /**
     * 新增维修工单
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:repair:add')")
    public AjaxResult add(@Valid @RequestBody RepairOrder repairOrder) {
        log.info("新增维修工单, repairOrder: {}", repairOrder);
        if (!repairOrderService.checkOrderNoUnique(repairOrder)) {
            return AjaxResult.error("新增维修工单'" + repairOrder.getOrderNo() + "'失败，工单编号已存在");
        }
        repairOrder.setCreateBy(SecurityUtils.getUsername());
        // 设置工单初始状态为 1-待派工
        if (repairOrder.getOrderStatus() == null) {
            repairOrder.setOrderStatus(1);
        }
        return toAjax(repairOrderService.save(repairOrder));
    }

    /**
     * 修改保存维修工单
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:repair:edit')")
    public AjaxResult edit(@Valid @RequestBody RepairOrder repairOrder) {
        log.info("修改维修工单, repairOrder: {}", repairOrder);
        if (!repairOrderService.checkOrderNoUnique(repairOrder)) {
            return AjaxResult.error("修改维修工单'" + repairOrder.getOrderNo() + "'失败，工单编号已存在");
        }
        repairOrder.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(repairOrderService.updateById(repairOrder));
    }

    /**
     * 获取维修人员列表
     */
    @GetMapping("/repairers")
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    public AjaxResult getRepairers() {
        log.info("获取维修人员列表");
        List<Map<String, Object>> repairers = repairOrderService.getRepairerList();
        return AjaxResult.success(repairers);
    }

    /**
     * 维修人员查看分配给自己的工单
     */
    @GetMapping("/worker/my-orders")
    @PreAuthorize("@ss.hasPermi('property:repair:accept')")
    public AjaxResult getMyWorkerOrders(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String orderNo,
                                         @RequestParam(required = false) String repairType,
                                         @RequestParam(required = false) Integer orderStatus,
                                         @RequestParam(required = false) String phone) {
        log.info("维修人员查看我的工单, pageNum: {}, pageSize: {}, orderNo: {}, repairType: {}, orderStatus: {}, phone: {}",
                 pageNum, pageSize, orderNo, repairType, orderStatus, phone);

        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        QueryWrapper<RepairOrder> queryWrapper = new QueryWrapper<>();

        // 只查询分配给当前维修人员的工单
        queryWrapper.eq("worker_id", SecurityUtils.getUserId())
                   .eq("deleted", 0);

        if (StringUtils.isNotEmpty(orderNo)) {
            queryWrapper.like("order_no", orderNo);
        }
        if (StringUtils.isNotEmpty(repairType)) {
            queryWrapper.eq("repair_type", repairType);
        }
        if (orderStatus != null) {
            queryWrapper.eq("order_status", orderStatus);
        }
        if (StringUtils.isNotEmpty(phone)) {
            queryWrapper.like("phone", phone);
        }

        queryWrapper.orderByDesc("create_time");

        Page<RepairOrder> resultPage = repairOrderService.page(page, queryWrapper);

        // 设置维修人员真实姓名和电话
        resultPage.getRecords().forEach(order -> {
            if (order.getCreateTime() != null) {
                order.setReportTime(order.getCreateTime());
            }

            // 获取报修人真实姓名
            if (order.getUserId() != null) {
                SysUser user = userService.getById(order.getUserId());
                if (user != null && StringUtils.isNotEmpty(user.getRealName())) {
                    order.setRealName(user.getRealName());
                }
            }
        });

        return AjaxResult.success(resultPage);
    }

    /**
     * 系统管理员派工
     */
    @PutMapping("/assign/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:assign')")
    public AjaxResult assign(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id,
                            @RequestBody Map<String, Object> params) {
        log.info("系统管理员派工, id: {}, params: {}", id, params);
        return toAjax(repairOrderService.assignOrder(id, params));
    }

    /**
     * 维修师傅接单
     */
    @PutMapping("/accept/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:accept')")
    public AjaxResult accept(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id) {
        log.info("维修师傅接单, id: {}", id);
        return toAjax(repairOrderService.acceptOrder(id));
    }

    /**
     * 完成维修
     */
    @PutMapping("/complete/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:complete')")
    public AjaxResult complete(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id,
                              @RequestBody Map<String, Object> params) {
        log.info("完成维修, id: {}, params: {}", id, params);
        return toAjax(repairOrderService.completeOrder(id, params));
    }

    /**
     * 维修师傅提交维修记录
     */
    @PutMapping("/handle/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:handle')")
    public AjaxResult handle(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id,
                            @RequestBody Map<String, Object> params) {
        log.info("维修师傅提交维修记录, id: {}, params: {}", id, params);
        return toAjax(repairOrderService.completeOrder(id, params));
    }

    /**
     * 验收维修
     */
    @PutMapping("/inspect/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:inspect')")
    public AjaxResult inspect(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id,
                             @RequestBody Map<String, Object> params) {
        log.info("验收维修, id: {}, params: {}", id, params);
        return toAjax(repairOrderService.inspectOrder(id, params));
    }

    /**
     * 删除维修工单
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:repair:delete')")
    public AjaxResult remove(@NotNull(message = "维修工单ID不能为空") @PathVariable Long[] ids) {
        log.info("删除维修工单, ids: {}", ids);
        return toAjax(repairOrderService.removeByIds(java.util.Arrays.asList(ids)));
    }

    /**
     * 业主报修 - 创建维修工单
     */
    @PostMapping("/owner")
    public AjaxResult ownerReport(@Valid @RequestBody RepairOrder repairOrder) {
        log.info("业主报修, repairOrder: {}", repairOrder);

        // 设置当前用户ID和用户名为报修人
        Long currentUserId = SecurityUtils.getUserId();
        String currentUsername = SecurityUtils.getUsername();
        repairOrder.setUserId(currentUserId);
        repairOrder.setUserName(currentUsername);

        // 从用户信息中获取联系电话
        String currentUserPhone = SecurityUtils.getLoginUser().getPhone();
        repairOrder.setPhone(currentUserPhone);

        // 获取用户的第一个当前房产信息
        log.info("开始查询用户 {} 的房产信息", currentUserId);
        List<UserHouse> userHouses = userHouseService.selectCurrentUserHouseByUserId(currentUserId);
        log.info("查询到用户房产信息数量: {}", userHouses.size());

        if (!userHouses.isEmpty()) {
            UserHouse userHouse = userHouses.get(0);
            repairOrder.setHouseId(userHouse.getHouseId());
            repairOrder.setHouseNo(userHouse.getHouseNo()); // 注意：这里可能需要从house表获取house_no
            log.info("使用用户房产信息: houseId={}, houseNo={}", userHouse.getHouseId(), userHouse.getHouseNo());
        } else {
            log.warn("用户 {} 没有关联的房产信息，使用默认值", currentUserId);
            repairOrder.setHouseId(1L);  // 默认房产ID
            repairOrder.setHouseNo("待分配");  // 默认房间编号
        }

        log.info("设置后的repairOrder: houseId={}, houseNo={}", repairOrder.getHouseId(), repairOrder.getHouseNo());

        // 生成工单编号
        String orderNo = "RO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                       "_" + String.format("%04d", new java.util.Random().nextInt(10000));
        repairOrder.setOrderNo(orderNo);

        // 设置初始状态为待派工
        repairOrder.setOrderStatus(1);

        // 验证工单编号唯一性
        if (!repairOrderService.checkOrderNoUnique(repairOrder)) {
            return AjaxResult.error("新增维修工单'" + repairOrder.getOrderNo() + "'失败，工单编号已存在");
        }

        repairOrder.setCreateBy(SecurityUtils.getUsername());
        return toAjax(repairOrderService.save(repairOrder));
    }

    /**
     * 业主查看我的报修
     */
    @GetMapping("/owner/my-repairs")
    public AjaxResult getMyRepairs(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(required = false) String orderNo,
                                   @RequestParam(required = false) String repairType,
                                   @RequestParam(required = false) Integer orderStatus,
                                   @RequestParam(required = false) String phone) {
        log.info("业主查看我的报修, pageNum: {}, pageSize: {}, orderNo: {}, repairType: {}, orderStatus: {}, phone: {}",
                pageNum, pageSize, orderNo, repairType, orderStatus, phone);

        Long currentUserId = SecurityUtils.getUserId();
        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        RepairOrder repairOrder = new RepairOrder();
        repairOrder.setUserId(currentUserId);
        repairOrder.setOrderNo(orderNo);
        repairOrder.setRepairType(repairType);
        repairOrder.setOrderStatus(orderStatus);
        repairOrder.setPhone(phone);

        Page<RepairOrder> result = repairOrderService.selectRepairOrderPage(page, repairOrder);
        return AjaxResult.success("查询成功", PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 业主删除自己的报修工单（仅限待派工状态）
     */
    @DeleteMapping("/owner/{id}")
    public AjaxResult ownerDelete(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id) {
        log.info("业主删除报修工单, id: {}", id);

        // 获取当前用户ID
        Long currentUserId = SecurityUtils.getUserId();

        // 查询维修工单
        RepairOrder repairOrder = repairOrderService.getById(id);
        if (repairOrder == null) {
            return AjaxResult.error("维修工单不存在");
        }

        // 验证是否为当前用户的工单
        if (!currentUserId.equals(repairOrder.getUserId())) {
            return AjaxResult.error("无权限删除此工单");
        }

        // 验证工单状态是否为待派工
        if (repairOrder.getOrderStatus() != null && repairOrder.getOrderStatus() != 0) {
            return AjaxResult.error("只能删除待派工状态的工单");
        }

        return toAjax(repairOrderService.removeById(id));
    }

    /**
     * 上传维修图片
     */
    @PostMapping("/upload-images")
    public AjaxResult uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            log.info("上传维修图片文件，数量: {}", files.length);

            if (files.length == 0) {
                return AjaxResult.error("请选择要上传的图片");
            }

            // 确保上传目录存在
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
                log.info("创建上传目录: {}", uploadPath);
            }

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                // 验证文件类型
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return AjaxResult.error("只能上传图片文件");
                }

                // 验证文件大小（限制为5MB）
                if (file.getSize() > 5 * 1024 * 1024) {
                    return AjaxResult.error("图片文件大小不能超过5MB");
                }

                // 生成唯一文件名
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                String fileName = UUID.randomUUID().toString() + extension;

                // 保存文件
                Path filePath = Paths.get(uploadPath, fileName);
                Files.copy(file.getInputStream(), filePath);

                // 构建访问URL
                String imageUrl = "/images/" + fileName;
                imageUrls.add(imageUrl);

                log.info("保存图片文件: {}", fileName);
            }

            return AjaxResult.success("图片上传成功", imageUrls);

        } catch (IOException e) {
            log.error("上传图片失败", e);
            return AjaxResult.error("上传图片失败: " + e.getMessage());
        }
    }

    /**
     * 访问维修工单图片文件
     * 注意：Spring Boot会自动提供static目录下的静态资源访问
     * 前端可以直接通过 /images/filename.jpg 访问图片
     */
    @GetMapping("/images/check/{filename:.+}")
    public AjaxResult checkImage(@PathVariable String filename) {
        try {
            log.info("检查维修图片文件: {}", filename);

            // 安全检查：防止路径遍历攻击
            if (filename.contains("..") || filename.contains("\\")) {
                log.warn("检测到不安全的文件路径: {}", filename);
                return AjaxResult.error("不安全的文件路径");
            }

            // 构建文件路径
            Path filePath = Paths.get(uploadPath, filename);
            File file = filePath.toFile();

            // 检查文件是否存在
            if (!file.exists() || !file.isFile()) {
                log.warn("图片文件不存在: {}", filePath);
                return AjaxResult.error("图片文件不存在");
            }

            // 检查文件类型
            String contentType = Files.probeContentType(filePath);
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("文件不是图片类型: {}", filePath);
                return AjaxResult.error("文件不是图片类型");
            }

            return AjaxResult.success("图片文件存在");

        } catch (IOException e) {
            log.error("检查图片文件失败: {}", filename, e);
            return AjaxResult.error("检查图片文件失败");
        }
    }

    /**
     * 业主评价维修工单
     */
    @PutMapping("/rate/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:rate')")
    public AjaxResult rate(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id,
                           @RequestBody Map<String, Object> params) {
        log.info("业主评价维修工单, id: {}, params: {}", id, params);
        return toAjax(repairOrderService.rateOrder(id, params));
    }

    /**
     * 系统管理员归档维修工单
     */
    @PutMapping("/archive/{id}")
    @PreAuthorize("@ss.hasPermi('property:repair:archive')")
    public AjaxResult archive(@NotNull(message = "维修工单ID不能为空") @PathVariable Long id) {
        log.info("系统管理员归档维修工单, id: {}", id);
        return toAjax(repairOrderService.archiveOrder(id));
    }

    /**
     * 批量归档维修工单
     */
    @PutMapping("/archive/batch")
    @PreAuthorize("@ss.hasPermi('property:repair:archive')")
    public AjaxResult batchArchive(@RequestBody Long[] ids) {
        log.info("系统管理员批量归档维修工单, ids: {}", ids);
        return toAjax(repairOrderService.batchArchiveOrders(java.util.Arrays.asList(ids)));
    }

    /**
     * 获取归档维修工单列表
     */
    @GetMapping("/archive/list")
    @PreAuthorize("@ss.hasPermi('property:repair:list')")
    public AjaxResult getArchivedList(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     RepairOrder repairOrder) {
        log.info("获取归档维修工单列表, pageNum: {}, pageSize: {}, params: {}", pageNum, pageSize, repairOrder);
        Page<RepairOrder> page = new Page<>(pageNum, pageSize);
        // 只查询已归档的工单（状态6）
        repairOrder.setOrderStatus(6);
        Page<RepairOrder> result = repairOrderService.selectRepairOrderPage(page, repairOrder);
        return AjaxResult.success(result);
    }

    /**
     * 返回AjaxResult
     */
    private AjaxResult toAjax(Boolean result) {
        return result ? AjaxResult.success() : AjaxResult.error();
    }
}
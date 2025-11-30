package com.hyu.property.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.core.domain.PageResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.common.utils.StringUtils;
import com.hyu.property.domain.Complaint;
import com.hyu.property.service.IComplaintService;
import com.hyu.property.service.IUserHouseService;
import com.hyu.property.domain.UserHouse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 投诉管理
 *
 * @author system
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/property/complaint")
@Validated
public class ComplaintController {

    @Autowired
    private IComplaintService complaintService;

    @Autowired
    private IUserHouseService userHouseService;

    @Value("${complaint.upload.path:./uploads/complaint}")
    private String uploadPath;

    /**
     * 分页查询投诉列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('property:complaint:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String complaintNo,
                          @RequestParam(required = false) Long userId,
                          @RequestParam(required = false) String userName,
                          @RequestParam(required = false) Long houseId,
                          @RequestParam(required = false) String houseNo,
                          @RequestParam(required = false) String complaintType,
                          @RequestParam(required = false) Integer urgencyLevel,
                          @RequestParam(required = false) Integer complaintStatus,
                          @RequestParam(required = false) Long handlerId,
                          @RequestParam(required = false) String handlerName,
                          @RequestParam(required = false) String phone,
                          @RequestParam(required = false) String complaintContent,
                          @RequestParam(required = false) String beginTime,
                          @RequestParam(required = false) String endTime) {
        log.info("分页查询投诉列表, pageNum: {}, pageSize: {}, complaintNo: {}, userId: {}, userName: {}, houseId: {}, houseNo: {}, complaintType: {}, urgencyLevel: {}, complaintStatus: {}, handlerId: {}, handlerName: {}, phone: {}, beginTime: {}, endTime: {}",
                pageNum, pageSize, complaintNo, userId, userName, houseId, houseNo, complaintType, urgencyLevel, complaintStatus, handlerId, handlerName, phone, beginTime, endTime);

        Page<Complaint> page = new Page<>(pageNum, pageSize);
        Complaint complaint = new Complaint();
        complaint.setComplaintNo(complaintNo);
        complaint.setUserId(userId);
        complaint.setUserName(userName);
        complaint.setHouseId(houseId);
        complaint.setHouseNo(houseNo);
        complaint.setComplaintType(complaintType);
        complaint.setUrgencyLevel(urgencyLevel);
        complaint.setComplaintStatus(complaintStatus);
        complaint.setHandlerId(handlerId);
        complaint.setHandlerName(handlerName);
        complaint.setPhone(phone);
        complaint.setComplaintContent(complaintContent);

        // 设置时间范围参数
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(beginTime)) {
            params.put("beginTime", beginTime);
        }
        if (StringUtils.isNotEmpty(endTime)) {
            params.put("endTime", endTime);
        }
        complaint.setParams(params);

        Page<Complaint> result = complaintService.selectComplaintPage(page, complaint);

        log.info("分页查询投诉列表成功，记录数: {}, 总数: {}", result.getRecords().size(), result.getTotal());

        return AjaxResult.success(new PageResult(result.getRecords(), result.getTotal()));
    }

    /**
     * 查询投诉详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('property:complaint:query')")
    public AjaxResult getInfo(@NotNull(message = "投诉ID不能为空") @PathVariable Long id) {
        log.info("查询投诉详情, id: {}", id);
        Complaint complaint = complaintService.selectComplaintById(id);
        return AjaxResult.success(complaint);
    }

    /**
     * 新增投诉
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('property:complaint:add')")
    public AjaxResult add(@Valid @RequestBody Complaint complaint) {
        log.info("新增投诉: {}", complaint);

        // 设置投诉人信息
        complaint.setUserId(SecurityUtils.getUserId());
        complaint.setUserName(SecurityUtils.getUsername());
        complaint.setCreateBy(SecurityUtils.getUsername());
        complaint.setUpdateBy(SecurityUtils.getUsername());

        // 校验投诉单号是否唯一
        if (!complaintService.checkComplaintNoUnique(complaint)) {
            return AjaxResult.error("新增投诉'" + complaint.getComplaintNo() + "'失败，投诉单号已存在");
        }

        return toAjax(complaintService.insertComplaint(complaint));
    }

    /**
     * 修改投诉
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('property:complaint:edit')")
    public AjaxResult edit(@Valid @RequestBody Complaint complaint) {
        log.info("修改投诉: {}", complaint);

        // 设置更新人信息
        complaint.setUpdateBy(SecurityUtils.getUsername());

        // 校验投诉单号是否唯一
        if (!complaintService.checkComplaintNoUnique(complaint)) {
            return AjaxResult.error("修改投诉'" + complaint.getComplaintNo() + "'失败，投诉单号已存在");
        }

        return toAjax(complaintService.updateComplaint(complaint));
    }

    /**
     * 删除投诉
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('property:complaint:remove')")
    public AjaxResult remove(@NotNull(message = "投诉ID不能为空") @PathVariable Long[] ids) {
        log.info("删除投诉, ids: {}", Arrays.toString(ids));
        return toAjax(complaintService.deleteComplaintByIds(ids));
    }

    /**
     * 派单处理
     */
    @PutMapping("/assign/{id}")
    @PreAuthorize("@ss.hasPermi('property:complaint:assign')")
    public AjaxResult assign(@NotNull(message = "投诉ID不能为空") @PathVariable Long id,
                             @RequestBody Map<String, Object> params) {
        log.info("投诉派单, id: {}, params: {}", id, params);

        Long handlerId = Long.valueOf(params.get("handlerId").toString());
        String remark = params.get("remark") != null ? params.get("remark").toString() : null;

        return toAjax(complaintService.assignComplaint(id, handlerId, remark));
    }

    /**
     * 处理投诉
     */
    @PutMapping("/handle/{id}")
    @PreAuthorize("@ss.hasPermi('property:complaint:handle')")
    public AjaxResult handle(@NotNull(message = "投诉ID不能为空") @PathVariable Long id,
                             @RequestBody Map<String, Object> params) {
        log.info("处理投诉, id: {}, params: {}", id, params);

        return toAjax(complaintService.handleComplaint(id, params));
    }

    /**
     * 评价投诉
     */
    @PutMapping("/rate/{id}")
    @PreAuthorize("@ss.hasPermi('property:complaint:rate')")
    public AjaxResult rate(@NotNull(message = "投诉ID不能为空") @PathVariable Long id,
                           @RequestBody Map<String, Object> params) {
        log.info("评价投诉, id: {}, params: {}", id, params);

        return toAjax(complaintService.rateComplaint(id, params));
    }

    /**
     * 关闭投诉
     */
    @PutMapping("/close/{id}")
    @PreAuthorize("@ss.hasPermi('property:complaint:close')")
    public AjaxResult close(@NotNull(message = "投诉ID不能为空") @PathVariable Long id) {
        log.info("关闭投诉, id: {}", id);

        return toAjax(complaintService.closeComplaint(id));
    }

    /**
     * 获取我的投诉列表
     */
    @GetMapping("/my")
    @PreAuthorize("@ss.hasPermi('property:complaint:my')")
    public AjaxResult getMyComplaints(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @RequestParam(required = false) String complaintNo,
                                     @RequestParam(required = false) String userName,
                                     @RequestParam(required = false) String houseNo,
                                     @RequestParam(required = false) String complaintType,
                                     @RequestParam(required = false) Integer complaintStatus,
                                     @RequestParam(required = false) Integer urgencyLevel,
                                     @RequestParam(required = false) String phone) {
        log.info("获取我的投诉列表, pageNum: {}, pageSize: {}, complaintNo: {}, userName: {}, houseNo: {}, complaintType: {}, complaintStatus: {}, urgencyLevel: {}, phone: {}",
                 pageNum, pageSize, complaintNo, userName, houseNo, complaintType, complaintStatus, urgencyLevel, phone);

        Page<Complaint> page = new Page<>(pageNum, pageSize);
        Complaint complaint = new Complaint();
        complaint.setUserId(SecurityUtils.getUserId());
        complaint.setComplaintNo(complaintNo);
        complaint.setUserName(userName);
        complaint.setHouseNo(houseNo);
        complaint.setComplaintType(complaintType);
        complaint.setComplaintStatus(complaintStatus);
        complaint.setUrgencyLevel(urgencyLevel);
        complaint.setPhone(phone);

        Page<Complaint> result = complaintService.selectComplaintPage(page, complaint);

        log.info("获取我的投诉列表成功，记录数: {}, 总数: {}", result.getRecords().size(), result.getTotal());

        return AjaxResult.success(new PageResult(result.getRecords(), result.getTotal()));
    }

    /**
     * 上传投诉图片
     */
    @PostMapping("/upload")
    @PreAuthorize("@ss.hasPermi('property:complaint:upload')")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("上传投诉图片, 文件名: {}, 文件大小: {}", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }

        // 检查文件类型
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.endsWithAny(originalFilename.toLowerCase(), ".jpg", ".jpeg", ".png", ".gif")) {
            return AjaxResult.error("只支持jpg、jpeg、png、gif格式的图片");
        }

        // 检查文件大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return AjaxResult.error("文件大小不能超过5MB");
        }

        // 创建上传目录
        String uploadDir = uploadPath + "/complaint/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + fileExtension;
        String filePath = uploadDir + "/" + newFileName;

        // 保存文件
        file.transferTo(new File(filePath));

        // 返回文件访问路径
        String accessPath = "/images/complaint/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")) + "/" + newFileName;

        log.info("投诉图片上传成功: {}", accessPath);

        return AjaxResult.success(accessPath);
    }

    /**
     * 获取投诉图片
     */
    @GetMapping("/image/**")
    public ResponseEntity<Resource> getImage(@RequestParam String path) {
        try {
            // 构建文件路径
            String filePath = uploadPath + path;
            Resource resource = new FileSystemResource(filePath);

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 确定文件类型
            String contentType = "application/octet-stream";
            if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (path.endsWith(".png")) {
                contentType = "image/png";
            } else if (path.endsWith(".gif")) {
                contentType = "image/gif";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("获取投诉图片失败: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 获取投诉统计信息
     */
    @GetMapping("/stats")
    @PreAuthorize("@ss.hasPermi('property:complaint:list')")
    public AjaxResult getStats(@RequestParam(required = false) Long userId) {
        log.info("获取投诉统计信息, userId: {}", userId);

        Complaint complaint = new Complaint();
        complaint.setUserId(userId);

        Map<String, Object> stats = complaintService.countComplaintStats(complaint);
        return AjaxResult.success(stats);
    }

    /**
     * 获取可用的处理人列表（物业管理员）
     */
    @GetMapping("/handlers")
    @PreAuthorize("@ss.hasPermi('property:complaint:assign')")
    public AjaxResult getAvailableHandlers() {
        log.info("获取可用的投诉处理人列表");

        // 获取物业管理员列表（userType=2）
        List<Map<String, Object>> handlers = complaintService.getAvailableHandlers();

        return AjaxResult.success(handlers);
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows) {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }
}
package com.hyu.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hyu.common.core.domain.AjaxResult;
import com.hyu.common.utils.SecurityUtils;
import com.hyu.property.domain.ParkingRentalContract;
import com.hyu.property.service.IParkingRentalContractService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 车位租赁合同控制器
 *
 * @author system
 * @date 2025-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/parking/rental/contract")
@Validated
public class ParkingRentalContractController {

    @Autowired
    private IParkingRentalContractService contractService;

    /**
     * 分页查询合同列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('parking:contract:list')")
    public AjaxResult list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(required = false) String contractNo,
                          @RequestParam(required = false) String spaceNo,
                          @RequestParam(required = false) Long parkingSpaceId,
                          @RequestParam(required = false) String ownerName,
                          @RequestParam(required = false) String vehicleNumber,
                          @RequestParam(required = false) Integer contractStatus) {
        log.info("分页查询租赁合同列表, pageNum: {}, pageSize: {}, parkingSpaceId: {}", pageNum, pageSize, parkingSpaceId);

        Page<ParkingRentalContract> page = new Page<>(pageNum, pageSize);
        ParkingRentalContract contract = new ParkingRentalContract();
        contract.setContractNo(contractNo);
        contract.setSpaceNo(spaceNo);
        contract.setParkingSpaceId(parkingSpaceId);
        contract.setOwnerName(ownerName);
        contract.setVehicleNumber(vehicleNumber);
        contract.setContractStatus(contractStatus);

        Page<ParkingRentalContract> result = contractService.selectContractPage(page, contract);
        return AjaxResult.success("查询成功",
                com.hyu.common.core.domain.PageResult.success(result.getTotal(), result.getRecords()));
    }

    /**
     * 获取合同详细信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('parking:contract:query')")
    public AjaxResult getInfo(@NotNull(message = "合同ID不能为空") @PathVariable Long id) {
        log.info("获取合同详细信息, id: {}", id);
        ParkingRentalContract contract = contractService.selectContractById(id);
        return AjaxResult.success(contract);
    }

    /**
     * 根据申请ID生成合同
     */
    @PostMapping("/generate/{applicationId}")
    @PreAuthorize("@ss.hasPermi('parking:contract:add')")
    public AjaxResult generate(@NotNull(message = "申请ID不能为空") @PathVariable Long applicationId) {
        log.info("生成租赁合同, applicationId: {}", applicationId);

        ParkingRentalContract contract = contractService.generateContract(applicationId);
        return AjaxResult.success("合同生成成功", contract);
    }

    /**
     * 终止合同
     */
    @PutMapping("/terminate/{id}")
    @PreAuthorize("@ss.hasPermi('parking:contract:terminate')")
    public AjaxResult terminate(@NotNull(message = "合同ID不能为空") @PathVariable Long id,
                               @RequestParam(required = false) String terminateReason) {
        log.info("终止租赁合同, id: {}, terminateReason: {}", id, terminateReason);

        boolean result = contractService.terminateContract(id, terminateReason);
        return result ? AjaxResult.success("合同终止成功") : AjaxResult.error("合同终止失败");
    }

    /**
     * 获取合同统计数据
     */
    @GetMapping("/statistics")
    @PreAuthorize("@ss.hasPermi('parking:contract:list')")
    public AjaxResult getStatistics() {
        log.info("获取合同统计数据");
        Map<String, Object> stats = contractService.getContractStats();
        return AjaxResult.success(stats);
    }

    /**
     * 查询即将到期的合同
     */
    @GetMapping("/expiring")
    @PreAuthorize("@ss.hasPermi('parking:contract:list')")
    public AjaxResult getExpiringContracts(@RequestParam(defaultValue = "7") Integer days) {
        log.info("查询即将到期的合同, days: {}", days);
        List<ParkingRentalContract> contracts = contractService.getExpiringContracts(days);
        return AjaxResult.success(contracts);
    }

    /**
     * 查询当前用户的车位租赁合同（我的车位）
     */
    @GetMapping("/my")
    public AjaxResult getMyContracts() {
        Long currentUserId = SecurityUtils.getUserId();
        log.info("查询当前用户的车位租赁合同, userId: {}", currentUserId);

        ParkingRentalContract contract = new ParkingRentalContract();
        contract.setOwnerId(currentUserId);

        List<ParkingRentalContract> contracts = contractService.selectContractList(contract);
        return AjaxResult.success(contracts);
    }
}

package com.hyu.property.task;

import com.hyu.property.domain.ParkingRentalContract;
import com.hyu.property.service.IParkingRentalContractService;
import com.hyu.property.service.INotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 车位租赁定时任务
 *
 * @author system
 * @date 2025-01-31
 */
@Slf4j
@Component
public class ParkingRentTask {

    @Autowired
    private IParkingRentalContractService contractService;

    @Autowired(required = false)
    private INotificationService notificationService;

    /**
     * 每天早上9点检查即将到期的租赁合同（提前7天提醒）
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkExpiringContracts() {
        try {
            log.info("开始执行车位到期检查任务");

            // 查询7天内到期的合同
            List<ParkingRentalContract> expiringContracts = contractService.getExpiringContracts(7);

            if (expiringContracts.isEmpty()) {
                log.info("没有即将到期的车位租赁合同");
                return;
            }

            log.info("发现{}个即将到期的车位租赁合同", expiringContracts.size());

            // 发送到期提醒通知
            for (ParkingRentalContract contract : expiringContracts) {
                try {
                    if (notificationService != null) {
                        // TODO: 实现通知发送逻辑
                        log.info("发送到期提醒：业主{}, 车位{}, 合同到期日期：{}",
                                contract.getOwnerName(),
                                contract.getSpaceNo(),
                                contract.getEndDate());
                    }
                } catch (Exception e) {
                    log.error("发送到期提醒失败，合同ID：{}", contract.getId(), e);
                }
            }

            log.info("车位到期检查任务执行完成");
        } catch (Exception e) {
            log.error("车位到期检查任务执行失败", e);
        }
    }

    /**
     * 每天凌晨0点检查已到期的合同并自动释放车位
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void releaseExpiredParkingSpaces() {
        try {
            log.info("开始执行过期合同处理任务");

            // 更新过期合同状态并释放车位
            int count = contractService.updateExpiredContracts();

            if (count > 0) {
                log.info("已处理{}个过期合同，车位状态已更新为空闲", count);
            } else {
                log.info("没有需要处理的过期合同");
            }

            log.info("过期合同处理任务执行完成");
        } catch (Exception e) {
            log.error("过期合同处理任务执行失败", e);
        }
    }
}

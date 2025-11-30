package com.hyu.property.task;

import com.hyu.property.domain.Bill;
import com.hyu.property.domain.FeeType;
import com.hyu.property.domain.House;
import com.hyu.property.domain.UserHouse;
import com.hyu.property.service.IBillService;
import com.hyu.property.service.IFeeTypeService;
import com.hyu.property.service.IHouseService;
import com.hyu.property.service.IUserHouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 账单生成定时任务
 * 每天00:00执行，为当前居住的业主/租户生成月度账单
 *
 * @author hyu
 */
@Slf4j
@Component
public class BillGenerationTask {

    @Autowired
    private IUserHouseService userHouseService;

    @Autowired
    private IFeeTypeService feeTypeService;

    @Autowired
    private IHouseService houseService;

    @Autowired
    private IBillService billService;

    /**
     * 每30秒执行一次账单生成任务（测试用）
     * 为当月新生效的住户生成账单
     */
    @Scheduled(fixedRate = 3000000)
    public void generateMonthlyBills() {
        log.info("===== 开始执行账单生成任务（每30秒执行），执行时间：{} =====", java.time.LocalDateTime.now());

        try {
            // 获取当前日期
            LocalDate currentDate = LocalDate.now();
            YearMonth currentMonth = YearMonth.from(currentDate);
            String billingPeriod = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));

            log.info("正在生成 {} 计费周期的账单", billingPeriod);

            // 获取所有当前居住的用户房产关联记录
            List<UserHouse> currentResidents = userHouseService.getCurrentResidents();
            log.info("找到 {} 个当前居住的住户", currentResidents.size());

            int totalBillsGenerated = 0;
            int successCount = 0;
            int errorCount = 0;

            for (UserHouse userHouse : currentResidents) {
                try {
                    // 检查入住时间是否在当前计费周期之前
                    if (shouldGenerateBillForUser(userHouse, billingPeriod, currentDate)) {
                        // 为每个住户生成账单
                        int billsGenerated = generateBillsForResident(userHouse, billingPeriod, currentDate);
                        totalBillsGenerated += billsGenerated;
                        successCount++;

                        log.debug("为住户ID {} 生成了 {} 个账单", userHouse.getUserId(), billsGenerated);
                    } else {
                        log.debug("住户ID {} 入住时间晚于计费周期 {}，跳过生成账单",
                                userHouse.getUserId(), billingPeriod);
                        continue;
                    }

                } catch (Exception e) {
                    errorCount++;
                    log.error("为住户ID {} 生成账单时发生错误：{}", userHouse.getUserId(), e.getMessage(), e);
                }
            }

            log.info("===== 账单生成任务完成 ===== 总住户数：{}，成功：{}，失败：{}，总生成账单数：{} =====",
                    currentResidents.size(), successCount, errorCount, totalBillsGenerated);

        } catch (Exception e) {
            log.error("账单生成任务执行失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 为单个住户生成账单
     */
    private int generateBillsForResident(UserHouse userHouse, String billingPeriod, LocalDate currentDate) {
        int billsGenerated = 0;

        // 获取房产信息
        House house = houseService.selectHouseById(userHouse.getHouseId());
        if (house == null) {
            log.warn("房产ID {} 不存在，跳过账单生成", userHouse.getHouseId());
            return 0;
        }

        // 获取所有有效的费用类型（排除维修费和停车费）
        List<FeeType> feeTypes = feeTypeService.getActiveFeeTypesForBillGeneration();
        log.debug("找到 {} 个有效费用类型", feeTypes.size());

        for (FeeType feeType : feeTypes) {
            try {
                // 检查是否已经生成过该费用类型的账单
                if (billService.existsBill(userHouse.getUserId(), userHouse.getHouseId(),
                        feeType.getId(), billingPeriod)) {
                    log.debug("住户ID {} 房产ID {} 费用类型 {} 在计费周期 {} 的账单已存在，跳过生成",
                            userHouse.getUserId(), userHouse.getHouseId(), feeType.getTypeCode(), billingPeriod);
                    continue;
                }

                // 计算完整月费用
                BigDecimal fullAmount = calculateBillAmount(house, feeType);
                if (fullAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.debug("费用类型 {} 计算金额为0，跳过生成账单", feeType.getTypeCode());
                    continue;
                }

                // 根据入住时间按比例计算实际费用
                LocalDate checkInDate = userHouse.getStartDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
                BigDecimal actualAmount = calculateProratedAmount(fullAmount, checkInDate, billingPeriod);
                if (actualAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    log.debug("费用类型 {} 按比例计算金额为0，跳过生成账单", feeType.getTypeCode());
                    continue;
                }

                // 创建账单
                Bill bill = createBill(userHouse, house, feeType, actualAmount, billingPeriod, currentDate);
                billService.insertBill(bill);

                billsGenerated++;
                log.debug("成功生成账单：住户ID {}，房产ID {}，费用类型 {}，完整费用{}，实际费用{}",
                        userHouse.getUserId(), userHouse.getHouseId(), feeType.getTypeName(), fullAmount, actualAmount);

            } catch (Exception e) {
                log.error("生成账单时发生错误：住户ID {}，房产ID {}，费用类型 {}，错误：{}",
                        userHouse.getUserId(), userHouse.getHouseId(), feeType.getTypeCode(), e.getMessage(), e);
            }
        }

        return billsGenerated;
    }

    /**
     * 计算账单金额
     */
    private BigDecimal calculateBillAmount(House house, FeeType feeType) {
        BigDecimal unitPrice = feeType.getUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        String billingUnit = feeType.getBillingUnit();
        BigDecimal area;

        // 根据计费单位确定计算基数
        if (billingUnit.contains("平方米")) {
            // 按面积计费，优先使用使用面积，如果为空则使用建筑面积
            if (house.getUsableArea() != null && house.getUsableArea().compareTo(BigDecimal.ZERO) > 0) {
                area = house.getUsableArea();
                log.debug("费用类型 {} 按使用面积计费，面积：{} ㎡，单价：{}",
                        feeType.getTypeCode(), area, unitPrice);
            } else {
                area = house.getBuildingArea();
                log.debug("费用类型 {} 按建筑面积计费，面积：{} ㎡，单价：{}",
                        feeType.getTypeCode(), area, unitPrice);
            }
        } else if (billingUnit.equals("元/月")) {
            // 固定月费
            area = BigDecimal.ONE;
            log.debug("费用类型 {} 按固定月费计费，金额：{}", feeType.getTypeCode(), unitPrice);
        } else {
            // 其他计费方式默认按固定费用
            area = BigDecimal.ONE;
            log.debug("费用类型 {} 按固定费用计费，金额：{}", feeType.getTypeCode(), unitPrice);
        }

        // 计算总金额：单价 × 面积或数量
        BigDecimal totalAmount = unitPrice.multiply(area).setScale(2, RoundingMode.HALF_UP);

        log.debug("账单金额计算：{} × {} = {}", unitPrice, area, totalAmount);

        return totalAmount;
    }

    /**
     * 创建账单对象
     */
    private Bill createBill(UserHouse userHouse, House house, FeeType feeType,
                           BigDecimal amount, String billingPeriod, LocalDate currentDate) {
        Bill bill = new Bill();

        // 生成账单号：BILL + 用户ID + 房产ID + 费用类型代码 + 计费周期 + 序列号
        String billNo = generateBillNo(userHouse.getUserId(), userHouse.getHouseId(),
                feeType.getTypeCode(), billingPeriod);

        bill.setBillNo(billNo);
        bill.setUserId(userHouse.getUserId());
        bill.setHouseId(userHouse.getHouseId());
        bill.setFeeTypeId(feeType.getId());
        bill.setFeeTypeName(feeType.getTypeName()); // 设置费用名称
        bill.setBillPeriod(billingPeriod);
                bill.setAmount(amount);
        bill.setPaidAmount(BigDecimal.ZERO); // 已缴金额初始化为0
        bill.setDiscountAmount(BigDecimal.ZERO); // 折扣金额初始化为0
        bill.setBillStatus(1); // 1-待缴费
        bill.setDeleted(0); // 未删除
        // 转换LocalDate为Date
        bill.setDueDate(java.util.Date.from(currentDate.plusMonths(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant()));
        bill.setRemark("系统自动生成：" + feeType.getDescription());
        bill.setCreateBy("system");

        return bill;
    }

    /**
     * 检查是否应该为用户生成账单
     * 只要用户在计费周期内入住就应该生成账单（按比例计算）
     *
     * @param userHouse 用户房产关联
     * @param billingPeriod 计费周期（yyyy-MM格式）
     * @param currentDate 当前日期
     * @return 是否应该生成账单
     */
    private boolean shouldGenerateBillForUser(UserHouse userHouse, String billingPeriod, LocalDate currentDate) {
        try {
            // 解析计费周期
            YearMonth billYearMonth = YearMonth.parse(billingPeriod);
            LocalDate firstDayOfBillingPeriod = billYearMonth.atDay(1);
            LocalDate lastDayOfBillingPeriod = billYearMonth.atEndOfMonth();

            // 获取用户的入住时间
            LocalDate checkInDate = userHouse.getStartDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

            // 如果用户在计费周期结束前入住，就应该生成账单（按比例计算）
            return checkInDate != null && !checkInDate.isAfter(lastDayOfBillingPeriod);

        } catch (Exception e) {
            log.error("检查用户ID {} 的入住时间时发生错误：{}", userHouse.getUserId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 计算按入住时间比例的账单金额
     *
     * @param fullAmount 完整月费用
     * @param checkInDate 入住日期
     * @param billingPeriod 计费周期（yyyy-MM格式）
     * @return 按比例计算的费用金额
     */
    private BigDecimal calculateProratedAmount(BigDecimal fullAmount, LocalDate checkInDate, String billingPeriod) {
        try {
            // 解析计费周期
            YearMonth billYearMonth = YearMonth.parse(billingPeriod);
            LocalDate firstDayOfMonth = billYearMonth.atDay(1);
            LocalDate lastDayOfMonth = billYearMonth.atEndOfMonth();

            // 计算该月的总天数
            int totalDaysInMonth = lastDayOfMonth.getDayOfMonth();

            // 计算实际居住天数（从入住日期或该月第一天开始）
            LocalDate startDate = checkInDate.isBefore(firstDayOfMonth) ? firstDayOfMonth : checkInDate;

            // 计算居住天数（包含入住当天）
            int actualDays = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, lastDayOfMonth) + 1;

            // 确保不超过该月的总天数
            actualDays = Math.min(actualDays, totalDaysInMonth);

            // 按比例计算费用
            BigDecimal proratedAmount = fullAmount.multiply(new BigDecimal(actualDays))
                    .divide(new BigDecimal(totalDaysInMonth), 2, RoundingMode.HALF_UP);

            log.debug("按比例计算费用：完整费用{}，总天数{}天，实际居住{}天，按比例费用={}",
                    fullAmount, totalDaysInMonth, actualDays, proratedAmount);

            return proratedAmount;

        } catch (Exception e) {
            log.error("按比例计算费用时发生错误：{}", e.getMessage(), e);
            return fullAmount; // 出错时返回完整费用
        }
    }

    /**
     * 生成账单号
     */
    private String generateBillNo(Long userId, Long houseId, String feeTypeCode, String billingPeriod) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        // 取时间戳后6位作为序列号
        String sequence = timestamp.substring(timestamp.length() - 6);

        return String.format("BILL%s%s%s%s%s",
                userId, houseId, feeTypeCode.toUpperCase(),
                billingPeriod.replace("-", ""), sequence);
    }
}
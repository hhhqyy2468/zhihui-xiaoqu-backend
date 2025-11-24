package com.hyu.common.utils.excel;

import com.hyu.property.domain.Bill;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 账单Excel导出工具类
 *
 * @author hyu
 */
@Slf4j
public class BillExcelUtil {

    private static final String[] HEADERS = {
        "账单编号", "业主ID", "业主姓名", "房产编号", "费用类型", "账期",
        "计费周期", "账单金额", "已缴金额", "折扣金额", "账单状态",
        "缴费截止日期", "缴费时间", "支付方式", "备注"
    };

    private static final String[] FIELDS = {
        "billNo", "userId", "ownerName", "houseCode", "feeTypeName", "billPeriod",
        "billingCycle", "amount", "paidAmount", "discountAmount", "billStatus",
        "dueDate", "paidTime", "payMethod", "remark"
    };

    /**
     * 导出账单列表到Excel
     *
     * @param billList 账单列表
     * @param response HTTP响应
     * @param fileName 文件名
     */
    public static void exportBillsToExcel(List<Bill> billList, HttpServletResponse response, String fileName) {
        try (Workbook workbook = new XSSFWorkbook();
             OutputStream outputStream = response.getOutputStream()) {

            // 创建工作表
            Sheet sheet = workbook.createSheet("账单明细");

            // 设置列宽
            setColumnWidth(sheet);

            // 创建标题行
            createTitleRow(workbook, sheet, fileName);

            // 创建表头
            createHeaderRow(workbook, sheet);

            // 创建数据行
            createDataRows(workbook, sheet, billList);

            // 创建统计行
            createSummaryRow(workbook, sheet, billList);

            // 设置响应头
            setResponseHeader(response, fileName);

            // 写入输出流
            workbook.write(outputStream);
            outputStream.flush();

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            throw new RuntimeException("导出Excel失败", e);
        }
    }

    /**
     * 设置列宽
     */
    private static void setColumnWidth(Sheet sheet) {
        int[] columnWidths = {15, 10, 12, 15, 15, 12, 10, 12, 12, 12, 10, 12, 15, 12, 20};
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i] * 256);
        }
    }

    /**
     * 创建标题行
     */
    private static void createTitleRow(Workbook workbook, Sheet sheet, String fileName) {
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);

        // 设置标题样式
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        titleCell.setCellValue(fileName);
        titleCell.setCellStyle(titleStyle);

        // 合并标题单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

        // 创建空行作为间隔
        sheet.createRow(1);
    }

    /**
     * 创建表头行
     */
    private static void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(2);

        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 创建数据行
     */
    private static void createDataRows(Workbook workbook, Sheet sheet, List<Bill> billList) {
        // 设置数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 设置金额样式（右对齐）
        CellStyle amountStyle = workbook.createCellStyle();
        amountStyle.cloneStyleFrom(dataStyle);
        amountStyle.setAlignment(HorizontalAlignment.RIGHT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (int i = 0; i < billList.size(); i++) {
            Row dataRow = sheet.createRow(i + 3);
            Bill bill = billList.get(i);

            for (int j = 0; j < FIELDS.length; j++) {
                Cell cell = dataRow.createCell(j);
                Object value = getFieldValue(bill, FIELDS[j]);

                // 设置单元格样式
                if (FIELDS[j].contains("Amount")) {
                    cell.setCellStyle(amountStyle);
                } else {
                    cell.setCellStyle(dataStyle);
                }

                // 设置单元格值
                setCellValue(cell, value, dateFormat, dateTimeFormat);
            }
        }
    }

    /**
     * 获取字段值
     */
    private static Object getFieldValue(Bill bill, String fieldName) {
        try {
            switch (fieldName) {
                case "billNo": return bill.getBillNo();
                case "userId": return bill.getUserId();
                case "ownerName": return bill.getOwnerName();
                case "houseCode": return bill.getHouseCode();
                case "feeTypeName": return bill.getFeeTypeName();
                case "billPeriod": return bill.getBillPeriod();
                case "amount": return bill.getAmount();
                case "paidAmount": return bill.getPaidAmount();
                case "discountAmount": return bill.getDiscountAmount();
                case "billStatus": return getBillStatusText(bill.getBillStatus());
                case "dueDate": return bill.getDueDate();
                case "paidTime": return bill.getPaidTime();
                case "payMethod": return getPayMethodText(bill.getPayMethod());
                case "remark": return bill.getRemark();
                default: return "";
            }
        } catch (Exception e) {
            log.warn("获取字段值失败: {}", fieldName, e);
            return "";
        }
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value, SimpleDateFormat dateFormat, SimpleDateFormat dateTimeFormat) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else if (value instanceof Date) {
            Date date = (Date) value;
            // 如果时间部分为0，只显示日期
            if (date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0) {
                cell.setCellValue(dateFormat.format(date));
            } else {
                cell.setCellValue(dateTimeFormat.format(date));
            }
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 获取账单状态文本
     */
    private static String getBillStatusText(Integer billStatus) {
        if (billStatus == null) return "";
        switch (billStatus) {
            case 1: return "待缴费";
            case 2: return "已缴费";
            case 3: return "已超期";
            default: return "未知";
        }
    }

    /**
     * 获取支付方式文本
     */
    private static String getPayMethodText(Integer payMethod) {
        if (payMethod == null) return "";
        switch (payMethod) {
            case 1: return "现金";
            case 2: return "银行转账";
            case 3: return "在线支付";
            case 4: return "钱包支付";
            default: return "其他";
        }
    }

    /**
     * 创建统计行
     */
    private static void createSummaryRow(Workbook workbook, Sheet sheet, List<Bill> billList) {
        int summaryRowIndex = billList.size() + 4;
        Row summaryRow = sheet.createRow(summaryRowIndex);

        // 设置统计行样式
        CellStyle summaryStyle = workbook.createCellStyle();
        Font summaryFont = workbook.createFont();
        summaryFont.setBold(true);
        summaryStyle.setFont(summaryFont);
        summaryStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        summaryStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        summaryStyle.setBorderTop(BorderStyle.THIN);
        summaryStyle.setBorderBottom(BorderStyle.THIN);
        summaryStyle.setBorderLeft(BorderStyle.THIN);
        summaryStyle.setBorderRight(BorderStyle.THIN);

        // 计算统计数据
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;
        int pendingCount = 0;
        int paidCount = 0;
        int overdueCount = 0;

        for (Bill bill : billList) {
            if (bill.getAmount() != null) {
                totalAmount = totalAmount.add(bill.getAmount());
            }
            if (bill.getPaidAmount() != null) {
                totalPaidAmount = totalPaidAmount.add(bill.getPaidAmount());
            }
            if (bill.getBillStatus() != null) {
                switch (bill.getBillStatus()) {
                    case 1: pendingCount++; break;
                    case 2: paidCount++; break;
                    case 3: overdueCount++; break;
                }
            }
        }

        // 填充统计信息
        Cell labelCell = summaryRow.createCell(0);
        labelCell.setCellValue("统计汇总");
        labelCell.setCellStyle(summaryStyle);

        Cell totalAmountCell = summaryRow.createCell(7);
        totalAmountCell.setCellValue(totalAmount.doubleValue());
        totalAmountCell.setCellStyle(summaryStyle);

        Cell totalPaidCell = summaryRow.createCell(8);
        totalPaidCell.setCellValue(totalPaidAmount.doubleValue());
        totalPaidCell.setCellStyle(summaryStyle);

        Cell unpaidAmountCell = summaryRow.createCell(9);
        unpaidAmountCell.setCellValue(totalAmount.subtract(totalPaidAmount).doubleValue());
        unpaidAmountCell.setCellStyle(summaryStyle);

        Cell statusCell = summaryRow.createCell(10);
        statusCell.setCellValue(String.format("待缴费:%d, 已缴费:%d, 已超期:%d", pendingCount, paidCount, overdueCount));
        statusCell.setCellStyle(summaryStyle);

        // 合并统计行
        sheet.addMergedRegion(new CellRangeAddress(summaryRowIndex, summaryRowIndex, 0, 6));
    }

    /**
     * 设置响应头
     */
    private static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            String encodedFileName = URLEncoder.encode(fileName + ".xlsx", "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);
        } catch (Exception e) {
            log.error("设置响应头失败", e);
        }
    }
}
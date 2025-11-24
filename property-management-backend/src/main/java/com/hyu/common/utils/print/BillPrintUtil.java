package com.hyu.common.utils.print;

import com.hyu.property.domain.Bill;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 账单打印工具类
 *
 * @author hyu
 */
@Slf4j
public class BillPrintUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 生成批量打印账单的PDF字节数组
     *
     * @param billList 账单列表
     * @param params 打印参数
     * @return PDF字节数组
     */
    public static byte[] generateBatchPrintPdf(List<Bill> billList, Map<String, Object> params) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 检查账单数量限制
            if (billList.size() > 50) {
                throw new IllegalArgumentException("批量打印最多支持50个账单");
            }

            // 为每个账单创建单独的工作表
            for (int i = 0; i < billList.size(); i++) {
                String sheetName = "账单" + (i + 1);
                if (sheetName.length() > 31) {
                    sheetName = sheetName.substring(0, 31);
                }
                Sheet sheet = workbook.createSheet(sheetName);

                createPrintSheet(workbook, sheet, billList.get(i), i + 1, params);
            }

            // 写入输出流
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("生成打印PDF失败", e);
            throw new RuntimeException("生成打印PDF失败", e);
        }
    }

    /**
     * 创建单个账单的打印工作表
     */
    private static void createPrintSheet(Workbook workbook, Sheet sheet, Bill bill, int billIndex, Map<String, Object> params) {
        // 设置页面设置
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setPaperSize(PrintSetup.A4_PAPERSIZE);
        printSetup.setFitHeight((short) 0);
        printSetup.setFitWidth((short) 1);

        // 设置打印边距 (使用英寸为单位)
        sheet.setMargin(Sheet.TopMargin, 0.75); // 0.75英寸
        sheet.setMargin(Sheet.BottomMargin, 0.75); // 0.75英寸
        sheet.setMargin(Sheet.LeftMargin, 0.5); // 0.5英寸
        sheet.setMargin(Sheet.RightMargin, 0.5); // 0.5英寸

        // 创建打印内容
        createPrintContent(workbook, sheet, bill, billIndex, params);
    }

    /**
     * 创建打印内容
     */
    private static void createPrintContent(Workbook workbook, Sheet sheet, Bill bill, int billIndex, Map<String, Object> params) {
        int currentRow = 0;

        // 1. 创建表头
        currentRow = createPrintHeader(workbook, sheet, billIndex, params);
        currentRow += 2; // 留空行

        // 2. 创建账单基本信息
        currentRow = createBillInfo(workbook, sheet, bill, currentRow);
        currentRow += 2; // 留行

        // 3. 创建费用明细
        currentRow = createFeeDetails(workbook, sheet, bill, currentRow);
        currentRow += 2; // 留行

        // 4. 创建缴费信息
        currentRow = createPaymentInfo(workbook, sheet, bill, currentRow);
        currentRow += 2; // 留行

        // 5. 创建底部信息
        currentRow = createFooterInfo(workbook, sheet, params, currentRow);

        // 设置打印区域（只打印到有内容的区域）
        if (currentRow > 0) {
            sheet.setAutobreaks(true);
        }
    }

    /**
     * 创建打印表头
     */
    private static int createPrintHeader(Workbook workbook, Sheet sheet, int billIndex, Map<String, Object> params) {
        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 18);
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 创建表头行
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);

        // 设置表头内容
        String companyName = params != null && params.containsKey("companyName")
            ? params.get("companyName").toString() : "物业公司";
        String headerText = companyName + " - 费用账单";

        headerCell.setCellValue(headerText);
        headerCell.setCellStyle(headerStyle);

        // 合并表头单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // 设置行高
        headerRow.setHeightInPoints(30);

        return 0;
    }

    /**
     * 创建账单基本信息
     */
    private static int createBillInfo(Workbook workbook, Sheet sheet, Bill bill, int startRow) {
        CellStyle labelStyle = createLabelStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        // 基本信息
        String[][] billInfo = {
            {"账单编号：", "业主姓名：", "房产地址：", "账期："},
            {bill.getBillNo(), bill.getOwnerName(), bill.getHouseCode(), bill.getBillPeriod()}
        };

        for (int i = 0; i < billInfo.length; i++) {
            Row row = sheet.createRow(startRow + i);

            // 标签
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(billInfo[i][0]);
            labelCell.setCellStyle(labelStyle);

            // 值
            Cell valueCell = row.createCell(2);
            valueCell.setCellValue(billInfo[i][1]);
            valueCell.setCellStyle(valueStyle);

            // 合并单元格（跨3列）
            if (i < billInfo.length - 1) {
                sheet.addMergedRegion(new CellRangeAddress(startRow + i, startRow + i, 2, 4));
            }

            // 设置行高
            row.setHeightInPoints(25);
        }

        return startRow + billInfo.length;
    }

    /**
     * 创建费用明细
     */
    private static int createFeeDetails(Workbook workbook, Sheet sheet, Bill bill, int startRow) {
        // 设置标题样式
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle amountStyle = createAmountStyle(workbook);

        // 费用明细标题
        Row titleRow = sheet.createRow(startRow);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("费用明细");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 6));

        // 费用明细内容
        Row dataRow = sheet.createRow(startRow + 1);

        String[] labels = {"费用类型", "计费周期", "账单金额", "已缴金额", "未缴金额"};
        Object[] values = {
            bill.getFeeTypeName(),
            bill.getBillPeriod(),
            bill.getAmount(),
            bill.getPaidAmount(),
            bill.getAmount().subtract(bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO)
        };

        for (int i = 0; i < labels.length; i++) {
            Cell labelCell = dataRow.createCell(i);
            labelCell.setCellValue(labels[i]);
            labelCell.setCellStyle(titleStyle);

            Cell valueCell = dataRow.createCell(i);
            setCellValue(valueCell, values[i]);
            if (i >= 2 && i <= 4) { // 金额列
                valueCell.setCellStyle(amountStyle);
            } else {
                valueCell.setCellStyle(dataStyle);
            }
        }

        return startRow + 2;
    }

    /**
     * 创建缴费信息
     */
    private static int createPaymentInfo(Workbook workbook, Sheet sheet, Bill bill, int startRow) {
        CellStyle labelStyle = createLabelStyle(workbook);
        CellStyle valueStyle = createValueStyle(workbook);

        // 缴费状态显示
        Row statusRow = sheet.createRow(startRow);
        Cell statusLabel = statusRow.createCell(0);
        statusLabel.setCellValue("缴费状态：");
        statusLabel.setCellStyle(labelStyle);

        Cell statusValue = statusRow.createCell(2);
        String statusText = getBillStatusText(bill.getBillStatus());
        statusValue.setCellValue(statusText);
        statusValue.setCellStyle(valueStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 2, 4));

        // 缴费截止日期
        if (bill.getDueDate() != null) {
            Row dueDateRow = sheet.createRow(startRow + 1);
            Cell dueDateLabel = dueDateRow.createCell(0);
            dueDateLabel.setCellValue("缴费截止日期：");
            dueDateLabel.setCellStyle(labelStyle);

            Cell dueDateValue = dueDateRow.createCell(2);
            dueDateValue.setCellValue(DATE_FORMAT.format(bill.getDueDate()));
            dueDateValue.setCellStyle(valueStyle);
            sheet.addMergedRegion(new CellRangeAddress(startRow + 1, startRow + 1, 2, 4));
        }

        return startRow + 2;
    }

    /**
     * 创建底部信息
     */
    private static int createFooterInfo(Workbook workbook, Sheet sheet, Map<String, Object> params, int startRow) {
        CellStyle footerStyle = createFooterStyle(workbook);

        // 联系方式信息
        Row contactRow = sheet.createRow(startRow);
        String contactInfo = params != null && params.containsKey("contactInfo")
            ? params.get("contactInfo").toString()
            : "联系电话：400-123-4567 | 营业时间：周一至周五 9:00-18:00";

        Cell contactCell = contactRow.createCell(0);
        contactCell.setCellValue(contactInfo);
        contactCell.setCellStyle(footerStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 6));

        // 打印信息
        Row printRow = sheet.createRow(startRow + 1);
        String printInfo = "打印时间：" + DATE_TIME_FORMAT.format(new Date());

        Cell printCell = printRow.createCell(0);
        printCell.setCellValue(printInfo);
        printCell.setCellStyle(footerStyle);
        sheet.addMergedRegion(new CellRangeAddress(startRow + 1, startRow + 1, 0, 6));

        return startRow + 2;
    }

    /**
     * 设置单元格值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof BigDecimal) {
            cell.setCellValue(((BigDecimal) value).doubleValue());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 创建标签样式
     */
    private static CellStyle createLabelStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建值样式
     */
    private static CellStyle createValueStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    /**
     * 创建金额样式
     */
    private static CellStyle createAmountStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建标题样式
     */
    private static CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    /**
     * 创建底部样式
     */
    private static CellStyle createFooterStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
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
}
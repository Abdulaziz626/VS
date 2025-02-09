package com.example.violations.system.service;

import com.example.violations.system.entity.Violation;
import com.example.violations.system.entity.Violation.ViolationStatus;
import com.example.violations.system.repository.ViolationRepository;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ViolationXlsxService {
    private final ViolationRepository violationRepository;

    private static final String[] HEADERS = {
            "ID", "Description", "Location", "Plate Number", "Violation Type",
            "Inspector Name", "Region", "Status", "Date"
    };

    private static final String[] REGIONS = {"Middle", "Western", "Eastern", "Southern", "Northern"};
    private static final String[] VIOLATION_TYPES = {"SPEEDING", "USING_PHONE", "RED_LIGHT_CROSSING", "WRONG_PARKING"};

    public void exportViolationsToExcel(HttpServletResponse response) throws IOException {
        List<Violation> violations = violationRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Violations");
        createHeaders(sheet, workbook);

        CellStyle dateStyle = createDateCellStyle(workbook);

        int rowIndex = 1;
        for (Violation violation : violations) {
            createViolationRow(sheet, violation, rowIndex++, dateStyle);
        }

        // Add statistics section
        addStatisticsSection(sheet, violations, rowIndex);

        autoSizeColumns(sheet);
        writeToResponse(workbook, response);
    }

    private void addStatisticsSection(Sheet sheet, List<Violation> violations, int startRow) {
        int rowIndex = startRow + 2;


        Row titleRow = sheet.createRow(rowIndex++);
        titleRow.createCell(0).setCellValue("Statistics by Violation Types:");

        for (String violationType : VIOLATION_TYPES) {
            long count = violations.stream()
                    .filter(v -> violationType.equalsIgnoreCase(
                            v.getViolationType() != null ? v.getViolationType().name() : "UNKNOWN"))
                    .count();
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(violationType);
            row.createCell(1).setCellValue(count);
        }

        rowIndex++;

        // Add Region stats
        Row regionTitleRow = sheet.createRow(rowIndex++);
        regionTitleRow.createCell(0).setCellValue("Statistics by Region:");

        for (String region : REGIONS) {
            long count = violations.stream()
                    .filter(v -> region.equalsIgnoreCase(
                            v.getRegion() != null ? v.getRegion().name() : "UNKNOWN"))
                    .count();
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(region);
            row.createCell(1).setCellValue(count);
        }
    }

    private void createHeaders(Sheet sheet, Workbook workbook) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private CellStyle createDateCellStyle(Workbook workbook) {
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        return dateStyle;
    }

    private void createViolationRow(Sheet sheet, Violation violation, int rowIndex, CellStyle dateStyle) {
        Row row = sheet.createRow(rowIndex);

        row.createCell(0).setCellValue(violation.getId());
        row.createCell(1).setCellValue(violation.getDescription());
        row.createCell(2).setCellValue(violation.getViolationLocation());
        row.createCell(3).setCellValue(violation.getPlateNumber());

        row.createCell(4).setCellValue(
                violation.getViolationType() != null ? violation.getViolationType().name() : "UNKNOWN"
        );
        row.createCell(5).setCellValue(violation.getInspectorName());
        row.createCell(6).setCellValue(
                violation.getRegion() != null ? violation.getRegion().name() : "UNKNOWN"
        );
        row.createCell(7).setCellValue(
                violation.getStatus() != null ? violation.getStatus().name() : "UNKNOWN"
        );

        Cell dateCell = row.createCell(8);
        if (violation.getViolationDate() != null) {
            dateCell.setCellValue(violation.getViolationDate());
            dateCell.setCellStyle(dateStyle);
        } else {
            dateCell.setCellValue("N/A");
        }
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeToResponse(Workbook workbook, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=violations.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
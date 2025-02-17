package com.example.violations.system.service;

import com.example.violations.system.entity.Violation;
import com.example.violations.system.repository.ViolationRepository;
import com.itextpdf.html2pdf.HtmlConverter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViolationPdfService {

    private final ViolationRepository violationRepository;

    private static final String[] REGIONS = {"Middle", "Western", "Eastern", "Southern", "Northern"};
    private static final String[] VIOLATION_TYPES = {"SPEEDING", "USING_PHONE", "RED_LIGHT_CROSSING", "WRONG_PARKING"};

    public void exportViolationsToPdf(HttpServletResponse response) {
        List<Violation> violations = violationRepository.findAll();


        String tableRows = buildTableRows(violations);
        String statisticsTable = buildStatistics(violations);


        String htmlTemplate = getHtmlTemplate(tableRows, statisticsTable);

        try (ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            HtmlConverter.convertToPdf(new ByteArrayInputStream(htmlTemplate.getBytes(StandardCharsets.UTF_8)), pdfOutputStream);


            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=violations_report.pdf");
            response.getOutputStream().write(pdfOutputStream.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }

    private String buildTableRows(List<Violation> violations) {
        return violations.stream()
                .map(violation -> "<tr>" +
                        "<td>" + violation.getId() + "</td>" +
                        "<td>" + violation.getDescription() + "</td>" +
                        "<td>" + violation.getViolationLocation() + "</td>" +
                        "<td>" + violation.getPlateNumber() + "</td>" +
                        "<td>" + (violation.getViolationType() != null ? violation.getViolationType() : "UNKNOWN") + "</td>" +
                        "<td>" + violation.getInspectorName() + "</td>" +
                        "<td>" + (violation.getRegion() != null ? violation.getRegion() : "UNKNOWN") + "</td>" +
                        "<td>" + (violation.getStatus() != null ? violation.getStatus() : "UNKNOWN") + "</td>" +
                        "<td>" + (violation.getViolationDate() != null ? violation.getViolationDate() : "N/A") + "</td>" +
                        "</tr>")
                .collect(Collectors.joining());
    }

    private String buildStatistics(List<Violation> violations) {
        StringBuilder statsBuilder = new StringBuilder();

        statsBuilder.append("<h2>Statistics by Violation Types:</h2>");
        statsBuilder.append("<table style='margin-left: 20px;'>");
        statsBuilder.append("<tr><th>Violation Type</th><th>Count</th></tr>");
        for (String violationType : VIOLATION_TYPES) {
            long count = violations.stream()
                    .filter(v -> violationType.equalsIgnoreCase(
                            v.getViolationType() != null ? v.getViolationType().name() : "UNKNOWN"))
                    .count();
            statsBuilder.append("<tr>")
                    .append("<td>").append(violationType).append("</td>")
                    .append("<td>").append(count).append("</td>")
                    .append("</tr>");
        }
        statsBuilder.append("</table>");


        statsBuilder.append("<h2>Statistics by Region:</h2>");
        statsBuilder.append("<table style='margin-left: 20px;'>");
        statsBuilder.append("<tr><th>Region</th><th>Count</th></tr>");
        for (String region : REGIONS) {
            long count = violations.stream()
                    .filter(v -> region.equalsIgnoreCase(
                            v.getRegion() != null ? v.getRegion().name() : "UNKNOWN"))
                    .count();
            statsBuilder.append("<tr>")
                    .append("<td>").append(region).append("</td>")
                    .append("<td>").append(count).append("</td>")
                    .append("</tr>");
        }
        statsBuilder.append("</table>");

        return statsBuilder.toString();
    }

    private String getHtmlTemplate(String tableRows, String statisticsTable) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Violations Report</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 20px;
                        }
                        table {
                            width: 90%;
                            border-collapse: collapse;
                            margin-left: auto;
                            margin-right: auto;
                            font-size: 12px;
                        }
                        th, td {
                            border: 1px solid #ddd;
                            padding: 8px;
                            text-align: left;
                        }
                        th {
                            background-color: #f4f4f4;
                        }
                        h1, h2 {
                            text-align: center;
                            color: #333;
                        }
                        h2 {
                            margin-top: 30px;
                        }
                    </style>
                </head>
                <body>
                <h1>Violations Report</h1>
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Description</th>
                        <th>Location</th>
                        <th>Plate Number</th>
                        <th>Violation Type</th>
                        <th>Inspector Name</th>
                        <th>Region</th>
                        <th>Status</th>
                        <th>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    {{tableRows}}
                    </tbody>
                </table>
                {{statisticsTable}}
                </body>
                </html>
                """
                .replace("{{tableRows}}", tableRows)
                .replace("{{statisticsTable}}", statisticsTable);
    }
}
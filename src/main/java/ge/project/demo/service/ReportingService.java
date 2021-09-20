package ge.project.demo.service;

import ge.project.demo.dto.ReportDto;
import ge.project.demo.dto.product.ProductReportDto;
import ge.project.demo.repository.ProductActivityRepository;
import ge.project.demo.repository.UserActivityRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Log4j2
@Service
@EnableScheduling
public class ReportingService {

  private UserActivityRepository userActivityRepository;
  private ProductActivityRepository productActivityRepository;
  private EmailService emailService;

  private String[] reportHeaders = new String[] { "purchasedProductsCount", "purchasedProductsTotalPrice", "commissionPrice", "newProducts",
      "uniqueUsers", "userActivities" };
  private String[] productHeaders = new String[] { "productName", "purchaserEmail", "purchaserPersonalNumber" };

  @Autowired
  public ReportingService(UserActivityRepository userActivityRepository, ProductActivityRepository productActivityRepository,
      EmailService emailService) {
    this.userActivityRepository = userActivityRepository;
    this.productActivityRepository = productActivityRepository;
    this.emailService = emailService;
  }

  @Transactional(readOnly = true)
  @Scheduled(cron = "45 59 23 * * *")
  public void sendReport() throws IOException {
    LocalDateTime localDateTime = LocalDateTime.now();
    LocalDateTime startDate = localDateTime.with(LocalTime.MIDNIGHT);

    ReportDto reportDto = productActivityRepository.getProductReport(startDate, localDateTime);
    long uniqueUsers = userActivityRepository.countLogedInUsers(startDate, localDateTime);
    long userActivities = userActivityRepository.countActivities(startDate, localDateTime);
    reportDto.setUniqueUsers(uniqueUsers);
    reportDto.setUserActivities(userActivities);
    List<ProductReportDto> productReport = productActivityRepository.getProductsReport(startDate, localDateTime);
    reportDto.setProductReport(productReport);
    generateExcepFile(reportDto);

    log.info("Generating report");

  }

  private void generateExcepFile(ReportDto reportDto) throws IOException {
    try (Workbook workbook = new XSSFWorkbook()) {
      File excelFile = new File("." + "/files/" + "report" + System.currentTimeMillis() + ".xlsx");
      CellStyle style = workbook.createCellStyle();
      XSSFFont font = ((XSSFWorkbook) workbook).createFont();
      font.setFontName("Arial");
      font.setBold(true);
      style.setFont(font);
      Sheet sheet = workbook.createSheet("statistic");
      Row row = sheet.createRow(0);
      for (int i = 0; i < this.reportHeaders.length; i++) {
        Cell cell = row.createCell(i);
        cell.setCellValue(this.reportHeaders[i]);
        cell.setCellStyle(style);
      }
      int cellIndex = 0;

      row = sheet.createRow(1);
      createLongCell(row, cellIndex++, reportDto.getPurchasedProductsCount());
      createCell(row, cellIndex++, reportDto.getPurchasedProductsTotalPrice());
      createCell(row, cellIndex++, reportDto.getCommissionPrice());
      createLongCell(row, cellIndex++, reportDto.getNewProducts());
      createLongCell(row, cellIndex++, reportDto.getUniqueUsers());
      createLongCell(row, cellIndex++, reportDto.getUserActivities());

      row = sheet.createRow(4);

      for (int i = 0; i < this.productHeaders.length; i++) {
        Cell cell = row.createCell(i);
        cell.setCellValue(this.productHeaders[i]);
        cell.setCellStyle(style);
      }

      int rowCount = 5;
      for (ProductReportDto productReportDto : reportDto.getProductReport()) {
        cellIndex = 0;
        row = sheet.createRow(rowCount++);
        createStringCell(row, cellIndex++, productReportDto.getProductName());
        createStringCell(row, cellIndex++, productReportDto.getPurchaserEmail());
        createStringCell(row, cellIndex++, productReportDto.getPurchaserPersonalNumber());
      }
      FileOutputStream outputStream = new FileOutputStream(excelFile);
      workbook.write(outputStream);
      emailService.sendEmailAttachment("Report", "there is daily report", excelFile);
      Files.deleteIfExists(excelFile.toPath());
    }
  }

  private void createStringCell(Row row, int cellIndex, String value) {
    Cell cell = row.createCell(cellIndex++);
    if (value == null) {
      cell.setCellType(CellType.BLANK);
    } else {
      cell.setCellType(CellType.STRING);
      cell.setCellValue(value);
    }
  }

  private void createLongCell(Row row, int cellIndex, Long value) {
    Cell cell = row.createCell(cellIndex++);
    if (value == null) {
      cell.setCellType(CellType.BLANK);
    } else {
      cell.setCellType(CellType.NUMERIC);
      cell.setCellValue(value);
    }
  }

  private void createCell(Row row, int cellIndex, Double value) {
    Cell cell = row.createCell(cellIndex++);
    if (value == null) {
      cell.setCellType(CellType.BLANK);
    } else {
      cell.setCellType(CellType.NUMERIC);
      cell.setCellValue(value);
    }
  }
}


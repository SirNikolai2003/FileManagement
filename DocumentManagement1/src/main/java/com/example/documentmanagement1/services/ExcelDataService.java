package com.example.documentmanagement1.services;



import com.example.documentmanagement1.entities.Address;
import com.example.documentmanagement1.repositories.AddressRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@Service
public class ExcelDataService {

    @Autowired
    private AddressRepository addressRepository;

    public void importDataFromExcel(MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0); // Assuming only one sheet

        Iterator<Row> rows = sheet.iterator();
        List<Address> addresses = new ArrayList<>();

        while (rows.hasNext()) {
            Row row = rows.next();

            // Skip header row
            if (row.getRowNum() == 0) {
                continue;
            }

            Address address = new Address();
            address.setName(row.getCell(1).getStringCellValue()); // Assuming name is in the second column (index 1)

            addresses.add(address);
        }

        workbook.close();

        // Save all addresses to database
        addressRepository.saveAll(addresses);
    }

    public void exportDataToExcel() {
        String excelFilePath = "addresses.xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Addresses");

            List<Address> addresses = addressRepository.findAll();

            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Name");

            for (Address address : addresses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(address.getId());
                row.createCell(1).setCellValue(address.getName());
            }

            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
                System.out.println("Excel file has been exported successfully!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

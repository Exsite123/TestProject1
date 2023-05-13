package com.github.exsite123.task;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ShutdownHookThread extends Thread implements Runnable {

    private int position = 0;

    @Override
    public void run() {
        var rs_1 = SQL.executeQuery("SELECT * FROM user_info WHERE (user_id % 2) = 0"); // Вывод всех нечетных user_id's
        var rs_2 = SQL.executeQuery("SELECT * FROM user_info WHERE (user_id % 2) = 1"); // Вывод всех четных user_id's

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();

        var f = CompletableFuture.supplyAsync(() -> writeToExcel(rs_2, sheet));
        var f2 = CompletableFuture.supplyAsync(() -> writeToExcel(rs_1, sheet));
        f.join();
        f2.join();

        try {
            String fileName = Main.cfg.settings.get("xlsx_file").getAsString();
            FileOutputStream out = new FileOutputStream(fileName);
            workbook.write(out);
            out.close();
            System.out.println("Данные были записаны в указанный файл");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SQL.closeConnection();
    }

    public synchronized boolean writeToExcel(ResultSet rs, XSSFSheet sheet) {
        try {
            List<String> titles = new ArrayList<>();
            while(rs.next()) {
                if(titles.isEmpty()) {
                    Row header = sheet.createRow(0);
                    var meta = rs.getMetaData();
                    int columns = meta.getColumnCount();
                    for(int i = 1; i <= columns; i++) {
                        String name = meta.getColumnName(i);

                        titles.add(name);
                        header.createCell(i).setCellValue(name);
                    }
                }
                Row dataRow = sheet.createRow(++position);
                for(var title:titles) {
                    int index = titles.indexOf(title);
                    dataRow.createCell(index+1).setCellValue(rs.getString(title));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }
}

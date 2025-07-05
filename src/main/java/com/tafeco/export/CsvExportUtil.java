package com.tafeco.export;

import com.tafeco.Models.Entity.Order;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.output.ByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CsvExportUtil {

    public static ByteArrayInputStream ordersToCsv(List<Order> orders) {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("ID", "Дата", "Статус", "Email", "Сумма");

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter printer = new CSVPrinter(new PrintWriter(out), format)) {

            for (Order order : orders) {
                printer.printRecord(
                        order.getId(),
                        order.getOrderDate(),
                        order.getStatus().name(),
                        order.getUser().getEmail(),
                        order.getTotalPrice()
                );
            }

            printer.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Ошибка экспорта CSV", e);
        }
    }
}

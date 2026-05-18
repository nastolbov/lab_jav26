package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:/Users/nikita/Desktop/Java_LAb3/JavaStoreProject/store.db";

        int inPacks = 0;
        int outPacks = 0;
        String unit = "";
        double packQty = 0.0;
        double packKg = 0.0;
        double netKg = 0.0;
        boolean hasData = false;

        try (Connection conn = DriverManager.getConnection(url)) {
            String sql =
                "SELECT " +
                "  SUM(CASE WHEN m.operation_type = 'Поступление' THEN m.pack_count ELSE 0 END) AS in_packs, " +
                "  SUM(CASE WHEN m.operation_type = 'Продажа'     THEN m.pack_count ELSE 0 END) AS out_packs, " +
                "  p.unit, " +
                "  p.pack_quantity " +
                "FROM movement m " +
                "JOIN product p ON m.article_id = p.article_id " +
                "JOIN store   s ON m.store_id   = s.store_id " +
                "WHERE p.name = 'Творог 9% жирности' " +
                "  AND s.district = 'Заречный';";

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {

                if (rs.next()) {
                    hasData = true;

                    inPacks  = rs.getInt("in_packs");
                    outPacks = rs.getInt("out_packs");
                    unit     = rs.getString("unit");

                    String packQtyStr = rs.getString("pack_quantity");
                    if (packQtyStr != null) {
                        packQtyStr = packQtyStr.replace(',', '.');
                        packQty = Double.parseDouble(packQtyStr);
                    }

                    int netPacks = inPacks - outPacks;

                    if ("кг".equals(unit)) {
                        packKg = packQty;
                    } else if ("г".equals(unit)) {
                        packKg = packQty / 1000.0;
                    }

                    netKg = netPacks * packKg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (!hasData) {
            System.out.println("Данных по товару 'Творог 9% жирности' в Заречном районе нет.");
        } else {
            System.out.println("Поступило упаковок: " + inPacks);
            System.out.println("Продано упаковок:   " + outPacks);
            System.out.println("Единица измерения:  " + unit);
            System.out.println("Количество в упак.: " + packQty);
            System.out.printf("Увеличение запаса Творог 9%% жирности в Заречном районе (кг): %.1f%n", netKg);
        }
    }
}

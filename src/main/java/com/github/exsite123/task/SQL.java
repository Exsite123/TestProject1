package com.github.exsite123.task;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SQL {

    private static Connection connection;
    private static ExecutorService service;

    public static void login(String host, int port, String user, String pass, String table, int maxThreads) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String format = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8", host, port, table);
        connection = DriverManager.getConnection(format, user, pass);
        service = Executors.newFixedThreadPool(maxThreads);
    }

    public static void closeConnection() {
        try {
            service.shutdown();
            connection.close();
            connection = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void execute(final String query, final Object... values) {
        service.submit(() -> {
            try {
                PreparedStatement e = connection.prepareStatement(query);
                for (int i = 0; i < values.length; ++i) {
                    e.setObject(i + 1, values[i]);
                }
                e.executeUpdate();
                e.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static ResultSet executeQuery(final String query, final Object... values) {
        Future<ResultSet> f = service.submit(() -> {
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < values.length; ++i) {
                ps.setObject(i + 1, values[i]);
            }
            return ps.executeQuery();
        });
        try {
            return f.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

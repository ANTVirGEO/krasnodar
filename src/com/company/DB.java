package com.company;

import java.sql.*;

class DB {


    private static final String mysql_db_url = "jdbc:mysql://localhost/TEST";       // url to connect, in this case is local but you can connect to an ip
    private static final String mysql_db_user = "root";                             // DB user name MYSQL
    private static final String mysql_pass = "vahvah123";                           //DB password
    private static final String table = "test";                                     //DB password
    private static final int pressureValue = 10000;                                 //for case when DB is overloaded, rows to insert by 1 batch
    private static final String query = "select * from " + table;

    private static Connection con;
    private static Statement stmt;
    private static ResultSet rs;

    void openConnection() {
        try {
            // opening database connection to MySQL server
            con = DriverManager.getConnection(mysql_db_url, mysql_db_user, mysql_pass);
            // getting Statement object to execute query
            stmt = con.createStatement();
        } catch (SQLException sqlEx) {
            System.out.println("Some error happened in opening connection");
            sqlEx.printStackTrace();
        }
    }
    void closeConnection() {
        try { rs.close(); } catch(SQLException se) { System.out.println("ResultSet can't be closed" + se); }
        try { stmt.close(); } catch(SQLException se) { System.out.println("Statement can't be closed" + se); }
        try { con.close(); } catch(SQLException se) { System.out.println("Connection can't be closed" + se); }
    }

    void fulFill(long N) {
        try {
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                System.out.println("Some data found in table " + table + ". Trying to TRUNCATE");
                try {
                    stmt.executeUpdate("truncate TEST");
                    System.out.println("All data in table " + table + " has been erased successfully");
                } catch (SQLException eTrunc) {
                    System.out.println("Something gone wrong in Clearing DataBase with TRUNCATE");
                    eTrunc.printStackTrace();
                }
            }
            System.out.println("Starting batch statement execution for " + N + " rows, please wait");
            if (N < 1) {
                System.out.println("You just can't use N < 1. So current N == 1");
                N = 1;
            }
            PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " VALUES (?)");
            for (int i = 1; i <= N; i++) {
                ps.setInt(1, i);
                ps.addBatch();
                if (i % pressureValue == 0) {
                    ps.executeBatch();
                    System.out.println("Inserted " + i + " rows, still not end");
                }
            }
            ps.executeBatch();
            System.out.println("Totally inserted " + N + " rows. Good job!");
        } catch (SQLException e) {
            System.out.println("Error while erasing/fulfilling table in DB");
            e.printStackTrace();
        }
    }


    Field select() {
        Field field = new Field();
        System.out.println("SELECTing from table " + table);
        try {
            rs = stmt.executeQuery(query);
            System.out.println("set Field bean");
            while (rs.next()){
                field.setField(rs.getLong("FIELD"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (field.getField().isEmpty())             //for "somehow' emergency
            field.setField(1L);
        return field;
    }
/*
    private ResultSet select(ResultSet rs) {
        return 'vah';
    }*/

}

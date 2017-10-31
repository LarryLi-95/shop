package com.li930197531.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class jdbc {
    public static void main(String args[])
    {

        String url = "jdbc:mysql://47.95.230.11/heimashop";
        String driver = "com.mysql.jdbc.Driver";
        Connection con;
        try {
            Class.forName(driver);
            try {
                con = DriverManager.getConnection(url, "root", "Lyc951207.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}


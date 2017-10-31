package com.li930197531.utils;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDataSourse {

    public static void main(String[] args) throws SQLException {
        QueryRunner runner=new QueryRunner(DataSourceUtils.getDataSource());
        String sql="select * from user where username =ccc";


     Connection connection= DataSourceUtils.getConnection();
     if(connection!=null){
         System.out.println("连接成功");
     }else {
         System.out.println("连接失败");
     }
    }

}

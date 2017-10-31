package com.li930197531.dao;

import com.li930197531.domain.User;
import com.li930197531.utils.DataSourceUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

public class UserDao {
    public int regist(User user) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "Insert into user values(?,?,?,?,?,?,?,?,?,?)";
int row=        runner.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(),
                user.getEmail(), user.getTelephone(),
                user.getBirthday(), user.getSex(), user.getState(), user.getCode());
        return row;
    }
//激活
    public void active(String activeCode) {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="update user set state=? where code=?";
        try {
            runner.update(sql,1,activeCode);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long checkUsername(String username) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql ="select count(*) from user where username=?";
  Long  query=(Long)  runner.query(sql,new ScalarHandler(),username);
    return query;
    }
}

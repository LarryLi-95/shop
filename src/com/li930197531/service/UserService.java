package com.li930197531.service;

import com.li930197531.dao.UserDao;
import com.li930197531.domain.User;

import java.sql.SQLException;

public class UserService {
    public Boolean regist(User user) {
        UserDao dao = new UserDao();
        int row =0;
        try {
            row = dao.regist(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (row > 0) {
            return true;
        } else {

            return false;
        }
    }
//用户激活
    public void active(String activeCode) {
        UserDao dao=new UserDao();
        dao.active(activeCode);
    }
//校验用户名是否存在
    public boolean checkUsername(String username) {
        UserDao dao=new UserDao();
        Long isExist=0L;
        try {
          isExist=   dao.checkUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isExist>0?true:false;
    }
}

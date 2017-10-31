package com.li930197531.web.servlet;

import com.li930197531.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class CheckUsernameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//获得用户名
        String username=request.getParameter("username");
        System.out.println(username);
        UserService service=new UserService();
        boolean isExist=service.checkUsername(username);
        String json="{\"isExist\":"+isExist+"}";
        response.getWriter().write(json);
    }
}

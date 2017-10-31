package com.li930197531.web.servlet;

import com.li930197531.domain.User;
import com.li930197531.service.UserService;
import com.li930197531.utils.CommonsUtils;
import com.li930197531.utils.MailUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;


import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RegisterServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
doGet(request,response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
//解决乱码
        request.setCharacterEncoding("UTF-8");
        Map<String, String[]> parameterMap = request.getParameterMap();
        User user = new User();
        try {
            //自己指定一个类型转换器（将String转为Date）
            ConvertUtils.register(new Converter() {
                @Override
                public Object convert(Class clazz, Object value) {
                    //将String 转为 Date
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = null;
                    try {
                        date = format.parse(value.toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return date;
                }
            }, Date.class);
            BeanUtils.populate(user, parameterMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        user.setUid(CommonsUtils.getUUID());
        user.setTelephone(null);
        user.setState(0);
        String activeCode=CommonsUtils.getUUID();
        user.setCode(activeCode);
        //将user传递给下一层service
        UserService service = new UserService();
        Boolean isRegistSuccess = service.regist(user);
//判断是否注册成功
        if (isRegistSuccess) {
            //发送激活邮件
            String emailMsg="恭喜您注册成功，请点击下面的连接进行激活<a href='http://localhost:8080/HeiMaShop/active?activeCode'+activeCode+>" +
                    "http://localhost:8080/HeiMaShop/active?activeCode="+activeCode+"</a>";
            try {
                MailUtils.sendMail(user.getEmail(),emailMsg);
            } catch (MessagingException e) {
                e.printStackTrace();e.printStackTrace();
            }
            //跳转到注册成功页面
            response.sendRedirect(request.getContextPath() + "/registerSuccess.jsp");
        } else {
//挑战到注册失败页面
            response.sendRedirect(request.getContextPath() + "/registerFail.jsp");
        }
    }
}

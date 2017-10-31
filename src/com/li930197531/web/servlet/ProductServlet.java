package com.li930197531.web.servlet;

import com.google.gson.Gson;
import com.li930197531.domain.*;
import com.li930197531.service.ProductService;
import com.li930197531.utils.JedisPoolUtils;
import redis.clients.jedis.Jedis;


import javax.servlet.ServletException;

import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;


public class ProductServlet extends BaseServlet {
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        doGet(request, response);
//    }
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//
//        //获得请求的那个方法的method
//        String methodName = request.getParameter("method");
//        if ("productList".equals(methodName)) {
//            productList(request, response);
//        } else if ("categoryList".equals(methodName)) {
//            categoryList(request, response);
//
//        } else if ("index".equals(methodName)) {
//            index(request, response);
//        } else if ("productInfo".equals(methodName)) {
//            productInfo(request, response);
//        }
//    }
    //模块中的功能通过方法进行区分
    //显示商品类别的功能

    public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService service = new ProductService();
        //先从缓存中查询categoreyList 如果有直接使用  没有在数据库中查询 存到缓存
        //1.获得jedis对象，连接redis数据库
        Jedis jedis = JedisPoolUtils.getJedis();
        String categoryListJson = jedis.get("categoryListJson");
//判断categoryListJson是否为空
        if (categoryListJson == null) {
            System.out.println("缓存没有数据，查询数据库");
            //准备分类数据
            List<Category> categoryList = service.findAllCategory();
            Gson gson = new Gson();
            categoryListJson = gson.toJson(categoryList);

            jedis.set("categoryListJson", categoryListJson);

        }


        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(categoryListJson);
    }

    //先是首页的功能index
    public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductService service = new ProductService();
        //准备热门商品---List<Product>
        List<Product> hotProductList = service.findHotProductList();

        //准备最新商品
        List<Product> newProductList = service.findNewProductList();
        //准备分类数据
//       List<Category> categoryList=service.findAllCategory();
        request.setAttribute("hotProductList", hotProductList);
        request.setAttribute("newProductList", newProductList);
//       request.setAttribute("categoryList", categoryList);
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    //显示商品的详细信息
    public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获得当前页
        String currentPage = request.getParameter("currentPage");
        //获得当前商品类别
        String cid = request.getParameter("cid");
        //获得要查询的商品的pid
        String pid = request.getParameter("pid");
        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);

        request.setAttribute("currentPage", currentPage);
        request.setAttribute("product", product);
        request.setAttribute("cid", cid);
        //获得客户端携带的cookie----获得名字时pids的cookie
        Cookie[] cookies = request.getCookies();
        String pids = pid;
        if (cid != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    pids = cookie.getValue();
                    //1-3-2 本次访问的商品的pid是8-->8-1-3-2
                    //1-3-2 本次访问的商品的pid是3--->3-1-2
                    //将pids拆成一个数组
                    String[] split = pids.split("-");//{3,1,2}
                    List<String> asList = Arrays.asList(split);//[3,1,2]
                    LinkedList<String> list = new LinkedList<String>(asList);//[3,1,2]
                    //判断集合中是否存在当前的pid
                    if (list.contains(pid)) {
                        //包含当前商品的pid
                        list.remove(pid);
                        list.addFirst(pid);
                    } else {
                        //不包含当前商品的pid  直接将pid放到头上
                        list.addFirst(pid);
                    }
                    //将[3,1,2]转成3-2-1字符串
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < list.size() && i < 7; i++) {
                        sb.append(list.get(i));
                        sb.append("-");
                    }
                    //去掉3-1-2-后的-
                    pids = sb.substring(0, sb.length() - 1);

                }
            }

        }
        Cookie cookie1 = new Cookie("pids", pids);
        response.addCookie(cookie1);
        request.getRequestDispatcher("/product_info.jsp").forward(request, response);
    }

    //将商品添加到购物车
    public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        //获得要放到购物车的商品的pid
        String pid = request.getParameter("pid");
        //获得该商品的购买数量
        int buyNum = Integer.parseInt(request.getParameter("buyNum"));

        //获得product对象
        ProductService service = new ProductService();
        Product product = service.findProductByPid(pid);
        //计算小计
        double subtotal = product.getShop_price() * buyNum;
//封装CartItem
        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setBuyNum(buyNum);
        cartItem.setSubtotal(subtotal);
//获得购物车---判断是否在session中已经存在购物车
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
//将购物项放到车中
        //先判断购物车中是否已经包含该购物项--------判断Key
        //如果购物车中已经存在该商品=---将现在买的与原有的数量相加
        Map<String, CartItem> cartItems = cart.getCartItems();

        double newsubtotal=0.0;
        if (cartItems.containsKey(pid)) {
//去除原有的商品的数量
             CartItem cartItem1=cartItems.get(pid);

             int oldBuyNum=cartItem1.getBuyNum();
             oldBuyNum+=buyNum;
            cartItem1.setBuyNum(oldBuyNum);
            cart.setCartItem(cartItems);
            //修改小计
            //原先商品的小计
          double oldSubTotal=  cartItem1.getSubtotal();//oldSubtotal
            //新买商品的小计
             newsubtotal=buyNum*product.getShop_price();
            cartItem1.setSubtotal(newsubtotal+oldSubTotal);

        } else {

            //如果车中没有该商品
            cart.getCartItems().put(product.getPid(), cartItem);
            newsubtotal=buyNum*product.getShop_price();
        }
        //计算总计
        double total = cart.getTotal() + newsubtotal;
        cart.setTotal(total);
        //将车再次放回session
        session.setAttribute("cart", cart);
        //直接跳转到购物车页面
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }
    //根据商品的类别 获取商品的列表

    public void productList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获得Cid
        String cid = request.getParameter("cid");
        String currentPageStr = request.getParameter("currentPage");
        if (currentPageStr == null) currentPageStr = "1";
        int currentPage = Integer.parseInt(currentPageStr);

        int currentCount = 12;
        ProductService service = new ProductService();
        PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);
        request.setAttribute("pageBean", pageBean);
        request.setAttribute("cid", cid);
        //定义一个集合 用来记录历史商品信息
        List<Product> historyProductList = new ArrayList<Product>();
        //获得客户端携带的名字叫pids的cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    String pids = cookie.getValue();//3-2-1
                    String[] split = pids.split("-");
                    for (String pid : split) {
                        Product product = service.findProductByPid(pid);
                        historyProductList.add(product);
                    }
                }
            }
        }
        //将历史记录的集合放到域中
        request.setAttribute("historyProductList", historyProductList);

        request.getRequestDispatcher("/product_list.jsp").forward(request, response);
    }
}

package com.li930197531.domain;

import java.util.HashMap;

import java.util.Map;

public class Cart {
    //该购物车中存储n个购物项
    private Map<String,CartItem> cartItems=new HashMap<String,CartItem>();
//购物车内商品的总计
    private  double total;

    public Map<String, CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItem(Map<String, CartItem> cartItem) {
        this.cartItems = cartItems;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

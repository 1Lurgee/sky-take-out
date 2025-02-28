package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

public interface ShoppingCartService {
    /**
     * 添加购物车
     * @param shoppingCartDTO 要添加到购物车的菜品
     */
    void add(ShoppingCartDTO shoppingCartDTO);
}

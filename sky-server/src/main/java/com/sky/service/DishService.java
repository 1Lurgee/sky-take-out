package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import org.springframework.stereotype.Service;

@Service
public interface DishService {
    /**
     * 添加菜品
     * @param dishDTO 前端菜品数据，包括dish和dishFlavors
     */
    void saveDishWithFlavors(DishDTO dishDTO);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品的分页条件及查询信息
     * @return 数据库中查到的数据
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);
}

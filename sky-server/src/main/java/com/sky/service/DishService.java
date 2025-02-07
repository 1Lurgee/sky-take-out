package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;

@Service
public interface DishService {
    /**
     * 添加菜品
     * @param dishDTO 前端菜品数据，包括dish和dishFlavors
     */
    void saveDishWithFlavors(DishDTO dishDTO);
}

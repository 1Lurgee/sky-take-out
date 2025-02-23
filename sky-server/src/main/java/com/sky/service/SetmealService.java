package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

public interface SetmealService {
    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 套餐分页查询数据
     * @return 查询出来的数据
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmealDTO 套餐信息
     */
    void saveSetmealWithDishes(SetmealDTO setmealDTO);
}

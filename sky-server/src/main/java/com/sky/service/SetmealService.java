package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.SetmealVO;

import java.util.List;

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

    /**
     * 批量删除套餐
     * @param ids 要删除的套餐id
     */
    void batchDelete(List<Long> ids);


    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐及其包含菜品
     */
    SetmealVO getSetmealWithDishesById(Long id);
}

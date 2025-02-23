package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 通过一个或多个菜品id获取套餐id
     * @param DishIds 菜品的id
     * @return 套餐的id
     */
    List<Long> getSetmealIdsbyDishIds(List<Long> DishIds);

    /**
     * 批量插入套餐菜品
     * @param setmealDishList 要插入的套餐菜品
     */
    void batchInsert(List<SetmealDish> setmealDishList);

    /**
     * 批量删除套餐相关菜品信息
     * @param setmealIds 相关套餐id
     */
    void batchDelete(List<Long> setmealIds);

//    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
//    void deleteById(Long setmealId);
}

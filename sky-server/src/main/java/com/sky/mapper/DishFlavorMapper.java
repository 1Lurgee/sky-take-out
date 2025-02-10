package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 添加菜品相应的口味
     * @param flavors 菜品的口味
     */
    void batchInsert(List<DishFlavor> flavors);

//    /**
//     * 根据菜品id删除其对应口味
//     * @param dishId 菜品id
//     */
//    @Delete("delete from dish_flavor where dish_id = #{dishId}")
//    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id删除其对应口味
     * @param dishIds 菜品id
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 通过dishId获取相应口味信息
     * @param dishId 菜品id
     * @return 菜品相应口味
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}

package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 添加菜品相应的口味
     * @param flavors 菜品的口味
     */
    void batchInsert(List<DishFlavor> flavors);
}

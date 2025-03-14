package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 删除菜品，包括单个删除和批量删除
     * @param ids 要删除的菜品id
     */
    void deleteDish(List<Long> ids);

    /**
     * 停售或禁售菜品
     * @param status 菜品要设置的状态值
     * @param id 菜品id
     */
    void updateDishStatus(Integer status, Long id);

    /**
     * 通过菜品id获取所有菜品相关信息
     * @param id 菜品id
     * @return 所有菜品相关信息
     */
    DishVO getDishById(Long id);

    /**
     * 修改菜品信息
     * @param dishDTO 从前端获取的菜品信息
     */
    void updateDish(DishDTO dishDTO);

    /**
     * 条件查询菜品和口味
     * @param dish 包含分类id
     * @return 相应菜品及分类id
     */
    List<DishVO> listWithFlavor(Dish dish);

    /**
     * 根据分类id查询菜品数据
     * @param dish 分类id
     * @return 属于该分类的菜品
     */
    List<Dish> list(Dish dish);
}

package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     *
     * @param categoryId 分类id
     * @return 菜品数量
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     *
     * @param dish 菜品信息
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 分页查询的条件
     * @return 查到的菜品信息
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 通过一个id查询菜品
     *
     * @param id 要查询的菜品id
     * @return 查到的菜品
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

//    /**
//     * 通过id删除菜品
//     * @param id 菜品id
//     */
//    @Delete("delete from dish where id = #{id}")
//    void deleteById(Long id);

    /**
     * 通过id删除菜品
     *
     * @param ids 菜品id
     */
    void deleteByIds(List<Long> ids);

//    /**
//     * 停售或禁售菜品
//     * @param status 菜品要设置的状态值
//     * @param id 菜品id
//     */
//    @Update("update dish set status = #{status} where id = #{id}")
//    void updateDishStatus(Integer status, Long id);

    /**
     * 更新菜品信息以及禁售起售菜品
     *
     * @param dish 更新后的菜品信息
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 查找所有菜品
     *
     * @param dish
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> list(Dish dish);

    /**
     * 根据条件统计菜品数量
     *
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}

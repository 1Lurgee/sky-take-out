package com.sky.mapper;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态查询购物车
     *
     * @param shoppingCart 查询
     * @return 查询到的信息
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id修改number
     *
     * @param shoppingCart 所需number以及id
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入数据
     *
     * @param shoppingCart 要插入的数据
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, amount, create_time) VALUE " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     *
     * @return 该用户购物车信息
     */
    @Select("select * from shopping_cart where user_id = #{userId}")
    List<ShoppingCart> showShoppingCart(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     *
     * @param userId 用户id
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 删除购物车中一个商品
     *
     * @param shoppingCartDTO 要删除的菜品活套餐信息
     * @param userId 用户id
     */
    void deleteItem(@Param("shoppingCartDTO") ShoppingCartDTO shoppingCartDTO, @Param("userId") Long userId);
}

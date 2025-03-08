package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface OrderMapper {

    /**
     * 提交订单
     * @param order 订单相关信息
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber 订单号
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders 要修改的订单信息
     */
    void update(Orders orders);

    /**
     * 直接生成已支付订单
     * @param orderStatus 订单状态
     * @param orderPaidStatus 订单支付状态
     * @param checkOutTime 结账时间
     * @param id 订单id
     */
    @Update("update orders set status = #{orderStatus},pay_status = #{orderPaidStatus} ,checkout_time = #{checkOutTime} where id = #{id}")
    void updateStatus(Integer orderStatus, Integer orderPaidStatus, LocalDateTime checkOutTime, Long id);

    /**
     * 订单分页查询
     * @param ordersPageQueryDTO 分页查询条件
     * @return 每页信息
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 查询订单
     * @param id 订单id
     * @return 相应订单
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    /**
     * 各个状态的订单数量统计
     * @return 各个状态的订单数量
     */
    OrderStatisticsVO getOrderStatistics();
}

package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {

    /**
     * 批量插入订单详细数据
     * @param orderDetailList 订单详细数据
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单id获取相应订单详情
     * @param orderId 订单id
     * @return 相应订单详情
     */
    @Select("select * from order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}

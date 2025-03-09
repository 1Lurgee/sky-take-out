package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 提交订单
     * @param ordersSubmitDTO 订单相关信息
     * @return 订单id、订单号、订单金额、下单时间
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 历史订单分页查询
     * @param page 页数
     * @param pageSize 页面大小
     * @param status 订单状态
     * @return 查询到的信息
     */
    PageResult getHistoryOrders(Integer page,Integer pageSize,Integer status);

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 相应订单详情
     */
    OrderVO getOrderDetail(Long id);

    /**
     * 取消订单
     * @param id 要取消的订单id
     */
    void cancel(Long id);

    /**
     * 再来一单
     * @param id 订单号
     */
    void repeat(Long id);

    /**
     * 订单查询
     * @param ordersPageQueryDTO 查询条件
     * @return 订单信息
     */
    PageResult getAllOrders(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     * @return 各个状态的订单数量
     */
    OrderStatisticsVO getOrderStatistics();

    /**
     * 接单
     * @param ordersConfirmDTO 订单id
     */
    void confirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 拒单
     * @param ordersRejectionDTO 订单id
     */
    void reject(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 取消订单
     * @param ordersCancelDTO 要取消的订单
     */
    void adminCancel(OrdersCancelDTO ordersCancelDTO);
    /**
     * 派送订单
     * @param id 订单id
     */
    void delivery(Long id);

    /**
     * 完成订单
     * @param id 订单id
     */
    void complete(Long id);

    /**
     * 催单
     * @param id 订单id
     */
    void reminder(Long id);
}

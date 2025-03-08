package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder() {
        log.info("处理超时订单：{}",LocalDateTime.now());
        //找出所有的超时订单
        LocalDateTime orderTime = LocalDateTime.now().minusMinutes(15);
        //找出15min前未付款的订单
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, orderTime);
        //取消这些订单
        if (orders != null && !orders.isEmpty()) {
            for (Orders order : orders) {
                order.setCancelTime(LocalDateTime.now());
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时，自动取消");
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder(){
        log.info("处理一直在配送中的订单：{}",LocalDateTime.now());
        //昨日的时间
        LocalDateTime orderTime = LocalDateTime.now().minusHours(1);
        //找寻昨日的订单
        List<Orders> orders = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, orderTime);
        if(orders != null && !orders.isEmpty()){
            for (Orders order : orders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }

}

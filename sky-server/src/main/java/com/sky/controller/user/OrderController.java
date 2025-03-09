package com.sky.controller.user;

import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api("用户订单相关功能")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 提交订单
     * @param ordersSubmitDTO 订单相关信息
     * @return 订单id、订单号、订单金额、下单时间
     */
    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户下单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 历史订单分页查询
     * @param page 页数
     * @param pageSize 页面大小
     * @param status 订单状态
     * @return 查询到的信息
     */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> historyOrdersPageQuery(Integer page,Integer pageSize,Integer status){
        PageResult pageResult = orderService.getHistoryOrders(page,pageSize,status);
        return Result.success(pageResult);
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 相应订单详情
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param id 要取消的订单id
     * @return 取消成功
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result<?> cancel(@PathVariable Long id){
        orderService.cancel(id);
        return Result.success();
    }

    /**
     * 再来一单
     * @param id 订单号
     * @return 成功再来一单
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result<?> repeat(@PathVariable Long id){
        orderService.repeat(id);
        return Result.success();
    }

    /**
     * 催单
     * @param id 订单id
     * @return 催单成功
     */
    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result<?> reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }

}

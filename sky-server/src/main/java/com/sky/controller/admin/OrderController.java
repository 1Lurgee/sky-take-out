package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.mapper.OrderMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Api("订单管理")
@RequestMapping("/admin/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 订单查询
     * @param ordersPageQueryDTO 查询条件
     * @return 订单信息
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        PageResult pageResult = orderService.getAllOrders(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 各个状态的订单数量统计
     * @return 各个状态的订单数量
     */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> getOrderStatistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.getOrderStatistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 订单信息与订单详情
     */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getOrderWithDetail(@PathVariable Long id){
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 接单
     * @param ordersConfirmDTO 订单id
     * @return 接单成功
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result<?> confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 拒单
     * @param ordersRejectionDTO 订单id
     * @return 拒单成功信息
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result<?> reject(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        orderService.reject(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 取消订单
     * @param ordersCancelDTO 要取消的订单
     * @return 取消成功
     */
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result<?> cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.adminCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 派送订单
     * @param id 订单id
     * @return 派送中的成功信息
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result<?> delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }

    /**
     * 完成订单
     * @param id 订单id
     * @return 订单完成成功
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result<?> complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
}

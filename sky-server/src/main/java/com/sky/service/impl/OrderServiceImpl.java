package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    //    @Autowired
//    private WeChatPayUtil weChatPayUtil;
    private Orders orders;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO 订单相关信息
     * @return 订单id、订单号、订单金额、下单时间
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        //处理业务异常（地址簿为空，菜品数据为空）
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartlist = shoppingCartMapper.showShoppingCart(shoppingCart);
        if (shoppingCartlist == null) {
            //抛出业务异常
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //封装订单信息
        Orders order = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, order);
        //封装user_id
        order.setUserId(userId);
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        //封装订单冗余数据
        order.setPhone(addressBook.getPhone());
        order.setAddress(addressBook.getDetail());
        order.setConsignee(addressBook.getConsignee());
        this.orders = order;
        orderMapper.insert(order);
        //封装订单明细
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart cart : shoppingCartlist) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(order.getId());
            orderDetailList.add(orderDetail);
        }
        //批量插入订单详情
        orderDetailMapper.insertBatch(orderDetailList);
        //清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        //封装VO
        return OrderSubmitVO.builder()
                .orderAmount(order.getAmount())
                .orderNumber(order.getNumber())
                .orderTime(order.getOrderTime())
                .id(order.getId())
                .build();
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO 订单支付相关信息
     * @return 回调结果
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
        JSONObject jsonObject = new JSONObject();
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));
        Integer OrderPaidStatus = Orders.PAID;//支付状态，已支付
        Integer OrderStatus = Orders.TO_BE_CONFIRMED;  //订单状态，待接单
        LocalDateTime check_out_time = LocalDateTime.now();//更新支付时间
        orderMapper.updateStatus(OrderStatus, OrderPaidStatus, check_out_time, this.orders.getId());
        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo 订单号
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 历史订单分页查询
     *
     * @param page     页数
     * @param pageSize 页面大小
     * @param status   订单状态
     * @return 查询到的信息
     */
    @Override
    public PageResult getHistoryOrders(Integer page, Integer pageSize, Integer status) {
        //获取当前用户id
        Long userId = BaseContext.getCurrentId();
        //封装参数,方便后续接口复用
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setPage(page);
        ordersPageQueryDTO.setPageSize(pageSize);
        ordersPageQueryDTO.setUserId(userId);
        ordersPageQueryDTO.setStatus(status);
        //执行分页查询
        PageHelper.startPage(page, pageSize);
        //处理数据
        Page<Orders> ordersList = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderDetailList = new ArrayList<>();
        for (Orders o : ordersList) {
            List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(o.getId());
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(o,orderVO);
            orderVO.setOrderDetailList(orderDetails);
            orderDetailList.add(orderVO);
        }
        //封装返回数据
        PageResult pageResult = new PageResult();
        pageResult.setTotal(ordersList.getTotal());
        pageResult.setRecords(orderDetailList);
        return pageResult;
    }

    /**
     * 查询订单详情
     * @param id 订单id
     * @return 相应订单详情
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        //获取订单号
        Orders order = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
        OrderVO orderVO = new OrderVO();
        //封装信息
        BeanUtils.copyProperties(order,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }

    /**
     * 取消订单
     * @param id 要取消的订单id
     */
    @Override
    public void cancel(Long id) {
        Orders order = new Orders();
        //设置取消时间、订单状态
        order.setId(id);
        order.setCancelTime(LocalDateTime.now());
        order.setStatus(Orders.CANCELLED);
        //调用mapper层接口
        orderMapper.updateById(order);
    }

    /**
     * 再来一单
     * @param id 订单号
     */
    @Override
    public void repeat(Long id) {
        //获取订单
        Orders order = orderMapper.getById(id);
        //重新设置订单属性
        order.setOrderTime(LocalDateTime.now());
        order.setStatus(Orders.PENDING_PAYMENT);
        order.setCheckoutTime(LocalDateTime.now());
        order.setPayStatus(Orders.UN_PAID);
        order.setNumber(String.valueOf(System.currentTimeMillis()));
        //重新插入订单表
        orderMapper.insert(order);
        //获取订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        for (OrderDetail OD : orderDetailList) {
            OD.setOrderId(order.getId());
        }
        //批量插入订单详情
        orderDetailMapper.insertBatch(orderDetailList);
    }

    /**
     * 订单查询
     * @param ordersPageQueryDTO 查询条件
     * @return 订单信息
     */
    @Override
    public PageResult getAllOrders(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> list = new ArrayList<>();
        for (Orders order : page) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order,orderVO);
            List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(order.getId());
            orderVO.setOrderDetailList(orderDetailList);
            list.add(orderVO);
        }
        return new PageResult(page.getTotal(),list);
    }

    /**
     * 各个状态的订单数量统计
     * @return 各个状态的订单数量
     */
    @Override
    public OrderStatisticsVO getOrderStatistics() {
        OrderStatisticsVO orderStatisticsVO = orderMapper.getOrderStatistics();
        return orderStatisticsVO;
    }


}

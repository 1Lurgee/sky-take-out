package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 营业额统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 统计数据
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 用户统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 用户统计数据
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 订单统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return 订单统计数据
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 获取销量前十
     * @param begin 开始日期
     * @param end 结束日期
     * @return 菜品榜单
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}

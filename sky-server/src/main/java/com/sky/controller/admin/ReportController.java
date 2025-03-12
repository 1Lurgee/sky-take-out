package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@Api("数据统计相关接口")
@Slf4j
@RequestMapping("/admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 统计数据
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额数据统计：{} {}", begin, end);
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(begin, end);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 用户统计数据
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计：{}，{}", begin, end);
        UserReportVO userReportVO = reportService.getUserStatistics(begin, end);
        return Result.success(userReportVO);
    }

    /**
     * 订单统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 订单统计数据
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单数")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        OrderReportVO orderStatisticsVO = reportService.getOrdersStatistics(begin, end);
        return Result.success(orderStatisticsVO);
    }


    /**
     * 获取销量前十
     * @param begin 开始日期
     * @param end 结束日期
     * @return 菜品榜单
     */
    @GetMapping("/top10")
    @ApiOperation("销量top10")
    public Result<SalesTop10ReportVO> getSalesTop10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        return Result.success(reportService.getSalesTop10(begin,end));
    }

    /**
     * 导出报表
     * @param response http响应
     */
    @GetMapping("/export")
    @ApiOperation("导出报表")
    public void export(HttpServletResponse response){
        reportService.export(response);
    }
}

package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 统计数据
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> list = new ArrayList<>();
        list.add(begin);
        //封装dateList
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            list.add(begin);
        }
        //封装金额数据
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : list) {
            Map<Object, Object> map = new HashMap<>();
            map.put("begin", LocalDateTime.of(localDate, LocalTime.MIN));
            map.put("end", LocalDateTime.of(localDate, LocalTime.MAX));
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }

        String dateList = StringUtils.join(list, ",");
        return TurnoverReportVO
                .builder()
                .dateList(dateList)
                .turnoverList(StringUtils.join(turnoverList, ','))
                .build();
    }

    /**
     * 用户统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 用户统计数据
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //封装日期数据
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //总用户量
        List<Integer> totalUserList = new ArrayList<>();
        //新增用户量
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            Map<Object, Object> map = new HashMap<>();
            map.put("end", LocalDateTime.of(localDate, LocalTime.MAX));
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            map.put("begin", LocalDateTime.of(localDate, LocalTime.MIN));
            Integer newUser = userMapper.countByMap(map);
            newUser = newUser == null ? 0 : newUser;
            newUserList.add(newUser);
        }


        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }

    /**
     * 订单统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return 订单统计数据
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //封装日期时
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //封装每日订单数
        List<Integer> orderCountList = new ArrayList<>();
        //封装每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();
        //封装订单总数
        Integer totalOrderCount = 0;
        //封装有效订单数
        Integer totalValidOrderCount = 0;
        for (LocalDate localDate : dateList) {
            //获取每日订单
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            orderCountList.add(orderCount);
            totalOrderCount += orderCount;
            //获取每日有效订单
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            validOrderCountList.add(validOrderCount);
            totalValidOrderCount += validOrderCount;
        }

        double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = totalValidOrderCount.doubleValue() / totalOrderCount;
        }
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(totalValidOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 获取销量前十
     *
     * @param begin 开始日期
     * @param end   结束日期
     * @return 菜品榜单
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> list = orderMapper.getSalesTop10(beginTime, endTime);
        //封装VO属性
        List<String> nameList = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    /**
     * 导出http报表
     *
     * @param response http响应
     */
    @Override
    public void export(HttpServletResponse response) {
        //封装起始时间
        LocalDateTime beginTime = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        //获取营业数据
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        //读取模板
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //开始写入
        try {
            XSSFWorkbook sheets = new XSSFWorkbook(in);
            //通过表明获取表
            XSSFSheet sheet = sheets.getSheet("Sheet1");
            //获取行，写入数据
            XSSFRow row2 = sheet.getRow(1);
            row2.createCell(1).setCellValue("时间：" + beginTime.getYear()+"-"+beginTime.getMonth()+"-"+beginTime.getDayOfMonth()
                    + "至" + endTime.getYear()+"-"+endTime.getMonth()+"-"+endTime.getDayOfMonth());
            //获取第四行和第五行，写入营业数据
            XSSFRow row4 = sheet.getRow(3);
            row4.getCell(2).setCellValue(businessData.getTurnover());
            row4.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row4.getCell(6).setCellValue(businessData.getNewUsers());
            XSSFRow row5 = sheet.getRow(4);
            row5.getCell(2).setCellValue(businessData.getValidOrderCount());
            row5.getCell(4).setCellValue(businessData.getUnitPrice());
            //填写明细数据
            for(int i = 0; i < 30; i++){
                LocalDate begin = LocalDate.now().minusDays(30);
                begin = begin.plusDays(i);
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(begin, LocalTime.MAX));
                //写入excel
                XSSFRow row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(begin.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
            }
            //通过输出流下载到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            sheets.write(outputStream);

            //关闭资源
            sheets.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map<Object, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}

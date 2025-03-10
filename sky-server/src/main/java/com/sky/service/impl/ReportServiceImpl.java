package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

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
            newUser = newUser == null? 0 : newUser;
            newUserList.add(newUser);
        }


        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();
    }
}

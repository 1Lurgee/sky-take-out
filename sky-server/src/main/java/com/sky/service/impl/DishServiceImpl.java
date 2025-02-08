package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 添加菜品
     * @param dishDTO 前端菜品数据，包括dish和dishFlavors
     */
    @Transactional
    @Override
    public void saveDishWithFlavors(DishDTO dishDTO) {
        //将前端数据封装为实体类
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //将数据插入dish表
        dishMapper.insert(dish);
        //获取数据库回显的主键
        Long id = dish.getId();
        //批量插入dish_flavor表
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.batchInsert(flavors);
        }

    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品的分页条件及查询信息
     * @return 数据库中查到的数据
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //使用pageHelper分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        //获取page中的结果
        PageResult pageResult = new PageResult();
        List<DishVO> result = page.getResult();
        long total = page.getTotal();
        pageResult.setRecords(result);
        pageResult.setTotal(total);
        return pageResult;
    }
}

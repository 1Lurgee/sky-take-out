package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

    /**
     * 根据分类id查询菜品数据
     * @param dish 里面包含分类id
     * @return 属于该分类的菜品
     */
    @Override
    public List<Dish> list(Dish dish) {
        return dishMapper.list(dish);
    }

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
        log.info("口味信息{}",flavors);
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

    /**
     * 删除菜品，包括单个删除和批量删除
     * @param ids 要删除的菜品id
     */
    @Override
    @Transactional
    public void deleteDish(List<Long> ids) {
        Dish dish;
        //判断菜品是否还在起售
        for (Long id : ids) {
           dish = dishMapper.getById(id);
           if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
               throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
           }
        }
        //判断菜品被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsbyDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品信息
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            //删除菜品菜品的口味信息
//            dishFlavorMapper.deleteByDishId(id);
//        }
        //优化后
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 停售或禁售菜品
     * @param status 菜品要设置的状态值
     * @param id 菜品id
     */
    @Override
    public void updateDishStatus(Integer status, Long id) {

        dishMapper.update(Dish.builder().status(status).id(id).build());
    }

    /**
     * 通过菜品id获取所有菜品相关信息
     * @param id 菜品id
     * @return 所有菜品相关信息
     */
    @Override
    public DishVO getDishById(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 修改菜品信息
     * @param dishDTO 从前端获取的菜品信息
     */
    @Override
    @Transactional
    public void updateDish(DishDTO dishDTO) {
        //先更新菜品表信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //删除口味信息
        List<Long> dishId = new ArrayList<>();
        dishId.add(dishDTO.getId());
        dishFlavorMapper.deleteByDishIds(dishId);
        //重新添加修改后的菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.batchInsert(flavors);
        }
    }
}

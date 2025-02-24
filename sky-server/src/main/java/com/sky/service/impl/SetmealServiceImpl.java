package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 套餐分页查询数据
     * @return 查询出来的数据
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    /**
     * 新增套餐
     * @param setmealDTO 套餐信息
     */
    @Override
    @Transactional
    public void saveSetmealWithDishes(SetmealDTO setmealDTO) {
        //复制套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //向套餐表插入信息，并返回套餐主键
        setmealMapper.insert(setmeal);
        //封装setmeal_dish
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        List<SetmealDish> setmealDishList = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
            return setmealDish;
        }).collect(Collectors.toList());
        setmealDishMapper.batchInsert(setmealDishList);

    }

    /**
     * 批量删除套餐
     * @param ids 要删除的套餐id
     */
    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        //删除套餐信息
        setmealMapper.batchDelete(ids);
        //删除套餐相关菜品信息
        setmealDishMapper.batchDelete(ids);
    }

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐及其包含菜品
     */
    @Override
    public SetmealVO getSetmealWithDishesById(Long id) {
        SetmealVO setmealVO = new SetmealVO();
        //获取套餐信息
        Setmeal setmeal = setmealMapper.getById(id);
        //获取套餐包含菜品信息
        List<SetmealDish> list = setmealDishMapper.getBySetmealId(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(list);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO 需要修改的套餐信息
     */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        //复制setmeal属性
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.update(setmeal);
        //删除套餐中的菜品,复用批量删除方法
        List<Long> setmealId = new ArrayList<>();
        setmealId.add(setmealDTO.getId());
        setmealDishMapper.batchDelete(setmealId);
        //重新插入套餐菜品
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        List<SetmealDish> setmealDishList = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(setmealDTO.getId());
            return setmealDish;
        }).collect(Collectors.toList());
        setmealDishMapper.batchInsert(setmealDishList);
    }

    /**
     * 起售或停售套餐
     * @param status 要改变成的状态
     */
    @Override
    public void updateStatus(Integer status) {
        Setmeal setmeal = Setmeal.builder().status(status).build();
        setmealMapper.update(setmeal);
    }
}

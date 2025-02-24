package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api("套餐管理接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO 套餐信息
     * @return 插入成功信息
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result<?> saveSetmealWithDishes(@RequestBody SetmealDTO setmealDTO){
        log.info("要新增的菜品：{}",setmealDTO);
        setmealService.saveSetmealWithDishes(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO 套餐分页查询数据
     * @return 查询出来的数据
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询数据：{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @param ids 要删除的套餐id
     * @return 删除成功信息
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result<?> batchDelete(@RequestParam List<Long> ids){
        setmealService.batchDelete(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id 套餐id
     * @return 套餐及其包含菜品
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getSetmealWithDishesById(@PathVariable Long id){
        return Result.success(setmealService.getSetmealWithDishesById(id));
    }
}

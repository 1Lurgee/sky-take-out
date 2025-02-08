package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品管理接口
 */
@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理接口")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 添加菜品
     * @param dishDTO 前端菜品数据，包括dish和dishFlavors
     * @return 返回传输成功数据
     */
    @PostMapping
    @ApiOperation("添加菜品")
    public Result<?> saveDishWithFlavors(@RequestBody DishDTO dishDTO){
        dishService.saveDishWithFlavors(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO 菜品的分页条件及查询信息
     * @return 数据库中查到的数据
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }
}

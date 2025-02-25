package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Set;

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
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 添加菜品
     * @param dishDTO 前端菜品数据，包括dish和dishFlavors
     * @return 返回传输成功数据
     */
    @PostMapping
    @ApiOperation("添加菜品")
    public Result<?> saveDishWithFlavors(@RequestBody DishDTO dishDTO){
        dishService.saveDishWithFlavors(dishDTO);
        String pattern = "dish_" + dishDTO.getCategoryId();
        cleanCache(pattern);
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

    /**
     * 删除菜品，包括单个删除和批量删除
     * @param ids 要删除的菜品id
     * @return 删除成功信息
     */
    @ApiOperation("删除菜品")
    @DeleteMapping
    public Result<?> deleteDish(@RequestParam(name="ids") List<Long> ids){
        dishService.deleteDish(ids);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 停售或禁售菜品
     * @param status 菜品要设置的状态值
     * @param id 菜品id
     * @return 更新成功的信息
     */
    @PostMapping("/status/{status}")
    public Result<?> updateDishStatus(@PathVariable Integer status,Long id){
        dishService.updateDishStatus(status,id);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 通过菜品id获取所有菜品相关信息
     * @param id 菜品id
     * @return 所有菜品相关信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "通过菜品id获取所有菜品相关信息")
    public Result<DishVO> getDishById(@PathVariable Long id){
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品信息
     * @param dishDTO 从前端获取的菜品信息
     * @return 修改成功的信息
     */
    @PutMapping
    @ApiOperation(value = "修改菜品信息")
    public Result<?> updateDish(@RequestBody DishDTO dishDTO){
        log.info("修改后的菜品信息{}",dishDTO);
        dishService.updateDish(dishDTO);
        cleanCache("dish_*");
        return Result.success();
    }

    /**
     * 根据分类id查询菜品数据
     * @param categoryId 分类id
     * @return 属于该分类的菜品
     */
    @GetMapping("/list")
    @ApiOperation("通过分类id查询菜品")
    public Result<List<Dish>> getDishByCategoryId(Long categoryId){
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        List<Dish> list = dishService.list(dish);
        return Result.success(list);
    }

    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        assert keys != null;
        redisTemplate.delete(keys);
    }
}

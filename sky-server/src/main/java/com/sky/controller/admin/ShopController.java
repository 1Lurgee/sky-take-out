package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@Slf4j
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
public class ShopController {

    private static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("更改店铺状态")
    public Result<?> setShopStatus(@PathVariable Integer status){
        log.info("设置店铺状态为{}",status==1?"营业中":"打烊中");
        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        stringObjectValueOperations.set(KEY,status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result<Integer> getShopStatus(){
        ValueOperations<String, Object> stringObjectValueOperations = redisTemplate.opsForValue();
        Integer shopStatus = (Integer) stringObjectValueOperations.get(KEY);
        log.info("获取店铺的状态为{}",shopStatus);
        return Result.success(shopStatus);
    }
}

package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api("购物车接口")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     * 添加购物车
     * @param shoppingCartDTO 要添加到购物车的菜品
     * @return 添加成功的信息
     */
    @ApiOperation("添加购物车")
    @PostMapping("/add")
    public Result<?> add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

}

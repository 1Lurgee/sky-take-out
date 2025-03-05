package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid 从微信服务器获取的openid
     * @return 用户信息
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user 要插入的用户数据
     */
    void insert(User user);

    /**
     * 通过用户id获取用户
     * @param id 用户id
     * @return 用户信息
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);
}

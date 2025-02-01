package com.sky.mapper;

import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username 登录界面传来的用户名
     * @return 返回查询到的员工
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     *  差入员工数据
     * @param employee 要插入到表的员工数据信息
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) VALUE" +
            " (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);
}

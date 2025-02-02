package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.EmployeePageQueryDTO;
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
     *  插入员工数据
     * @param employee 要插入到表的员工数据信息
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user) VALUE" +
            " (#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Employee employee);

    /**
     * 员工分页查询
     * @param employeePageQueryDTO 分页查询所需数据，员工姓名，起始页，每页条目
     * @return 每一页的员工信息，以及总条目数
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键动态的修改员工属性
     * @param employee 要修改的员工相关数据
     */
    void update(Employee employee);
}

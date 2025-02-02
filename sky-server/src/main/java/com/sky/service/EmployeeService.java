package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 包含登录的账号的密码
     * @return 返回查询到的员工
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employeeLoginDTO 新增的员工的信息
     */
    void save(EmployeeDTO employeeLoginDTO);

    /**
     *员工分页查询
     * @param employeePageQueryDTO 分页查询所需数据，员工姓名，起始页，每页条目
     * @return 每一页的信息，以及总条目数
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 禁用或启用员工
     * @param status 要给予员工的状态
     * @param id 要改变状态的员工id
     */
    void updateAccountStatus(Integer status, Long id);

    /**
     * 根据员工id查询
     * @param id 员工id
     * @return 员工信息
     */
    Employee getEmpById(Long id);

    /**
     * 编辑员工信息
     * @param employeeDTO 修改后的员工信息
     */
    void updateEmp(EmployeeDTO employeeDTO);
}

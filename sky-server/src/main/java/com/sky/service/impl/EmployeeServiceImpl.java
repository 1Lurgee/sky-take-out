package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 员工登录所需要信息的数据传输模型
     * @return 通过用户名查询到的员工信息
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes(StandardCharsets.UTF_8));

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO 包含了新员工信息的数据传输对象
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        //创建与数据库连接时所需对象
        Employee employee = new Employee();
        //使用BeanUtils拷贝从传输数据模型中拷贝信息
        BeanUtils.copyProperties(employeeDTO,employee);
        //补充剩余信息
        //设置密码，使用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置状态，默认是正常，数值为1。禁止数值为0
        employee.setStatus(StatusConstant.ENABLE);
        //设置创建和更新日期
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //设置当前记录的创建人id,从ThreadLocal拿到，该值由拦截器中完成赋值
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);

    }
    /**
     *员工分页查询
     * @param employeePageQueryDTO 分页查询所需数据，员工姓名，起始页，每页条目
     * @return 每一页的员工信息，以及总条目数
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询，设置分页查询信息
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        //从返回的page提取信息，总条目数和指定页面的信息
        long total = page.getTotal();
        PageResult pageResult = new PageResult();
        List<Employee> result = page.getResult();
        //封装数据
        pageResult.setTotal(total);
        pageResult.setRecords(result);
        return pageResult;
    }

    /**
     * 禁用或启用员工
     * @param status 要给予员工的状态
     * @param id 要改变状态的员工id
     */
    @Override
    public void updateAccountStatus(Integer status, Long id) {
        //构建实体类，统一参数，方便后续复用Mapper接口中的update函数
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        //update方法使用动态SQL，可满足后续修改员工功能
        employeeMapper.update(employee);
    }

    /**
     * 根据员工id查询
     * @param id 员工id
     * @return 员工信息
     */
    @Override
    public Employee getEmpById(Long id) {
        Employee employee = employeeMapper.getEmpById(id);
        //设置密码，防止泄露
        employee.setPassword("*");
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO 修改后的员工信息
     */
    @Override
    public void updateEmp(EmployeeDTO employeeDTO) {
        //拷贝员工信息至employee,之前设计的函数参数为employee
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);
        //设置修改时间
        employee.setUpdateTime(LocalDateTime.now());
        //设置修改人id
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }


}

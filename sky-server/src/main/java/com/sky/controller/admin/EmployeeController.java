package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工信息相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO 包含了员工登录时所需信息的数据传输对象
     * @return 登录结果信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return 登出结果信息
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<?> logout() {
        return Result.success();
    }


    /**
     * 新增员工
     * @param employeeDTO 包含了新员工信息的数据传输对象
     * @return 新增员工结果信息
     */
    @PostMapping()
    @ApiOperation(value = "新增员工")
    public Result<?> save(@RequestBody EmployeeDTO employeeDTO){
        //输出日志信息
        log.info("新增员工信息：{}",employeeDTO);
        //调用service层方法
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO 分页查询所需数据，员工姓名，起始页，每页条目
     * @return 每一页的信息
     */
    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 禁用或启用员工
     * @param status 要给予员工的状态
     * @param id 要改变状态的员工id
     * @return 如果没有捕获异常则返回成功
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "禁用或启用员工")
    public Result<?> updateAccountStatus(@PathVariable Integer status,Long id){
        employeeService.updateAccountStatus(status,id);
        return Result.success();
    }

    /**
     * 根据员工id查询
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据员工id查询")
    public Result<Employee> getEmpById(@PathVariable Long id){
        Employee employee = employeeService.getEmpById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO 修改后的员工信息
     * @return 修改成功与否
     */
    @PutMapping
    @ApiOperation(value = "编辑员工信息")
    public Result<?> updateEmp(@RequestBody EmployeeDTO employeeDTO){
        employeeService.updateEmp(employeeDTO);
        return Result.success();
    }

}

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
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password =DigestUtils.md5DigestAsHex(password.getBytes());
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
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置密码,默认123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置账号状态
        employee.setStatus(StatusConstant.ENABLE);
        //设置当前修改时间和创建时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //创建人id
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());
        //执行添加员工的SQL语句
        employeeMapper.Insert(employee);
    }

    /**    (non-Javadoc)
     * 分页查询
     * @see com.sky.service.EmployeeService#pageQuery(com.sky.dto.EmployeePageQueryDTO)
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //分页查 利用工具pegehelper
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page=employeeMapper.pageQuery(employeePageQueryDTO);
        //员工总数
        long total = page.getTotal();
        //查询的结果
        List<Employee> result = page.getResult();
        return new PageResult(total,result);
    }

    /**    (non-Javadoc)
     * 员工账号启用和禁止
     * @see com.sky.service.EmployeeService#startOrStop(java.lang.Integer, java.lang.Long)
     */
    public void startOrStop(Integer status, Long id) {
        // Employee employee = new Employee();
        // employee.setId(id);
        // employee.setStatus(status);
        Employee employee = Employee.builder()
            .id(id)
            .status(status)
            .build();
        employeeMapper.update(employee);
    }

    /**    (non-Javadoc)
     * 根据id查询
     * @see com.sky.service.EmployeeService#findById(java.lang.Long)
     */
    public Employee findById(Long id) {
        Employee employee=employeeMapper.findById(id);
        return employee;
    }

    /**    (non-Javadoc)
     * 编辑员工信息
     * @see com.sky.service.EmployeeService#update(com.sky.dto.EmployeeDTO)
     */
    public void update(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employeeMapper.update(employee);
    }

    
}

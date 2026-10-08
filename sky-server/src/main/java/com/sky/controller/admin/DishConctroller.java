package com.sky.controller.admin;

import com.sky.entity.Dish;
import com.sky.mapper.DishMapper;
import com.sky.vo.DishVO;
import org.springframework.web.bind.annotation.*;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;


@RestController 
@RequestMapping("/admin/dish")
@Slf4j 
@Api (tags = "菜品相关接口")
public class DishConctroller {

    @Autowired 
    private DishService dishService;

    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping 
    @ApiOperation ("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping ("/page")
    @ApiOperation ("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}",dishPageQueryDTO);
        PageResult pageResult=dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品删除
     * @param ids
     * @return
     */
    @DeleteMapping 
    @ApiOperation ("菜品删除")
    public Result delete(@RequestParam  List<Long> ids){
        log.info("菜品批量删除");
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping ("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getId(@PathVariable Long id){
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long id){
        return Result.success(dishService.list(id));
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}",dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }
}

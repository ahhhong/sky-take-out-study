package com.sky.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Select;

@Mapper 
public interface DishFlavorMapper {

    /**
     * 新增菜品口味插入
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除口味
     * @param dishId
     */
    @Delete ("delete from dish_flavor where dish_id =#{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id批量删除口味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id查询口味
     * @param id
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{id}")
    List<DishFlavor> getByDishId(Long id);
}

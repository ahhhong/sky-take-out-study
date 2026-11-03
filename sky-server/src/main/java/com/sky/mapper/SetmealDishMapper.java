package com.sky.mapper;

import java.util.List;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper 
public interface SetmealDishMapper {

    /** 批量保存套餐和菜品的关联关系。 */
    void insertBatch(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /** 删除套餐的全部菜品关联。 */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /** 查询套餐关联的菜品数据，用于修改页面回显。 */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}

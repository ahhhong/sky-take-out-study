package com.sky.service.impl;

import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;

@Service 
public class DishServiceImpl implements DishService{

    @Autowired 
    private DishMapper dishMapper;
    @Autowired 
    private DishFlavorMapper dishFlavorMapper;
    @Autowired 
    private SetmealDishMapper setmealDishMapper;
    
    /**    (non-Javadoc)
     * 新增菜品和对应口味
     * @see com.sky.service.DishService#saveWithFlavor(com.sky.dto.DishDTO)
     */
    @Transactional 
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors!=null&&flavors.size()>0) {
            for (DishFlavor dishFlavor : flavors) {
                dishFlavor.setDishId(dishId);
            }
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**    (non-Javadoc)
     * 菜品分页查询
     * @see com.sky.service.DishService#pageQuery(com.sky.dto.DishPageQueryDTO)
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /**    (non-Javadoc)
     * 批量删除菜品
     * @see com.sky.service.DishService#deleteBatch(java.util.List)
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断菜品是否启用
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus()==StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否关联套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds!=null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        //删除菜品及相关数据
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }*/

        dishMapper.deleteByIds(ids);
    }

}

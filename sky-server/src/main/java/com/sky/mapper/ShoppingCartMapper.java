package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    ShoppingCart getByDish(@Param("userId") Long userId, @Param("dishId") Long dishId,
                           @Param("dishFlavor") String dishFlavor);

    ShoppingCart getBySetmeal(@Param("userId") Long userId, @Param("setmealId") Long setmealId);

    void insert(ShoppingCart shoppingCart);

    void incrementNumber(Long id);

    void decrementNumber(Long id);

    void deleteById(Long id);

    List<ShoppingCart> listByUserId(Long userId);

    void deleteByUserId(Long userId);
}

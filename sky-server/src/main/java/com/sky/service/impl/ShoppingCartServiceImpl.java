package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import java.time.LocalDateTime;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void add(ShoppingCartDTO shoppingCartDTO) {
        validateItem(shoppingCartDTO);
        Long userId = BaseContext.getCurrentId();

        ShoppingCart.ShoppingCartBuilder cart = ShoppingCart.builder()
                .userId(userId)
                .number(1)
                .createTime(LocalDateTime.now());

        if (shoppingCartDTO.getDishId() != null) {
            //根据菜品id来查询
            Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
            if (dish == null || !StatusConstant.ENABLE.equals(dish.getStatus())) {
                throw new ShoppingCartBusinessException(MessageConstant.PARAMETER_ERROR);
            }
            cart.dishId(dish.getId())
                    .dishFlavor(shoppingCartDTO.getDishFlavor())
                    .name(dish.getName())
                    .amount(dish.getPrice())
                    .image(dish.getImage());
        } else {
            //根据套餐id查询
            Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
            if (setmeal == null || !StatusConstant.ENABLE.equals(setmeal.getStatus())) {
                throw new ShoppingCartBusinessException(MessageConstant.PARAMETER_ERROR);
            }
            cart.setmealId(setmeal.getId())
                    .name(setmeal.getName())
                    .amount(setmeal.getPrice())
                    .image(setmeal.getImage());
        }
        //查询是购物车是否有相同的商品
        ShoppingCart existing = findExistingCartItem(userId, shoppingCartDTO);
        if (existing != null) {
            //有则number+1
            shoppingCartMapper.incrementNumber(existing.getId());
        } else {
            //没有则新加一条商品
            shoppingCartMapper.insert(cart.build());
        }
    }

    @Override
    @Transactional
    public void subtract(ShoppingCartDTO shoppingCartDTO) {
        validateItem(shoppingCartDTO);
        Long userId = BaseContext.getCurrentId();
        ShoppingCart existing = findExistingCartItem(userId, shoppingCartDTO);
        if (existing == null) {
            throw new ShoppingCartBusinessException("购物车中不存在该商品");
        }

        // 最后一件商品直接删除该行，其余情况只减少数量。
        if (existing.getNumber() == 1) {
            shoppingCartMapper.deleteById(existing.getId());
        } else {
            shoppingCartMapper.decrementNumber(existing.getId());
        }
    }

    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.listByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void clean() {
        shoppingCartMapper.deleteByUserId(BaseContext.getCurrentId());
    }

    /** 菜品按“菜品 + 口味”定位，套餐按套餐 ID 定位；两种操作共用这条规则。 */
    private ShoppingCart findExistingCartItem(Long userId, ShoppingCartDTO item) {
        if (item.getDishId() != null) {
            return shoppingCartMapper.getByDish(userId, item.getDishId(), item.getDishFlavor());
        }
        return shoppingCartMapper.getBySetmeal(userId, item.getSetmealId());
    }

    /**
     *验证数据合法性
     * @param item
     */
    private void validateItem(ShoppingCartDTO item) {
        if (item == null) {
            throw new ShoppingCartBusinessException(MessageConstant.PARAMETER_ERROR);
        }
        boolean hasDish = item.getDishId() != null;
        boolean hasSetmeal = item.getSetmealId() != null;
        if (hasDish == hasSetmeal || (hasSetmeal && item.getDishFlavor() != null)) {
            throw new ShoppingCartBusinessException(MessageConstant.PARAMETER_ERROR);
        }
    }
}

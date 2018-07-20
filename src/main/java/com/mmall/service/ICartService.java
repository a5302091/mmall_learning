package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {

    //购物车添加商品
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    //更新购物车中商品数量
    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    //购物车中删除商品
    ServerResponse<CartVo> deleteProduct(Integer userId, String productIds);

    //查看购物车中商品
    ServerResponse<CartVo> list(Integer userId);

    //购物车全选或全返选
    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    //获取购物车中商品数量
    ServerResponse<Integer> getCartProductCount(Integer userId);

}

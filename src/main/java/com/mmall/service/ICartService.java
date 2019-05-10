package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVo;

public interface ICartService {
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    public ServerResponse<CartVo> update(Integer userId,Integer productId, Integer count);
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds);

}

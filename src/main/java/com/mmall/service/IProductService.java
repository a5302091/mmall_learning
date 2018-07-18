package com.mmall.service;


import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

public interface IProductService {

    //新增或更新产品
    ServerResponse saveOrUpdateProduct(Product product);

    //产品销售状态更新
    ServerResponse setSaleStatus(Integer productId, Integer status);

    //获取产品详情
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    //分页查询商品列表
    ServerResponse<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    //商品搜索
    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

}

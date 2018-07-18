package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;

public interface ICategoryService {
    //添加品类
    ServerResponse addCategory(String categoryName, Integer parentId);

    //更新品类名称
    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    //平级查询
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    //递归查询孩子节点
    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);

}

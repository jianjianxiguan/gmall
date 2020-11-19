package com.atguigu.gmall.search.pojo;


import lombok.Data;

import java.util.List;

/*
*  * 接受页面传递过来的检索参数
 * search?keyword=小米&brandId=1,3&cid=225&props=5:高通-麒麟
 * &props=6:骁龙865-硅谷1000&sort=1&priceFrom=1000&priceTo=6000&pageNum=1&store=true
*/

@Data
public class SearchParamVo {

    //搜索关键字
    private String keyword;

    //品牌过滤条件
    private List<Long> brandId;

    //分类的过滤条件
    private List<Long> cid;

    //规格参数的过滤 ,传递方式 props=4:8-12props=5:128-256
    private List<String> props;

    //排序字段:1价格升序,2价格降序,3销量的降序,4.新品的降序,默认0得分的降序
    private Integer sort;

    //价格区间的过滤
    private Double priceTo;
    private Double priceFrom;

    //是否有货
    private Boolean store;

    //页码,默认第一页,pageSize,每页数量
    private Integer pageNum = 1;
    private final Integer pageSize = 20;
}




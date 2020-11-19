package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "goods", type = "info", shards = 3, replicas = 2)
@Data
public class Goods {


    //商品列表需要的字段
    @Id
    @Field(type = FieldType.Long)
    private Long skuId;
    @Field(type = FieldType.Keyword, index = false)
    private String defaultImage;
    @Field(type = FieldType.Double)
    private Double price;
    @Field(type = FieldType.Text, analyzer = "ik_max+word")
    private String title;
    @Field(type = FieldType.Keyword,index = false)
    private String subTitle;

    //排序分页筛选需要字段
    @Field(type = FieldType.Long)
    private Long sales = 0l; //销量
    @Field(type = FieldType.Date)
    private Data createTime; //创建时间
    @Field(type = FieldType.Boolean)
    private Boolean store = false; //库存信息

    //过滤需要的字段

    //品牌需要字段
    @Field(type = FieldType.Long)
    private Long brandId;
    @Field(type = FieldType.Keyword)
    private String brandName;
    @Field(type = FieldType.Keyword)
    private String logo;

    ///分类需要的字段
    @Field(type = FieldType.Long)
    private Long categoryId;
    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Nested) //嵌套字段
    private List<SearchAttrValue> searchAttrs;

}

package com.atguigu.gmall.search.service;


import com.atguigu.gmall.search.pojo.SearchParamVo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Service
public class SearchService {

    @Autowired
    private RestHighLevelClient highLevelClient;

    public void search(SearchParamVo searchParamVo) {


        try {
            //索引库,搜索条件
            SearchRequest searchRequest = new SearchRequest(new String[]{"goods"}, buildDsl(searchParamVo));
            this.highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private SearchSourceBuilder buildDsl(SearchParamVo searchParamVo) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        //1.构建查询及过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        searchSourceBuilder.query(boolQueryBuilder);
        String keyword = searchParamVo.getKeyword();
        if (StringUtils.isBlank(keyword)) {

            return searchSourceBuilder;
        }
        //1.1.构建匹配查询
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword).operator(Operator.AND));

        //1.2.构建匹配条件

        //1.2.1.构建品牌过滤
        List<Long> brandId = searchParamVo.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandId));
        }

        //1.2.2.构建分类过滤
        List<Long> categoryId = searchParamVo.getCid();
        if(!CollectionUtils.isEmpty(categoryId)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",categoryId));
        }

        //1.2.3.构建价格区间过滤
        Double priceTo  = searchParamVo.getPriceTo();
        Double priceFrom = searchParamVo.getPriceFrom();

        if(priceFrom != null && priceTo != null){

            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            boolQueryBuilder.filter(rangeQuery);

            if (priceFrom != null){
                rangeQuery.gte(priceFrom);
            }
            if(priceTo != null){
                rangeQuery.lte(priceTo);
            }
        }

        //1.2.4.构建是否有货的过滤
        Boolean store = searchParamVo.getStore();
        if(store != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("store",store));
        }

        //1.2.5.构建规格参数的嵌套过滤
        List<String> props = searchParamVo.getProps();
        if(!CollectionUtils.isEmpty(props)){
            props.forEach(prop ->{
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                String[] attrs = StringUtils.split(prop, ":");
                if(attrs != null &&  attrs.length == 2)
                    boolQuery.must(QueryBuilders.termQuery("searchAttrs.attrId",attrs[0]));
                String attrValue = attrs[1];
                String[] attrValues = StringUtils.split(attrValue,"-");
                boolQuery.must(QueryBuilders.termsQuery("searchAttrs.attrValue",attrValues));
                boolQueryBuilder.filter(QueryBuilders.nestedQuery("searchAttrs",null, ScoreMode.None));
            });
        }


        //2.构建排序条件

        //3.构建分页条件

        //4.构建高亮条件

        //5.构建聚合插叙条件
        //5.1构建品牌聚合

        //5.2构建分类聚合

        //5.3构建规格参数的聚合
        return searchSourceBuilder;
    }
}

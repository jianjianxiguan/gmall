package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.mapper.AttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuAttrValueMapper;
import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


@Service("spuAttrValueService")
public class SpuAttrValueServiceImpl extends ServiceImpl<SpuAttrValueMapper, SpuAttrValueEntity> implements SpuAttrValueService {

    @Autowired
    private AttrMapper attrMapper;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuAttrValueEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuAttrValueEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<SpuAttrValueEntity> querySearchSpuAttrValueBySpuIdAndCid(Long spuId, Long cid) {

        //第一步: 先根据pms_attr表查询searchType为1的数据,查询参数cid,searchType为1,获得search为1的字段表.
        List<AttrEntity> attrEntities = this.attrMapper.selectList(new QueryWrapper<AttrEntity>().eq("search_type", 1).eq("category_id", cid));
        //在该集合中过滤出searchType的Id,作为新集合.
        if (CollectionUtils.isEmpty(attrEntities)) {
            return null;
        }
        Stream<Long> attrIds = attrEntities.stream().map(AttrEntity::getId);
        //第二步: 用第一步查询的值,过滤出pms_spu_attr_value表中attr_id为searchType类型的数据;
        List<SpuAttrValueEntity> spuAttrValueEntities = this.list(new QueryWrapper<SpuAttrValueEntity>().eq("spu_id", spuId).in("attr_id", attrIds));

        Iterator<SpuAttrValueEntity> itr = spuAttrValueEntities.iterator();// Iterator声明对象，list.iterator返回Iterator对象
        while (itr.hasNext()) {// 判断是否有下一个数据
            System.out.println(itr.next());// 遍历输出
        }
        return spuAttrValueEntities;


    }

}
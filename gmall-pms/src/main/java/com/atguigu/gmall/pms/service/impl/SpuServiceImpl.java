package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.entity.vo.SkuVo;
import com.atguigu.gmall.pms.entity.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.entity.vo.SpuVo;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescMapper descMapper;

    @Autowired
    private SpuAttrValueService attrValueService;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private GmallSmsClient smsClient;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuInfo(PageParamVo pageParamVo, Long categoryId) {

        //封装查询条件
        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();
        //如果分类id不为0,要根据分类id查,否则查询全部
        if (categoryId != 0) {
            wrapper.eq("category_id", categoryId);
        }

        // 如果用户输入了检索条件，根据检索条件查
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(t -> t.like("name", key).or().like("id", key));
        }

        return new PageResultVo(this.page(pageParamVo.getPage(), wrapper));
    }

    @GlobalTransactional
    @Override
    public void bigSave(SpuVo spu) {        //1保存Spu相关信息
        //1.1 保存spu表
        spu.setCreateTime(new Date());
        spu.setUpdateTime(spu.getCreateTime());
        this.save(spu);
        Long spuId = spu.getId();
        //1.2保存spu_desc表
        List<String> spuImages = spu.getSpuImages();
        if (!CollectionUtils.isEmpty(spuImages)) {
            SpuDescEntity spuDescEntity = new SpuDescEntity();
            spuDescEntity.setSpuId(spuId);
            spuDescEntity.setDecript(StringUtils.join(spuImages, ","));
            this.descMapper.insert(spuDescEntity);
        }
        //1.3保存spu_attr_value表
        List<SpuAttrValueVo> baseAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueVO -> {
                spuAttrValueVO.setSpuId(spuId);
                spuAttrValueVO.setSort(0);
                return spuAttrValueVO;
            }).collect(Collectors.toList());
            this.attrValueService.saveBatch(spuAttrValueEntities);
        }

        //2保存sku相关信息
        List<SkuVo> skus = spu.getSkus();
        if(CollectionUtils.isEmpty(skus)){
            return;
        }

       skus.forEach(skuVo -> {
           //2.1保存sku表

           skuVo.setSpuId(spuId);
           skuVo.setBrandId(spu.getBrandId());
           skuVo.setCatagoryId(spu.getCategoryId());
           List<String> images = skuVo.getImages();
           if(!CollectionUtils.isEmpty(images)){
               skuVo.setDefaultImage(StringUtils.isNotBlank(skuVo.getDefaultImage()) ? skuVo.getDefaultImage():images.get(0));
           }
           this.skuMapper.insert(skuVo);
           Long skuId = skuVo.getId();
           //2.2保存sku图片表
           if(!CollectionUtils.isEmpty(images)){
               this.skuImagesService.saveBatch(images.stream().map(image ->{
                   SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setUrl(image);
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(image,skuVo.getDefaultImage()) ? 1 : 0);
                   return skuImagesEntity;
               } ).collect(Collectors.toList()));
           }
           //2.3保存sku_attr_value表
           List<SkuAttrValueEntity> saleAttrs =  skuVo.getSaleAttrs();

           if(!CollectionUtils.isEmpty(saleAttrs)){
               saleAttrs.forEach(skuAttrValueEntity -> skuAttrValueEntity.setSkuId(skuId));
               this.skuAttrValueService.saveBatch(saleAttrs);
           }
           //3保存sku的营销信息
           SkuSaleVo skuSaleVo = new SkuSaleVo();
           BeanUtils.copyProperties(skuVo,skuSaleVo);
           skuSaleVo.setSkuId(skuVo.getId());
           this.smsClient.saveSales(skuSaleVo);

       });

    }
}


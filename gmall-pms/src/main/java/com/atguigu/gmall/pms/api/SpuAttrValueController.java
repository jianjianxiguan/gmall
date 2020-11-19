package com.atguigu.gmall.pms.api;

import java.util.List;

import com.atguigu.gmall.pms.entity.SkuAttrValueEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.common.bean.PageParamVo;

/**
 * spu属性值
 *
 * @author zl
 * @email fengge@atguigu.com
 * @date 2020-10-28 16:26:42
 */
@Api(tags = "spu属性值 管理")
@RestController
@RequestMapping("pms/spuattrvalue")
public class SpuAttrValueController {

    @Autowired
    private SpuAttrValueService spuAttrValueService;


    //返回值是当前的pms_sku_attr_value表中集合
    //专门为es数据导入提供
    @GetMapping("search/attr/{spuId}")
    public ResponseVo<List<SpuAttrValueEntity>> querySearchSpuAttrValueBySpuIdAndCid(
            @PathVariable("spuId")Long spuId,
            @RequestParam("cid")Long cid
    ){
        List<SpuAttrValueEntity> spuAttrValueEntities = this.spuAttrValueService.querySearchSpuAttrValueBySpuIdAndCid(spuId,cid);
        return ResponseVo.ok(spuAttrValueEntities);
    }

    /**
     * 列表
     */
    @GetMapping
    @ApiOperation("分页查询")
    public ResponseVo<PageResultVo> querySpuAttrValueByPage(PageParamVo paramVo){
        PageResultVo pageResultVo = spuAttrValueService.queryPage(paramVo);

        return ResponseVo.ok(pageResultVo);
    }


    /**
     * 信息
     */
    @GetMapping("{id}")
    @ApiOperation("详情查询")
    public ResponseVo<SpuAttrValueEntity> querySpuAttrValueById(@PathVariable("id") Long id){
		SpuAttrValueEntity spuAttrValue = spuAttrValueService.getById(id);

        return ResponseVo.ok(spuAttrValue);
    }

    /**
     * 保存
     */
    @PostMapping
    @ApiOperation("保存")
    public ResponseVo<Object> save(@RequestBody SpuAttrValueEntity spuAttrValue){
		spuAttrValueService.save(spuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperation("修改")
    public ResponseVo update(@RequestBody SpuAttrValueEntity spuAttrValue){
		spuAttrValueService.updateById(spuAttrValue);

        return ResponseVo.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除")
    public ResponseVo delete(@RequestBody List<Long> ids){
		spuAttrValueService.removeByIds(ids);

        return ResponseVo.ok();
    }

}

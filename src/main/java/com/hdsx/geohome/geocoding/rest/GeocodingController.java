package com.hdsx.geohome.geocoding.rest;


import com.hdsx.geohome.geocoding.api.IndexDao;
import com.hdsx.geohome.geocoding.parameter.ModelParameter;
import com.hdsx.geohome.geocoding.service.LayerCache;
import com.hdsx.geohome.geocoding.utile.StringUtile;
import com.hdsx.geohome.geocoding.vo.*;
import com.hdsx.toolkit.jts.JTSTools;

import com.vividsolutions.jts.io.WKTReader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by jzh on 2017/7/4.
 */
@RestController
@RequestMapping(value = "geocoding")
@Api(description = "周边搜索")
//@CacheConfig(cacheNames = "cacheTest")
public class GeocodingController {

    private final static Logger logger = LoggerFactory.getLogger(GeocodingController.class);

    @Autowired
    private IndexDao indexDao;

    @Autowired
    private LayerCache layerCache;
    
    @ApiOperation("circle")
    @RequestMapping(value = "/circle", method = {RequestMethod.GET}, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "keywords", dataType = "String",value = "输入关键字 例如：北京"),
            @ApiImplicitParam(paramType = "query", name = "tables", dataType = "String", value = "查询的图层：例如：桥梁"),
            @ApiImplicitParam(paramType = "query", name = "field", dataType = "String",value = "查询的字段：例如：name"),
            @ApiImplicitParam(paramType = "query", name = "center", dataType = "String",value = "WKT 例如：POINT(120.620055 32.361612)"),
            @ApiImplicitParam(paramType = "query", name = "distance", dataType = "String",value = "查询范围默认0.5公里"),
            @ApiImplicitParam(paramType = "query", name = "current", dataType = "String",value = "当前页码"),
            @ApiImplicitParam(paramType = "query", name = "limit", dataType = "String",value = "每页条数，默认为10")})
    @ResponseBody
   //@Cacheable
    public QueryResult cicle(   @RequestParam(value = "keywords",required = false) String keywords,
                                @RequestParam(value = "tables",required = false)   String tables,
                                @RequestParam(value = "field",required = false)    String field,
                                @RequestParam(value = "center",required = false)   String center,
                                @RequestParam(value = "distance",required = false,defaultValue = "0.5") double distance,      
                                @RequestParam(value = "current",required = false,defaultValue = "1")  int current,
                                @RequestParam(value = "limit",required = false,defaultValue = "10")    int limit) {
        try{
            ModelParameter modelParameter = new ModelParameter();
            modelParameter.setKeywords(keywords);
            modelParameter.setCurrent(current);
            modelParameter.setLimit(limit);
            if(!StringUtils.isEmpty(center)){
                modelParameter.setGeometry(JTSTools.getInstance().toGeometry(center));
                modelParameter.setDistance(distance);
            }
            if(!StringUtils.isEmpty(tables)){
                modelParameter.setTables(tables.split(","));
            }
            modelParameter.setField(field);
            return indexDao.search(modelParameter, DIRECTORYTYPE.FILE);
        }catch (Exception e){
            logger.error("周边搜索查询失败:{}", e.getMessage(), e);
        }
        return null;
    }

    @ApiOperation("fulltext")
    @RequestMapping(value = "/fulltext", method = {RequestMethod.GET}, produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "keywords", dataType = "String",value = "输入关键字 例如：北京"),
            @ApiImplicitParam(paramType = "query", name = "tables", dataType = "String", value = "查询的图层：例如：桥梁"),
            @ApiImplicitParam(paramType = "query", name = "unit", dataType = "String",value = "查询数据范围（根据管辖单位）"),
            @ApiImplicitParam(paramType = "query", name = "current", dataType = "String",value = "当前页码"),
            @ApiImplicitParam(paramType = "query", name = "limit", dataType = "String",value = "每页条数，默认为10")})
    @ResponseBody
    public QueryResult fulltext(   @RequestParam(value = "keywords",required = false) String keywords,
                                @RequestParam(value = "tables",required = false)   String tables,
                                @RequestParam(value = "unit",required = false)    String unit,
                                @RequestParam(value = "current",required = false,defaultValue = "1")  int current,
                                @RequestParam(value = "limit",required = false,defaultValue = "10")    int limit) {
        try{
            ModelParameter modelParameter = new ModelParameter();
            modelParameter.setKeywords(keywords);
            modelParameter.setCurrent(current);
            modelParameter.setLimit(limit);
            if(!StringUtils.isEmpty(tables)){
                modelParameter.setTables(tables.split(","));
            }
            return indexDao.search(modelParameter, DIRECTORYTYPE.FILE);
        }catch (Exception e){
            logger.error("周边搜索查询失败:{}", e.getMessage(), e);
        }
        return null;
    }

    @ApiOperation("基础要素")
    @RequestMapping(value = "/layers", method = {RequestMethod.GET}, produces = "application/json")
    @ResponseBody
    public List<Layer> layers() {
        return layerCache.LayerCache();
    }

    @ApiOperation("pushdata")
    @RequestMapping(value = "/pushdata", method = {RequestMethod.POST}, produces = "application/json")
    public boolean pushData( @RequestParam(value = "id",required = false) String id,
                             @RequestParam(value = "code",required = false) String code,
                             @RequestParam(value = "name",required = true) String name,
                             @RequestParam(value = "address",required = true) String address,
                             @RequestParam(value = "type",required = true) String type,
                             @RequestParam(value = "admincode",required = false) String admincode,
                             @RequestParam(value = "longtitude",required = true) double longtitude,
                             @RequestParam(value = "latitude",required = true) double latitude,
                             @RequestParam(value = "forder",required = false,defaultValue = "1") int forder){
        
        return indexDao.save(id,code, name, address, type, admincode, longtitude, latitude, forder);
    }

    @ApiOperation("单条插入")
    @RequestMapping(value = "/save", method = {RequestMethod.POST}, produces = "application/json")
    public ApiResult save(@RequestBody Map<String,Object> map){
        if(StringUtile.isEmpty(map.get("lat")) || StringUtile.isEmpty(map.get("lon"))){
            return new ApiResult(ApiResult.ERR_CODE,"经纬度参数不能为空",false);
        }
        if(StringUtile.isEmpty(map.get("table"))){
            return new ApiResult(ApiResult.ERR_CODE,"图层参数不能为空",false);
        }
        ApiResult apiResult = null;
        List<Map<String,Object>> elements = new ArrayList<>();
        try{
        	map.put("geometry", new WKTReader().read("POINT ("+map.get("lon")+" "+map.get("lat")+")"));
			map.put("geometryType", "Point");

            elements.add(map);
            indexDao.save(elements,"POI",DIRECTORYTYPE.FILE);
            apiResult = new ApiResult(ApiResult.SUC_CODE,"",true);
        }catch (Exception e){
            apiResult = new ApiResult(ApiResult.ERR_CODE,e.getMessage(),false);
            e.printStackTrace();
        }
        return apiResult;
    }

    @ApiOperation("批量插入")
    @RequestMapping(value = "/batchInsert", method = {RequestMethod.POST}, produces = "application/json")
    public ApiResult batchInsert(@RequestBody List<Map<String,Object>> eles){
        if(eles == null){
            return new ApiResult(ApiResult.ERR_CODE,"数据为空",false);
        }
        ApiResult apiResult = null;
        try{
            for(int i = 0 ;i < eles.size();i++){
            	Map<String,Object> ele= eles.get(i);
                if(StringUtile.isEmpty(ele.get("lat")) || StringUtile.isEmpty(ele.get("lon"))){
                    return new ApiResult(ApiResult.ERR_CODE,"经纬度参数不能为空",false);
                }
                if(StringUtile.isEmpty(ele.get("table"))){
                    return new ApiResult(ApiResult.ERR_CODE,"图层参数不能为空",false);
                }
                ele.put("geometry", new WKTReader().read("POINT ("+ele.get("lon")+" "+ele.get("lat")+")"));
//              
                ele.put("geometryType", "Point");
            }
            indexDao.save(eles,"POI",DIRECTORYTYPE.FILE);
            apiResult = new ApiResult(ApiResult.SUC_CODE,"",true);
        }catch (Exception e){
            apiResult = new ApiResult(ApiResult.ERR_CODE,e.getMessage(),false);
            e.printStackTrace();
        }
        return apiResult;
    }

    @ApiOperation("清空图层")
    @RequestMapping(value = "/deletetable", method = {RequestMethod.POST}, produces = "application/json")
    public ApiResult deletetable(@RequestBody String table){
        ApiResult apiResult = null;
         try{
            indexDao.deleteTable(table,DIRECTORYTYPE.FILE);
            apiResult = new ApiResult(ApiResult.SUC_CODE,"",true);
        }catch (Exception e){
            apiResult = new ApiResult(ApiResult.ERR_CODE,e.getMessage(),false);
            e.printStackTrace();
        }
        return apiResult;
    }

    @ApiOperation("按照条件删除")
    @RequestMapping(value = "/delete", method = {RequestMethod.POST}, produces = "application/json")
    public ApiResult delete(@RequestBody String ids){
        ApiResult apiResult = null;
        try{
            String[] idArr = ids.split(",");
            indexDao.deleteList(idArr,DIRECTORYTYPE.FILE);
            apiResult = new ApiResult(ApiResult.SUC_CODE,"",true);
        }catch (Exception e){
            apiResult = new ApiResult(ApiResult.ERR_CODE,e.getMessage(),false);
            e.printStackTrace();
        }
        return apiResult;
    }
    

    @ApiOperation("自动提示")
    @RequestMapping(value = "/auto", method = {RequestMethod.GET}, produces = "application/json")
    public ApiResult auto(@RequestParam(value = "keywords",required = true) String keywords,
                          @RequestParam(value = "current",required = false,defaultValue = "1") int current,
                          @RequestParam(value = "limit",required = false,defaultValue = "10") int limit){
        ApiResult apiResult = new ApiResult();
        apiResult.setSuccessed(true);
        apiResult.setErrMsg("");
        apiResult.setObject(fulltext(keywords,null,null,current,limit));
        return apiResult;
    }


}

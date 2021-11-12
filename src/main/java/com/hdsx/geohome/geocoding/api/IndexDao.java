package com.hdsx.geohome.geocoding.api;

import com.hdsx.geohome.geocoding.parameter.ModelParameter;
import com.hdsx.geohome.geocoding.vo.DIRECTORYTYPE;

import com.hdsx.geohome.geocoding.vo.QueryResult;

import java.io.IOException;

import java.util.List;
import java.util.Map;

/**
 * Created by jzh on 2017/6/24.
 */
public interface IndexDao {

    void createIndex(String indexName)throws IOException;

    void save(List<Map<String,Object>> elementList, String table, DIRECTORYTYPE directorytype) throws IOException;

    void delete(String id, DIRECTORYTYPE directorytype);

    void deleteList(String[] ids,DIRECTORYTYPE directorytype);

    void deleteTable(String table,DIRECTORYTYPE directorytype);
    
    void deleteAll(DIRECTORYTYPE directorytype);

    void update(Map<String,Object> map, DIRECTORYTYPE directorytype);

    QueryResult search(ModelParameter parameter, DIRECTORYTYPE directorytype);

    boolean save(String id,String code,String name,String address,String type,String admincode,double longtitude,double latitude,int forder);
}

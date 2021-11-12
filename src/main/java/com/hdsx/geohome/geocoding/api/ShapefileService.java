package com.hdsx.geohome.geocoding.api;



import java.util.List;
import java.util.Map;

/**
 * Created by jzh on 2016/9/22.
 */
public interface ShapefileService {

    List<Map<String,Object>> read(String filepath,String code);
}

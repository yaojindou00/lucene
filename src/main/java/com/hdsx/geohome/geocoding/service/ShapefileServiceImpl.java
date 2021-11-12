package com.hdsx.geohome.geocoding.service;


import com.hdsx.geohome.geocoding.api.IndexDao;
import com.hdsx.geohome.geocoding.api.ShapefileService;

import com.hdsx.toolkit.jts.JTSTools;
import com.hdsx.toolkit.number.NumberUtile;
import com.hdsx.toolkit.uuid.UUIDGenerator;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.DataStore;
import org.geotools.data.shapefile.ShapefileDirectoryFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jzh on 2016/9/22.
 */
@Service
public class ShapefileServiceImpl implements ShapefileService {

    private static Logger logger = LoggerFactory.getLogger(ShapefileServiceImpl.class);

    @Autowired
    private TaskExecutor taskExecutor;

    public DataStore getDataStore(String filepath,String code){
        DataStore store = null;
        try{
            Map<String,Serializable> params = new HashMap<>();
            File file = new File(filepath);
            file.setReadOnly();
            params.put( "url", file.toURI().toURL() );
            params.put( "create spatial index", true );
            params.put( "memory mapped buffer", false );
            params.put( "charset", code );
            ShapefileDirectoryFactory factory = new ShapefileDirectoryFactory();
            store = factory.createDataStore(params);
        }catch (Exception e){
            logger.error("DataStore 加载失败{}",e.getCause());
            e.printStackTrace();
        }
        return store;
    }


    @Override
    public List<Map<String,Object>> read(String filepath,String code) {
        DataStore store = getDataStore(filepath,code);
        List<Map<String,Object>> elementList = new ArrayList<>();
        try{
            String[] typeNames = store.getTypeNames();
            IndexDao indexDao = new IndexDaoImpl();
            for(int i = 0 ; i < typeNames.length ;i++){
                taskExecutor.execute(new IndexCallable(store,typeNames[i],indexDao));
            }
        }catch (Exception e){
            logger.error("shp资源加载失败{}",e.getCause());
            e.printStackTrace();
        }
        finally {
            try{
                if(store != null){
                    store.dispose();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return elementList;
    }

   
}

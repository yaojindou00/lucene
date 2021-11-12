package com.hdsx.geohome.geocoding.service;


import com.hdsx.geohome.geocoding.utile.XParser;
import com.hdsx.geohome.geocoding.vo.Layer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.List;


/**
 * Created by jzh on 2017/7/25.
 */
@Service
public class LayerCache  {

    List<Layer> layers;
    
    @Cacheable("cacheTest")
    public List<Layer> LayerCache(){
        try {
            Resource resource = new ClassPathResource("features-conf.xml");
            return init(new XParser(resource.getInputStream()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Layer> init(XParser xml){
        List<Layer> layers = xml.node2List(new Layer(),"/layer");
        return layers;
    }
    
}

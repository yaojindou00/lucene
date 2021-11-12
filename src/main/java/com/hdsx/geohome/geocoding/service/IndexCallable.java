package com.hdsx.geohome.geocoding.service;


import com.hdsx.geohome.geocoding.api.IndexDao;
import com.hdsx.geohome.geocoding.vo.DIRECTORYTYPE;

import com.hdsx.toolkit.uuid.UUIDGenerator;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.data.DataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jzh on 2016/10/22.
 */
public class IndexCallable implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(IndexCallable.class);
    private IndexDao indexDao;
    private String typeName;
    private DataStore store;
    private SimpleFeatureSource featureSource;
    private SimpleFeatureCollection featureCollection;

    public IndexCallable(DataStore store, String typeName, IndexDao indexDao)
    {
        this.store = store;
        this.typeName = typeName;
        this.indexDao = indexDao;
        try {
            this.featureSource = store.getFeatureSource(typeName);
            this.featureCollection = this.featureSource.getFeatures();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        SimpleFeatureIterator iterator = this.featureCollection.features();
        List<Map<String,Object>> elements = new ArrayList<Map<String,Object>>(this.featureCollection.size());

        WKTReader wktReader = new WKTReader();
        while (iterator.hasNext()) {
        	Map<String,Object>  element = new HashMap<String,Object> ();
            SimpleFeature feature = iterator.next();
            Collection<Property> properties = feature.getProperties();
            for (Property property : properties) {
          
	            if(property.getName()!=null) {
	            	element.put(property.getName().toString().toLowerCase(), property.getValue());
	            }
			}
            Object table = feature.getAttribute("table");
            if (table != null) {
            	element.put("table",table.toString());
            }       
            Object oid = feature.getAttribute("OBJECTID");
            if(oid!=null) {
            	element.put("id",oid.toString());
            }else{
            	element.put("id",UUIDGenerator.randomUUID());
            }
            try {
                if ((feature.getDefaultGeometry() instanceof Point)) {
                    Geometry geometry = wktReader.read(feature.getDefaultGeometryProperty().getValue().toString());
                    if (geometry != null)
                        element.put("geometry", geometry);
                }
            }
            catch (ParseException e) {
                e.printStackTrace();
            }           
            elements.add(element);
        }
        try {
            this.indexDao.save(elements, this.typeName, DIRECTORYTYPE.FILE);
        } catch (IOException e) {
            logger.info("{}加载失败，数据总计:{}", this.typeName);
            e.printStackTrace();
        }
        logger.info("{}加载完毕，数据总计:{}", this.typeName, Integer.valueOf(this.featureCollection.size()));
    }
}

package com.hdsx.geohome.geocoding.utile;

import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.prefix.RecursivePrefixTreeStrategy;
import org.apache.lucene.spatial.prefix.tree.GeohashPrefixTree;
import org.locationtech.spatial4j.context.SpatialContext;

/**
 * Created by jzh on 2016/11/29.
 */
public class SpatialUtils {

    public static  final String SHAPE_FIELD= "shape";
    private SpatialUtils(){

    }
    public static SpatialStrategy createStrategy(){
        GeohashPrefixTree geohashPrefixTree = new GeohashPrefixTree(SpatialContext.GEO, 11);
        return new RecursivePrefixTreeStrategy(geohashPrefixTree, SHAPE_FIELD);
    }
}

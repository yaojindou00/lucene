package com.hdsx.toolkit.jts;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;


/**
 * Created by jzh on 2016/11/9.
 */
public class JTSTools {

    private WKTReader wktReader;

    private WKTWriter wktWriter;

    public static JTSTools instance ;

    GeometryFactory factory = new GeometryFactory(new DefaultCoordinateSequenceFactory());

    private JTSTools(){
        wktReader = new WKTReader();
        wktWriter = new WKTWriter();
    }
    /**
     * create a point
     * @return
     */
    public Point createPoint(double lon,double lat){
        Coordinate coord = new Coordinate(lon, lat);
        return  factory.createPoint( coord );
    }


    public String toWKT(Geometry geometry){
      return wktWriter.write(geometry);
    }

    public Geometry toGeometry(String wkt) {
        Geometry geometry = null;
        try {
            geometry = wktReader.read(wkt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return geometry;
    }

    public static synchronized JTSTools getInstance() {
        if(instance == null){
            instance = new JTSTools();
        }
        return instance;
    }

}

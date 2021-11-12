package com.hdsx.geohome.geocoding.vo;

import com.vividsolutions.jts.geom.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jzh on 2017/6/20.
 */
public class JtsUtile {


    public static final String GEOMETRY_TYPE_POINT = "Point";
    public static final String GEOMETRY_TYPE_LINE = "LineString";
    public static final String GEOMETRY_TYPE_MUTILLINE = "MultiLineString";
    public static final String GEOMETRY_TYPE_GEOCOLLECTION = "GeometryCollection";
    public static final String ATTRIBUTE_REFERRENCE = "spatialReference";
    public static final String ATTRIBUTE_WKID = "wkid";
    public static final String ATTRIBUTE_PATHS = "paths";

    public JtsUtile() {
    }
    public static Object geometryToJSON(Geometry geometry) {
        if(geometry == null) {
            return null;
        } else if(!geometry.isEmpty() && geometry.isValid()) {
            return "Point".equalsIgnoreCase(geometry.getGeometryType())?pointToJSON(geometry):("LineString".equalsIgnoreCase(geometry.getGeometryType())?lineToJSON(geometry):("MultiLineString".equalsIgnoreCase(geometry.getGeometryType())?mutilLineToJSON(geometry):("GeometryCollection".equalsIgnoreCase(geometry.getGeometryType())?geometryCollectionToJSON(geometry):otherToJSON(geometry))));
        } else {
            return geometry.toText();
        }
    }

    public static Object getReference(Geometry geometry) {
        HashMap map = new HashMap();
        map.put("wkid", Integer.valueOf(geometry.getSRID() == 0?4326:geometry.getSRID()));
        return map;
    }

    public static Object pointToJSON(Geometry geometry) {
        Point point = (Point)geometry;
        HashMap map = new HashMap();
        map.put("x", Double.valueOf(point.getX()));
        map.put("y", Double.valueOf(point.getY()));
        map.put("spatialReference", getReference(geometry));
        return map;
    }

    public static Object lineToJSON(Geometry geometry) {
        ArrayList parts = new ArrayList();
        ArrayList part = new ArrayList();
        LineString line = (LineString)geometry;
        Coordinate[] coordinates = line.getCoordinates();
        Coordinate[] map = coordinates;
        int len$ = coordinates.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Coordinate coordinate = map[i$];
            part.add(new double[]{coordinate.x, coordinate.y});
        }

        parts.add(part);
        HashMap var9 = new HashMap();
        var9.put("paths", parts);
        var9.put("spatialReference", getReference(geometry));
        return var9;
    }

    public static void addLinePoints(LineString line, ArrayList list) {
        Coordinate[] coordinates = line.getCoordinates();
        ArrayList part = new ArrayList();
        Coordinate[] arr$ = coordinates;
        int len$ = coordinates.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Coordinate coordinate = arr$[i$];
            part.add(new double[]{coordinate.x, coordinate.y});
        }

        list.add(part);
    }

    public static void addMutilLinePoints(MultiLineString mutilLine, ArrayList list) {
        for(int i = 0; i < mutilLine.getNumGeometries(); ++i) {
            Geometry geo = mutilLine.getGeometryN(i);
            if("LineString".equalsIgnoreCase(geo.getGeometryType())) {
                addLinePoints((LineString)geo, list);
            } else if("MultiLineString".equalsIgnoreCase(geo.getGeometryType())) {
                addMutilLinePoints((MultiLineString)geo, list);
            }
        }

    }

    public static Object mutilLineToJSON(Geometry geometry) {
        ArrayList parts = new ArrayList();
        addMutilLinePoints((MultiLineString)geometry, parts);
        HashMap map = new HashMap();
        map.put("paths", parts);
        map.put("spatialReference", getReference(geometry));
        return map;
    }

    public static Object geometryCollectionToJSON(Geometry geometry) {
        GeometryCollection collection = (GeometryCollection)geometry;
        Coordinate[] coordinates = collection.getCoordinates();
        ArrayList parts = new ArrayList();
        ArrayList part = new ArrayList();
        Coordinate[] map = coordinates;
        int len$ = coordinates.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Coordinate coordinate = map[i$];
            part.add(new double[]{coordinate.x, coordinate.y});
        }

        parts.add(part);
        HashMap var9 = new HashMap();
        var9.put("paths", parts);
        var9.put("spatialReference", getReference(geometry));
        return var9;
    }

    public static Object otherToJSON(Geometry geometry) {
        Coordinate[] coordinates = geometry.getCoordinates();
        ArrayList parts = new ArrayList();
        ArrayList part = new ArrayList();
        Coordinate[] map = coordinates;
        int len$ = coordinates.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Coordinate coordinate = map[i$];
            part.add(new double[]{coordinate.x, coordinate.y});
        }

        parts.add(part);
        HashMap var8 = new HashMap();
        var8.put("paths", parts);
        var8.put("spatialReference", getReference(geometry));
        return var8;
    }

    public static Object polygonToJSON(Geometry geometry) {
        Polygon polygon = (Polygon)geometry;
        int num = polygon.getNumGeometries();

        for(int i = 0; i < num; ++i) {
        }

        return geometry;
    }

    public static double meterToRadian(double meters) {
        double radian = meters * 8.983152841195214E-6D;
        return radian;
    }
}

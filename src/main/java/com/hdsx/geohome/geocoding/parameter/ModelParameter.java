package com.hdsx.geohome.geocoding.parameter;

import com.vividsolutions.jts.geom.Geometry;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by jzh on 2017/6/24.
 */
public class ModelParameter implements Serializable {

    private static final long serialVersionUID = -9137014122673726616L;

    private String keywords;

    private String field;

    private String[] tables;

    private int current;

    private int limit;

    private double distance = 5000;

    private Geometry geometry;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }



    @Override
    public String toString() {
        return "ModelParameter{" +
                "keywords='" + keywords + '\'' +
                ", field=" + field +
                ", tables=" + Arrays.toString(tables) +
                ", current=" + current +
                ", limit=" + limit +
                '}';
    }
}

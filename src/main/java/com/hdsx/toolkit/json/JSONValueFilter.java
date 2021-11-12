package com.hdsx.toolkit.json;


import com.alibaba.fastjson.serializer.ValueFilter;
import com.hdsx.toolkit.jts.JtsUtile;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

/**
 * 

* @ClassName: JSONValueFilter 

* @Description: Geometry格式转换

* @author <a href="mailto:meijinbo@qq.com">kimbo</a>  

* @date 2015年2月11日 下午1:39:47 

*
 */
public class JSONValueFilter implements ValueFilter {
	/**
	 * 空间信息的描述方式 
	 * 1：WKT 
	 * 2：ARCGIS JSON格式
	 * {"x": -122.65, "y": 45.53, "spatialReference": {"wkid": 4326 } }
	 * var point = new Point( {"x": -122.65, "y": 45.53, "spatialReference": {"wkid": 4326 } });
	 * var polylineJson = {
    "paths":[[[-122.68,45.53], [-122.58,45.55],
    [-122.57,45.58],[-122.53,45.6]]],
    "spatialReference":{"wkid":4326}
  };
	 */
	private int geoFormatType = 1;
	
	public JSONValueFilter(int geometryType){
		this.geoFormatType=geometryType;
	}
	public Object process(Object source, String name, Object value) {
		if(value!=null&&isGeometry(value)){
			if(this.geoFormatType==1){
				Geometry geo=(Geometry)value;
				return geo.toText();
			}else{
				//非WKT描述方式
				value = JtsUtile.geometryToJSON((Geometry)value);
				return value;
			}
		}
		return value;
	}
	private boolean isGeometry(Object value){
		return value.getClass().getSuperclass().getName().equals(Geometry.class.getName())
			  ||Geometry.class.isAssignableFrom(value.getClass())
			  ||value.getClass().getSuperclass().getName().equals(GeometryCollection.class.getName())
			  ||GeometryCollection.class.isAssignableFrom(value.getClass());
	}
}

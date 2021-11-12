package com.hdsx.geohome.geocoding.utile;
/**
 *

 /*
 doc.add(new SortedDocValuesField("name", new BytesRef(element.getName()==null?"":element.getName())););
 doc.add(new SortedDocValuesField("code", new BytesRef(element.getCode()==null?"":element.getCode())));
 doc.add(new SortedDocValuesField("describe", new BytesRef(element.getDescribe()==null?"":element.getDescribe())));
 */




import com.hdsx.toolkit.jts.JTSTools;
import com.vividsolutions.jts.geom.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.spatial.SpatialStrategy;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.context.SpatialContextFactory;
import org.locationtech.spatial4j.io.WKTReader;
import org.locationtech.spatial4j.shape.Shape;


/**
 * Created by jingzh on 2016/9/13.
 * http://www.fengxiaochuang.com/?p=160
 */
public class DocumentUtils {

    //默认分词 TextField
    //默认不分词 StringField
    //数字排序专用 NumericDocValuesField

    private DocumentUtils(){}

    public static Document element2Document(Map<String,Object> element){
        Document doc=new Document();//索引文档
        Set<String> keySet = element.keySet();
        for (String key : keySet) {
        	 doc.add(new StringField(key,element.get(key)==null?"":element.get(key).toString(),StringField.Store.YES));
		}
        doc.add(new StringField("id",element.get("id")==null?"":element.get("id").toString(),StringField.Store.YES));
        ITextField fName = new ITextField("name",element.get("name")==null?"":element.get("name").toString(),StringField.Store.YES);
        doc.add(fName);
        ITextField fCode = new ITextField("code",element.get("code")==null?"":element.get("code").toString(),StringField.Store.YES);  
        doc.add(fCode);
        ITextField fAddress = new ITextField("address",element.get("address")==null?"":element.get("address").toString(),StringField.Store.YES);
        doc.add(fAddress);      
        doc.add(new StringField("table",element.get("table")==null?"":element.get("table").toString(),StringField.Store.YES));

        if(element.get("geometry") != null){
        	if(element.get("geometry").toString().indexOf("POINT")>-1) {
        		
            Point jtsPoint = (Point)element.get("geometry");
            try {
                WKTReader wktReader = new WKTReader(SpatialContext.GEO,new SpatialContextFactory());
                Shape shape =  wktReader.read(jtsPoint.toText());
                SpatialStrategy strategy = SpatialUtils.createStrategy();
                for (Field f : strategy.createIndexableFields(shape)) {
                    doc.add(f);
                }
                doc.add(new StoredField(strategy.getFieldName(), JTSTools.getInstance().toWKT(jtsPoint)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        	}else {
        		System.out.println(element.get("geometry").toString());
        	
        	}
        }
        return doc;
    }

    public static Map<String,Object> document2Element(Document document){
    	Map<String,Object> element=new HashMap<String,Object>();
    	List<IndexableField> fields = document.getFields();
    	for (IndexableField field : fields) {
    		 
            if(field.name().toString().equals("shape")){
            	try{
            		if(document.get("shape")!=null) {
            			
            			element.put("geometry",JTSTools.getInstance().toGeometry(document.get("shape")));
            		}
    	        }catch (Exception e){
    	            e.printStackTrace();
    	        }
            }else if(!(field.name().toString().equals("the_geom"))){
            	
            	element.put(field.name().toString(), document.get(field.name().toString()));
            }
        
		}
    	element.put("id", document.get("id"));
    	element.put("name", document.get("name"));
    	element.put("code", document.get("code"));
    	element.put("table", document.get("table"));
    	element.put("address", document.get("address"));
       
        return  element;
    }


}
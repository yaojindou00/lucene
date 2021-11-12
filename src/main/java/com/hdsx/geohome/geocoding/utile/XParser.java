package com.hdsx.geohome.geocoding.utile;


import com.hdsx.geohome.geocoding.vo.Layer;

import com.hdsx.geohome.geocoding.vo.NodeField;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jzh on 2017/7/20.
 */
public class XParser {

    XPathParser pathParser;

    public XParser(InputStream is){
         this.pathParser = new XPathParser(is);
    }
    public <T> T node2target(T target,String nodeStr) {
        XNode xNode = pathParser.evalNode(nodeStr);
        return  toTarget(target,xNode);
    }

    public <T> T toTarget(T target,XNode xNode){
        Field[] fields = ClassReflect.getDeclaredFields(target);
        for(int i = 0 ,count = fields.length; i < count ;i++ ){
            Field field =  fields[i];
            ClassReflect.makeAccessible(field);
            NodeField nodeField =  field.getAnnotation(NodeField.class);
            if(nodeField == null){
                continue;
            }
            Object value = getNodeValue(nodeField,xNode);
            if(value == null){
                continue;
            }
            ClassReflect.setField(field,target,value);
        }
        return target;
    }

    private Object getNodeValue(NodeField nodeField, XNode node){
        String attribute = nodeField.value();
        Class clas = nodeField.clas();
        if(clas.equals(Integer.class)&&!StringUtile.isEmpty(node.getStringAttribute(attribute))){
            return node.getIntAttribute(attribute);
        }
        if(clas.equals(Double.class)&&!StringUtile.isEmpty(node.getStringAttribute(attribute))){
            return node.getDoubleAttribute(attribute);
        }
        if(clas.equals(Float.class)&&!StringUtile.isEmpty(node.getStringAttribute(attribute))){
            return node.getFloatAttribute(attribute);
        }
        if(clas.equals(String.class)){
            return node.getStringAttribute(attribute);
        }
        return null;
    }

    public  List<XNode> getNodes(String nodeStr,XNode xNode) {
        if(xNode == null){
           return  pathParser.evalNodes(nodeStr);
        }else{
            return xNode.getChildren();
        }
    }

    public  <T> List<T> subNodes2target(T target,XNode xNode) {
        List<XNode> nodes = xNode.getChildren();
        List<T> tList = new ArrayList<>();
        for(int i = 0,count =nodes.size() ; i < count ;i++){
            try {
                tList.add(toTarget((T)target.getClass().newInstance(),nodes.get(i)));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return tList;
    }

    public  <T> List<T> node2List(T target,String nodeStr) {
        XNode node ;
        List<XNode> nodes = pathParser.evalNodes(nodeStr);
        List<T> tList = new ArrayList<>();
        for(int i = 0,len = nodes.size();i < len;i ++){
            node = nodes.get(i);
            tList.add( toTarget(target,node));
        }
        return tList;
    }

    public static void main(String[] args) {
        try {
            Resource resource = new ClassPathResource("features-conf.xml");
            XParser xParser = new XParser(resource.getInputStream());
            List<Layer> layers = xParser.node2List(new Layer(),"/root/layer");
            System.out.println(layers.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

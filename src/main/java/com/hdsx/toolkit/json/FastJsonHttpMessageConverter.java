package com.hdsx.toolkit.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class FastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

  public static final Charset UTF8 = Charset.forName("UTF-8");

  private Charset charset = UTF8;

  private SerializerFeature[] features = new SerializerFeature[0];
  
  public FastJsonHttpMessageConverter() {
    super(new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8));
  }
  
  protected boolean supports(Class<?> clazz)
  {
    return true;
  }
  
  public Charset getCharset()
  {
    return this.charset;
  }
  
  public void setCharset(Charset charset)
  {
    this.charset = charset;
  }
  
  public SerializerFeature[] getFeatures()
  {
    return this.features;
  }
  
  public void setFeatures(SerializerFeature... features)
  {
    this.features = features;
  }
  
  protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage)
    throws IOException, HttpMessageNotReadableException
  {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    
    InputStream in = inputMessage.getBody();
    
    byte[] buf = new byte[1024];
    for (;;)
    {
      int len = in.read(buf);
      if (len == -1) {
        break;
      }
      if (len > 0) {
        baos.write(buf, 0, len);
      }
    }
    byte[] bytes = baos.toByteArray();
    return JSON.parseObject(bytes, 0, bytes.length, this.charset.newDecoder(), clazz);
  }
  private boolean isGeometry(Class<?> clazz){
	  return clazz.getSuperclass().getName().equals(GeometryCollection.class.getName())
			  ||clazz.getSuperclass().getName().equals(Geometry.class.getName())
			  ||GeometryCollection.class.isAssignableFrom(clazz)
			  ||Geometry.class.isAssignableFrom(clazz);
  }
  protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
    throws IOException, HttpMessageNotWritableException
  {
	  OutputStream out = outputMessage.getBody();
	  SerializeWriter jsonOut = new SerializeWriter();
	  JSONSerializer serializer = new JSONSerializer(jsonOut);
	  serializer.getValueFilters().add(new JSONValueFilter(1));
      serializer.write(obj);
      byte[] bytes = jsonOut.toBytes("UTF-8");
      out.write(bytes);
  }
}

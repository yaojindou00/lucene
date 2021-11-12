package com.hdsx.toolkit.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
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

public class GeometryHttpMessageConverter extends AbstractHttpMessageConverter<Geometry>{
	 private SerializerFeature[] features = new SerializerFeature[0];
	public static final Charset UTF8 = Charset.forName("UTF-8");
	private Charset charset = UTF8;
	 public GeometryHttpMessageConverter()
	  {
	    super(new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8));
	  }
	 public Charset getCharset()
	  {
	    return this.charset;
	  }
	  
	  public void setCharset(Charset charset)
	  {
	    this.charset = charset;
	  }
	@Override
	protected boolean supports(Class<?> clazz) {
		return clazz.getSuperclass().getName().equals(GeometryCollection.class.getName())
		  ||clazz.getSuperclass().getName().equals(Geometry.class.getName())
		  ||GeometryCollection.class.isAssignableFrom(clazz)
		  ||Geometry.class.isAssignableFrom(clazz);
	}

	@Override
	protected Geometry readInternal(Class<? extends Geometry> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
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
		    return JSON.parseObject(bytes, 0, bytes.length, this.charset.newDecoder(), clazz, new Feature[0]);
	}

	@Override
	protected void writeInternal(Geometry obj, HttpOutputMessage outputMessage)
		    throws IOException, HttpMessageNotWritableException
  {
	System.out.println("writeInternal:"+obj);
    OutputStream out = outputMessage.getBody();
    String text = JSON.toJSONString(obj, this.features);
    byte[] bytes = text.getBytes(this.charset);
    out.write(bytes);
  }
}

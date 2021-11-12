package com.hdsx.geohome.geocoding.rest;

import com.hdsx.geohome.geocoding.api.IndexDao;
import com.hdsx.geohome.geocoding.api.ShapefileService;
import com.hdsx.geohome.geocoding.vo.DIRECTORYTYPE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by jzh on 2017/7/4.
 */
@Service
@ConfigurationProperties(prefix="lucene")
public class PreloadStartup implements ApplicationListener<ContextRefreshedEvent> {

    private String filepath;

    private String utf8filepath;
    
    private boolean mode;

    @Autowired
    private IndexDao indexDao;

    @Autowired
    private ShapefileService  shapefileService;

    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() != null){
            return;
        }
        new Runnable(){
            @Override
            public void run() {
                if(mode){
                    return;
                    //indexDao.deleteAll(DIRECTORYTYPE.FILE);
                }
                List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
                List<Map<String,Object>> elementList = shapefileService.read(filepath,"GBK");
                List<Map<String,Object>> elementList2 = shapefileService.read(utf8filepath,"UTF-8");
                list.addAll(elementList);
                list.addAll(elementList2);
                try {
                    indexDao.save(list,"POI", DIRECTORYTYPE.FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("数据加载完毕.....");
            }
        }.run();
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getUtf8filepath() {
		return utf8filepath;
	}

	public void setUtf8filepath(String utf8filepath) {
		this.utf8filepath = utf8filepath;
	}

	public boolean isMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }
}


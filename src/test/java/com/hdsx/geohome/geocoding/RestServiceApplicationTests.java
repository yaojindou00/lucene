package com.hdsx.geohome.geocoding;



import com.hdsx.geohome.geocoding.api.IndexDao;
import com.hdsx.geohome.geocoding.api.ShapefileService;
import com.hdsx.geohome.geocoding.parameter.ModelParameter;
import com.hdsx.geohome.geocoding.vo.DIRECTORYTYPE;

import com.hdsx.geohome.geocoding.vo.QueryResult;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
import io.swagger.models.parameters.QueryParameter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GeocodingApplication.class})
public class RestServiceApplicationTests {


	@Autowired
	private IndexDao indexDao;

	@Autowired
	private ShapefileService shapefileService;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	/*@Test
	public void contextQuery() {
		ModelParameter modelParameter = new ModelParameter();
		modelParameter.setKeywords("name");
		try {
			QueryResult queryResult = indexDao.search(modelParameter,DIRECTORYTYPE.FILE);
			System.out.println(queryResult.getCount());
		} catch (Exception exceptionMsg) {
			exceptionMsg.printStackTrace();
		}
	}*/

	@Test
	public void shpService() {
		try {
			List<Map<String,Object>> elements =	shapefileService.read("D:\\gisserver\\lucene\\file","GBK");
			System.out.println("读取数据总资源:"+elements.size());
			int count = 0;
			while(count < 10){
				Thread.sleep(5*1000);
				System.out.println("当前活跃线程："+taskExecutor.getActiveCount());
				count++;
			}
		   } catch (Exception exceptionMsg) {
			exceptionMsg.printStackTrace();
		}
	}

}

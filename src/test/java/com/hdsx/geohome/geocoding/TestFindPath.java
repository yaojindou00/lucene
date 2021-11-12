package com.hdsx.geohome.geocoding;



import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.data.FeatureReader;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.graph.build.GraphGenerator;
import org.geotools.graph.build.basic.BasicGraphGenerator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.BasicLineGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.Node;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

/**
 * Created by jzh on 2017/12/25.
 */
public class TestFindPath {
    
    private Logger logger = LoggerFactory.getLogger(TestFindPath.class);
    
    public void test2() throws ParseException {
        URL url;
        ShapefileDataStore shpDataStore = null;
        FeatureReader<SimpleFeatureType, SimpleFeature> reader = null;
        BasicLineGraphGenerator graphGenerator = new LineStringGraphGenerator(50);
        try {
            url = new File("D:\\HDSXMapData\\JIANGXI\\Network\\network.shp").toURI().toURL();
            shpDataStore = new ShapefileDataStore(url);
            reader = shpDataStore.getFeatureReader();
            while (reader.hasNext()) {
                SimpleFeature feature = reader.next();
                Geometry line = (Geometry) feature.getDefaultGeometry();
                /*LineString geometry = Util.getInstance().getGeometryFactory().createLineString(line.getCoordinates());
                if (geometry.getSRID() == 0){
                    geometry.setSRID(4326);
                }*/
                graphGenerator.add(line);
            }
            Graph graph = graphGenerator.getGraph();
            DijkstraIterator.EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter(){
                @Override
                public double getWeight(Edge edge) {
                    //这个方法返回的值就是权重，这里使用的最简单的线的长度
                    //如果有路况、限速等信息，可以做的更复杂一些
                    SimpleFeature feature = (SimpleFeature)edge.getObject();
                    Geometry geometry = (Geometry)feature.getDefaultGeometry();
                    return geometry.getLength();
                }
            };
            Date startT = new Date();
            //初始化查找器
            Node start = graphGenerator.getNode(new Coordinate(115.82081892938083 ,29.431414429155954));
            Node destination = graphGenerator.getNode(new Coordinate(115.75419402121759 ,28.432040806707235));
            DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder(graph,start,weighter);
            pf.calculate();
            //传入终点，得到最短路径
            Path path = pf.getPath(destination);
            Date end = new Date();
            System.out.println("迪杰斯特拉算法耗时：" +(end.getTime() - startT.getTime()));
            //System.out.println("迪杰斯特拉算法距离："+getPathLength(path));
            System.out.println(destination.getID()+""+start.equals(destination));      
        } catch (IOException e) {
            logger.error("加载路网失败",e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (shpDataStore != null) {
                    shpDataStore.dispose();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TestFindPath findPath = new TestFindPath();
        try {
            findPath.test2();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
   
}

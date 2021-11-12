package com.hdsx.geohome.geocoding.service;


import com.hdsx.geohome.geocoding.api.IndexDao;
import com.hdsx.geohome.geocoding.parameter.ModelParameter;
import com.hdsx.geohome.geocoding.utile.DocumentUtils;
import com.hdsx.geohome.geocoding.utile.LuceneUtils;
import com.hdsx.geohome.geocoding.utile.SpatialUtils;
import com.hdsx.geohome.geocoding.vo.DIRECTORYTYPE;

import com.hdsx.geohome.geocoding.vo.QueryResult;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.spatial.SpatialStrategy;
import org.apache.lucene.spatial.query.SpatialArgs;
import org.apache.lucene.spatial.query.SpatialOperation;
import org.apache.lucene.store.Directory;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by jzh on 2017/6/24.
 */
@Service
public class IndexDaoImpl implements IndexDao {

    private static Logger logger = LoggerFactory.getLogger(IndexDaoImpl.class);


    @Override
    public void createIndex(String indexName) throws IOException {

    }

    public synchronized void save(List<Map<String,Object>> elementList, String table, DIRECTORYTYPE directorytype) {
        IndexWriter ramIndexWriter = null;
        Directory ramDirectory = null;
        try {
            IndexWriterConfig ramConfig = new IndexWriterConfig(LuceneUtils.getAnalyzer());
            ramConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            ramDirectory = LuceneUtils.getDirectory(directorytype);
            while (IndexWriter.isLocked(ramDirectory)) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ramIndexWriter = new IndexWriter(ramDirectory, ramConfig);

            int i = 0; for (int size = elementList.size(); i < size; i++) {
            	Map<String,Object> element = elementList.get(i);
                
                element.put("table", table);
                Document document = DocumentUtils.element2Document(element);
                ramIndexWriter.addDocument(document);
            }
        }
        catch (IOException e)
        {
            logger.error("保存失败", e);
        }
        finally {
            if (ramIndexWriter != null)
                try {
                    ramIndexWriter.flush();
                    ramIndexWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void delete(String id, DIRECTORYTYPE directorytype) {
        IndexWriter indexWriter = null;
        try {
            Term term = new Term("id", id);
            IndexWriterConfig config = new IndexWriterConfig(LuceneUtils.getAnalyzer());
            indexWriter = new IndexWriter(LuceneUtils.getDirectory(directorytype), config);
            indexWriter.deleteDocuments(term);
        } catch (IOException e) {
            logger.error("删除失败", e);
        } finally {
            LuceneUtils.closeIndexWriter(indexWriter);
        }
    }

    @Override
    public void deleteTable(String table, DIRECTORYTYPE directorytype) {
        IndexWriter indexWriter = null;
        try {
            IndexWriterConfig config = new IndexWriterConfig(LuceneUtils.getAnalyzer());
            indexWriter = new IndexWriter(LuceneUtils.getDirectory(directorytype), config);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            TermQuery termQuery = new TermQuery(new Term("table", table));
            builder.add(termQuery, BooleanClause.Occur.MUST);
            indexWriter.deleteDocuments(builder.build());
        } catch (IOException e) {
            logger.error("删除失败", e);
        } finally {
            LuceneUtils.closeIndexWriter(indexWriter);
        }
    }

    @Override
    public void deleteList(String[] ids, DIRECTORYTYPE directorytype) {
        IndexWriter indexWriter = null;
        try {
            Term[] terms = new Term[ids.length];
            int i = 0;
            for(String ele : ids){
                terms[i++]=new Term("id",ele);
            }
            IndexWriterConfig config = new IndexWriterConfig(LuceneUtils.getAnalyzer());
            indexWriter = new IndexWriter(LuceneUtils.getDirectory(directorytype), config);
            indexWriter.deleteDocuments(terms);
        } catch (IOException e) {
            logger.error("删除失败", e);
        } finally {
            LuceneUtils.closeIndexWriter(indexWriter);
        }
    }

    @Override
    public void deleteAll(DIRECTORYTYPE directorytype) {
        IndexWriter indexWriter = null;
        try {
            IndexWriterConfig config = new IndexWriterConfig(LuceneUtils.getAnalyzer());
            indexWriter = new IndexWriter(LuceneUtils.getDirectory(directorytype), config);
            indexWriter.deleteAll();
        } catch (IOException e) {
            logger.error("删除失败", e);
        } finally {
            LuceneUtils.closeIndexWriter(indexWriter);
        }
    }

    public void update(Map<String,Object> element, DIRECTORYTYPE directorytype) {

        Document document = DocumentUtils.element2Document(element);
        IndexWriter indexWriter = null;
        try {
            Term term = new Term("id", element.get("id").toString());
            IndexWriterConfig config = new IndexWriterConfig(LuceneUtils.getAnalyzer());
            indexWriter = new IndexWriter(LuceneUtils.getDirectory(directorytype), config);
            indexWriter.updateDocument(term, document);
        } catch (IOException e) {
            logger.error("更新失败", e);
        } finally {
            LuceneUtils.closeIndexWriter(indexWriter);
        }
    }

    @Override
    public QueryResult search(ModelParameter parameter, DIRECTORYTYPE directorytype) {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        try {
            IndexReader reader = DirectoryReader.open(LuceneUtils.getDirectory(directorytype));
            IndexSearcher searcher = new IndexSearcher(reader);
            BooleanQuery.Builder builder = new BooleanQuery.Builder();
            attributeFilter(parameter,builder);
            spatialFilter(parameter,builder);

            TopDocs topDocs = searcher.search(builder.build(), 100000);
            int count = topDocs.totalHits;
            ScoreDoc[] hits = topDocs.scoreDocs;
            int current = parameter.getCurrent();
            int limit = parameter.getLimit();
            int begin = limit * (current - 1);
            int end = Math.min(begin + limit, hits.length);
            for (int i = begin; i < end; i++) {
                ScoreDoc scoreDoc = hits[i];
                Document document = searcher.doc(scoreDoc.doc);
                Map<String,Object> element = DocumentUtils.document2Element(document);
                list.add(element);
            }
            reader.close();
            return new QueryResult(count, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(String id,String code, String name, String address, String type, String admincode, double longtitude, double latitude, int forder) {
        IndexWriter indexWriter = null;
        try {
        	Map<String,Object> element = new HashMap<String,Object>();
            if(StringUtils.isEmpty(id)) {
            	element.put("id", UUID.randomUUID().toString());
            }else {
            	element.put("id", id);
            }
            element.put("address", address);
            element.put("name", name);
            element.put("code", code);
            element.put("table", type);
            element.put("geometry", new WKTReader().read("POINT ("+longtitude+" "+latitude+")"));
            element.put("geometryType", "Point");
            
               
            List<Map<String,Object>> elements = new ArrayList<>();
            elements.add(element);
            delete(element.get("id").toString(),DIRECTORYTYPE.FILE);
            save(elements,"POI", DIRECTORYTYPE.FILE);
            return  true;
        } catch (Exception e) {
            logger.error("保存失败", e);
        } finally {
            if(indexWriter!=null)
                LuceneUtils.closeIndexWriter(indexWriter);
        }
        return false;
    }

    public static  BooleanQuery.Builder spatialFilter(ModelParameter parameter,BooleanQuery.Builder builder) {
        Geometry geometry = parameter.getGeometry();
        if(geometry == null || geometry.isEmpty()){
           return builder;
        }
        Query geoQuery = null;
        if ((geometry instanceof Point)) {
            Point jtsPoint = (Point)geometry;
            SpatialStrategy strategy = SpatialUtils.createStrategy();
            SpatialArgs args = new SpatialArgs(SpatialOperation.Intersects, SpatialContext.GEO.getShapeFactory().circle(jtsPoint.getX(), jtsPoint.getY(), DistanceUtils.dist2Degrees(parameter.getDistance(), 6371.0087714000001D)));
            geoQuery = strategy.makeQuery(args);
        }
        if (geoQuery != null) {
            builder.add(geoQuery, BooleanClause.Occur.MUST);
        }
        return builder;
    }

    public static BooleanQuery.Builder attributeFilter(ModelParameter parameter,BooleanQuery.Builder builder) {
        BooleanQuery.Builder typeBuilder = null;//匹配类型
        BooleanQuery.Builder keywordsBuilder = null;  //匹配关键字
        if (!StringUtils.isEmpty(parameter.getField())) {
            keywordsBuilder = new BooleanQuery.Builder();  //匹配关键字
            WildcardQuery wildcardQuery = new WildcardQuery(new Term(parameter.getField(), "*" + parameter.getKeywords() + "*"));
            BoostQuery boostQuery0 = new BoostQuery(wildcardQuery, 3.0F);
            keywordsBuilder.add(boostQuery0, BooleanClause.Occur.SHOULD);
        }else{
            if(!StringUtils.isEmpty(parameter.getKeywords())){
                keywordsBuilder = new BooleanQuery.Builder();  //匹配关键字
                WildcardQuery wildcardQuery1 = new WildcardQuery(new Term("code", "*" + parameter.getKeywords() + "*"));
                WildcardQuery wildcardQuery2 = new WildcardQuery(new Term("name", "*" + parameter.getKeywords() + "*"));
                WildcardQuery wildcardQuery3 = new WildcardQuery(new Term("address", "*" + parameter.getKeywords() + "*"));                   
                BoostQuery boostQuery1 = new BoostQuery(wildcardQuery1, 3.0F);
                BoostQuery boostQuery2 = new BoostQuery(wildcardQuery2, 9.0F);
                BoostQuery boostQuery3 = new BoostQuery(wildcardQuery3, 0.5F);
                keywordsBuilder.add(boostQuery1, BooleanClause.Occur.SHOULD);
                keywordsBuilder.add(boostQuery2, BooleanClause.Occur.SHOULD);
                keywordsBuilder.add(boostQuery3, BooleanClause.Occur.SHOULD);
            }
        }
        if (parameter.getTables() != null) {
            typeBuilder = new BooleanQuery.Builder(); //匹配类型
            for(int i = 0 ; i < parameter.getTables().length ; i++){
                WildcardQuery termQuery = new WildcardQuery(new Term("table",  parameter.getTables()[i]));
                typeBuilder.add(termQuery, BooleanClause.Occur.SHOULD);
            }
        }
        if(typeBuilder != null){
            BooleanQuery typeQuery = typeBuilder.build();
            builder.add(typeQuery,BooleanClause.Occur.MUST);
        }
        if(keywordsBuilder != null){
            BooleanQuery keywordsQuery = keywordsBuilder.build();
            builder.add(keywordsQuery,BooleanClause.Occur.MUST);
        }
        return builder;
    }
}
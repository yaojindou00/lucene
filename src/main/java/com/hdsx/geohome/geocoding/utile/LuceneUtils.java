package com.hdsx.geohome.geocoding.utile;


import com.hdsx.geohome.geocoding.vo.DIRECTORYTYPE;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created by kemi on 2016/9/15.
 */
public class LuceneUtils {
    private static Logger logger= LoggerFactory.getLogger(LuceneUtils.class);
    private static Directory ramDirectory;//内存索引存储位置
    private static Directory fileDirectory;//文件索引存储位置
    private static Analyzer  analyzer;//分析器
    private static String indexFile;
    static {
        try{
            if(indexFile==null){
                PropertiesUtil.readProperties("application.properties");
                indexFile = PropertiesUtil.getProperty("lucene.indexpath");
            }
            fileDirectory = FSDirectory.open(Paths.get(indexFile));
            ramDirectory = new RAMDirectory();
            //analyzer=new StandardAnalyzer();
            analyzer=new SmartChineseAnalyzer();
            //analyzer=new HDKAnalyzer();
        } catch (IOException e) {
            logger.error("创建索引错误",e);
        }
    }

    public static Directory getDirectory(DIRECTORYTYPE directorytype){
       return directorytype==DIRECTORYTYPE.FILE?fileDirectory:ramDirectory;
    }
    public static Analyzer getAnalyzer() {
        return analyzer;
    }

    public static void closeIndexWriter(IndexWriter indexWriter){
        if(indexWriter!=null){
            try {
                indexWriter.flush();
                indexWriter.close();
            }catch (Exception e2){
                logger.error("管理索引失败",e2);
            }
        }
    }
}

package com.bj.java.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by baijian on 2016/10/21.
 */
public class LuceneSearchTest {

    public static void search(String indexDir, String s) throws IOException, ParseException {
        Directory dir = FSDirectory.open(new File(indexDir));
        IndexSearcher is = new IndexSearcher(dir);
        QueryParser parser = new QueryParser(Version.LUCENE_33,"文档",new StandardAnalyzer(Version.LUCENE_33));
        Query query = parser.parse(s);
        long start = System.currentTimeMillis();
        TopDocs hits = is.search(query,10);
        System.out.printf("命中数:"+hits.totalHits);
        long end = System.currentTimeMillis();
        System.out.printf("搜索"+hits.totalHits +"文档(共花费"+(end - start)+"毫秒)和字符   "+s+"   匹配");

        for(ScoreDoc scoreDoc:hits.scoreDocs){
            Document document = is.doc(scoreDoc.doc);
            System.out.printf(document.get("路径"));
        }
        is.close();
    }



    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "E:\\lunceneTest\\test1";
        String s = "ss";
        search(indexDir,s);
    }
}

package com.bj.java.lucene;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;

/**
 * Created by baijain on 2016/10/20.
 */
public class LuceneIndexTest {

    private IndexWriter indexWriter ;

    @SuppressWarnings("deptecation")
    public LuceneIndexTest(String dir) throws IOException {
        Directory directory = FSDirectory.open(new File(dir));
        //使用 lucene3.3.0jar
        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_33);
        //创建 lucene index
        indexWriter = new IndexWriter(directory,analyzer,true, IndexWriter.MaxFieldLength.UNLIMITED);

    }

    /**
     * 关闭lucene
     * @throws IOException
     */
    public void close() throws IOException {
        indexWriter.close();
    }

    public int index(String dataDir, FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        for (File f : files) {
            if(!f.isDirectory() &&
                    !f.isHidden() &&
                    f.exists() &&
                    f.canRead() &&
                    (filter == null || filter.accept(f))){
                    indexFile(f);
            }
        }
        return indexWriter.numDocs();//返回文档数
    }

    public void indexFile(File f) throws IOException {
        System.out.printf("indexing"+f.getCanonicalPath());
        Document doc = getDocument(f);
        indexWriter.addDocument(doc); //将文档结果放入lucene中
    }
    public Document getDocument(File f) throws IOException {
        Document doc = new Document();
        doc.add(new Field("文档",new FileReader(f)));
        doc.add(new Field("文件名",f.getName(),Field.Store.YES,Field.Index.ANALYZED));
        doc.add(new Field("路径",f.getCanonicalPath(),Field.Store.YES,Field.Index.ANALYZED));
        return doc; //返回索引结果
    }
    public static void main(String[] args) throws IOException, ParseException {
        String indexDir = "E:\\lunceneTest\\test1";
        String dataDir = "E:\\lunceneTest\\test2";
        long start = System.currentTimeMillis();
        LuceneIndexTest luceneTest = new LuceneIndexTest(indexDir);
        int numIndexed;
        try {
            numIndexed = luceneTest.index(indexDir,new TestFilesFilter());
        }finally {
            luceneTest.close();
        }
        long end = System.currentTimeMillis();
        System.out.printf("搜索到"+numIndexed+"文件共花费"+(end - start) +"毫秒");
//        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_33);
        //解析queryParser
//        TokenStream ts = analyzer.tokenStream("平",new FileReader("E:\\lunceneTest\\test1\\test1.txt"));
//        ts.reset();
//        while(ts.incrementToken()){
//            System.out.println(ts.toString());
//        }
//        LuceneSearchTest.search("E:\\lunceneTest\\test1","平");
//        String content = FileUtil.readFileToString(new File("E:\\lunceneTest\\test1\\test1.txt"),"gbk");
//        System.out.println(content);
    }

    private static class TestFilesFilter implements FileFilter{
        public boolean accept(File path){
            return path.getName().toLowerCase().endsWith(".txt");//只对txt进行索引
        }
    }
}

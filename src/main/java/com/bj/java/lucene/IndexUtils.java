package com.bj.java.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
//import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * 索引类
 * IndexUtil
 * 创建人:xuchengfei
 * 时间：2016年6月13日-下午9:31:53
 * @version 1.0.0
 *
 */
public class IndexUtils {
	/**
	 * 索引磁盘文件
	 * 方法名：createIndex
	 * 创建人：xuchengfei
	 * 时间：2016年6月13日-下午10:05:04
	 * 手机:1564545646464
	 * @param path void
	 * @exception
	 * @since  1.0.0
	 */
	public static void createIndex(String path){//创建数据表，添加列名和列明对应的数据 table---document--一行数据
		IndexWriter writer = null;
		try {
			//1:建立写入磁盘索引的目录
			Directory directory = FSDirectory.open(new File("E:/lucence/test"));
			//2:建立索引写入流IndexWriter
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_33, new SmartChineseAnalyzer(Version.LUCENE_33));
			config.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(directory,config);
			//3:获取数据，讲文件信息写入到 Document中，进行索引，存储和分词
			List<HashMap<String, String>> fileMaps = new ArrayList<>();
			//递归获取目录下面的索引文件信息
			FileUtil.listFiles(path,fileMaps);
			for (HashMap<String, String> hashMap : fileMaps) {

				//文档对象，索引库存储的方式都讲义文档的方式进行存储，
				Document document = new Document();
				document.add(new Field("path",hashMap.get("path"), Field.Store.YES, Field.Index.NOT_ANALYZED));
				document.add(new Field("title",hashMap.get("name"), Field.Store.YES, Field.Index.ANALYZED));
				document.add(new Field("content",hashMap.get("content"), Field.Store.YES, Field.Index.ANALYZED));
				document.add(new Field("time",hashMap.get("time"), Field.Store.YES, Field.Index.NOT_ANALYZED));
				writer.addDocument(document);
				System.out.println("====>"+hashMap.get("path")+"==索引成功!!!");
			}
			System.out.println("索引完毕！！！");
			//关闭流witer.close()
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(writer!=null)writer.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	/**
	 * 搜索
	 * 方法名：search
	 * 创建人：xuchengfei
	 * 时间：2016年6月13日-下午10:15:48
	 * 手机:1564545646464 void
	 * @exception
	 * @since  1.0.0
	 */
	public static List<HashMap<String, String>> search(String keywords,int pageSize){
		IndexReader reader = null;
		List<HashMap<String, String>> datas = new ArrayList<>();
		try {
			//1:建立写入磁盘索引的目录
			Directory directory = FSDirectory.open(new File("E:/lucence/test"));
			//2:建立一个reader对象
			reader = IndexReader.open(directory);
			//建立搜索
			IndexSearcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_33);
			//解析queryParser
			QueryParser queryParser = new QueryParser(Version.LUCENE_33, "content", analyzer);
			//创建query对象,表示搜索域为content中包含"钦定"的文档对象
			Query query = queryParser.parse(keywords);// 查询document中title like '%java%' limit 0,10
			//根据search对象并且返回TopDocs
			TopDocs hits = searcher.search(query, pageSize);//命中文档数
			//根据searcher和scordDoc对象获取Document对象
			ScoreDoc[] scoreDocs =  hits.scoreDocs;
			//关键词高亮
			SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<em class='red'>", "</em>");
	        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
	        highlighter.setTextFragmenter(new SimpleFragmenter(120));

			for (ScoreDoc scoreDoc : scoreDocs) {
				HashMap<String, String> data = new HashMap<>();
				//返回查询文档对象
				Document document = searcher.doc(scoreDoc.doc);
				String content = document.get("content");
				data.put("title", document.get("title"));
				data.put("path", document.get("path"));
				data.put("time", document.get("time"));
				TokenStream tokenStream = analyzer.tokenStream("content",new StringReader(content));
				String highLightText = highlighter.getBestFragment(tokenStream,content);
				data.put("content", highLightText);
				datas.add(data);
			}
			return datas;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally{
			try {
				if(reader!=null)reader.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	//mp3 video jiexi
	//word exel pdf ppt jiexi
	//txt jsp html

	//sous
	//quanzhi
	//guolv


	public static void main(String[] args) throws IOException {
//		String path = "E:/lucence/test";
//		IndexUtils.createIndex(path);
		Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_33);
		//解析queryParser
		TokenStream ts = analyzer.tokenStream("坚持下",new FileReader("E:/lucence/test/test.txt"));
		ts.reset();
		while(ts.incrementToken()){
			System.out.println(ts.toString());
		}
		System.out.println(IndexUtils.search("坚持下",1));
		String content = FileUtil.readFileToString(new File("E:/lucence/test/test.txt"),"gbk");
		System.out.println(content);
	}
}

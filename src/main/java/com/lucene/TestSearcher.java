package com.lucene;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * <p>
 * Copyright 200 by Asiainfo-Linkage Software corporation
 * <p>
 * All rights reserved.
 * <p>
 * 版权所有：亚信联创
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分，
 * <p>
 * 侵权者将受到法律追究。
 * 
 * @PURPOSE: TODO
 * @DESCRIPTION: TODO
 * @AUTHOR: guozb
 * @DATE: 2014-5-29
 * @VERSION PSMA
 * @SINCE 1.0
 * @HISTORY: 2.0
 */
public class TestSearcher {
	Directory dir;
	IndexSearcher searcher;
	@Before
	public void setUp() throws CorruptIndexException, IOException{
		 dir = FSDirectory.open(new File(TestIndexer.indexPath));
		// 打开索引
		 searcher = new IndexSearcher(dir);
	}
	
	@Test
	public void testSearchByTerm() throws CorruptIndexException, IOException,
			ParseException {
		String q = "秦都区";
		Directory dir = FSDirectory.open(new File(TestIndexer.indexPath));
		// 打开索引
		IndexSearcher is = new IndexSearcher(dir);
		TermQuery tq = new TermQuery(new Term("district", q));
		TopDocs tps = is.search(tq, 10);
		// Analyzer az = new StandardAnalyzer(Version.LUCENE_30);
		ScoreDoc[] sds = tps.scoreDocs;
		for (ScoreDoc scoreDoc : sds) {
			Document doc = is.doc(scoreDoc.doc);
			// 返回匹配文件名
			System.out.println(doc.get("address") + " ----- " + doc.get("name"));
		}
		is.close();
	}
	@Test
	public void testByWildcardQuery() throws IOException{
		String q = "梦网*";
//		TermQuery tq = new TermQuery(new Term("district", q));
		WildcardQuery wq = new WildcardQuery(new Term("rule", q));
		TopDocs tps = searcher.search(wq, 10);
		// Analyzer az = new StandardAnalyzer(Version.LUCENE_30);
		ScoreDoc[] sds = tps.scoreDocs;
		for (ScoreDoc scoreDoc : sds) {
			Document doc = searcher.doc(scoreDoc.doc);
			// 返回匹配文件名
			System.out.println(doc.get("product") + " ----- " + doc.get("rule"));
		}
		searcher.close();
	}
	
	@Test
	public void testQueryParser() throws ParseException, IOException{
	
		QueryParser qp = new QueryParser(Version.LUCENE_30, "rule", new StandardAnalyzer(Version.LUCENE_30));
		Query query = qp.parse("梦网短信  ");
		TopDocs tps = searcher.search(query, 10);
		ScoreDoc[] sds = tps.scoreDocs;
		for (ScoreDoc scoreDoc : sds) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.print(scoreDoc.score);
			// 返回匹配文件名
			System.out.println(doc.get("product") + " ----- " + doc.get("rule"));
		}
		searcher.close();
		
	}
	
	@Test
	public void testMultiQueryParser() throws ParseException, CorruptIndexException, IOException{
		String[] fields = {"product","rule0","rule1"};
		QueryParser qp = new MultiFieldQueryParser(Version.LUCENE_30,fields , new IKAnalyzer()/*new StandardAnalyzer(Version.LUCENE_30)*/);
		Query query = qp.parse("梦网短~0.5");
		TopDocs tps = searcher.search(query, 10);
		ScoreDoc[] sds = tps.scoreDocs;
		for (ScoreDoc scoreDoc : sds) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.print(scoreDoc.score);
			// 返回匹配文件名
			System.out.println(doc.get("product") + " ----- " + doc.get("rule0")+doc.get("rule1"));
		}
		searcher.close();
	}

}
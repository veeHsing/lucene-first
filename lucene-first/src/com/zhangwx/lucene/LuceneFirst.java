package com.zhangwx.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.Queue;

public class LuceneFirst {

    @Test
    public void createIndex() throws Exception{
        //1 创建一个Director对象，指定索引库保存位置
        Directory directory = createDirector();
        //2 基于Director创建一个indexWriter对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter =new IndexWriter(directory,indexWriterConfig);
        //3 读取文档上的文件 对应每个文件创建一个文档对象
        File dir = new File("C:\\Users\\Administrator\\Desktop\\tmp");
        File[] files = dir.listFiles();
        for (File file:files
             ) {
            String fileName = file.getName();
            String filePath = file.getPath();
            String fileContent = FileUtils.readFileToString(file,"utf-8");
            long fileSize = FileUtils.sizeOf(file);

            //创建域
            Field fieldName =new TextField("name",fileName,Field.Store.YES);
            Field fieldPath =new TextField("path",filePath,Field.Store.YES);
            Field fieldContent =new TextField("content",fileContent,Field.Store.YES);
            Field fieldSize =new TextField("size",fileSize+"",Field.Store.YES);

            //4 向文档对象中添加域
            Document document = new Document();
            document.add(fieldName);
            document.add(fieldPath);
            document.add(fieldContent);
            document.add(fieldSize);
            //5 把文档对象写入索引库
            indexWriter.addDocument(document);

        }

        //6 关闭indexWriter对象
        indexWriter.close();
        
    }

    private Directory createDirector() throws Exception{
        Directory directory = FSDirectory.open(new File("D:\\lucene_\\index").toPath());
        return directory;
    }


    @Test
    public void searchIndex() throws Exception{
        Directory directory = FSDirectory.open(new File("D:\\lucene_\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Query query = new TermQuery(new Term("name","密码"));

        TopDocs topDocs = indexSearcher.search(query,10);
        System.out.println("命中："+topDocs.totalHits);
        ScoreDoc[] docs =  topDocs.scoreDocs;
        for (ScoreDoc doc:docs
             ) {

            int id =doc.doc;



            Document document = indexSearcher.doc(id);
            System.out.println( document.get("name"));
            System.out.println( document.get("path"));
            System.out.println( document.get("content"));
            System.out.println( document.get("size"));
            System.out.println("====================");

        }


        indexReader.close();






    }
}

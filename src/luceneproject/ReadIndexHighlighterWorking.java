/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package luceneproject;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.memory.MemoryIndex;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TextFragment;

/**
 *
 * @author kyuubi
 */
public class ReadIndexHighlighterWorking {
    private static final String INDEX_DIR = "Indexes";
    
    public static void main(String[] args) throws Exception
    {
        //while(true){
            


        
            
            IndexSearcher searcher = createSearcher();
            
            System.out.println("Press 1 to choose field Contents OR");
            System.out.println("Press 2 to choose field Description OR");
            System.out.println("Press 3 to choose field Name : ");
            Scanner scan2 = new Scanner(System.in);
            String sc2 = scan2.nextLine();
            String qField = "dsa";
            
            if(sc2.equals("1")){
                qField = "contents";
            }else if(sc2.equals("2")){
                qField = "Description";
            }else if(sc2.equals("3")){
                qField = "Name";
            }else {
                System.out.println("You Gave Wrong Number");
                System.exit(0);
            }
            System.out.println("");
            System.out.println("you chose " + qField);
            System.out.println("==============");
            System.out.println("give a word : ");

            Scanner scan = new Scanner(System.in);
            String sc = scan.nextLine();
            
            QueryParser qp = new QueryParser(qField, new EnglishAnalyzer());
            Query query = qp.parse(sc);
            

            
            System.out.println(query);
        
            TopDocs hits = searcher.search(query,1000);
            
            //QueryScorer scorer = new QueryScorer(query, "");
            
            
            TopDocs foundDocs = hits;
            int totalResults = (int) foundDocs.totalHits.value;
            
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
            EnglishAnalyzer analyzer = new EnglishAnalyzer();

            System.out.println("Total Results : " + totalResults );
            System.out.println("Total docs " + searcher.collectionStatistics(qField).docCount());
            System.out.println("Total Pages : " + (totalResults+10)/10);
            
            String matchQuery = query.toString().replaceAll(qField + ":", "");
            
            //int y = (int) foundDocs.totalHits.value;
            //double idf = (double)y/10000;
            //System.out.println("IDF : " + idf);
            //int a = 0;
            if(totalResults <= 0){
                System.out.println("Word Not Found");
                System.exit(0);
            }else if(totalResults<10){
                for(int g=0; g<30;g++){
                            System.out.println("");
                }
                System.out.println("=============");
                System.out.println("    Page 1                                                   Searching for the word/words : " + matchQuery);
                System.out.println("=============");
                System.out.println("");
                System.out.println("Total results : " + totalResults);
                System.out.println("Total docs " + searcher.collectionStatistics(qField).docCount());
                System.out.println("Total Pages : " + (totalResults+10)/10);
                for(int i=0; i < totalResults; i++)
                {
                    Document d = searcher.doc(foundDocs.scoreDocs[i].doc);
                    String path = d.get("path");
                    String content = d.get(("contents"));

                    //highlighter
                    float score = (float) foundDocs.scoreDocs[i].score;
                    int id = hits.scoreDocs[i].doc;
                    Document docHit = searcher.doc(id);
                    String text = docHit.get(qField);
                    TokenStream tokenStream = TokenSources.getTokenStream(searcher.getIndexReader(), id, qField, analyzer);
                    TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, true, 1000);
                    String finalString = "";
                    for (int j = 0; j < frag.length; j++) {
                        finalString = finalString + " " + frag[j];  
                    }
                    System.out.println(i + 1 + ") Path : " + path + ", Score : " + score + ", Contents : " + finalString);
                }
            }else{
                int firstPage = 0;
                int i = 0;
                int a = 0;
                int b = 10;
                int w = 10;
                for(int k=0; k<totalResults;){
                    for(int g=0; g<30;g++){
                            System.out.println("");
                    }
                    if(firstPage == 1){
                        System.out.println("============================================");
                        System.out.println("Cannot Go Before Page 1. Reprinting Page 1:");
                        System.out.println("============================================");
                        firstPage = 0;
                    }
                    System.out.println("=============");
                    System.out.println("    Page " + w/10 + "                                                  Searching for the word/words : " + matchQuery);
                    System.out.println("=============");
                    System.out.println("");
                    System.out.println("Total results : " + totalResults);
                    System.out.println("Total docs " + searcher.collectionStatistics(qField).docCount());
                    System.out.println("Total Pages : " + (totalResults+10)/10);
                    for(i=a; i < b; i++)
                    {
                        Document d = searcher.doc(foundDocs.scoreDocs[i].doc);
                        String path = d.get("path");
                        String content = d.get(("contents"));

                        //highlighter
                        float score = (float) foundDocs.scoreDocs[i].score;
                        //
                        int id = hits.scoreDocs[i].doc;
                        Document docHit = searcher.doc(id);
                        String text = docHit.get(qField);
                        TokenStream tokenStream = TokenSources.getTokenStream(searcher.getIndexReader(), id, qField, analyzer);
                        TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, true, 1000);
                        String finalString = "";
                        for (int j = 0; j < frag.length; j++) {
                            finalString = finalString + " " + frag[j];  
                        }
                        System.out.println(i + 1 + ") Path : " + path + ", Score : " + score + ", Contents : " + finalString);
                    }
                if(a<10){
                    System.out.println("");
                    System.out.println("=====================");
                    System.out.println("Press 1 for next page");
                    System.out.println("=====================");
                }else {
                    System.out.println("");
                    System.out.println("============================================");
                    System.out.println("Press 1 for next page Or 2 for Previous page");
                    System.out.println("============================================");
                }
                Scanner scan1 = new Scanner(System.in);
                String sc1 = scan.nextLine();
                
                if(sc1.equals("1")){
                    if((a + 20) > totalResults){
                        for(int g=0; g<30;g++){
                            System.out.println("");
                        }
                        System.out.println("=============");
                        System.out.println("    Page " + (w+10)/10 + "                                                  Searching for the word/words : " + matchQuery);
                        System.out.println("=============");
                        System.out.println("");
                        System.out.println("Total results : " + totalResults);
                        System.out.println("Total docs " + searcher.collectionStatistics(qField).docCount());
                        System.out.println("Total Pages : " + (totalResults+10)/10);
                        for(int m=(a+10); m < totalResults; m++)
                        {
                            Document d = searcher.doc(foundDocs.scoreDocs[m].doc);
                            String path = d.get("path");
                            String content = d.get(("contents"));

                            //highlighter
                            float score = (float) foundDocs.scoreDocs[m].score;
                            //
                            int id = hits.scoreDocs[m].doc;
                            Document docHit = searcher.doc(id);
                            String text = docHit.get(qField);
                            TokenStream tokenStream = TokenSources.getTokenStream(searcher.getIndexReader(), id, qField, analyzer);
                            TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, true, 1000);
                            String finalString = "";
                            for (int j = 0; j < frag.length; j++) {
                                finalString = finalString + " " + frag[j];  
                            }
                            System.out.println(m + 1 + ") Path : " + path + ", Score : " + score + ", Contents : " + finalString);
                        }
                        System.out.println("");
                        System.out.println("");
                        System.out.println("================================");
                        System.out.println("You have reached the FINAL PAGE ");
                        System.out.println("================================");
                        System.exit(0);
                    }else {
                    //    System.out.print("\033[H\033[2J");
                    //    System.out.flush();
                        k = k + 10;
                        a = a + 10;
                        b = b + 10;
                        w = w + 10;
                    }
                }else if(sc1.equals("2")){
                    if(a == 0){
                        firstPage = 1;
                    }else {
                        k = k - 10;
                        a = a - 10;
                        b = b - 10;
                        w = w - 10;
                    }
                }
                else{
                    System.exit(0);
                }
            }
        }
    }
    //}
    
//    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws IOException, ParseException, InvalidTokenOffsetsException
//    {
//        QueryParser qp = new QueryParser("contents", new EnglishAnalyzer());
//        Query query = qp.parse(textToFind);
//        System.out.println(query);
//        
//        TopDocs hits = searcher.search(query,10);
//        return hits;
//    }
     
     private static IndexSearcher createSearcher() throws IOException
     {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        
        IndexReader reader = DirectoryReader.open(dir);
        
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
     }
}

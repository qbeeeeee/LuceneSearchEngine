/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package luceneproject;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author kyuubi
 */
public class WriteIndex 
{
    public static void main(String[] args)
    {
        //Input Folder
        String docsPath = "dataset";

        //Output Folder
        String indexPath = "Indexes";

        //Input path variable
        final Path docDir = Paths.get(docsPath);

        try
        {
            Directory dir = FSDirectory.open( Paths.get(indexPath) );

            Analyzer analyzer = new EnglishAnalyzer();

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);

            IndexWriter writer = new IndexWriter(dir,iwc);

            indexDocs(writer, docDir);

            writer.close();    
        }
            catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        indexDoc(writer,file,attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
        });
        }
    }
    
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {
        try (InputStream stream = Files.newInputStream(file))
        {
            Document doc = new Document();
            
            doc.add(new StringField("path",file.toString(), Field.Store.YES));
            
            doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Store.YES));
            
            //doc.add(new TextField("year", new String(Files.readString(file).substring(0, 4)), Store.YES));
            
            String[] splitted = doc.get("contents").split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            String[] terms = {"Year","Term","YearTerm","Subject","Number","Name","Description","Credit Hours","Section Info","Degree Attributes","Schedule Information","CRN","Section","Status Code","Part of Term","Section Title","Section Credit Hours","Section Status","Enrollment Status","Type","Type Code","Start Time","End Time","Days of Week","Room","Building","Instructors"};

            
            for(int i=0; i < splitted.length; i++){
                doc.add(new TextField(terms[i], splitted[i], Store.YES));
            }
            
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }
}
        

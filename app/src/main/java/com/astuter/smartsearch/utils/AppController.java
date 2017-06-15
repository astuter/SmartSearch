package com.astuter.smartsearch.utils;

import android.app.Application;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by aniruddh on 14/06/17.
 */

public class AppController extends Application {

    public static final SearcherManager getSearcherManager() {
        return searcherManager;
    }

    public static final IndexWriter getIndexWriter() {
        return indexWriter;
    }

    private static SearcherManager searcherManager = null;
    private static IndexWriter indexWriter = null;

    @Override
    public void onCreate() {
        super.onCreate();

        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        //pick the buffer size from property
        String memorySize = "5.0";
        config.setRAMBufferSizeMB(Double.valueOf(memorySize));
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        //create index on external directory under lucene folder
        File path = new File(getApplicationContext().getExternalFilesDir(null), "lucene");
        try {
            Directory directory = FSDirectory.open(path);
            indexWriter = new IndexWriter(directory, config);
            boolean applyAllDeletes = true;
            //no need to warm the search
            searcherManager = new SearcherManager(indexWriter, applyAllDeletes, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

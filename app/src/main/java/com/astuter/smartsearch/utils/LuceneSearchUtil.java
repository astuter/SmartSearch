package com.astuter.smartsearch.utils;

import android.content.ContentValues;
import android.util.Log;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.spans.SpanMultiTermQueryWrapper;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by aniruddh on 14/06/17.
 */

public class LuceneSearchUtil {
    private static final String tag = LuceneSearchUtil.class.getName();

    public LuceneSearchUtil() {
    }

    //insert articles id,title and feedid
    public static void insertArticleDocument(ContentValues contentValues) {
        try {
            IndexWriter writer = AppController.getIndexWriter();
            Document document = new Document();
            //don't analyze id field, store as such
            Field idField = new StringField(FeedSQLLiteHelper.COLUMN_ID, String.valueOf(contentValues.get(FeedSQLLiteHelper.COLUMN_ID)), Field.Store.YES);
            document.add(idField);
            //analyze the url field so textfield
            Field titleField = new TextField(FeedSQLLiteHelper.COLUMN_ARTICLE_TITLE, String.valueOf(contentValues.get(FeedSQLLiteHelper.COLUMN_ARTICLE_TITLE)), Field.Store.YES);
            document.add(titleField);
            Field feedId = new StringField(FeedSQLLiteHelper.COLUMN_ARTICLE_FEED_ID, String.valueOf(contentValues.get(FeedSQLLiteHelper.COLUMN_ARTICLE_FEED_ID)), Field.Store.YES);
            document.add(feedId);
            writer.addDocument(document);
        } catch (IOException e) {
            Log.e(tag, "Unable to add document as " + e.getMessage(), e);
        }
    }

    //searching the articles searchterm is passed and broken down into individual terms
    public static ArrayList<String> searchAndGetMatchingIds(String searchTerm) {
        ArrayList result = new ArrayList<String>();
        //get the searchermanager
        SearcherManager searcherManager = AppController.getSearcherManager();
        IndexSearcher indexSearcher = null;

        indexSearcher = searcherManager.acquire();
        //split on space
        String[] terms = searchTerm.split("[\\s]+");
        //multiple terms are to be searched
        SpanQuery[] spanQueryArticleTitle = new SpanQuery[terms.length];
        int i = 0;
        for (String term : terms) {
            //wildcardquery
            WildcardQuery wildcardQuery = new WildcardQuery(new Term(FeedSQLLiteHelper.COLUMN_ARTICLE_TITLE, term.toLowerCase()));
            spanQueryArticleTitle[i] = new SpanMultiTermQueryWrapper<WildcardQuery>(wildcardQuery);
            i = i + 1;
        }
        //no words between the typed text you could increase this but then performance will be lowered
        SpanNearQuery spanNearQuery1 = new SpanNearQuery(spanQueryArticleTitle, 0, true);
        TopDocs topDocs = null;
        try {
            //execute topN query
            topDocs = indexSearcher.search(spanNearQuery1, ProjectConstants.LUCENE_TOP_N);
            if (topDocs != null) {
                for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                    Document document = indexSearcher.doc(scoreDoc.doc);
                    String id = document.get(FeedSQLLiteHelper.COLUMN_ID);
                    result.add(id);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                searcherManager.release(indexSearcher);
            } catch (IOException e) {
                Log.e(tag, "Exception while releasing Index Searcher " + e.getMessage(), e);
            }
        }

        return result;
    }

//sample delete method

    public static void deleteArticlesByFeedId(String feedId) {
        IndexWriter indexWriter = AppController.getIndexWriter();
        TermQuery query = new TermQuery(new Term(FeedSQLLiteHelper.COLUMN_ARTICLE_FEED_ID, feedId));
        try {
            indexWriter.deleteDocuments(query);
        } catch (IOException e) {
            Log.e(tag, "Unable to delete document as " + e.getMessage(), e);
        }
        try {
            indexWriter.commit();
        } catch (IOException e) {
            Log.e(tag, "Unable to commit changes " + e.getMessage(), e);
        }
    }
}
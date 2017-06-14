package com.astuter.smartsearch.ui;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuter.smartsearch.R;
import com.astuter.smartsearch.adapter.SearchAdapter;
import com.astuter.smartsearch.model.DummyContent;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.util.ArrayList;

/**
 * An activity representing a list of Contacts. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ContactDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ContactListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private static final String TAG = ContactListActivity.class.getName();
    private boolean mTwoPane;
    private MaterialSearchView mSearchView;

    private static final String INDEX_DIR_NAME = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        if (findViewById(R.id.contact_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contact_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchAdapter searchAdapter = new SearchAdapter(this, DummyContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(searchAdapter);

        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Log.e(TAG, "onQueryTextSubmit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.e(TAG, "onQueryTextChange: " + newText);
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        fetchPhoneContacts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        rebuildIndexIfNotExists();
    }

    private void fetchPhoneContacts() {
        ContentResolver resolver = getContentResolver();
        Cursor contacts = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (contacts != null && contacts.moveToFirst()) {
            while (contacts.moveToNext()) {
                String contactId = contacts.getString(contacts.getColumnIndex(ContactsContract.Contacts._ID));
                String name = contacts.getString(contacts.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));

                // get the phone number if available
                if (contacts.getInt(contacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{contactId}, null);

                    if (phones != null && phones.moveToFirst()) {
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }
                        phones.close();
                    }
                }
            }
            contacts.close();
        }

    }

    private File getIndexRootDir() {
        return new File(getCacheDir(), INDEX_DIR_NAME);
    }

    private void rebuildIndex() {
        final ProgressDialog dialog = ProgressDialog.show(this, getString(R.string.rebuild_index_progress_title), getString(R.string.rebuild_index_progress_message), true);
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
//                    InputStream is = MainActivity.this.getAssets().open(DATA_SOURCE);
//                    Study.importData(is, getIndexRootDir().getAbsolutePath(), false);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                dialog.dismiss();
                if (result) {
//                    setStatus(getString(R.string.search));
                } else {
                    Toast.makeText(ContactListActivity.this, R.string.rebuild_index_failed_msg, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void rebuildIndexIfNotExists() {
        if (!getIndexRootDir().exists()) {
            rebuildIndex();
        }
    }

//    void setStatus(String text) {
//        if (text == null) {
//            statusOuterView.setVisibility(View.INVISIBLE);
//            statusText.setText("");
//            listView.setVisibility(View.VISIBLE);
//        } else {
//            statusOuterView.setVisibility(View.VISIBLE);
//            statusText.setText(text);
//            listView.setVisibility(View.INVISIBLE);
//        }
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}

package com.astuter.smartsearch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.astuter.smartsearch.R;
import com.astuter.smartsearch.adapter.SearchAdapter;
import com.astuter.smartsearch.dummy.DummyContent;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;

import java.util.ArrayList;
import java.util.List;

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
    private boolean mTwoPane;
    private SearchView mSearchView;

    protected static final int NAV_ITEM_INVALID = -1;
    protected static final int NAV_ITEM_TOOLBAR = 0;
    protected static final int NAV_ITEM_MENU_ITEM = 1;
    protected static final int NAV_ITEM_FILTERS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.contact_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchAdapter searchAdapter = new SearchAdapter(this, DummyContent.ITEMS, mTwoPane);
        recyclerView.setAdapter(searchAdapter);

        if (findViewById(R.id.contact_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        setSearchView();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    if (mSearchView != null) {
                        mSearchView.setQuery(searchWrd, true);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search: {
                mSearchView.open(true); // enable or disable animation
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*private void setNavigationView() { // @Nullable
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_item_toolbar || id == R.id.nav_item_filters) {
                    Intent intent = new Intent(BaseActivity.this, id == R.id.nav_item_toolbar ? ToolbarActivity.class : FiltersActivity.class);
                    intent.putExtra(EXTRA_KEY_VERSION, SearchView.VERSION_TOOLBAR);
                    intent.putExtra(EXTRA_KEY_VERSION_MARGINS, SearchView.VERSION_MARGINS_TOOLBAR_SMALL);
                    intent.putExtra(EXTRA_KEY_THEME, SearchView.THEME_LIGHT);
                    startActivity(intent);
                    finish();
                }

                if (id == R.id.nav_item_menu_item) {
                    // Intent intent = new Intent(this, id == R.id.nav_toggle_versions ? ToggleActivity.class : HistoryActivity.class);
                    Intent intent = new Intent(BaseActivity.this, MenuItemActivity.class);
                    intent.putExtra(EXTRA_KEY_VERSION, SearchView.VERSION_MENU_ITEM);
                    intent.putExtra(EXTRA_KEY_VERSION_MARGINS, SearchView.VERSION_MARGINS_MENU_ITEM);
                    intent.putExtra(EXTRA_KEY_THEME, SearchView.THEME_LIGHT);
                    startActivity(intent);
                    finish();
                }
                return true;
            });
            if (getNavItem() > NAV_ITEM_INVALID) {
                navigationView.getMenu().getItem(getNavItem()).setChecked(true);
            }
        }
    }*/

    // it can be in OnCreate
    protected void setSearchView() {
        mSearchView = (SearchView) findViewById(R.id.search_view);
        if (mSearchView != null) {
            mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_SMALL);
            mSearchView.setHint(R.string.search);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
//                    getData(query, 0);
                    mSearchView.close(false);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mSearchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                @Override
                public boolean onOpen() {

                    return true;
                }

                @Override
                public boolean onClose() {

                    return true;
                }
            });
            mSearchView.setVoiceText("Set permission on Android 6.0+ !");
            mSearchView.setOnVoiceClickListener(new SearchView.OnVoiceClickListener() {
                @Override
                public void onVoiceClick() {

                }
            });

            List<SearchItem> suggestionsList = new ArrayList<>();
            suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));

            SearchAdapter searchAdapter = new SearchAdapter(this, DummyContent.ITEMS, mTwoPane);
//            searchAdapter.setOnSearchItemClickListener((view, position) -> {
//                TextView textView = (TextView) view.findViewById(R.id.textView);
//                String query = textView.getText().toString();
//                getData(query, position);
//                mSearchView.close(false);
//            });
            mSearchView.setAdapter(searchAdapter);

            /*suggestionsList.add(new SearchItem("search12"));
            suggestionsList.add(new SearchItem("search22"));
            suggestionsList.add(new SearchItem("search32"));
            searchAdapter.notifyDataSetChanged();*/
            /*
            List<SearchFilter> filter = new ArrayList<>();
            filter.add(new SearchFilter("Filter1", true));
            filter.add(new SearchFilter("Filter2", true));
            mSearchView.setFilters(filter);
            //use mSearchView.getFiltersStates() to consider filter when performing search
            */
        }
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.close(true);
        } else {
            super.onBackPressed();
        }
    }

//    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
//        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
//    }

}

package com.astuter.smartsearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuter.smartsearch.R;
import com.astuter.smartsearch.model.DummyContent;
import com.astuter.smartsearch.ui.ContactDetailActivity;
import com.astuter.smartsearch.ui.ContactDetailFragment;
import com.astuter.smartsearch.ui.ContactListActivity;

import java.util.List;

/**
 * Created by Astuter on 13/06/17.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<DummyContent.DummyItem> mValues;
    private boolean isTwoPane;
    private Context mContext;

    public SearchAdapter(Context ctx, List<DummyContent.DummyItem> items, boolean isTwoPane) {
        mContext = ctx;
        mValues = items;
        this.isTwoPane = isTwoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ContactDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    ContactDetailFragment fragment = new ContactDetailFragment();
                    fragment.setArguments(arguments);
                    if (mContext instanceof ContactListActivity) {
                        ((ContactListActivity) mContext).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.contact_detail_container, fragment)
                                .commit();
                    }

                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ContactDetailActivity.class);
                    intent.putExtra(ContactDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyContent.DummyItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
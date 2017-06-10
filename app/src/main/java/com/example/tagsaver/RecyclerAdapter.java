package com.example.tagsaver;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeremy on 07-Jun-17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TagViewHolder> {

    private ArrayList<String> mTagList;

    public RecyclerAdapter() {
        mTagList = new ArrayList<String>();
    }

    public void addTag(String tag){
        mTagList.add(tag);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mTagList.size();
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.tag_list_items, parent, false);
        TagViewHolder viewHolder = new TagViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        String tag = mTagList.get(mTagList.size() - position - 1);
        holder.bind(tag);
    }


    class TagViewHolder extends RecyclerView.ViewHolder {
        private TextView mTodoTextView;

        public TagViewHolder(View itemView) {
            super(itemView);
            mTodoTextView = (TextView)itemView.findViewById(R.id.tagTextView);
        }

        public void bind(String tag) {
            mTodoTextView.setText(tag);
        }
    }


}

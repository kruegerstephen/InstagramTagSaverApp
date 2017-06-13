package com.example.tagsaver;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Jeremy on 07-Jun-17.
 */

public class catRecyclerAdapter extends RecyclerView.Adapter<catRecyclerAdapter.CatViewHolder> {

    private ArrayList<String> mCatList;


    public catRecyclerAdapter() {
        mCatList = new ArrayList<String>();
    }



    @Override
    public int getItemCount() {
        return mCatList.size();
    }

    @Override
    public CatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cat_list_items, parent, false);
        CatViewHolder viewHolder = new CatViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CatViewHolder holder, int position) {
        String tag = mCatList.get(mCatList.size() - position - 1);
        holder.bind(tag);
    }

    public void addTag(String tag){
        mCatList.add(tag);
        notifyDataSetChanged();
    }


    class CatViewHolder extends RecyclerView.ViewHolder {
        private TextView mCatTextView;
        private Button mEditBtn;

        public CatViewHolder(final View itemView) {
            super(itemView);
            mCatTextView = (TextView)itemView.findViewById(R.id.catTextView);
            mEditBtn = (Button)itemView.findViewById(R.id.catEditBtn);
            mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, Editor.class);
                    context.startActivity(intent);
                }
            });
        }

        public void bind(String tag) {
            mCatTextView.setText(tag);
        }
    }
}

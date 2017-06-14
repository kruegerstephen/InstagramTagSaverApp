package com.example.tagsaver;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Jeremy on 07-Jun-17.
 */

public class adderRecyclerAdapter extends RecyclerView.Adapter<adderRecyclerAdapter.TagViewHolder> {

    private ArrayList<String> mTagList;


    public adderRecyclerAdapter() {
        mTagList = new ArrayList<String>();
    }

    public void addTag(String tag){
        mTagList.add(tag);
        notifyDataSetChanged();
    }

    public void remTag(String tag){
        mTagList.remove(tag);
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
        private TextView mTagTextView;
        private TextView mTagNumber;
        private Button mRemoveBtn;

        public TagViewHolder(final View itemView) {
            super(itemView);
            mTagTextView = (TextView)itemView.findViewById(R.id.tagTextView);
            mTagNumber = (TextView)itemView.findViewById(R.id.score);
            mRemoveBtn = (Button)itemView.findViewById(R.id.tagRmvBtn);
            mRemoveBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String tag = (String) mTagTextView.getText();
                    remTag(tag);

                }
            });
        }

        public void bind(String tag) {
            mTagTextView.setText(tag);
        } //when we are done with the request, the idea is to do mTagTextView.setText(tag.split(" ").get(0)))
            //        and then to do mTagNumber.setText(tag.split(" ").get(1).deleteCharAt(0))
    }


}

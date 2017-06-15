package com.example.tagsaver;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.example.tagsaver.utils.CategoriesContract;
import com.example.tagsaver.utils.CategoryObj;
import com.example.tagsaver.utils.TagsDBHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by Jeremy on 07-Jun-17.
 */

public class catRecyclerAdapter extends RecyclerView.Adapter<catRecyclerAdapter.CatViewHolder> {

    private ArrayList<String> mCatList = new ArrayList<>();
    public static final String EXTRA_MESSAGE = "MessageName";


    public catRecyclerAdapter() {
        mCatList = new ArrayList<String>();
    }


    public ArrayList<String> getmTagList(){
        return mCatList;
    }

    public void setmTagList(ArrayList<String> catListFromMain){
        mCatList = catListFromMain;
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
        private Button cpyBtn;
        private ArrayList<CategoryObj> userCatList;
        private String listOfStrings = "";


        public CatViewHolder(final View itemView) {
            super(itemView);
            mCatTextView = (TextView)itemView.findViewById(R.id.catTextView);
            mEditBtn = (Button)itemView.findViewById(R.id.catEditBtn);
            mEditBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    String message = mCatTextView.getText().toString();
                    Intent intent = new Intent(context, Editer.class);
                    intent.putExtra(EXTRA_MESSAGE, message);
                    context.startActivity(intent);
                }
            });

            cpyBtn = (Button)itemView.findViewById(R.id.buttonCopy);
            cpyBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    userCatList = MainActivity.getCatList();
                    int i = 0;
                    for(i = 0; i < userCatList.size(); i++) {
                        if(userCatList.get(i).catName.equals(mCatTextView.getText())) {
                            for(int x = 0; x < userCatList.get(i).tagList.size(); x++) {
                                listOfStrings += " "+userCatList.get(i).tagList.get(x).name;
                            }
                        }
                    }

                    Context context = v.getContext();
                    ClipboardManager clipboard = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("tags", listOfStrings);
                    clipboard.setPrimaryClip(clip);
                }
            });
            
        }

        public void bind(String tag) {
            mCatTextView.setText(tag);
        }
    }
}

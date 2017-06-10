package com.example.tagsaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by Jeremy on 07-Jun-17.
 */

public class Adder extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditName;
    private EditText mTagsName;
    private Button mDoneButton;
    private Button mAddTagButton;


    private RecyclerView mTagListRecyclerView;
    private RecyclerAdapter mTagAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addcategory);
        mEditName = (EditText)findViewById(R.id.cat_name);
        mTagsName = (EditText)findViewById(R.id.editTag);
        mAddTagButton= (Button)findViewById(R.id.addTag);
        mDoneButton = (Button)findViewById(R.id.done_button);

        //recycler adapter
        mTagListRecyclerView = (RecyclerView)findViewById(R.id.rv_tags_list);
        mTagListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTagListRecyclerView.setHasFixedSize(true);
        mTagAdapter = new RecyclerAdapter();
        mTagListRecyclerView.setAdapter(mTagAdapter);

        mAddTagButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        String tagText = mTagsName.getText().toString();
        if (!TextUtils.isEmpty(tagText)) {
            mTagAdapter.addTag(tagText);
            mTagsName.setText("");
        }

    }
}

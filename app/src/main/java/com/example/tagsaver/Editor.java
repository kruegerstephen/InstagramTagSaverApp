package com.example.tagsaver;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.tagsaver.utils.CategoriesContract;
import com.example.tagsaver.utils.TagsDBHelper;


/**
 * Created by Jeremy on 07-Jun-17.
 */

public class Editor extends AppCompatActivity implements View.OnClickListener {
    private EditText mEditName;
    private EditText mTagsName;
    private Button mDoneButton;
    private Button mAddTagButton;
    private SQLiteDatabase mDB;
    private String tags="";


    private RecyclerView mCatListRecyclerView;
    private adderRecyclerAdapter mTagAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_category);
        mEditName = (EditText)findViewById(R.id.cat_name);
        mTagsName = (EditText)findViewById(R.id.editTag);
        mDoneButton = (Button)findViewById(R.id.done_button);
        TagsDBHelper dbHelper = new TagsDBHelper(this);
        mDB=dbHelper.getWritableDatabase();
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                createNewCategory();
                Log.d(TAG,"write new category" + tags);

            }
        });

        //recycler adapter
        mCatListRecyclerView = (RecyclerView)findViewById(R.id.rv_tags_list);
        mCatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCatListRecyclerView.setHasFixedSize(true);
        mTagAdapter = new adderRecyclerAdapter();
        mCatListRecyclerView.setAdapter(mTagAdapter);
        mAddTagButton.setOnClickListener(this);


    }

    private long createNewCategory(){

        if (mEditName.getText() != null) {
            ContentValues values = new ContentValues();

            values.put(CategoriesContract.FavoriteRepos.COLUMN_FULL_NAME,
                    mEditName.getText().toString());
            values.put(CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION, tags);
            return mDB.insert(CategoriesContract.FavoriteRepos.TABLE_NAME, null, values);

        }else{
            return -1;
        }
    }

    @Override
    public void onClick(View v) {
        String tagText = mTagsName.getText().toString();
        tags+=" "+tagText;
        if (!TextUtils.isEmpty(tagText)) {
            mTagAdapter.addTag(tagText);

            mTagsName.setText("");
        }

    }
}

package com.example.tagsaver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tagsaver.utils.CategoriesContract;
import com.example.tagsaver.utils.CategoryObj;
import com.example.tagsaver.utils.InstagramUtils;
import com.example.tagsaver.utils.InstagramUtils.TagItem;
import com.example.tagsaver.utils.NetworkUtils;
import com.example.tagsaver.utils.TagsDBHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

//import android.os.AsyncTask;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import java.net.URL;


//import okhttp3.Call;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;


/**
 * Created by Jeremy on 07-Jun-17.
 */

public class Editer extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<String> {
    private EditText mEditName;
    private EditText mTagsName;
    private Button mDoneButton;
    private Button mAddTagButton;
    private SQLiteDatabase mDB;
    private SQLiteDatabase mDataBaseToCheck;
    private String tags = "";
    private String message;
    private Context context;
    public ArrayList<CategoryObj> userCatList = new ArrayList<>();


    private static final String INSTAGRAM_URL_KEY = "instagramUrl";
    private static final int INSTAGRAM_LOADER_ID = 0;
    private InstagramUtils instagramUtil;

    private RecyclerView mCatListRecyclerView;
    private adderRecyclerAdapter mTagAdapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        CharSequence text = "Hello toast!";
        final Toast[] toast = new Toast[1];
        //new ApiRequest().execute();

        setContentView(R.layout.addcategory);

        // Defining activity elements
        mEditName = (EditText) findViewById(R.id.cat_name);
        mTagsName = (EditText) findViewById(R.id.editTag);
        mAddTagButton = (Button) findViewById(R.id.addTag);
        mDoneButton = (Button) findViewById(R.id.done_button);

        // Instagram
        instagramUtil = new InstagramUtils();

        // Database setup
        TagsDBHelper dbHelper = new TagsDBHelper(this);
        mDB = dbHelper.getWritableDatabase();
        mDataBaseToCheck = dbHelper.getReadableDatabase();

        // When done save tags
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateCategory();
                Editer.super.onBackPressed();
            }
        });


        //Get Cat List
        Cursor cursor = mDataBaseToCheck.query(
                CategoriesContract.FavoriteRepos.TABLE_NAME, // The table to query
                null,                               // The columns to return
                null,                               // The columns for the WHERE clause
                null,                               // The values for the WHERE clause
                null,                               // don't group the rows
                null,                               // don't filter by row groups
                null                           // The sort order
        );
        while (cursor.moveToNext()) {
            byte[] blob = cursor.getBlob(cursor.getColumnIndex(CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION));
            if(blob!=null){
                String json = new String(blob);
                Gson gson = new Gson();
                userCatList = gson.fromJson(json, new TypeToken<ArrayList<CategoryObj>>(){}.getType());
            }
        }

        //recycler adapter
        mCatListRecyclerView = (RecyclerView) findViewById(R.id.rv_tags_list);
        mCatListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCatListRecyclerView.setHasFixedSize(true);
        mTagAdapter = new adderRecyclerAdapter();
        mCatListRecyclerView.setAdapter(mTagAdapter);
        mAddTagButton.setOnClickListener(this);


        //get Intent for Cat Name
        Intent intent = getIntent();
        message = intent.getStringExtra(catRecyclerAdapter.EXTRA_MESSAGE);



        //add all tags to tag list
        //needs to only add tags from cat
        int i = 0;
        for(i = 0; i < userCatList.size(); i++) {
            if(userCatList.get(i).catName.equals(message)){
                mTagAdapter.setmTagList(userCatList.get(i).tagList);
            }
        }

        mEditName.setText(message);
    }

    private long updateCategory() {

        if (userCatList != null) {
            ContentValues values = new ContentValues();

            Gson gson = new Gson();

            int i = 0;
            for(i = 0; i < userCatList.size(); i++) {
                if(userCatList.get(i).catName.equals(message)){
                    if(!mEditName.getText().equals(message)){
                        userCatList.get(i).catName = String.valueOf(mEditName.getText());
                    }
                }
            }

            values.put(CategoriesContract.FavoriteRepos.COLUMN_DESCRIPTION,
                    gson.toJson(userCatList).getBytes());
            return mDB.update(CategoriesContract.FavoriteRepos.TABLE_NAME, values, null, null);

        } else {
            return -1;
        }
    }


    @Override
    public void onClick(View v) {

        String tagText = mTagsName.getText().toString();

        String instagramURL = instagramUtil.buildTagURL(tagText);

        Bundle argsBundle = new Bundle();
        argsBundle.putString(INSTAGRAM_URL_KEY, instagramURL);
        getSupportLoaderManager().initLoader(INSTAGRAM_LOADER_ID, argsBundle, this);

    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mInstagramJSON;

            @Override
            protected void onStartLoading() {
                if (mInstagramJSON != null) {
                    Log.d(TAG, "AsyncTaskLoader delivering cached Instagram");
                    deliverResult(mInstagramJSON);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
                String InstagramURL = args.getString(INSTAGRAM_URL_KEY);

                if (InstagramURL == null || InstagramURL.equals("")) {
                    return null;
                }

                Log.d(TAG, "AsyncTaskLoader loading Instagram from url: " + InstagramURL);

                String InstagramJSON = null;

                try {
                    InstagramJSON = NetworkUtils.doHTTPGet(InstagramURL);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return InstagramJSON;
            }

            @Override
            public void deliverResult(String InstagramJSON) {
                mInstagramJSON = InstagramJSON;
                super.deliverResult(InstagramJSON);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String InstagramJSON) {
        Log.d(TAG, "AsyncTaskLoader load finished");
        TagItem tag = instagramUtil.parseTagJSON(InstagramJSON);
        mTagAdapter.addTag(tag);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        // Nothing to do here...
    }
}

package com.example.engremonatef.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private String bookTitle;
    private String bookAuthor;
    private String editTextInput;
    private ListView listView;
    private ArrayList<ListItem> items;
    private int index;
    private int top;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.btn_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    Intent intent = new Intent(MainActivity.this, NoConnection.class);
                    startActivity(intent);
                }

                EditText editText = (EditText) findViewById(R.id.edit_text_searchKey);
                editTextInput = editText.getText().toString();
                Loader<String> loader = null;
                LoaderManager loaderManager = getSupportLoaderManager();
                loader = loaderManager.getLoader(1);
                if (loader != null) {
                    loader = getSupportLoaderManager().restartLoader(1, null, MainActivity.this);
                    loader.forceLoad();
                } else {
                    loader = getSupportLoaderManager().initLoader(1, null, MainActivity.this);
                    loader.forceLoad();
                }


            }
        });


        if (savedInstanceState != null) {

            ArrayList<ListItem> recvItems;
            recvItems = savedInstanceState.getParcelableArrayList("ListItems");
            items = recvItems;
            MyCustomeAdapter customeAdapter = new MyCustomeAdapter(recvItems);
            if (recvItems == null) {
                Log.d("TEST", "Empty");
            } else {
                listView = (ListView) findViewById(R.id.list_view);
                listView.setAdapter(customeAdapter);
                listView.setSelectionFromTop(index, top);
            }

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<ListItem> recvItems = items;

        outState.putParcelableArrayList("ListItems", recvItems);

        if (listView != null) {
            index = listView.getFirstVisiblePosition();
            View v = listView.getChildAt(0);
            top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Connection connection = new Connection(MainActivity.this, editTextInput);
        return connection;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("items");
            items = null;
            items = new ArrayList<ListItem>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                JSONObject jsonObject2 = jsonObject1.getJSONObject("volumeInfo");

                if (bookTitle == null || bookTitle.length() > 40 || jsonObject2.isNull("title")) {
                    bookTitle = "Not Found";
                } else {
                    bookTitle = jsonObject2.getString("title");
                }

                if (bookAuthor == null || jsonObject2.isNull("authors")) {
                    bookAuthor = "Not Found";
                } else {
                    JSONArray jsonArray1 = jsonObject2.getJSONArray("authors");
                    bookAuthor = jsonArray1.get(0).toString();
                }
                items.add(new ListItem(bookTitle, bookAuthor));
            }
            MyCustomeAdapter myCustomeAdapter = new MyCustomeAdapter(items);
            listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(myCustomeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TEST", "Parse Error");
            Intent intent = new Intent(MainActivity.this, NoData.class);
            startActivity(intent);
        }
        loader.reset();
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }


    private class MyCustomeAdapter extends BaseAdapter {

        ArrayList<ListItem> listItems = new ArrayList<ListItem>();

        MyCustomeAdapter(ArrayList<ListItem> items) {
            listItems = items;
        }

        @Override
        public int getCount() {
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            return listItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.listitem_layout, null);

            TextView textView1 = (TextView) view.findViewById(R.id.text_view1_listitem);
            TextView textView2 = (TextView) view.findViewById(R.id.text_view2_listitem);

            textView1.setText(listItems.get(position).title);
            textView2.setText(listItems.get(position).author);
            return view;
        }
    }


}





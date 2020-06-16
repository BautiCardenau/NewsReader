package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> news = new ArrayList<>();
    static ArrayList<String> urls = new ArrayList<>();
    static ArrayList<String> topStoriesId = new ArrayList<>();
    static ArrayAdapter arrayAdapter;
    SQLiteDatabase articlesDB;
    ListView listView;

    public class DownloadTopStoriesTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String topStoriesResult = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    topStoriesResult += current;
                    data = reader.read();
                }

                return topStoriesResult;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray arr = new JSONArray(s);
                for (int i = 0; i < 10;i++) {
                    String id = arr.get(i).toString();
                    topStoriesId.add(id);
                    Log.i("id",id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //RETRIEVE NEWS DATA
            String result = null;
            for (String id : topStoriesId) {
                DownloadTask downloadTask = new DownloadTask();
                try {
                    String url = "https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty";
                    Log.i("url",url);

                    result = downloadTask.execute("https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty").get();
                    Log.i("result", result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection;

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String title = jsonObject.getString("title");
                String url = jsonObject.getString("url");

                if (!title.equals("")){
                    news.add(title);
                    urls.add(url);
                }
                } catch (Exception e) {
                e.printStackTrace();
            }

            arrayAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SAVE DATA ON SQL
        articlesDB = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        articlesDB.execSQL("CREATE TABLE IF NOT EXISTS articles (id INTEGER PRIMARY KEY, articleId INTEGER, title VARCHAR, content VARCHAR)");


        listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,news);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),SecondActivity.class);
                intent.putExtra("News clicked",i);
                startActivity(intent);
            }
        });

        //RETRIEVE TOP STORIES
        String topStoriesResult = null;
        DownloadTopStoriesTask downloadTopStoriesTask = new DownloadTopStoriesTask();

        try {
            topStoriesResult = downloadTopStoriesTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty").get();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}

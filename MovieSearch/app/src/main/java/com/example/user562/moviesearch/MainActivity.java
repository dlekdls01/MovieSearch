package com.example.user562.moviesearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.example.user562.moviesearch.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding layout;
    ProgressDialog progressDialog;
    Handler handler = new Handler();
    boolean isLastView = false;
    boolean isFininsh = true;
    int finishdate = 100000;

    ArrayList<Movie> movies = new ArrayList<Movie>();
    ArrayList<Movie> addMovies = new ArrayList<Movie>();
    MyAdapter myAdapter = null;

    //api url
    private String CLIENT_ID = "qHBsG71pIg49DyjCKGsd";
    private String CLIENT_SECRET = "7CQYm4DjQn";
    private int URL_DISPLAY = 10;
    private int URL_START = 1; //1~1000
    private String URL_SEARCH = "https://openapi.naver.com/v1/search/movie.json?query=";
    private String URL_API = "";
    private String keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},1);
        }

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait");

        myAdapter = new MyAdapter(this, movies);
        layout.listV.setAdapter(myAdapter);
        layout.listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("super","click "+position);
                String url = ((Movie)myAdapter.getItem(position)).link;
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("LINK",url);
                startActivity(intent);
            }
        });
        layout.listV.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(isFininsh && isLastView && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    URL_START += URL_DISPLAY;
                    getAPI(keyword);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isLastView = (totalItemCount>0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            }
        });

    }

    public void onClick(View v){
        keyword = layout.edt.getText().toString();
        if(keyword.isEmpty())
            return;

        URL_START = 1;
        finishdate = 100000;
        isFininsh = true;
        progressDialog.show();
        myAdapter.clear();
        getAPI(keyword);
    }

    public void getAPI(final String keyword){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = null;

                try{
                    String query = URLEncoder.encode(keyword, "utf-8");
                    URL_SEARCH = "https://openapi.naver.com/v1/search/movie.json?query=" + query + "&display=" + URL_DISPLAY +"&start=" + URL_START;
                    URL url = new URL(URL_SEARCH);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
                    conn.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

                    int responseCode = conn.getResponseCode();
                    BufferedReader bufferedReader;
                    if(responseCode == 200)
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    else
                        bufferedReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));

                    StringBuilder sb = new StringBuilder();
                    String line;

                    while((line = bufferedReader.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    bufferedReader.close();
                    conn.disconnect();

                    result = sb.toString().trim();
                }
                catch (Exception e){
                    Log.d("super","getapi error: "+e.toString());
                }

                if(jsonPasing(result)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myAdapter.add(myAdapter.getCount(), addMovies);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public boolean jsonPasing(String jsonstr){

        try{
            JSONObject jsonObject = new JSONObject(jsonstr);
            JSONArray items = jsonObject.getJSONArray("items");
            JSONObject item;
            addMovies.clear();

            for(int i=0; i<items.length(); i++){
                item = items.getJSONObject(i);
                Movie movie = new Movie();

                movie.title = item.getString("title");
                movie.link = item.getString("link");
                movie.image = item.getString("image");
                movie.pubDate = item.getString("pubDate");
                movie.director = item.getString("director");
                movie.actor = item.getString("actor");
                movie.userRating = item.getString("userRating");
                movie.bitmap = null;

                if(finishdate < Integer.valueOf(movie.pubDate)){
                    isFininsh = false;
                    break;
                }
                finishdate = Integer.valueOf(movie.pubDate);

                addMovies.add(movie);
                imageLoad(movie.image, i);
            }
            return true;
        }
        catch (Exception e){
            Log.d("super","jsonPasing error "+e.toString());
        }
        return false;
    }


    public void imageLoad(final String str, final int idx){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Bitmap bitmap = null;
                    URL url = new URL(str);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    addMovies.get(idx).bitmap = bitmap;
                }
                catch (Exception e){
                    Log.d("super","imageload error "+e.toString());
                }
            }
        });
        thread.start();
    }
}

package com.example.user562.moviesearch;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user562.moviesearch.databinding.ItemlayoutBinding;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.os.Handler;
import java.util.logging.LogRecord;

/**
 * Created by user562 on 2018-12-14.
 */

public class MyAdapter extends BaseAdapter {
    Context context = null;
    ArrayList<Movie> data = null;
    LayoutInflater layoutInflater = null;
    Handler handler = new Handler();

    public MyAdapter(Context context, ArrayList<Movie> data){
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(int position, ArrayList<Movie> addData){
        data.addAll(position, addData);
        notifyDataSetChanged();
    }
    public void clear(){
        data.clear();
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemlayout = convertView;
        ViewHolder holder = null;

        if (itemlayout == null) {
            itemlayout = layoutInflater.inflate(R.layout.itemlayout, null);
            holder = new ViewHolder();
            holder.itemLayout = DataBindingUtil.bind(itemlayout);
            itemlayout.setTag(holder);
        } else {
            holder = (ViewHolder) itemlayout.getTag();
        }

        holder.itemLayout.title.setText(Html.fromHtml(data.get(position).title));
        holder.itemLayout.pubDate.setText(data.get(position).pubDate);
        holder.itemLayout.director.setText(data.get(position).director);
        holder.itemLayout.actor.setText(data.get(position).actor);
        holder.itemLayout.userRating.setRating(Float.valueOf(data.get(position).userRating)/(float)2.0);
        holder.itemLayout.image.setImageBitmap(data.get(position).bitmap);

        return itemlayout;
    }

    class ViewHolder{
        ItemlayoutBinding itemLayout;
    }


}

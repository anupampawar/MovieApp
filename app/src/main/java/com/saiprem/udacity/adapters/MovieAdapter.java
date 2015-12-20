package com.saiprem.udacity.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.saiprem.udacity.MovieDetailsActivity;
import com.saiprem.udacity.R;
import com.saiprem.udacity.pojos.MovieDetailsBean;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by anupam on 13/12/2015.
 */
public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private List<MovieDetailsBean> lstMovieDetailsBean;

    public MovieAdapter(Context c, List<MovieDetailsBean> lstMovies) {
        mContext = c;
        this.lstMovieDetailsBean = lstMovies;
    }

    @Override
    public int getCount() {
        return lstMovieDetailsBean.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gvMovies;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView textView;
        ImageView imageView;
        MovieDetailsBean mdb = lstMovieDetailsBean.get(position);

        if (convertView == null) {
            gvMovies = new View(mContext);
            gvMovies = inflater.inflate(R.layout.movie_grid_items, null);
            //textView = (TextView) gvMovies.findViewById(R.id.grid_text);
            imageView = (ImageView)gvMovies.findViewById(R.id.grid_image);
            //textView.setText(mdb.getTitle());
            imageView.setTag(mdb);
            Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185//" + mdb.getPoster_path()).into(imageView);
        } else {
            gvMovies = (View) convertView;
            imageView = (ImageView)gvMovies.findViewById(R.id.grid_image);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieDetailsBean selMovieBeanObj =(MovieDetailsBean) v.getTag();
                Bundle mb = new Bundle();
                mb.putSerializable("selMovieObj", selMovieBeanObj);
                Intent mdIntent = new Intent(mContext, MovieDetailsActivity.class);
                mdIntent.putExtras(mb);
                mContext.startActivity(mdIntent);
            }
        });

        return gvMovies;
    }
}

package com.saiprem.udacity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.saiprem.udacity.pojos.MovieDetailsBean;
import com.squareup.picasso.Picasso;


public class MovieDetailsActivity extends AppCompatActivity {

    private Context mdContext;
    private MovieDetailsBean mvDetailBean;
    private TextView tvOrgTitle;
    private TextView tvRelDate;
    private TextView tvRating;
    private TextView tvDesc;
    private ImageView ivPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mdContext= this;

        tvOrgTitle = (TextView) findViewById(R.id.tvOrgTitle);
        tvRelDate = (TextView) findViewById(R.id.tvReleaseOn);
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvDesc = (TextView) findViewById(R.id.tvOverview);
        ivPoster = (ImageView) findViewById(R.id.ivPoster);

        Bundle extras = getIntent().getExtras();

        if(null!=extras) {

            mvDetailBean = (MovieDetailsBean) extras.getSerializable("selMovieObj");
            String pUrl = getString(R.string.poster_base_path);

            tvOrgTitle.setText(mvDetailBean.getOriginal_title());
            Picasso.with(mdContext).load(pUrl + mvDetailBean.getPoster_path()).into(ivPoster);
            tvRelDate.setText("Release Date:  "+mvDetailBean.getRelease_date());
            tvRating.setText("User Rating:  "+mvDetailBean.getVote_average().toString());
            tvDesc.setText(mvDetailBean.getOverview());
        }
    }
}

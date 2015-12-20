package com.saiprem.udacity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.saiprem.udacity.adapters.MovieAdapter;
import com.saiprem.udacity.pojos.MovieDetailsBean;
import com.saiprem.udacity.prefs.SettingsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainLayoutActivity extends AppCompatActivity {

    private GridView movie_grid;
    public Context mContext;
    private int pageNo=1;
    private List<MovieDetailsBean> lstMovies;
    private boolean loadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "App By Anupam Pawar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mContext = this;
        movie_grid = (GridView) findViewById(R.id.gvmoviegrid);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String syncConnPref = sharedPref.getString(getString(R.string.pref_sort), getString(R.string.spinner_default));
        Log.i(">>> syncConnPref >>** ", syncConnPref);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        lstMovies = new ArrayList<>();
        FetchMoviesAsyncTask movieTask =  new FetchMoviesAsyncTask();
        movieTask.execute();

        //Here is where the magic happens
        movie_grid.setOnScrollChangeListener(new AbsListView.OnScrollChangeListener() {

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                //what is the bottom item that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;
                Log.i("***** LAST SCREEN ** ",""+lastInScreen);
                Log.i("** LvisibleCount ** ",""+visibleItemCount);
                //is the bottom item visible & not loading more already ? Load more !
                if ((lastInScreen == totalItemCount) && !(loadingMore) && totalItemCount != 0
                        && lastInScreen > (lstMovies.size() - 18)) {
                    loadingMore = true;
                    FetchMoviesAsyncTask movieTask = new FetchMoviesAsyncTask();
                    movieTask.execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_layout, menu);
        MenuItem spinnerItem = menu.findItem(R.id.mnu_spinner);
        View view = spinnerItem.getActionView();

        //Spinner spinnerItem = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mContext,
                R.array.pref_array_entries, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if(view instanceof Spinner) {
            Log.i("IF..... ",view.toString());
            final Spinner spinner = (Spinner) view;
            spinner.setAdapter(adapter);
        }else{
            Log.i("in ELSE.... ","View Grtting NULL");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

       /*  if (id == R.id.action_settings) {

           getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
            return false;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public class FetchMoviesAsyncTask extends AsyncTask<String, Void , String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String respJsonStr = null;
        MovieDetailsBean movieDetailsBean;
        ProgressDialog pDialog;

        protected void onPreExecute() {
            if (loadingMore) {

            }
            //pDialog = ProgressDialog.show(mContext, "MovieApp", "Loading Movies", true);
        }

        protected String doInBackground(String... str) {

            try {
                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=API_KEY_VALUE&page=" + pageNo);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                StringBuffer buffer = new StringBuffer();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream != null) {
                    //http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=9fa29adda04fb75cc952dda13a0fe4a6
                    JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
                }
                if (inputStream == null) {
                    respJsonStr = null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    respJsonStr = null;
                }
                respJsonStr = buffer.toString();

                JSONObject jsonRootObject = new JSONObject(respJsonStr);
                JSONArray jsonArray = jsonRootObject.optJSONArray("results");

                //Iterate the jsonArray and print the info of JSONObjects
                for (int i = 0; i < jsonArray.length(); i++) {
                    MovieDetailsBean mdb = new MovieDetailsBean();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String posterPath = jsonObject.optString("poster_path").toString();
                    Boolean isAdult = Boolean.valueOf(jsonObject.optString("adult"));
                    String overview = jsonObject.optString("overview").toString();
                    String releaseDate = jsonObject.optString("release_date").toString();
                    String orgTitle = jsonObject.optString("original_title").toString();
                    String backdropPath = jsonObject.optString("backdrop_path").toString();
                    int id = Integer.parseInt(jsonObject.optString("id").toString());
                    String title = jsonObject.optString("title").toString();
                    Double popularity = Double.valueOf(jsonObject.optString("popularity"));
                    Integer votecnt = Integer.valueOf(jsonObject.optString("vote_count"));
                    Double voteAvg = Double.valueOf(jsonObject.optString("vote_average"));

                    mdb.setPoster_path(posterPath);
                    mdb.setAdult(isAdult);
                    mdb.setOverview(overview);
                    mdb.setRelease_date(releaseDate);
                    mdb.setOriginal_title(orgTitle);
                    mdb.setBackdrop_path(backdropPath);
                    mdb.setId(id);
                    mdb.setTitle(title);
                    mdb.setPopularity(popularity);
                    mdb.setVote_count(votecnt);
                    mdb.setVote_average(voteAvg);

                    lstMovies.add(mdb);
                    //Log.i("Movies OBJ  ===> ",mdb.toString());
                }
            } catch (Exception e) {
                Log.e("[MainLayoutActivity]", "Error ", e);
                respJsonStr = null;
                loadingMore = false;
            } finally {
                loadingMore = false;
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            } //finally
            return null;
        }

        protected void onPostExecute(String result) {

            if (lstMovies.size() > 0) {
                // pDialog.dismiss();
                movie_grid.setAdapter(new MovieAdapter(MainLayoutActivity.this, lstMovies));
                movie_grid.setSelection(lstMovies.size() - 2);
                pageNo = pageNo + 1;
            }
            loadingMore = false;
        }
    }
}

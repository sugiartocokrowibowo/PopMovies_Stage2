package intermediate.iak.asaila.popmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.adapter.MovieAdapter;
import intermediate.iak.asaila.popmovies.model.Movie;
import intermediate.iak.asaila.popmovies.utils.NetworkUtils;
import intermediate.iak.asaila.popmovies.utils.TmdbMovieUtils;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.OnItemClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>{

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int TMDB_LOADER_ID = 0;

    private MovieAdapter mAdapter;

    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    //hold textview for displaying internet availability
    private TextView tvInternet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.pb_main);
        tvInternet = (TextView) findViewById(R.id.tv_no_internet);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_popular_movies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(getApplicationContext(),this);
        mRecyclerView.setAdapter(mAdapter);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey("movies")){
            ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList("movies");
            mAdapter.setMovieData(movies);
            return;
        }

        makeTmdbMovieRequest(sharedPreferences
                .getString(getString(R.string.pref_key_sort_movie),getString(R.string.pref_title_popular)));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Movie movie) {
        startActivity(new Intent(getApplicationContext(),MovieDetailActivity.class)
                .putExtra("movie",movie));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mAdapter.getMovieData() != null) outState.putParcelableArrayList("movies",mAdapter.getMovieData());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_sort_movie))) {
            invalidateData();
            makeTmdbMovieRequest(sharedPreferences
                    .getString(key, getString(R.string.pref_key_popular)));

        }
    }

    private void makeTmdbMovieRequest(String sort){
        LoaderManager manager = getSupportLoaderManager();
        Loader<ArrayList<Movie>> tmdbMovieLoader = manager.getLoader(TMDB_LOADER_ID);

        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.pref_key_sort_movie),sort);
        if (tmdbMovieLoader != null) {
            getSupportLoaderManager().restartLoader(TMDB_LOADER_ID,bundle,this);
        } else {
            getSupportLoaderManager().initLoader(TMDB_LOADER_ID,bundle,this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister VisualizerActivity as an OnPreferenceChangedListener to avoid any memory leaks.
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            ArrayList<Movie> mMovieData;
            @Override
            protected void onStartLoading() {
                if (args == null){
                    return;
                }
                if (mMovieData != null){
                    deliverResult(mMovieData);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                String sort = args.getString(getString(R.string.pref_key_sort_movie));

                try {
                    URL url = NetworkUtils.buildPopularUrl(sort);
                    String jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                    Log.d(TAG,jsonString);
                    return TmdbMovieUtils.getMoviesFromJson(jsonString);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                mMovieData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        progressBar.setVisibility(View.INVISIBLE);
        mAdapter.setMovieData(data);
        if (data == null){
            showErrorMessageView(getString(R.string.general_error_err));
        } else {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mAdapter.setMovieData(null);
    }


    private void showMovieDataView(){
        tvInternet.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessageView(String error_msg){
        mRecyclerView.setVisibility(View.INVISIBLE);
        tvInternet.setVisibility(View.VISIBLE);
        tvInternet.setText(error_msg);
    }

    /*
     * Checking for network connection
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

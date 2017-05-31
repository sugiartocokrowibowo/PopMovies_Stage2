package intermediate.iak.asaila.popmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import intermediate.iak.asaila.popmovies.data.MovieContract;
import intermediate.iak.asaila.popmovies.data.PopMoviesPreferences;
import intermediate.iak.asaila.popmovies.model.Movie;
import intermediate.iak.asaila.popmovies.utils.NetworkUtils;
import intermediate.iak.asaila.popmovies.utils.TmdbMovieUtils;

public class MainActivity extends AppCompatActivity implements
        MovieAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int TMDB_LOADER_ID = 0;

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_TITLE = 1;
    public static final int INDEX_MOVIE_POSTER_PATH = 2;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 3;
    public static final int INDEX_MOVIE_OVERVIEW = 4;
    public static final int INDEX_MOVIE_RELEASE_DATE = 5;

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


        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        if (savedInstanceState != null && savedInstanceState.containsKey("movies")){
            if (!PopMoviesPreferences.getPreferredMovieSortOption(this).equals(getString(R.string.pref_key_favorited))) {
                ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList("movies");
                if (movies.size() == 0){
                    showErrorMessageView(getString(R.string.no_data_err));
                } else {
                    mAdapter.setMovieData(movies);
                }
                return;
            }
        }

        makeTmdbMovieRequest(PopMoviesPreferences.getPreferredMovieSortOption(this));
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
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
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

                if (sort.equals(getString(R.string.pref_key_favorited))) {
                    Uri favoriteMovieUri = MovieContract.MovieEntry.CONTENT_URI;

                    Cursor cursor = null;
                    try {
                        cursor = getContentResolver().query(favoriteMovieUri,
                                MAIN_MOVIE_PROJECTION,null,null,null);
                        ArrayList<Movie> favoriteMovie = new ArrayList<>();

                        if (cursor != null) {
                            while (cursor.moveToNext()){
                                Movie movie = new Movie(cursor.getInt(INDEX_MOVIE_ID),
                                        cursor.getString(INDEX_MOVIE_TITLE),
                                        cursor.getString(INDEX_MOVIE_POSTER_PATH),
                                        (float) cursor.getDouble(INDEX_MOVIE_VOTE_AVERAGE),
                                        cursor.getString(INDEX_MOVIE_OVERVIEW),
                                        cursor.getString(INDEX_MOVIE_RELEASE_DATE));
                                favoriteMovie.add(movie);
                            }
                        }

                        return favoriteMovie;
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to asynchronously load data.");
                        e.printStackTrace();
                        return null;
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                } else {
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
        } else if (data.size() == 0) {
            showErrorMessageView(getString(R.string.no_data_err));
        }else {
            showMovieDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key == getString(R.string.pref_key_sort_movie)){
            makeTmdbMovieRequest(PopMoviesPreferences.getPreferredMovieSortOption(this));
        }
    }
}

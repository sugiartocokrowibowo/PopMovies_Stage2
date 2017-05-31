package intermediate.iak.asaila.popmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.adapter.ReviewAdapter;
import intermediate.iak.asaila.popmovies.adapter.VideoAdapter;
import intermediate.iak.asaila.popmovies.constant.AppConfig;
import intermediate.iak.asaila.popmovies.data.MovieContract;
import intermediate.iak.asaila.popmovies.model.Movie;
import intermediate.iak.asaila.popmovies.model.Review;
import intermediate.iak.asaila.popmovies.model.Video;
import intermediate.iak.asaila.popmovies.utils.NetworkUtils;
import intermediate.iak.asaila.popmovies.utils.TmdbMovieUtils;

public class MovieDetailActivity extends AppCompatActivity implements VideoAdapter.OnItemVideoClickListener{
    private static final String TAG = MovieDetailActivity.class.getSimpleName();
    private Movie movie;
    private Cursor cursor;

    private RecyclerView videoRecyclerView, reviewRecyclerView;
    private VideoAdapter videoAdapter;
    private ReviewAdapter reviewAdapter;

    private TextView tvErrVideo,tvErrReview;
    private ProgressBar pbVideo,pbReview;

    private static final int VIDEO_LOADER = 200;
    private static final int REVIEW_LOADER = 201;

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
    };

    Button btnFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initWidget();

        videoRecyclerView = (RecyclerView) findViewById(R.id.rv_videos);
        reviewRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        videoRecyclerView.setLayoutManager(layoutManager1);
        reviewRecyclerView.setLayoutManager(layoutManager2);

        videoRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setHasFixedSize(true);

        videoAdapter = new VideoAdapter(getApplicationContext(),this);
        reviewAdapter = new ReviewAdapter(getApplicationContext());

        videoRecyclerView.setAdapter(videoAdapter);
        reviewRecyclerView.setAdapter(reviewAdapter);

            LoaderManager loaderManager = getSupportLoaderManager();
            Bundle bundle = new Bundle();
            bundle.putLong("id",movie.getId());
            if (savedInstanceState != null && savedInstanceState.containsKey("videos")){
                videoAdapter.swapData(savedInstanceState.<Video>getParcelableArrayList("videos"));
            } else {
                if (loaderManager.getLoader(VIDEO_LOADER) == null) {
                    loaderManager.initLoader(VIDEO_LOADER,bundle,mLoaderCallbackVideo);
                } else {
                    loaderManager.restartLoader(VIDEO_LOADER,bundle,mLoaderCallbackVideo);
                }
            }

            if (savedInstanceState != null && savedInstanceState.containsKey("reviews")){
                reviewAdapter.swapData(savedInstanceState.<Review>getParcelableArrayList("reviews"));
            } else {
                if (loaderManager.getLoader(REVIEW_LOADER) == null) {
                    loaderManager.initLoader(REVIEW_LOADER,bundle,mLoaderCallbackReview);
                } else {
                    loaderManager.restartLoader(REVIEW_LOADER,bundle,mLoaderCallbackReview);
                }
            }
    }
    /*
     * Initialize Widge Content
     */
    private void initWidget(){
        movie = getIntent().getParcelableExtra("movie");

        Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(movie.getId());
        cursor = getContentResolver().query(uri,MAIN_MOVIE_PROJECTION,null,null,null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        TextView tvOverview = (TextView) findViewById(R.id.tv_synopsis);
        TextView tvDate = (TextView) findViewById(R.id.tv_date);
        TextView tvVote = (TextView) findViewById(R.id.tv_rating);
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);

        toolbar.setTitle(movie.getOriginal_title());
        tvOverview.setText(movie.getOverview());
        tvVote.setText(""+String.valueOf(movie.getVote_average()) + "/10");
        tvDate.setText(movie.getRelease_date());
        tvTitle.setText(movie.getOriginal_title());

        btnFav = (Button) findViewById(R.id.btn_favorite);
        initializeButtonFavorite();

        tvErrReview = (TextView) findViewById(R.id.tv_err_review);
        tvErrVideo = (TextView) findViewById(R.id.tv_err_video);

        pbReview = (ProgressBar) findViewById(R.id.pb_review);
        pbVideo = (ProgressBar) findViewById(R.id.pb_video);

        //Check whether internet connection available and id sent from previous intent
        if (isNetworkAvailable()){
            String poster_path = AppConfig.IMG_BASE_URL +
                    movie.getPoster_image();
            final ImageView ivPoster = (ImageView) findViewById(R.id.iv_poster_detail);
            Picasso.with(this).load(poster_path).into(ivPoster, new Callback() {
                @Override
                public void onSuccess() {}
                @Override
                public void onError() {
                    ivPoster.setImageDrawable(getResources()
                            .getDrawable(R.drawable.ic_error_outline_black_24dp));
                }
            });
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (videoAdapter.getVideoData() != null) {
            outState.putParcelableArrayList("videos",videoAdapter.getVideoData());
        }

        if (reviewAdapter.getReviewData() != null) {
            outState.putParcelableArrayList("reviews",reviewAdapter.getReviewData());
        }

        super.onSaveInstanceState(outState);
    }

    /*
         * Update the favorite button if the movie is already inserted to favorite movie table
         */
    private void initializeButtonFavorite() {
        if (cursor.getCount() != 0 ){
            btnFav.setSelected(true);
            updateViewUnfavoriteButton();
        } else {
            btnFav.setSelected(false);
            updateViewFavoriteButton();
        }
    }

    private LoaderManager.LoaderCallbacks<ArrayList<Video>> mLoaderCallbackVideo =
            new LoaderManager.LoaderCallbacks<ArrayList<Video>>() {

        @Override
        public Loader<ArrayList<Video>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<Video>>(getApplicationContext()) {

                private ArrayList<Video> mVideo;
                @Override
                protected void onStartLoading() {
                    if (args == null) return;

                    if (mVideo != null){
                        deliverResult(mVideo);
                    } else {
                        pbVideo.setVisibility(View.VISIBLE);
                        forceLoad();
                    }
                }

                @Override
                public ArrayList<Video> loadInBackground() {
                    try {
                        URL url = NetworkUtils.buildReviewsOrVideosUrl(
                                args.getLong("id"),"videos");
                        String jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                        Log.d(TAG,jsonString);
                        return TmdbMovieUtils.getVideosFromJson(jsonString);
                    }catch (Exception e){
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(ArrayList<Video> data) {
                    mVideo = data;
                    super.deliverResult(data);
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<Video>> loader, ArrayList<Video> data) {
            pbVideo.setVisibility(View.INVISIBLE);
            videoAdapter.swapData(data);
            if ( data == null) {
                showErrorVideoView(getString(R.string.general_error_err));
            } else if (data.size() == 0) {
                showErrorVideoView(getString(R.string.no_data_err));
            } else {
                showVideoData();
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<Video>> loader) {}
    };

    private LoaderManager.LoaderCallbacks<ArrayList<Review>> mLoaderCallbackReview =
            new LoaderManager.LoaderCallbacks<ArrayList<Review>>() {

                @Override
                public Loader<ArrayList<Review>> onCreateLoader(int id, final Bundle args) {
                    return new AsyncTaskLoader<ArrayList<Review>>(getApplicationContext()) {

                        private ArrayList<Review> mReview;
                        @Override
                        protected void onStartLoading() {
                            if (args == null) return;

                            if (mReview != null){
                                deliverResult(mReview);
                            } else {
                                pbReview.setVisibility(View.VISIBLE);
                                forceLoad();
                            }
                        }

                        @Override
                        public ArrayList<Review> loadInBackground() {
                            try {
                                URL url = NetworkUtils.buildReviewsOrVideosUrl(
                                        args.getLong("id"),"reviews");
                                String jsonString = NetworkUtils.getResponseFromHttpUrl(url);
                                Log.d(TAG,jsonString);
                                return TmdbMovieUtils.getReviewFromJson(jsonString);
                            }catch (Exception e){
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        public void deliverResult(ArrayList<Review> data) {
                            mReview = data;
                            super.deliverResult(data);
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<Review>> loader, ArrayList<Review> data) {
                    pbReview.setVisibility(View.INVISIBLE);
                    reviewAdapter.swapData(data);
                    if ( data == null) {
                        showErrorReviewView(getString(R.string.general_error_err));
                    } else if (data.size() == 0) {
                        showErrorReviewView(getString(R.string.no_data_err));
                    } else {
                        showReviewData();
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<Review>> loader) {}
            };


    /*
     * Check Connectivity
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private View.OnClickListener unfavoriteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Uri uri = MovieContract.MovieEntry.buildMovieUriWithId(movie.getId());
            int numRow = getContentResolver().delete(uri,null,null);
            if (numRow != 0){
                btnFav.setSelected(false);
                updateViewFavoriteButton();
                btnFav.setOnClickListener(favoriteClickListener);
            }
        }
    };

    private View.OnClickListener favoriteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,movie.getId());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,movie.getOriginal_title());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVERAGE,movie.getVote_average());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,movie.getOverview());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,movie.getPoster_image());
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,movie.getRelease_date());
            Uri returnUri = getContentResolver().insert(uri,cv);
            if (returnUri != null){
                btnFav.setSelected(true);
                updateViewUnfavoriteButton();
                btnFav.setOnClickListener(unfavoriteClickListener);
            }
        }
    };

    void updateViewUnfavoriteButton(){
        btnFav.setOnClickListener(unfavoriteClickListener);
        btnFav.setText(getString(R.string.mark_as_unfavorite));

        Drawable normalDrawable = getResources().getDrawable(R.drawable.ic_favorite_black_24dp);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.colorWhite));
        btnFav.setCompoundDrawablesWithIntrinsicBounds(wrapDrawable,null,null,null);
        btnFav.setTextColor(getResources().getColor(R.color.colorWhite));


    }

    void updateViewFavoriteButton(){
        btnFav.setOnClickListener(favoriteClickListener);
        btnFav.setText(getString(R.string.mark_as_favorite));

        Drawable normalDrawable = getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, getResources().getColor(R.color.colorBlack));
        btnFav.setCompoundDrawablesWithIntrinsicBounds(wrapDrawable,null,null,null);
        btnFav.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null){
            cursor.close();
        }
    }

    void showErrorReviewView(String msg){
        reviewRecyclerView.setVisibility(View.INVISIBLE);
        tvErrReview.setVisibility(View.VISIBLE);
        tvErrReview.setText(msg);
    }

    void showReviewData(){
        tvErrReview.setVisibility(View.INVISIBLE);
        reviewRecyclerView.setVisibility(View.VISIBLE);
    }

    void showErrorVideoView(String msg){
        videoRecyclerView.setVisibility(View.INVISIBLE);
        tvErrVideo.setVisibility(View.VISIBLE);
        tvErrVideo.setText(msg);
    }

    void showVideoData(){
        tvErrVideo.setVisibility(View.INVISIBLE);
        videoRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemVideoClick(String key) {
        Intent applicationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + key));
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + key));
        try {
            startActivity(applicationIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(browserIntent);
        }
    }
}

package intermediate.iak.asaila.popmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import intermediate.iak.asaila.popmovies.constant.AppConfig;
import intermediate.iak.asaila.popmovies.model.Movie;

public class MovieDetailActivity extends AppCompatActivity{
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        initWidget();
    }

    /*
     * Initialize Widge Content
     */
    private void initWidget(){
        movie = getIntent().getParcelableExtra("movie");

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

        //Check whether internet connection available and id sent from previous intent
        if (isNetworkAvailable()){
            String poster_path = AppConfig.IMG_BASE_URL +
                    movie.getPoster_image();
            ImageView ivPoster = (ImageView) findViewById(R.id.iv_poster_detail);
            Picasso.with(this).load(poster_path).into(ivPoster);
        }
    }

    /*
     * Check Connectivity
     */
    private boolean isNetworkAvailable(){
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}

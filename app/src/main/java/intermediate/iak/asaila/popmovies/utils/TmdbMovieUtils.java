package intermediate.iak.asaila.popmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.model.Movie;
import intermediate.iak.asaila.popmovies.model.Review;
import intermediate.iak.asaila.popmovies.model.Video;

/**
 * Created by arisal on 30/05/17.
 * Utility functions to handle Tmdb Movie JSON data
 */

public final class TmdbMovieUtils {

    public static final ArrayList<Movie> getMoviesFromJson(String movieJsonString)
            throws JSONException{
        final String COLUMN_MOVIE_RESULTS = "results";
        final String COLUMN_MOVIE_ID = "id";
        final String COLUMN_MOVIE_TITLE = "original_title";
        final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        final String COLUMN_MOVIE_OVERVIEW = "overview";
        final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        ArrayList<Movie> parsedMovieData = new ArrayList<>();

        JSONObject movieJson = new JSONObject(movieJsonString);
        JSONArray movieArray = movieJson.getJSONArray(COLUMN_MOVIE_RESULTS);

        JSONObject jMovie;
        Movie movie;
        for (int i=0; i<movieArray.length(); i++){
            jMovie = movieArray.getJSONObject(i);
            movie = new Movie(jMovie.getLong(COLUMN_MOVIE_ID),
                    jMovie.getString(COLUMN_MOVIE_TITLE),
                    jMovie.getString(COLUMN_MOVIE_POSTER_PATH),
                    (float) jMovie.getDouble(COLUMN_MOVIE_VOTE_AVERAGE),
                    jMovie.getString(COLUMN_MOVIE_OVERVIEW),
                    jMovie.getString(COLUMN_MOVIE_RELEASE_DATE));
            parsedMovieData.add(movie);
        }

        return parsedMovieData;
    }

    public static final ArrayList<Video> getVideosFromJson(String videoJsonString)
            throws JSONException{
        final String COLUMN_MOVIE_RESULTS = "results";
        final String COLUMN_MOVIE_NAME = "name";
        final String COLUMN_MOVIE_SITE = "site";
        final String COLUMN_MOVIE_KEY = "key";
        ArrayList<Video> videoMovieData = new ArrayList<>();

        JSONObject videoJson = new JSONObject(videoJsonString);
        JSONArray videoArray = videoJson.getJSONArray(COLUMN_MOVIE_RESULTS);

        JSONObject jVideo;
        Video video;
        for (int i=0; i<videoArray.length(); i++){
            jVideo = videoArray.getJSONObject(i);
            video = new Video(jVideo.getString(COLUMN_MOVIE_NAME),
                    jVideo.getString(COLUMN_MOVIE_SITE),
                    jVideo.getString(COLUMN_MOVIE_KEY));
            videoMovieData.add(video);
        }

        return videoMovieData;
    }

    public static final ArrayList<Review> getReviewFromJson(String movieJsonString)
            throws JSONException{
        final String COLUMN_MOVIE_RESULTS = "results";
        final String COLUMN_MOVIE_AUTHOR = "author";
        final String COLUMN_MOVIE_CONTENT = "content";
        ArrayList<Review> parsedReviewData = new ArrayList<>();

        JSONObject reviewJson = new JSONObject(movieJsonString);
        JSONArray reviewArray = reviewJson.getJSONArray(COLUMN_MOVIE_RESULTS);

        JSONObject jReview;
        Review review;
        for (int i=0; i<reviewArray.length(); i++){
            jReview = reviewArray.getJSONObject(i);
            review = new Review(jReview.getString(COLUMN_MOVIE_AUTHOR),
                    jReview.getString(COLUMN_MOVIE_CONTENT));
            parsedReviewData.add(review);
        }

        return parsedReviewData;
    }
}

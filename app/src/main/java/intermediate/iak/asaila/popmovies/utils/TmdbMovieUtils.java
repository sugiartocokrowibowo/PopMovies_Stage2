package intermediate.iak.asaila.popmovies.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.model.Movie;

/**
 * Created by arisal on 30/05/17.
 * Utility functions to handle Tmdb Movie JSON data
 */

public final class TmdbMovieUtils {

    public static final ArrayList<Movie> getMoviesFromJson(String movieJsonString)
            throws JSONException{

        final String MOVIE_RESULTS = "results";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "original_title";
        final String MOVIE_RELEASE_DATE = "release_date";
        final String MOVIE_VOTE_AVERAGE = "vote_average";
        final String MOVIE_OVERVIEW = "overview";
        final String MOVIE_POSTER_PATH = "poster_path";

        ArrayList<Movie> parsedMovieData = new ArrayList<>();

        JSONObject movieJson = new JSONObject(movieJsonString);
        JSONArray movieArray = movieJson.getJSONArray(MOVIE_RESULTS);

        JSONObject jMovie;
        Movie movie;
        for (int i=0; i<movieArray.length(); i++){
            jMovie = movieArray.getJSONObject(i);
            movie = new Movie(jMovie.getLong(MOVIE_ID),
                    jMovie.getString(MOVIE_TITLE),
                    jMovie.getString(MOVIE_POSTER_PATH),
                    (float) jMovie.getDouble(MOVIE_VOTE_AVERAGE),
                    jMovie.getString(MOVIE_OVERVIEW),
                    jMovie.getString(MOVIE_RELEASE_DATE));
            parsedMovieData.add(movie);
        }

        return parsedMovieData;
    }
}

package intermediate.iak.asaila.popmovies.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import intermediate.iak.asaila.popmovies.constant.AppConfig;

/**
 * Created by arisal on 27/05/17.
 * Class for handle Network Request
 */

public class NetworkUtils {
    private final static String PARAM_API = "api_key";
    private final static String PATH_MOVIE = "movie";

    public static URL buildPopularUrl(String type) {
        Uri builtUri = Uri.parse(AppConfig.API_BASE_URL)
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(type)
                .appendQueryParameter(PARAM_API,AppConfig.API_KEY_TMDB)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /*
     * Methods for fetching videos or reviews of a movie referenced
     * by its id
     */

    public static URL buildReviewsOrVideosUrl(long movieId, String segment){
        Uri builtUri = Uri.parse(AppConfig.API_BASE_URL)
                .buildUpon()
                .appendPath(PATH_MOVIE)
                .appendPath(String.valueOf(movieId))
                .appendPath(segment)
                .appendQueryParameter(PARAM_API,AppConfig.API_KEY_TMDB)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }


}

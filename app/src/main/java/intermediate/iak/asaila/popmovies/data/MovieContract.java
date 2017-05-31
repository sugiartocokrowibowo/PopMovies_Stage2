package intermediate.iak.asaila.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by arisal on 30/05/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "intermediate.iak.asaila.popmovies";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Pop Movies.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that Pop Movies
     * can handle.
     */
    public static final String PATH_FAVORITE_MOVIE = "fav_movie";

    /* Inner class that defines the table contents of the favorited movie table */
    public static final class MovieEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the favorited mobie table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE_MOVIE)
                .build();

        /* Used internally as the name of our favorited movie table. */
        public static final String TABLE_NAME = "fav_movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "original_title";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";

        public static Uri buildMovieUriWithId(long id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}

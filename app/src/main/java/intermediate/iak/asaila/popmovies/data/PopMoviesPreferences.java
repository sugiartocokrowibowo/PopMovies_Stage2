package intermediate.iak.asaila.popmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import intermediate.iak.asaila.popmovies.R;

/**
 * Created by arisal on 30/05/17.
 */

public final class PopMoviesPreferences {

    public static String getPreferredMovieSortOption(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String keyForLocation = context.getString(R.string.pref_key_sort_movie);
        String defaultLocation = context.getString(R.string.pref_key_popular);

        return sp.getString(keyForLocation, defaultLocation);
    }
}

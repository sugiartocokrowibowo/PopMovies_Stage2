package intermediate.iak.asaila.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by arisal on 28/05/17.
 * Movie Model
 */

public class Movie implements Parcelable {
    private long id;
    private String original_title;
    private String poster_image;
    private float vote_average;
    private String overview;
    private String release_date;

    public Movie(long id, String original_title, String poster_image,
                 float vote_average, String overview, String release_date) {
        this.id = id;
        this.original_title = original_title;
        this.poster_image = poster_image;
        this.vote_average = vote_average;
        this.overview = overview;
        this.release_date = release_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.original_title);
        dest.writeString(this.poster_image);
        dest.writeFloat(this.vote_average);
        dest.writeString(this.overview);
        dest.writeString(this.release_date);
    }

    protected Movie(Parcel in) {
        this.id = in.readLong();
        this.original_title = in.readString();
        this.poster_image = in.readString();
        this.vote_average = in.readFloat();
        this.overview = in.readString();
        this.release_date = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setId(long id) {
        this.id = id;
    }
    public long getId() {return id;}

    public String getOriginal_title() {
        return original_title;
    }
    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_image() {
        return poster_image;
    }
    public void setPoster_image(String poster_image) {
        this.poster_image = poster_image;
    }

    public float getVote_average() {
        return vote_average;
    }
    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }
    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }
    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }
}

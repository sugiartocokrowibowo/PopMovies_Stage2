package intermediate.iak.asaila.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arisal on 31/05/17.
 */

public class Video implements Parcelable {
    private String name;
    private String site;
    private String key;

    public Video (String name, String site, String key){
        this.name = name;
        this.site = site;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeString(this.key);
    }

    protected Video(Parcel in) {
        this.name = in.readString();
        this.site = in.readString();
        this.key = in.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}

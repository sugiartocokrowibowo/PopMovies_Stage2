package intermediate.iak.asaila.popmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.R;
import intermediate.iak.asaila.popmovies.constant.AppConfig;
import intermediate.iak.asaila.popmovies.model.Movie;

/**
 * Created by arisal on 27/05/17.
 * Custom Adapter for Movie List
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private Context mContext = null;
    private ArrayList<Movie> mMovieData = null;
    private OnItemClickListener onItemClickListener = null;

    public MovieAdapter(Context context, OnItemClickListener onItemClickListener){
        mContext = context;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mMovieData == null ? 0 : mMovieData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivPoster = null;

        ViewHolder(View itemView) {
            super(itemView);
            ivPoster = (ImageView) itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        void bind(int position){
            String poster_path = AppConfig.IMG_BASE_URL +
                        mMovieData.get(position).getPoster_image();
            Picasso.with(mContext).load(poster_path).into(ivPoster);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(mMovieData.get(getAdapterPosition()));
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Movie movie);
    }

    public void setMovieData(ArrayList<Movie> data){
        mMovieData = data;
        notifyDataSetChanged();
    }

    public ArrayList<Movie> getMovieData(){
        return mMovieData;
    }

}

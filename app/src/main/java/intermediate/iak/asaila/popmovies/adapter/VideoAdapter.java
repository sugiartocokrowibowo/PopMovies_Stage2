package intermediate.iak.asaila.popmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.R;
import intermediate.iak.asaila.popmovies.model.Video;

/**
 * Created by arisal on 31/05/17.
 * Custom Video Adaptor
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private ArrayList<Video> mVideoData = new ArrayList<>();
    private Context mContext;

    private OnItemVideoClickListener onItemVideoClickListener;

    public VideoAdapter(Context mContext, OnItemVideoClickListener onItemVideoClickListener) {
        this.mContext = mContext;
        this.onItemVideoClickListener = onItemVideoClickListener;
    }

    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item_list,parent,false);

        return new VideoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapter.ViewHolder holder, int position) {
        Video video = mVideoData.get(position);
        holder.tvName.setText(video.getName());
        holder.tvSite.setText(video.getSite());

        String ytube_thumb = "http://img.youtube.com/vi/"+video.getKey()+"/0.jpg";
        Picasso.with(mContext).load(ytube_thumb).into(holder.ivThumb);
    }

    @Override
    public int getItemCount() {
        return mVideoData == null ? 0 : mVideoData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvName,tvSite;
        ImageView ivThumb;
        ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_vid_title);
            tvSite = (TextView) itemView.findViewById(R.id.tv_vid_site);
            ivThumb = (ImageView) itemView.findViewById(R.id.iv_video_thumb);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemVideoClickListener.onItemVideoClick(mVideoData.get(getAdapterPosition()).getKey());
        }
    }

    public void swapData(ArrayList<Video> mVideoData){
        this.mVideoData = mVideoData;
        notifyDataSetChanged();
    }

    public ArrayList<Video> getVideoData(){
        return mVideoData;
    }

    public interface OnItemVideoClickListener{
        void onItemVideoClick(String key);
    }
}

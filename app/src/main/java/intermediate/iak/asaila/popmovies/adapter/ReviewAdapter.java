package intermediate.iak.asaila.popmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import intermediate.iak.asaila.popmovies.R;
import intermediate.iak.asaila.popmovies.model.Review;

/**
 * Created by arisal on 31/05/17.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<Review> mReviewData = new ArrayList<>();
    private Context mContext;

    public ReviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item_list,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        Review review = mReviewData.get(position);
        holder.tvContent.setText(review.getContent());
        holder.tvAuthor.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mReviewData == null ? 0 : mReviewData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor,tvContent;
        public ViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView) itemView.findViewById(R.id.tv_rev_author);
            tvContent = (TextView) itemView.findViewById(R.id.tv_rev_content);
        }
    }

    public void swapData(ArrayList<Review> mReviewData){
        this.mReviewData = mReviewData;
        notifyDataSetChanged();
    }

    public ArrayList<Review> getReviewData(){
        return mReviewData;
    }
}

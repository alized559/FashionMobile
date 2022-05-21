package com.fashion.fashionmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.fashion.fashionmobile.R;

import java.util.ArrayList;

public class ReviewFlexBoxAdapter extends RecyclerView.Adapter<ReviewFlexBoxAdapter.ViewHolder>{

    Context context;
    ArrayList<ReviewDataModel> arrayList = new ArrayList<ReviewDataModel>();

    public ReviewFlexBoxAdapter(Context context, ArrayList<ReviewDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ReviewFlexBoxAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_review_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewFlexBoxAdapter.ViewHolder holder, int position) {

        holder.setViewModel(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture, delete;
        TextView title, subtitle;
        TextView rating;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.review_cardview);
            picture = itemView.findViewById(R.id.review_picture);
            title = itemView.findViewById(R.id.review_title);
            subtitle = itemView.findViewById(R.id.review_subtitle);
            rating = itemView.findViewById(R.id.review_ratings);
            delete = itemView.findViewById(R.id.review_delete);
        }

        public void setViewModel(ReviewDataModel model){
            picture.setImageBitmap(model.Picture);
            title.setText(model.Title);
            subtitle.setText(model.Subtitle);
            rating.setText("Rated: " + model.Ratings + " Stars");
            if(model.CanDelete){
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(model.onDeleteClicked);
            }else {
                delete.setVisibility(View.GONE);
            }
            if(model.CanVisitProduct){
                cardView.setOnClickListener(model.onCardClicked);
            }
        }
    }
}

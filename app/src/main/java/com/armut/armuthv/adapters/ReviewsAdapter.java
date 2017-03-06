package com.armut.armuthv.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.armut.armuthv.R;
import com.armut.armuthv.objects.Review;
import com.armut.armuthv.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 03/10/16.
 */

public class ReviewsAdapter extends ArrayAdapter<Review> {

    private ArrayList<Review> data;
    private LayoutInflater inflater = null;
    private final Drawable fullStar;
    private final Drawable halfStar;
    private final Drawable noStar;

    public ReviewsAdapter(Activity act, ArrayList<Review> reviewList){
        super(act, -1, reviewList);
        data = reviewList;
        inflater = (LayoutInflater) act
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        fullStar = ContextCompat.getDrawable(act, R.drawable.icn_star_full);
        halfStar = ContextCompat.getDrawable(act, R.drawable.icn_star_half);
        noStar = ContextCompat.getDrawable(act, R.drawable.icn_star_empy);
    }

    public void addData(ArrayList<Review> data){
        if(this.data == null){
            this.data = data;
        }else{
            // this.data.addAll(data);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public Review getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        final Review review = data.get(position);
        if (vi == null) {
            vi = inflater.inflate(R.layout.review_row, parent,
                    false);
            holder = new ViewHolder();
            holder.nameTv = (TextView) vi.findViewById(R.id.commenterName);
            holder.commentDateTv = (TextView) vi.findViewById(R.id.commentDate);
            holder.commentTv = (TextView) vi.findViewById(R.id.commentTv);
            //holder.thumbView = (RoundedImageView) vi.findViewById(R.id.rowProfileThumbnail);
            holder.star1 = (ImageView) vi.findViewById(R.id.star1);
            holder.star2 = (ImageView) vi.findViewById(R.id.star2);
            holder.star3 = (ImageView) vi.findViewById(R.id.star3);
            holder.star4 = (ImageView) vi.findViewById(R.id.star4);
            holder.star5 = (ImageView) vi.findViewById(R.id.star5);
            holder.stars = new ArrayList<>(5);
            vi.setTag(holder);
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.nameTv.setText(review.getFromUserName());//quote.getUserFirstName().concat(" ").concat(quote.getUserLastName())
        holder.commentTv.setText(review.getReview());
        try{
            holder.commentDateTv.setText(TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOut, review.getCreateDate()));
        }catch(NullPointerException e){
            e.printStackTrace();
        }catch(IllegalArgumentException ex){
            holder.commentDateTv.setText(review.getCreateDate());
        }
        //holder.ratingTv.setText(Double.toString(comment.getRating()));
        holder.stars.add(holder.star1);
        holder.stars.add(holder.star2);
        holder.stars.add(holder.star3);
        holder.stars.add(holder.star4);
        holder.stars.add(holder.star5);
        setStarVisibility(holder, review.getRating());
        return vi;
    }

    private void setStarVisibility(ViewHolder holder, Double rating){
        if(rating == null){
            return;
        }

        int ratingReal = rating.intValue();
        double precision = rating - ratingReal;
        //Log.d(TAG, "RATING REAL:  " + rating + " - rating: " + ratingReal + " - precision: " + precision);
        for(int i = 1; i <= 5; i++){
            if( i <= ratingReal){
                holder.stars.get(i - 1).setImageDrawable(fullStar);
            }else if(i  == (ratingReal+1) && precision >= 0.5){
                holder.stars.get(i-1).setImageDrawable(halfStar);
            }else if(i > ratingReal){
                holder.stars.get(i - 1).setImageDrawable(noStar);
            }
        }
    }

    public static class ViewHolder {
        TextView nameTv;
        TextView commentDateTv;
        TextView commentTv;
        ImageView star1;
        ImageView star2;
        ImageView star3;
        ImageView star4;
        ImageView star5;
        ArrayList<ImageView> stars;
    }
}


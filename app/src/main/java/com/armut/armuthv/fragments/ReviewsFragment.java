package com.armut.armuthv.fragments;

/**
 * Created by oguzemreozcan on 03/10/16.
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.armut.armuthv.R;
import com.armut.armuthv.adapters.ReviewsAdapter;
import com.armut.armuthv.objects.Review;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ReviewsFragment extends BasicFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private ReviewLoader loader;
    //private String profileId;
    private ViewSwitcher switcher;
    private ListView commentsListView;
    private int sectionNumber;

    private final Comparator<Review> descendingComparator = new Comparator<Review>() {
        @Override
        public int compare(Review lhs, Review rhs) {
            return lhs.getRating().compareTo(rhs.getRating());
        }
    };

    private final Comparator<Review> ascendingComparator = new Comparator<Review>() {
        @Override
        public int compare(Review lhs, Review rhs) {
            return rhs.getRating().compareTo(lhs.getRating());
        }
    };

    public static ReviewsFragment newInstance(int sectionNumber, String profileId, ArrayList<Review> reviewList) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putParcelableArrayList("REVIEW_LIST", reviewList);
        Log.d("REVIEWS_FRAGMENT", "PROFILE ID: " + profileId);
        args.putString("PROFILE_ID", profileId);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewsFragment() {
    }

    @Override
    public void setTag() {
        TAG = "REVIEWS_FRAGMENT";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);
        commentsListView = (ListView) rootView.findViewById(R.id.commentsListView);
        switcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcher);

        //profileId = getArguments().getString("PROFILE_ID");
        sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        ArrayList<Review> reviewArrayList = getArguments().getParcelableArrayList("REVIEW_LIST");
        if (reviewArrayList == null) {
            Log.d(TAG, "ARRAYLIST IS NULL - Load new data");
            //useLoader(profileId);
        } else {
            fillData(reviewArrayList);
        }
        //ReviewsAdapter adapter = new ReviewsAdapter(getActivity(),prepareTestData());
        //commentsListView.setAdapter(adapter);
        return rootView;
    }

    private ArrayList<Review> sortReviews(boolean ascending, ArrayList<Review> reviews) {
        if (reviews == null) {
            return new ArrayList<>();
        }

        if (ascending) {
            Collections.sort(reviews, ascendingComparator);
            //Log.d(TAG, "SORT ASCENDING : " + reviews.get(0).getRating());
        } else {
            Collections.sort(reviews, descendingComparator);
            //Log.d(TAG, "SORT DESCENDING : " + reviews.get(0).getRating());
        }
        return reviews;
    }

//private void useLoader(String profileId){
//        //loadingCallback.loadingIsInProgress(true);
//        int loaderId= Constants.LOADER_JOB_REVIEWS+sectionNumber;
//        switcher.setDisplayedChild(0);
//        if(loader==null){
//        Bundle bundle=new Bundle();
//        bundle.putString("PROFILE_ID",profileId);
//        loader=(ReviewLoader)getActivity().getLoaderManager()
//        .initLoader(loaderId,bundle,this);
//        } //else {
//        Log.d(TAG,"USE LOADER REVIEWS - ON CONTENT CHANGEDD");
//        loader.forceLoad();
//        //  }
//        }

//@Override
//public Loader<ArrayList<Review>>onCreateLoader(int id,Bundle args){
//        String profileId=this.profileId;
//        if(args!=null){
//        profileId=args.getString("PROFILE_ID");
//        }
//        Log.d(TAG,"GET REVIEWS with id: "+profileId);
//        loader=new ReviewLoader(getActivity().getApplicationContext(),Constants.LOADER_JOB_REVIEWS,profileId,sectionNumber);//app.me.getUserId() // oguz@armut.com
//        return loader;
//        }
//
//@Override
//public void onLoadFinished(Loader<ArrayList<Review>>loader,ArrayList<Review>data){
//        if(attached){
//        fillData(data);
//        }
//        }
//
//@Override
//public void onLoaderReset(Loader<ArrayList<Review>>loader){
//        }

    private void fillData(ArrayList<Review> data) {
        if (data == null) {
            data = new ArrayList<>();
        }
        Log.d(TAG, "ON LOAD FINISHED:" + data.size() + " - stop loading indicator for page: " + sectionNumber);
        ArrayList sortData = new ArrayList<>(data);
        if (sectionNumber == 2) {
            sortData = new ArrayList(sortReviews(true, data));
        } else if (sectionNumber == 3) {
            sortData = new ArrayList(sortReviews(false, data));
        }
        ReviewsAdapter adapter = new ReviewsAdapter(getActivity(), sortData);
        commentsListView.setAdapter(adapter);
        switcher.setDisplayedChild(1);
    }

//@Subscribe
//public void onLoadingError(EventBusLoadingError eventBusLoadingError){
//        super.onLoadingError(eventBusLoadingError);
//        }

}


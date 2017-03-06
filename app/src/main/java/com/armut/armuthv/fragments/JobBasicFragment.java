package com.armut.armuthv.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.armut.armuthv.BaseActivity;
import com.armut.armuthv.LoginActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.WebActivity;
import com.armut.armuthv.adapters.JobItemAdapter;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.utils.Constants;

import java.util.ArrayList;

import retrofit2.Response;

/**
 * Created by oguzemreozcan on 07/06/16.
 */
public class JobBasicFragment extends BasicFragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView jobsListView;
    private SwipeRefreshLayout swipeLayout;
    protected TabLayoutRefreshCallback callback;
    protected int sectionNumber;
    public static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    protected void setTag() {
        TAG = "JobBasicFragment";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        jobsListView = (RecyclerView) rootView.findViewById(R.id.jobsListView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        //NestedScrollView jobListViewMainLayout = (NestedScrollView) rootView.findViewById(R.id.jobListViewMainLayout);
        //mLayoutManager.setSmoothScrollbarEnabled (true);
        jobsListView.setLayoutManager(mLayoutManager);
        jobsListView.setNestedScrollingEnabled(false);
        jobsListView.setItemAnimator(new DefaultItemAnimator());
        jobsListView.setHasFixedSize(true);
        RelativeLayout mainNoProfileLayout = (RelativeLayout) rootView.findViewById(R.id.mainNoProfileLayout);
//        Button loginButton = (Button)rootView.findViewById(R.id.tmpLoginButton);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(JobBasicFragment.this.getActivity(), LoginActivity.class);
//                startActivityForResult(intent, Constants.NEW_LOGIN_UPDATE_ALL);
//            }
//        });
//        if(!app.DEBUG_MODE){
//            loginButton.setVisibility(View.INVISIBLE);
//        }
        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(getContext(),R.color.kermit_green),
                ContextCompat.getColor(getContext(),android.R.color.holo_red_dark),
                ContextCompat.getColor(getContext(),android.R.color.holo_blue_dark),
                ContextCompat.getColor(getContext(),R.color.butterscotch));

        String token = app.isTokenPresent(TAG);
        if(token == null){
            initEmptyLayout(rootView, true);
            //jobListViewMainLayout.setVisibility(View.GONE);
            jobsListView.setVisibility(View.GONE);
            mainNoProfileLayout.setVisibility(View.VISIBLE);
        }else{
            mainNoProfileLayout.setVisibility(View.GONE);
            jobsListView.setVisibility(View.VISIBLE);
            //jobListViewMainLayout.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME");
    }

    private void isJobListEmpty(boolean empty){
        if(getView() != null){
            View rootView = getView();
            //NestedScrollView jobListViewMainLayout = (NestedScrollView) rootView.findViewById(R.id.jobListViewMainLayout);
            RelativeLayout mainNoProfileLayout = (RelativeLayout) rootView.findViewById(R.id.mainNoProfileLayout);
            if(empty){
                initEmptyLayout(rootView, false);
                jobsListView.setVisibility(View.GONE);
//                jobListViewMainLayout.setVisibility(View.GONE);
                mainNoProfileLayout.setVisibility(View.VISIBLE);
            }else{
                mainNoProfileLayout.setVisibility(View.GONE);
                jobsListView.setVisibility(View.VISIBLE);
//                jobListViewMainLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initEmptyLayout(View rootView, boolean noProfile){
        Log.d(TAG, "INIT EMPTY LAYOUT sectionNUMBER: " + sectionNumber);
        RelativeLayout noProfileLayout = (RelativeLayout) rootView.findViewById(R.id.noProfileLayout);
        ImageView noProfileIcon = (ImageView) rootView.findViewById(R.id.noNewProfileIcon);
        TextView noProfileTitle = (TextView) rootView.findViewById(R.id.noProfileTitle);
        TextView noProfileSubTitle = (TextView) rootView.findViewById(R.id.noProfileSubTitle);
        Button loginButton = (Button) rootView.findViewById(R.id.loginButton);
        Button signUp = (Button) rootView.findViewById(R.id.signUp);
        if(noProfile){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                signUp.setText(Html.fromHtml(getString(R.string.sign_up_link), Html.FROM_HTML_MODE_LEGACY));
            } else {
                signUp.setText(Html.fromHtml(getString(R.string.sign_up_link)));
            }
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("URL", Constants.REGISTER_HV_URL);
                    intent.putExtra("title", "Üye Ol");
                    startActivity(intent);
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(Constants.REGISTER_HV_URL));
//                    startActivity(intent);
                    BaseActivity.setTranslateAnimation(getActivity());
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "LOGIN BUTTON ON CLICK");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, Constants.UPDATE_JOBS_PAGE);
                }
            });
        }else{
            signUp.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
        }
        noProfileLayout.setVisibility(View.VISIBLE);
        noProfileIcon.setImageResource(R.drawable.no_jobs_icon);
        switch (sectionNumber){
            case Constants.OPPORTUNITY_SECTION_INDEX:
                noProfileTitle.setText(getString(R.string.tab1));
                noProfileSubTitle.setText(noProfile ? R.string.no_opportunities : R.string.no_opportunities_logged_in);
                break;
            case Constants.QUOTE_SECTION_INDEX:
                if(app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID) == Constants.BUSINESS_MODEL_RESERVATION){
                    noProfileTitle.setText(getString(R.string.tab3));
                    noProfileSubTitle.setText(noProfile ? R.string.no_deals : R.string.no_deals_logged_in);
                }else{
                    noProfileTitle.setText(getString(R.string.tab2));
                    noProfileSubTitle.setText(noProfile ? R.string.no_quotes : R.string.no_quotes_logged_in);
                }
                break;
            case Constants.DEALS_SECTION_INDEX:
                noProfileTitle.setText(getString(R.string.tab3));
                noProfileSubTitle.setText(noProfile ? R.string.no_deals : R.string.no_deals_logged_in);
                break;
        }
    }


    public boolean isJobListViewEmpty(){
        return jobsListView.getAdapter() == null;
    }

    protected void updateAll(String token){
        app.getBus().post(new JobEvents.OpportunitiesRequest(token, ""));
        app.getBus().post(new JobEvents.DealsRequest(token, ""));
        app.getBus().post(new JobEvents.QuotesRequest(token, ""));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (TabLayoutRefreshCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement TabLayoutRefreshCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(jobsListView != null){
            JobItemAdapter adapter = (JobItemAdapter) jobsListView.getAdapter();
            if(adapter != null){
                adapter.unregister(app);
            }
        }
    }

    protected void setJobsListView(Response<ArrayList<Job>> res) {
        if (attached && res != null) {
            if (res.isSuccessful()) {
                final ArrayList<Job> jobArrayList = res.body();
                Log.d(TAG, "SUCCESFUL " + jobArrayList.size() + " - sectionNumber: " + sectionNumber);
                int tmpSectionNumber = sectionNumber;
                if(app.getDataSaver().getBusinessModel(Constants.BUSINESS_MODEL_ID) == 2 && tmpSectionNumber == 3){
                    tmpSectionNumber = 2;
                }
                callback.refreshJobCountTitle(jobArrayList.size(), tmpSectionNumber);
                app.getBus().post(new MiscEvents.FillJobHashMapRequest(sectionNumber, jobArrayList));
                if(jobArrayList.size() == 0){
                    isJobListEmpty(true);
                }else{
                    isJobListEmpty(false);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            View fragmentView = getView();
                            if(fragmentView != null){
                                RelativeLayout mainNoProfileLayout = (RelativeLayout) fragmentView.findViewById(R.id.mainNoProfileLayout);
                                NestedScrollView jobListViewMainLayout = (NestedScrollView) fragmentView.findViewById(R.id.jobListViewMainLayout);
                                JobItemAdapter adapter = new JobItemAdapter(getActivity(), jobArrayList, jobsListView, sectionNumber);
                                if (jobsListView != null) {
                                    jobsListView.setAdapter(adapter);
                                    Log.d(TAG, "REGISTER ADAPTER");
                                    adapter.register(app);
                                    mainNoProfileLayout.setVisibility(View.GONE);
                                    jobListViewMainLayout.setVisibility(View.VISIBLE);
                                }
//                                else {
                                    //TODO
//                                Button signUp = (Button) mainNoProfileLayout.findViewById(R.id.signUp);
//                                Button loginButton = (Button) mainNoProfileLayout.findViewById(R.id.loginButton);
//                                signUp.setVisibility(View.INVISIBLE);
//                                loginButton.setVisibility(View.INVISIBLE);
//                                Log.d(TAG, "JOBS LIST VIEW IS NULL");
//                                }
                            }
                        }
                    });
                }
            } else {
                isJobListEmpty(true);
                app.getBus().post(new ApiErrorEvent(res.code(), res.message(), true));
            }
        } else if (res == null) {
            isJobListEmpty(true);
            //app.getBus().post(new ApiErrorEvent(0, "Problem!!!", true));

        }
        if(swipeLayout != null)
            swipeLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        String token = app.isTokenPresent(TAG);
        if(token == null){
            swipeLayout.setRefreshing(false);
            return;
        }
        if(this instanceof OpportunitiesFragment){
            app.getBus().post(new JobEvents.OpportunitiesRequest(token, ""));
        }else if(this instanceof DealsFragment){
            app.getBus().post(new JobEvents.DealsRequest(token, ""));
        }else if(this instanceof QuotesFragment){
            app.getBus().post(new JobEvents.QuotesRequest(token, ""));
        }
    }

    public interface TabLayoutRefreshCallback {
        void refreshJobCountTitle(int number, int sectionNo);
    }
}

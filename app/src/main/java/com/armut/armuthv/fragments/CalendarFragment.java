package com.armut.armuthv.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.armut.armuthv.adapters.CalendarAdapter;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.UserCalendarItem;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class CalendarFragment extends BasicFragment implements DialogTimePickerWheel.TimesSelectedCallback {
//CalendarAdapter.OnCalendarItemTimePickerSelectedListener

    private RecyclerView calendarRecyclerView;
    private View loadingLayout;

    public CalendarFragment() {
    }

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = app.isTokenPresent(TAG);
        setHasOptionsMenu(true);
        //setRetainInstance(true);
        if (token != null) {
            app.getBus().post(new MiscEvents.GetCalendarRequest());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_save, menu);
        MenuItem saveItem = menu.findItem(R.id.action_save);
        if (loadingLayout.getVisibility() == View.GONE && calendarRecyclerView.getVisibility() == View.VISIBLE) {
            saveItem.setVisible(true);
        } else {
            saveItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "ITEM ID: " + item.getItemId() + " - saveid: " + R.id.action_save);
        if (item.getItemId() == R.id.action_save) {
            if (calendarRecyclerView != null) {
                //calendarRecyclerView.updateItem(index, item);
                CalendarAdapter adapter = (CalendarAdapter) calendarRecyclerView.getAdapter();
                if (adapter != null) {
                    app.getBus().post(new MiscEvents.PatchCalendarRequest(adapter.getItems()));
                    loadingLayout.setVisibility(View.VISIBLE);
                    return true;
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendarRecyclerView = (RecyclerView) rootView.findViewById(R.id.calendarList);
        calendarRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this.getActivity());
        calendarRecyclerView.setLayoutManager(mLayoutManager);
        calendarRecyclerView.setNestedScrollingEnabled(true);
        calendarRecyclerView.setItemAnimator(new DefaultItemAnimator());
        loadingLayout = rootView.findViewById(R.id.loadingLayout);
        //loadingLayout.setVisibility(View.VISIBLE);
        String token = app.isTokenPresent(TAG);
        RelativeLayout mainNoProfileLayout = (RelativeLayout) rootView.findViewById(R.id.mainNoProfileLayout);
        if (token == null) {
            initEmptyLayout(rootView, true);
            //jobListViewMainLayout.setVisibility(View.GONE);
            calendarRecyclerView.setVisibility(View.GONE);
            mainNoProfileLayout.setVisibility(View.VISIBLE);
        } else {
            mainNoProfileLayout.setVisibility(View.GONE);
            calendarRecyclerView.setVisibility(View.VISIBLE);
            loadingLayout.setVisibility(View.VISIBLE);
            //jobListViewMainLayout.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    public void initEmptyLayout(View rootView, boolean noProfile) {
        RelativeLayout noProfileLayout = (RelativeLayout) rootView.findViewById(R.id.noProfileLayout);
        ImageView noProfileIcon = (ImageView) rootView.findViewById(R.id.noNewProfileIcon);
        TextView noProfileTitle = (TextView) rootView.findViewById(R.id.noProfileTitle);
        TextView noProfileSubTitle = (TextView) rootView.findViewById(R.id.noProfileSubTitle);
        Button loginButton = (Button) rootView.findViewById(R.id.loginButton);
        Button signUp = (Button) rootView.findViewById(R.id.signUp);
        if (noProfile) {
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
        } else {
            signUp.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
        }
        noProfileLayout.setVisibility(View.VISIBLE);
        noProfileIcon.setImageResource(R.drawable.no_jobs_icon);

        noProfileTitle.setText(getString(R.string.tab2_calendar));
        noProfileSubTitle.setText(getString(R.string.no_calendar_data));

    }

    @Override
    protected void setTag() {
        TAG = "CalendarFragment";
    }

    @Subscribe
    public void onCalendarDataSaved(MiscEvents.PatchCalendarResponse response) {
        Log.d(TAG, "TAKVIM GUNCELLENDI");
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.tab2_calendar)
                .setMessage(R.string.calendar_subtitle)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Subscribe
    public void onCalendarDataReceived(MiscEvents.GetCalendarResponse response) {
        Log.d(TAG, "ON CALENDAR DATA RECEIVED : ");
        loadingLayout.setVisibility(View.GONE);
        if (response != null) {
            if (response.getResponse() != null) {
                ArrayList<UserCalendarItem> calendarArrayList = response.getResponse().body();
                Log.d(TAG, "ON CALENDAR DATA RECEIVED : " + calendarArrayList.size());
                calendarRecyclerView.setAdapter(new CalendarAdapter(getContext(), this, calendarArrayList));
                getActivity().invalidateOptionsMenu();
                //calendarRecyclerView.setLayoutTransition(new LayoutTransition());
            }
        }
    }

//    @Override
//    public void onTimePickerSelected(UserCalendarItem calendarItem, int index) {
//        final DialogTimePickerWheel timePicker = DialogTimePickerWheel.newInstance(index, calendarItem.getDayStartHour(), calendarItem.getDayEndHour());
//        //logoutDialog.setTargetFragment(UserProfileFragment.this, Constants.LOGOUT);
//        timePicker.show(CalendarFragment.this.getChildFragmentManager(), "timePickerWheel");
//    }

    @Subscribe
    public void onCalendarItemUpdated(final MiscEvents.PatchCalendarResponse event) {
        loadingLayout.setVisibility(View.GONE);
//        if(event != null){
//
//        }
    }

    @Override
    public void timesSelected(int index, int startHour, int finishHour, boolean working) {
        Log.d(TAG, "START HOUR SELECTED: " + startHour + " endHour : " + finishHour + " - index: " + index);
        if (calendarRecyclerView != null) {
            CalendarAdapter adapter = (CalendarAdapter) calendarRecyclerView.getAdapter();
            UserCalendarItem item = adapter.getItem(index);
            item.setDayStartHour(startHour);
            item.setDayEndHour(finishHour);
            item.setWorking(working);
            adapter.updateData(index, item);
            Log.d(TAG, "ITEM UPDATED: ");
        }
    }
}

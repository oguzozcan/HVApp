package com.armut.armuthv.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;
import com.armut.armuthv.adapters.CancelJobReasonsAdapter;
import com.armut.armuthv.adapters.NothingSelectedSpinnerAdapter;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.components.DropdownSpinnerWithPopup;
import com.armut.armuthv.objects.OpportunityCancelReason;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 30/06/16.
 */
public class DialogCancelJob extends DialogFragment  {

    private CancelJobCallback callback;
    private LinearLayout progressBarLayout;
    private int selectedReasonId = -1;
    private String jobCancelReason = "";
    private LinearLayout spinnerParent;
    private final String TAG = "DialogCancelJob";

    public DialogCancelJob() {
    }

    public static DialogCancelJob newInstance(String title) {
        DialogCancelJob frag = new DialogCancelJob();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (CancelJobCallback) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement CancelJobCallback interface");
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        ArmutHVApp app = (ArmutHVApp)getActivity().getApplication();
        app.getBus().register(this);
        //Log.d(TAG, "REGISTER");
        app.getBus().post(new JobEvents.OpportunityRejectReasonsRequest());
    }

    @Override
    public void onPause() {
        super.onPause();
        ArmutHVApp app = (ArmutHVApp)getActivity().getApplication();
        app.getBus().unregister(this);
        //Log.d(TAG, "UNREGISTER");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_cancel_job_layout, container);
        getDialog().setCancelable(true);
        if(getDialog().getWindow() != null){
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setGravity(Gravity.CENTER);
        }
        spinnerParent = (LinearLayout) rootView.findViewById(R.id.dropdownMenuContainerLayout);
        progressBarLayout = (LinearLayout) rootView.findViewById(R.id.progressBarLayout);
        progressBarLayout.setVisibility(View.VISIBLE);
        getActivity().setFinishOnTouchOutside(true);
        getDialog().setCancelable(true);
        TextView dialogTitle = (TextView) rootView.findViewById(R.id.dialogTitle);
        TextView dialogSubTitle = (TextView) rootView.findViewById(R.id.dialogSubtitle);
        dialogTitle.setText(getString(R.string.reject_job));
        dialogSubTitle.setText(getString(R.string.reject_job_reason));

        Button cancelJob = (Button) rootView.findViewById(R.id.cancelJob);
        cancelJob.setText(getString(R.string.yes));
        cancelJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    if (selectedReasonId != -1) {
                        //TODO userId and jobId
                        callback.startJobCancelTask(new OpportunityCancelReason(selectedReasonId, jobCancelReason));
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), R.string.cancel_job_select_reason, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        cancelButton.setText(getString(R.string.no));
        return rootView;
    }

    @Subscribe
    public void onLoadJobCancelReasons(JobEvents.OpportunityRejectReasonsResponse response) {
        progressBarLayout.setVisibility(View.GONE);
        if (response == null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), getString(R.string.not_cancellable), Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        final ArrayList<OpportunityCancelReason> jobCancelReasons = response.getResponse().body();
        Log.d(TAG, "ON LOAD JOB CANCEL REASONS " + jobCancelReasons.size());
        if (jobCancelReasons.size() != 0) {

            selectedReasonId = -1;//dataList.get(0).getTokenId();
            jobCancelReason = "";
            CancelJobReasonsAdapter adapter = new CancelJobReasonsAdapter(this.getActivity(), R.layout.address_list_view_row, jobCancelReasons);
            NothingSelectedSpinnerAdapter nothingAdapter = new NothingSelectedSpinnerAdapter(adapter,
                    R.layout.nothing_selected_cancel_job_row,// R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                    getActivity());
            DropdownSpinnerWithPopup spinner = new DropdownSpinnerWithPopup(getActivity());
            adapter.setDropDownViewResource(R.layout.address_list_view_row);
            spinner.setAdapter(nothingAdapter);
            spinner.setSelection(0, false);
            spinner.setBackgroundResource(R.drawable.spinner_selector_shape);
            getActivity().setFinishOnTouchOutside(true);
            spinnerParent.addView(spinner);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    if(i > 0){
                        OpportunityCancelReason reason = jobCancelReasons.get(i-1);
                        selectedReasonId = reason.getReasonId();
                        jobCancelReason = reason.getReason();
                        Log.d(TAG, "ON ITEM CLICK: " + selectedReasonId + " i : " + i + " - reason : " + reason.getReason());
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }


    public interface CancelJobCallback {
        void startJobCancelTask(OpportunityCancelReason reason);
    }
}

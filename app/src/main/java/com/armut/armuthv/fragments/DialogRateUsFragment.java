package com.armut.armuthv.fragments;

import android.animation.LayoutTransition;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.CustomerReview;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

/**
 * Created by oguzemreozcan on 01/02/17.
 */

public class DialogRateUsFragment extends DialogFragment {

    private final String TAG = "DialogRateUsFragment";
    private LinearLayout progressBar;
    private int screenState = 0;
    private boolean dontAskAgainToggleChecked = false;

    public DialogRateUsFragment() {
    }

    public static DialogRateUsFragment newInstance(boolean isCalledFromProfile) {
        DialogRateUsFragment frag = new DialogRateUsFragment();
        Bundle args = new Bundle();
        args.putBoolean("IS_CALLED_FROM_PROFILE", isCalledFromProfile);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
        app.getBus().register(this);
    }

    @Override
    public void onDetach() {
        ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
        try{
            app.getBus().unregister(this);
        }catch(Exception e){
            e.printStackTrace();
        }

        if(screenState == 2){
            try {
                MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
                mixpanel.track("Request Product Page Rate", ArmutUtils.getBasicJson(new BasicNameValuePair("Origin", "User Click")
                        ,new BasicNameValuePair("Did you like", "No")
                        ,new BasicNameValuePair("Want to Rate", "No")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.dialog_rate_armut, container);

        final LinearLayout dialogPositiveAddon = (LinearLayout) inflater.inflate(
                R.layout.dialog_rate_positive_addon, null);
        dialogPositiveAddon.setVisibility(View.GONE);
        final LinearLayout dialogNegativeAddon = (LinearLayout) inflater.inflate(
                R.layout.dialog_rate_negative_addon, null);
        progressBar = (LinearLayout) rootView.findViewById(R.id.progressBarLayout);
        final LinearLayout dialogMainLayout = (LinearLayout) rootView.findViewById(R.id.dialogMainLayout);
        dialogMainLayout.setLayoutTransition(new LayoutTransition());
        final Button negativeButton = (Button) rootView.findViewById(R.id.closeButton);
        final Button positiveButton = (Button) rootView.findViewById(R.id.sendButton);
        final TextView dialogTitle = (TextView) rootView.findViewById(R.id.dialogTitle);
        String title = getString(R.string.rate_armut_title); // , "\u2764"
        dialogTitle.setText(title);
        getDialog().setTitle(title);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getActivity().setFinishOnTouchOutside(true);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final LinearLayout writeReviewLayout = (LinearLayout) rootView.findViewById(R.id.write_review_layout);
        final EditText editText = (EditText) dialogNegativeAddon.findViewById(R.id.editText);
        Button sendButton = (Button) dialogNegativeAddon.findViewById(R.id.send);
        ImageView closeButton = (ImageView) rootView.findViewById(R.id.closeImageButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenState == 0) {
                    screenState = 2;
                    dialogTitle.setText(getString(R.string.rate_armut_three, new String(Character.toChars(0x0001F423))));
                    getDialog().setTitle(getString(R.string.rate_armut_three, new String(Character.toChars(0x0001F423))));
                    //dialogMainLayout.removeView(positiveButton);
                    dialogMainLayout.removeView(rootView.findViewById(R.id.buttonsLayout));
                    writeReviewLayout.addView(dialogNegativeAddon);
                    editText.requestFocus();
                    getDialog().getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                } else if (screenState == 1) {
                    if (dontAskAgainToggleChecked && getActivity() != null) {
                        ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
                        app.getDataSaver().putBoolean(Constants.NO_SHOW_RATE_US_DIALOG_KEY, true);
                        app.getDataSaver().save();
                    }

                    try {
                        MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
                        mixpanel.track("Request Product Page Rate", ArmutUtils.getBasicJson(new BasicNameValuePair("Origin", "User Click")
                                ,new BasicNameValuePair("Did you like", "Yes")
                                ,new BasicNameValuePair("Want to Rate", "No")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dismiss();
                }
            }
        });

        final boolean isCalledFromUserProfile = getArguments().getBoolean("IS_CALLED_FROM_PROFILE");

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenState == 0) {
                    screenState = 1;
                    dialogTitle.setText(getString(R.string.rate_armut_second_title, new String(Character.toChars(0x0001F607))));
                    getDialog().setTitle(getString(R.string.rate_armut_second_title));
                    positiveButton.setText(getString(R.string.rate_armut_second_yes));
                    negativeButton.setText(getString(R.string.rate_armut_second_no));
                    if (!isCalledFromUserProfile)
                        initPositiveAddOn(writeReviewLayout, dialogPositiveAddon);
                    //writeReviewLayout.addView(dialogPositiveAddon);
                } else if (screenState == 1) {
                    if (getActivity() != null) {
                        ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
                        app.getDataSaver().putBoolean(Constants.NO_SHOW_RATE_US_DIALOG_KEY, true);
                        app.getDataSaver().save();
                        ArmutUtils.openHVAppPageFromPlayStore(getActivity());
                        try {
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
                            mixpanel.track("Request Product Page Rate", ArmutUtils.getBasicJson(new BasicNameValuePair("Origin", "User Click")
                                    ,new BasicNameValuePair("Did you like", "Yes")
                                    ,new BasicNameValuePair("Want to Rate", "Yes")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screenState == 2) {
                    ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
                    String review = editText.getText().toString().trim();
                    if (!review.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        app.getBus().post(new MiscEvents.PostUserAppReviewRequest(new CustomerReview(review)));
                        try {
                            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
                            mixpanel.track("Request Product Page Rate", ArmutUtils.getBasicJson(new BasicNameValuePair("Origin", "User Click")
                                    ,new BasicNameValuePair("Did you like", "No")
                                    ,new BasicNameValuePair("Give Feedback", "Yes")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Lütfen kısa da olsa fikirlerini belirt.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return rootView;
    }

    @Subscribe
    public void customerReviewSent(MiscEvents.PostUserAppReviewResponse response) {
        progressBar.setVisibility(View.GONE);
        if(response != null){
            dismiss();
            Toast.makeText(getActivity(), "Değerlendirmen alınmıştır, yorumların için çok teşekkür ederiz.", Toast.LENGTH_LONG).show();
        }
    }

    private void initPositiveAddOn(LinearLayout parent, View view) {
        final TextView reviewToggleTitle = (TextView) view.findViewById(R.id.reviewToggleTitle);
        final CheckBox reviewToggle = (CheckBox) view.findViewById(R.id.reviewToggle);
        LinearLayout toggleLayout = (LinearLayout) view.findViewById(R.id.reviewToggleLayout);
//        editText.requestFocus();
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        reviewToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dontAskAgainToggleChecked = b;
                if (b) {
                    reviewToggleTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
                } else {
                    reviewToggleTitle.setTextColor(ContextCompat.getColor(getActivity(), R.color.silver));
                }
            }
        });
        toggleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewToggle.performClick();
            }
        });
        view.setVisibility(View.VISIBLE);
        try {
            parent.addView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


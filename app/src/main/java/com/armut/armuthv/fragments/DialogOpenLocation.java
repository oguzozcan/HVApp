package com.armut.armuthv.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.MiscEvents;

/**
 * Created by oguzemreozcan on 10/11/16.
 */

public class DialogOpenLocation extends DialogFragment {

    private final String TAG = "DialogOpenLocation";

    public DialogOpenLocation() {

    }

    public static DialogOpenLocation newInstance() {
        //DialogOpenLocation frag =
//        Bundle args = new Bundle();
//        args.putBoolean("CREATE_PASSWORD", createPassword);
//        //args.putString("title", title);
//        frag.setArguments(args);
        return new DialogOpenLocation();
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
        app.getBus().unregister(this);
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_open_location, container);
        getActivity().setFinishOnTouchOutside(false);
        //TextView titleText = (TextView) rootView.findViewById(R.id.dialogTitle);
        Button okButton = (Button) rootView.findViewById(R.id.okButton);
        getActivity().setFinishOnTouchOutside(true);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
//                if(!app.isLocationPremissionGranted){
//                }
                app.getBus().post(new MiscEvents.DisplayTurnOnLocationServicePopupRequest());
                dismissAllowingStateLoss();
            }
        });
        setCancelable(false);
        Window window = getDialog().getWindow();
        // window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.requestFeature(Window.FEATURE_NO_TITLE);
        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return rootView;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        try {
//            callback = (DialogPasswordCreateFragment.PasswordCreatedCallback) getParentFragment();//getTargetFragment();
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Calling fragment must implement PasswordCreatedListener interface");
//        }
//    }
}

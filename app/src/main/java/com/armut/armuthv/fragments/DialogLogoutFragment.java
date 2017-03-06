package com.armut.armuthv.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.armut.armuthv.R;

/**
 * Created by oguzemreozcan on 12/08/16.
 */
public class DialogLogoutFragment extends DialogFragment {

    private LogoutCallback callback;

    public DialogLogoutFragment() {
    }

    public static DialogLogoutFragment newInstance() {
        return new DialogLogoutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        try {
        callback = (LogoutCallback) getParentFragment();//getTargetFragment();
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Calling fragment must implement LogoutCallback interface");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_logout_layout, container);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCancelable(true);
        getActivity().setFinishOnTouchOutside(true);
        Button logout = (Button) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    Log.d("LOGOUT", "LOGOUT CALLBACK NOT NULL");
                    callback.logoutSuccessful(true);
                    dismiss();
                }else{
                    Log.d("LOGOUT", "LOGOUT CALLBACK NULL");
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
        return rootView;
    }

    public interface LogoutCallback{
        void logoutSuccessful(boolean success);
    }
}


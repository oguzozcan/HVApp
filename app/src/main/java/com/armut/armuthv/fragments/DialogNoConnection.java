package com.armut.armuthv.fragments;

/**
 * Created by oguzemreozcan on 21/06/16.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.armut.armuthv.R;
import com.armut.armuthv.utils.Constants;

/**
 * Created by oguzemreozcan on 05/10/15.
 */
public class DialogNoConnection extends DialogFragment {

    private Fragment targetFragment;
    //private MainActivity activity;
    private int requestCode;

    public DialogNoConnection() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        targetFragment = getParentFragment();//getTargetFragment();
    }

//    public void setTargetFragment(Fragment fragment, int requestCode) {
//        targetFragment = fragment;
//        this.requestCode = requestCode;
//    }

//    public void setTargetActivity(MainActivity activity, int requestCode){
//        this.requestCode = requestCode;
//        this.activity = activity;
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString("title", "");
        String message = args.getString("message", "");
        requestCode = args.getInt("request_code", Constants.NO_CONNECTION);
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (targetFragment != null) {
                            targetFragment.onActivityResult(requestCode, 1, null);
                        }

//                        else if(activity != null){
//                            Log.d("NO CONNECTION", "SHOULD CALL ACTIVITY ON RESULT");
//                            activity.fakeOnActivityResult(requestCode, 1, null);
//                        }
                    }
                })
                .create();
    }
}
package com.armut.armuthv.fragments;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.armut.armuthv.MainActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;

/**
 * Created by oguzemreozcan on 12/08/16.
 */
public class DialogContactFragment extends DialogFragment {

//    private boolean callPermissionGranted;
//    private boolean attached = false;

    public DialogContactFragment() {

    }

    public static DialogContactFragment newInstance(String title) {
        DialogContactFragment frag = new DialogContactFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_contact_layout, container);
        getDialog().setCancelable(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getActivity().setFinishOnTouchOutside(true);
        Button callButton = (Button) rootView.findViewById(R.id.callButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmutUtils.callArmut(getActivity());
            }
        });

        Button sendMailButton = (Button) rootView.findViewById(R.id.sendMessageButton);
        sendMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArmutUtils.sendMail("", Constants.MAIL_TO_ARMUT, getString(R.string.email_send_communication_subject), null, getActivity());
                MainActivity.setTranslateAnimation(getActivity());
            }
        });
        return rootView;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        attached = true;
//    }
//
//    @Override
//    public void onDetach() {
//        attached = false;
//        super.onDetach();
//    }

//    private boolean requestCallPermission(){
//        if (!ArmutUtils.isMarshmallow()) {
//            callPermissionGranted = true;
//            return true;
//        }
//        if (ContextCompat.checkSelfPermission(getActivity(),
//                Manifest.permission.CALL_PHONE)
//                != PackageManager.PERMISSION_GRANTED) {
//            callPermissionGranted = false;
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.CALL_PHONE)) {
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                showMessage("Uygulama içinden arama yapabilmek için izin verin.");
//            }
////            else {
//            // No explanation needed, we can request the permission.
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{Manifest.permission.CALL_PHONE},
//                    Constants.PERMISSIONS_CALL);
////            }
//        }else{
//            callPermissionGranted = true;
//        }
//        return callPermissionGranted;
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case Constants.PERMISSIONS_CALL: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    callPermissionGranted = true;
//                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Constants.CALL_ARMUT));
//                    startActivity(intent);
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    callPermissionGranted = false;
//                }
//            }
//        }
//    }
//
//    private void showMessage(final String message){
//        if(attached){
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                }
//            });
//        }
//    }
}


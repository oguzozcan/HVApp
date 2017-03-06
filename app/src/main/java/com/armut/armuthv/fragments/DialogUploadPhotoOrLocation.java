package com.armut.armuthv.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.armut.armuthv.R;
import com.armut.armuthv.utils.PhotoSelectorHelper;

/**
 * Created by oguzemreozcan on 27/07/16.
 */
public class DialogUploadPhotoOrLocation extends DialogFragment {

    private PhotoSelectorHelper.PhotoSelector callback;

    public DialogUploadPhotoOrLocation() {
    }

    public static DialogUploadPhotoOrLocation newInstance() {
        return new DialogUploadPhotoOrLocation();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (PhotoSelectorHelper.PhotoSelector) getActivity();//getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling activity must implement PhotoSelector interface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_photo_or_location_upload, container);
        Window window = getDialog().getWindow();
        getDialog().setCancelable(true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.requestFeature(Window.FEATURE_NO_TITLE);
        getActivity().setFinishOnTouchOutside(true);
        window.setGravity(Gravity.CENTER); //  | Gravity.LEFT
//        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(
//                new int[] { android.R.attr.actionBarSize });
//        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
//        styledAttributes.recycle();
        WindowManager.LayoutParams params = window.getAttributes();
//        params.y = mActionBarSize;
//        params.x = mActionBarSize;
        params.dimAmount = 0.1f; // dim only a little bit
        window.setAttributes(params);
//        TextView dialogTitle = (TextView) rootView.findViewById(R.id.dialogTitle);
//        TextView dialogSubTitle = (TextView) rootView.findViewById(R.id.dialogSubtitle);

        Button selection1 = (Button) rootView.findViewById(R.id.selection1);
        Button selection2 = (Button) rootView.findViewById(R.id.selection2);
        Button selection3 = (Button) rootView.findViewById(R.id.selection3);
        selection1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.uploadMethodSelected(PhotoSelectorHelper.REQ_CAMERA);
                    dismiss();
                }
            }
        });
        selection2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.uploadMethodSelected(PhotoSelectorHelper.REQ_PICK_IMAGE);
                    dismiss();
                }
            }
        });

        selection3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback != null){
                    callback.uploadMethodSelected(PhotoSelectorHelper.ADD_LOCATION);
                    dismiss();
                }
            }
        });

//        AssetManager assetManager = getActivity().getAssets();
//        logout.setTypeface(ArmutUtils.loadFont(assetManager, getString(R.string.font_pera_bold)));
//        cancelButton.setTypeface(ArmutUtils.loadFont(assetManager, getString(R.string.font_pera_bold)));
//        selection1.setTypeface(ArmutUtils.loadFont(assetManager, getString(R.string.font_pera_regular)));
//        selection2.setTypeface(ArmutUtils.loadFont(assetManager, getString(R.string.font_pera_regular)));
//        selection3.setTypeface(ArmutUtils.loadFont(assetManager, getString(R.string.font_pera_regular)));
        return rootView;
    }
}


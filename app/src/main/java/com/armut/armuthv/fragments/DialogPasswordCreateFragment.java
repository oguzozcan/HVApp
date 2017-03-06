package com.armut.armuthv.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.User;
import com.squareup.otto.Subscribe;

/**
 * Created by oguzemreozcan on 12/08/16.
 */
public class DialogPasswordCreateFragment extends DialogFragment {

    private EditText editText;
    private EditText oldPasswordEditText;
    private EditText newPasswordRepeatEditText;
    private final String TAG = "PasswordCreateDialog";
    private LinearLayout progressBarLayout;
    private PasswordCreatedCallback callback;
    // private boolean createPassword;

    public DialogPasswordCreateFragment() {

    }

    public static DialogPasswordCreateFragment newInstance(boolean createPassword) {
        DialogPasswordCreateFragment frag = new DialogPasswordCreateFragment();
        Bundle args = new Bundle();
        args.putBoolean("CREATE_PASSWORD", createPassword);
        //args.putString("title", title);
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
        app.getBus().unregister(this);
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        createPassword = getArguments().getBoolean("CREATE_PASSWORD", true);
//        View rootView;
//        if (createPassword) {
//            rootView = inflater.inflate(R.layout.dialog_create_password_layout, container);
//            getActivity().setFinishOnTouchOutside(false);
//        } else {
        View rootView = inflater.inflate(R.layout.dialog_change_password_layout, container);
        oldPasswordEditText = (EditText) rootView.findViewById(R.id.oldPassword);
        newPasswordRepeatEditText = (EditText) rootView.findViewById(R.id.newPasswordRepeat);
        getActivity().setFinishOnTouchOutside(true);
//        }
        TextView titleText = (TextView) rootView.findViewById(R.id.dialogTitle);
        editText = (EditText) rootView.findViewById(R.id.password);
        progressBarLayout = (LinearLayout) rootView.findViewById(R.id.progressBarLayout);
        Button saveButton = (Button) rootView.findViewById(R.id.saveButton);
        getActivity().setFinishOnTouchOutside(true);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareUserJson();
            }
        });
//        if (!createPassword) {
        titleText.setText(getString(R.string.edit_password));
        oldPasswordEditText.requestFocus();
        final TextView toggleTitle = (TextView) rootView.findViewById(R.id.showPasswordToggleTitle);
        final CheckBox showPassToggle = (CheckBox) rootView.findViewById(R.id.showPasswordToggle);
        LinearLayout toggleLayout = (LinearLayout) rootView.findViewById(R.id.showPasswordToggleLayout);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPasswordRepeatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showPassToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    toggleTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    oldPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPasswordRepeatEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    toggleTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.silver));
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    oldPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    newPasswordRepeatEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                editText.setSelection(editText.getText().length());
                oldPasswordEditText.setSelection(oldPasswordEditText.getText().length());
                newPasswordRepeatEditText.setSelection(newPasswordRepeatEditText.getText().length());
            }
        });
        toggleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPassToggle.performClick();
            }
        });
        setCancelable(true);
//        }
//        else {
//            editText.requestFocus();
//            setCancelable(false);
//        }

        Window window = getDialog().getWindow();
        // window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.requestFeature(Window.FEATURE_NO_TITLE);

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        getDialog().getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        //String title = getArguments().getString("title", "Enter Name");
        //getDialog().setTitle(title);
        return rootView;
    }

    private boolean isPasswordValid(String password, EditText editText) {
        boolean valid = password.trim().length() > 5;
        if (!valid) {
            passwordInvalid(editText, R.string.password_short);
        }
        return valid;
    }

    private boolean isPasswordsValid(String oldPassword, String newPassRepeat, String password) {
        if (isPasswordValid(password, editText) && isPasswordValid(oldPassword, oldPasswordEditText) && isPasswordValid(newPassRepeat, newPasswordRepeatEditText)) {
            if (newPassRepeat.equals(password)) {
                return true;
            } else {
                passwordInvalid(editText, R.string.password_not_equal);
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkPasswordValidty() {
        String pass = editText.getText().toString();
        if (TextUtils.isEmpty(pass)) {
            passwordInvalid(editText, R.string.password_short);
            return false;
        }
//        else if (createPassword) {
//            return isPasswordValid(pass, editText);
//        }
        else {
            String oldPass = oldPasswordEditText.getText().toString();
            String newPassRepeat = newPasswordRepeatEditText.getText().toString();
            if (TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(newPassRepeat)) {
                if (TextUtils.isEmpty(oldPass)) {
                    passwordInvalid(oldPasswordEditText, R.string.password_short);
                } else if (TextUtils.isEmpty(newPassRepeat)) {
                    passwordInvalid(newPasswordRepeatEditText, R.string.password_short);
                }
                return false;
            } else {
                return isPasswordsValid(oldPass, newPassRepeat, pass);
            }
        }
    }

    private void passwordInvalid(EditText editText, int error) {
        showKeyboard(editText);
        editText.setError(getString(R.string.error_invalid_password));
        showMessage(getString(error));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            callback = (PasswordCreatedCallback) getParentFragment();//getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement PasswordCreatedListener interface");
        }
    }

    private void prepareUserJson() {
        if (!checkPasswordValidty()) {
            progressBarLayout.setVisibility(View.GONE);
        } else {
            hideKeyboard();
            String oldPass = oldPasswordEditText.getText().toString().trim();
            String newPass = editText.getText().toString().trim();
            progressBarLayout.setVisibility(View.VISIBLE);
            ArmutHVApp app = (ArmutHVApp) getActivity().getApplication();
            app.getBus().post(new MiscEvents.PatchUserPasswordRequest(new User.ModifyPassword(oldPass, newPass)));
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        if (editText != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showKeyboard(EditText editText) {
        if (editText == null || getActivity() == null) {
            return;
        }
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void showMessage(final String message) {
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void onPasswordUpdated(MiscEvents.PatchUserPasswordResponse response) {
        progressBarLayout.setVisibility(View.GONE);
        if (response.getResponse() != null) {
            Log.d(TAG, "REsponse != null");
            dismiss();
        } else {
            Log.d(TAG, "REsponse == null");
            if (editText != null)
                showKeyboard(oldPasswordEditText);
        }
    }

//    @Override
//    public void userPasswordUpdated(boolean succesful, int responseCode, String responseBody) {
//        progressBarLayout.setVisibility(View.GONE);
//        if (succesful) {
//            dismiss();
//            app.me.getUserInfo().setForceConfirmPassword(0);
//            callback.refreshJobsScreen(true);
//        } else {
//            showKeyboard(editText);
//            showMessage(responseBody);
//        }
//    }

    public interface PasswordCreatedCallback {
        void refreshJobsScreen(boolean succesful);
    }
}

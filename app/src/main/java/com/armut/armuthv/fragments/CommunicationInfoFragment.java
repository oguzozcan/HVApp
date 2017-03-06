package com.armut.armuthv.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.armut.armuthv.R;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.components.QuestionTextView;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.objects.UserInfo;
import com.armut.armuthv.utils.ArmutUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Subscribe;

import java.lang.reflect.Type;

/**
 * Created by oguzemreozcan on 29/09/16.
 */

public class CommunicationInfoFragment extends BasicFragment { //implements ChangeUserInfoCalback// , UserTask.UserTaskCallback , LoginTask.LoginTaskCallback

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText phoneEditText;
    private EditText mailEditText;
    //    private String newUserJson;
    private EditText focusView = null;
    private BasicFragment.LoadingProcess loadingCallback;
    //private String userId;

    @Override
    public void setTag() {
        TAG = "CommunicationInfoFragment";
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            loadingCallback = (BasicFragment.LoadingProcess) activity;
            if (loadingCallback != null) {
                loadingCallback.loadingIsInProgress(false);
            }
            //app.bus.register(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (focusView != null) {
            focusView.setClickable(true);
            focusView.setFocusable(true);
            focusView.requestFocus();
        }
        //app.bus.register(this);
        //TODO
//        if (getActivity() != null) {
//            QuestionsActivity.hideKeyboardFrom(getActivity().getApplicationContext(), focusView);
//            QuestionsActivity.hideKeyboard(getActivity());
        hideKeyboard();
//        }
    }

    //TODO
    public boolean answersGivenReadyToPassNewPage() {
        try {
            if ((getText(mailEditText).equals("") || getText(phoneEditText).equals(""))) {
                // getText(nameEditText).equals("") || getText(surnameEditText).equals("") ||
                return !checkValidity(getText(nameEditText), getText(surnameEditText), getText(mailEditText), getText(phoneEditText));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private String getText(EditText editText) {
        if (editText == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.com_info_layout, container, false);
        RelativeLayout mainLayout = (RelativeLayout) rootView.findViewById(R.id.comParentLayout);
        QuestionTextView qv = (QuestionTextView) rootView.findViewById(R.id.questionTv);
        qv.setVisibility(View.GONE);
        nameEditText = (EditText) rootView.findViewById(R.id.nameEditText);
        surnameEditText = (EditText) rootView.findViewById(R.id.surnameEditText);
        mailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
        phoneEditText = (EditText) rootView.findViewById(R.id.phoneEditText);
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phoneEditText.length() == 1) {
                    String text = phoneEditText.getText().toString();
                    if (text.startsWith("0")) {
                        text = text.substring(1);
                        phoneEditText.setText(text);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (focusView != null) {
                    if (focusView.getError() != null)
                        focusView.setError(null);
                }
            }
        });

        if (loadingCallback != null) {
            loadingCallback.loadingIsInProgress(false);
        }
        //userId = app.getUser().getUserInfo().getUserId();
        //int backgroundColor = ;
        //rootView.setBackgroundColor(backgroundColor);
        mainLayout.setBackgroundColor(getArguments().getInt("BACKGROUND_COLOR"));
        fillUserInfo();
        return rootView;
    }

    private void fillUserInfo() {
        Log.d(TAG, "FILL USER INFO: ");
        UserInfo info = null;
        if (app.getUser() != null) {
            info = app.getUser().getUserInfo();
        }
        if (info != null) {
            //Log.d(TAG, "USER INFO USER ID from app: " + app.me.getUserInfo().getUserId());
            String name = info.getFirstName();
            String surname = info.getLastName();
            String phone = info.getPhoneNumber();
            String email = info.getEmail();
            if (name != null) {
                nameEditText.setText(name);
            }
            if (phone != null) {
                phoneEditText.setText(phone);
            }
            if (email != null) {
                mailEditText.setText(email);
                //mailEditText.setBackgroundResource(R.drawable.selector_white_colored_edit_text);
            }
            if (surname != null) {
                surnameEditText.setText(surname);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO QuestionsActivity.hideKeyboard(getActivity());
        hideKeyboard();
        return true;
    }

    String newUserJson;

    public void prepareNewUserInfoJson() {
        User user = new User();
        focusView = null;
        String newFirstName = getText(nameEditText);
        String newLastName = getText(surnameEditText);
        String newEmail = getText(mailEditText);
        String newPhone = getText(phoneEditText);
        //int callPreference = getCallPreferenceId();
        String userId = app.getUser().getUserInfo().getUserId();
        if (checkValidity(newFirstName, newLastName, newEmail, newPhone)) {
            //showKeyboard(focusView);
            Log.d(TAG, "CANCEL LOADING");
            loadingCallback.loadingIsInProgress(false);
        } else {
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setEmail(newEmail);
            userInfo.setFirstName(newFirstName);
            userInfo.setLastName(newLastName);
            userInfo.setPhoneNumber(newPhone);
            //userInfo.setCallPreference(isNewUserComInfoScreen ? userInfo.getCallPreference() : callPreference);
            user.setUserInfo(userInfo);
            Gson gson = new GsonBuilder().create();
            Type type = new TypeToken<UserInfo>() {
            }.getType();
            newUserJson = gson.toJson(userInfo, type);
            Log.d(TAG, "JSON : " + newUserJson);
            hideKeyboard();
            app.getBus().post(new MiscEvents.PatchUserRequest(userInfo));
        }
    }

    private boolean checkEmailValidity(String newEmail) {
        boolean cancelProcess = false;
        if (!isEmailValid(newEmail)) {
            focusView = mailEditText;
            focusView.setError(null);
            mailEditText.setError(getString(R.string.error_invalid_email));
            loadingCallback.loadingIsInProgress(false);
            cancelProcess = true;
            showKeyboard(focusView);
        }
        return cancelProcess;
    }

    private boolean checkValidity(String newFirstName, String newLastName, String newEmail, String newPhone) {
        boolean cancelProcess = false;
        String errorText = getString(R.string.error_field_required);
        // Check for a valid email address.
        if (TextUtils.isEmpty(newEmail)) {
            errorText = getString(R.string.error_field_required);
            focusView = mailEditText;
            cancelProcess = true;
        } else if (!isEmailValid(newEmail)) {
            errorText = getString(R.string.error_invalid_email);
            focusView = mailEditText;
            cancelProcess = true;
        } else if (TextUtils.isEmpty(newPhone)) {
            errorText = getString(R.string.error_invalid_phone);
            focusView = phoneEditText;
            cancelProcess = true;
        } else if (!isPhoneNumberValid(newPhone)) {
            errorText = getString(R.string.error_invalid_phone);
            focusView = phoneEditText;
            cancelProcess = true;
        } else if (TextUtils.isEmpty(newFirstName)) {
            errorText = getString(R.string.error_invalid_name);
            focusView = nameEditText;
            Log.d(TAG, "EMPTY: " + getString(R.string.error_field_required));
            cancelProcess = true;
        } else if (TextUtils.isEmpty(newLastName)) {
            errorText = getString(R.string.error_invalid_surname);
            focusView = surnameEditText;
            cancelProcess = true;
        }

        if (cancelProcess) {
            Log.d(TAG, "CANCELL PROCESS : " + errorText);
            focusView.setError(errorText);
        }
        showKeyboard(focusView);
        return cancelProcess;
    }

    private boolean isEmailValid(String email) {
        return ArmutUtils.EMAIL_PATTERN.matcher(email).matches();//email.contains("@") && email.trim().length() > 4;
    }

    private boolean isPhoneNumberValid(String phone) {
        Log.d(TAG, "IS PHONE NUMBER VALID: " + phone);
        if (phone.startsWith("0")) {
            Log.d(TAG, "STARTS WITH 0");
            return false;
        }
        return phone.trim().length() == 10; //ArmutUtils.EMAIL_PATTERN.matcher(email).matches();//email.contains("@") && email.trim().length() > 4;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showKeyboard(EditText editText) {
        if (editText == null || getActivity() == null) {
            return;
        }
        editText.clearFocus();
        editText.setFocusable(true);
        editText.setClickable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }


    @Subscribe
    public void userInfoUpdated(MiscEvents.PatchUserResponse response) {
        Log.d(TAG, "USER INFO ADD RESULT ");
        if (response != null) {
            if(newUserJson != null){
                if(!newUserJson.isEmpty()){
                    Gson gson = new GsonBuilder().create();
                    Type type = new TypeToken<UserInfo>() {}.getType();
                    UserInfo info = gson.fromJson(newUserJson, type);
                    User user = app.getUser();
                    user.setUserInfo(info);
                    app.setUser(user);
                }
            }
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            Log.d(TAG, "USER INFO UPDATED RESULT OK, CLOSE ACTIVITY AND SEND MESSAGE");
        }
        if (loadingCallback != null) {
            loadingCallback.loadingIsInProgress(false);
        }
    }
}


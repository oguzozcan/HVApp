package com.armut.armuthv.fragments;

/**
 * Created by oguzemreozcan on 14/06/16.
 */

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.armut.armuthv.BaseActivity;
import com.armut.armuthv.BuildConfig;
import com.armut.armuthv.R;
import com.armut.armuthv.WebActivity;
import com.armut.armuthv.busevents.AuthRequest;
import com.armut.armuthv.busevents.AuthResponse;
import com.armut.armuthv.busevents.LiveAuthRequest;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.Profile;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.TimeUtils;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BasicFragment implements LoaderManager.LoaderCallbacks<Cursor>, BasicFragment.LoadingProcess {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mImpersonateView;
    private View mProgressView;
    private View mLoginFormView;
    private boolean permissionGranted;
    private String baseUrl = Constants.BASE_LIVE_URL;
    private String baseAuthUrl = Constants.AUTH_URL;

    @Override
    public void setTag() {
        TAG = "LoginFragment";
    }

    @Override
    public void onDetach() {
        hideKeyboard();
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.apiRadioGroup);
        RadioButton liveRadioButton = (RadioButton) radioGroup.findViewById(R.id.liveButton);
        RadioButton stageRadioButton = (RadioButton) radioGroup.findViewById(R.id.stageButton);
        RadioButton testRadioButton = (RadioButton) radioGroup.findViewById(R.id.testButton);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onApiSelected(view);
            }
        };
        testRadioButton.setOnClickListener(onClickListener);
        liveRadioButton.setOnClickListener(onClickListener);
        stageRadioButton.setOnClickListener(onClickListener);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mLoginFormView = rootView.findViewById(R.id.login_form);
        mProgressView = rootView.findViewById(R.id.loadingLayout);
        mProgressView.setVisibility(View.GONE);
        String email = getArguments().getString("EMAIL");
//        //mListener.setTitleName(TAG);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.email);
        if (email != null) {
            mEmailView.setText(email);
        }
        if (BuildConfig.DEBUG) {
            populateAutoComplete();
        }
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mImpersonateView = (EditText) rootView.findViewById(R.id.impersonateUser);
//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

        if (!BuildConfig.DEBUG) {
            radioGroup.setVisibility(View.GONE);
            mImpersonateView.setVisibility(View.GONE);
        } else {
            radioGroup.setVisibility(View.VISIBLE);
            mImpersonateView.setVisibility(View.VISIBLE);
        }

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        final Button mEmailSignInButton = (Button) rootView.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                attemptLogin();
            }
        });
        final Button forgetPasswordButton = (Button) rootView.findViewById(R.id.forgetPassword);
        forgetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("URL", Constants.FORGET_PASSWORD_URL);
                intent.putExtra("title", "Şifremi Unuttum");
                startActivity(intent);
                BaseActivity.setTranslateAnimation(getActivity());
                //mListener.onFragmentInteraction("password");
            }
        });
//        final TextView toggleTitle = (TextView) rootView.findViewById(R.id.showPasswordToggleTitle);
//        final CheckBox showPassToggle = (CheckBox) rootView.findViewById(R.id.showPasswordToggle);
//        LinearLayout toggleLayout = (LinearLayout) rootView.findViewById(R.id.showPasswordToggleLayout);
        final TextView showPass = (TextView) rootView.findViewById(R.id.showPassword);
        mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showPass.getText().equals(getString(R.string.show_password))) {
                    mPasswordView.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPass.setText(getString(R.string.hide_password));
                    mPasswordView.setSelection(mPasswordView.getText().length());
                } else {
                    mPasswordView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showPass.setText(getString(R.string.show_password));
                    mPasswordView.setSelection(mPasswordView.getText().length());
                }
            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    showPass.setVisibility(View.VISIBLE);
                } else {
                    showPass.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        forgetPasswordButton.setText(Html.fromHtml(getString(R.string.forget_password)));
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mEmailView.requestLayout();
                showKeyboard(mEmailView);
            }
        };
        //if(!loggedInBefore)
        if (attached) {
            handler.postDelayed(r, 300);
        }
        return rootView;
    }

    private void populateAutoComplete() {
        if (requestContactPermission()) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    private void attemptLogin() {
        hideKeyboard();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String impersonate = mImpersonateView.getText().toString();
        boolean cancel = false;
        EditText focusView = null;
        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            showKeyboard(mPasswordView);
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        }
//        else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            showKeyboard(focusView);
            showProgress(false);
        } else {
            try {
                showProgress(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (attached && ArmutUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
                //mProgressView.setVisibility(View.VISIBLE);
//                LoginTask authTask = new LoginTask(this, Constants.AUTH_URL, new BasicNameValuePair("username", email), new BasicNameValuePair("password", password));
//                authTask.execute(new BasicNameValuePair("client_id", Constants.CLIENT_ID),
//                        new BasicNameValuePair("grant_type", "password"),
//                        new BasicNameValuePair("client_secret", Constants.CLIENT_SECRET));
                Log.d(TAG, "SEND AUTH REQUEST");
                //TODO first check url and unregister&register from api
                app.getDataSaver().putString(Constants.BASE_URL_KEY, baseUrl);
                app.getDataSaver().putString(Constants.BASE_AUTH_URL_KEY, baseAuthUrl);
                app.getDataSaver().save();
                app.registerRestApi(true);
                Log.d(TAG, "UNREGISTER AND REGISTER COMPLETED");
                if (!TextUtils.isEmpty(impersonate)) {
                    app.getBus().post(new AuthRequest(email, password, impersonate, "password", Constants.CLIENT_ID));
                } else {
                    app.getBus().post(new LiveAuthRequest(email, password, "password", Constants.CLIENT_ID));
                }

            } else {
                app.openOKDialog((AppCompatActivity) getActivity(), this);
            }
        }
    }

    private boolean isEmailValid(String email) {
        return ArmutUtils.EMAIL_PATTERN.matcher(email).matches();//email.contains("@") && email.trim().length() > 4;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void onApiSelected(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.liveButton:
                if (checked) {
                    baseUrl = Constants.BASE_LIVE_URL;
                    baseAuthUrl = Constants.AUTH_URL;
                    break;
                }
            case R.id.stageButton:
                if (checked) {
                    baseUrl = Constants.BASE_STAGE_URL;
                    baseAuthUrl = Constants.AUTH_URL;
                    break;
                }
            case R.id.testButton:
                if (checked) {
                    baseUrl = Constants.BASE_TEST_URL;
                    baseAuthUrl = Constants.TEST_AUTH_URL;
                    break;
                }
        }
    }

    private void showKeyboard(EditText editText) {
        if (editText != null) {
            editText.requestFocus();
        }
        if (attached && getActivity() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.NO_CONNECTION && resultCode == 1) {
            Log.d(TAG, "RETRY CONECTION - CONNECTION NOT AVAILABLE");
            hideKeyboard();
            attemptLogin();
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this.getActivity().getApplicationContext(),
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,
                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},
                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor == null || cursorLoader == null) {
            if (cursor == null) {
                Log.d(TAG, "CURSOR NULL");
            }
            if (cursorLoader == null) {
                Log.d(TAG, "CURSOR LOADER IS NULL");
            }
            return;
        }
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Subscribe
    public void getAuthStatus(AuthResponse object) {
        if (object.getResponse() != null && attached) {
            String accessToken = object.getResponse().body().getAccessToken();
            Log.d(TAG, "AUTH RESPONSE: " + accessToken);
            if (accessToken != null) {
                app.getDataSaver().putString(Constants.ACCESS_TOKEN_KEY, "bearer " + accessToken);
                String userId = object.getUserId();
                app.getDataSaver().putString(Constants.USERNAME, userId);
                app.getDataSaver().save();
                MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
                mixpanelAPI.getPeople().set("Account Type", "HV");
                mixpanelAPI.identify(userId);
                mixpanelAPI.getPeople().identify(userId);
                mixpanelAPI.getPeople().set("Email", userId);
                mixpanelAPI.getPeople().set("Platform", "Android");
                mixpanelAPI.getPeople().set("Last Visit", TimeUtils.getTodayJoda(true));
                mixpanelAPI.track("Logged In");
                app.getBus().post(new MiscEvents.ProfileRequest());
            }
        } else {
            showProgress(false);
        }
    }

//    @Subscribe
//    public void getUser(EventBusUserObject userObject) {
//        Log.d(TAG, "USER OBJECT on login fragment: ");
//    }

    @Subscribe
    public void onLoadProfile(MiscEvents.ProfileResponse response) {
        Log.d(TAG, "GET PROFILE: ");
        if (response != null) {
            if (response.getResponse().isSuccessful()) {
                ArrayList<Profile> profiles = response.getResponse().body();
                int defaultProfileIndex = 0;
                if (profiles.size() > 1) {
                    for (Profile p : profiles) {
                        if (p.isDefaultProfile()) {
                            break;
                        }
                        defaultProfileIndex++;
                    }
                }
                if (defaultProfileIndex == profiles.size()) {
                    defaultProfileIndex = 0;
                }
                Profile profile = response.getResponse().body().get(defaultProfileIndex);
                int businessModel = profile.getBusinessModel();
                app.getDataSaver().putInt(Constants.BUSINESS_MODEL_ID, businessModel);
                app.getDataSaver().save();
                Log.d(TAG, "Profile loaded so close the screen");
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else if (response.getResponse().code() == 404) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Lütfen geçerli bir hizmet veren hesabıyla giriş yapınız.", Toast.LENGTH_LONG).show();
                    }
                });
                showProgress(false);
                app.logout();
            }
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        if (emailAddressCollection == null) {
            return;
        }
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEmailView.setAdapter(adapter);
    }

    private boolean requestContactPermission() {
        if (!ArmutUtils.isMarshmallow()) {
            permissionGranted = true;
            return true;
        }
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessage("Adres defteri bilgilerini, kullanıcı adı bilgini otomatik olarak doldurabilmek için istiyoruz. Bu izni vermek istemiyorsan kullanıcı adını her seferinde kendin girebilirsin.");
            }
//            else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    Constants.PERMISSIONS_READ_CONTACTS);
//            }
        } else {
            permissionGranted = true;
        }

        return permissionGranted;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permissionGranted = true;
                    populateAutoComplete();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    permissionGranted = false;
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void showMessage(final String message) {
        if (attached) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void loadingIsInProgress(boolean isLoading) {

    }
}

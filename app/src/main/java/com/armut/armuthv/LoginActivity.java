package com.armut.armuthv;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.armut.armuthv.fragments.LoginFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends BaseActivity {

    @Override
    protected void setTag() {
        TAG = "Login Activity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = (ArmutHVApp) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // true
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbarTitle.setText(getString(R.string.login));
        String email = getIntent().getStringExtra("EMAIL");
        FragmentTransaction fragmentTx = getSupportFragmentManager().beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString("EMAIL", email);
        loginFragment.setArguments(bundle);
        //fragmentTx.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_left, R.anim.enter_from_left, R.anim.exit_from_right);
        fragmentTx.replace(R.id.container, loginFragment)//.addToBackStack(loginFragment.TAG)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        app.hideKeyboard(this);
        //hideKeyboard();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

//    @Subscribe
//    public void onLoadingError(EventBusLoadingError eventBusLoadingError) {
//        int statusCode = eventBusLoadingError.getStatusCode();
//        switch (statusCode) {
//            case 200:
//            case 201:
//            case 204:
//            case 304:
//                return;
//            case 306:
//                Log.d(TAG, "UNSUPPORTED API");
//                DialogNoSupport dialogNoSupport = new DialogNoSupport();
//                //dialogNoSupport.setTargetFragment(BasicFragment.this, Constants.NO_SUPPORT);
//                dialogNoSupport.show(getSupportFragmentManager(), "noSupportDialog");
//                break;
//            default:
//                Log.d(TAG, "LOGIN ACTIVITY");
//                BasicActivity.showMessage(this, statusCode, eventBusLoadingError.getErrorMessage());
//        }
//    }
}
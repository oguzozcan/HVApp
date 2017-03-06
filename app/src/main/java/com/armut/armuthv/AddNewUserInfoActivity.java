package com.armut.armuthv;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.fragments.BasicFragment;
import com.armut.armuthv.fragments.CommunicationInfoFragment;

public class AddNewUserInfoActivity extends BaseActivity implements BasicFragment.LoadingProcess {

    private boolean loadingInProgress;
    private LinearLayout progressBarLayout;

    @Override
    protected void setTag() {
        TAG = "AddNewUserInfoActivity";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user_data);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        Button continueButton = (Button) findViewById(R.id.continueButton);
        progressBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);
        mainToolbar.setContentInsetsAbsolute(0, 0);
        mainToolbar.setTitle("");
        setSupportActionBar(mainToolbar);
        ActionBar actionBar;
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getString(R.string.update_userinfo));
        final CommunicationInfoFragment fragment = new CommunicationInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("BACKGROUND_COLOR", ContextCompat.getColor(this, R.color.whitish_background));
        fragment.setArguments(bundle);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingInProgress) {
                    return;
                }
                if (fragment.answersGivenReadyToPassNewPage()) {
                    showProgressBar(true);
                    fragment.prepareNewUserInfoJson();
                }
            }
        });
        setSupportActionBar(mainToolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    private void showProgressBar(final boolean show) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingInProgress = show;
                    progressBarLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                    Log.d("ADD NEW USER ACT", "SET VISIBLITY TO GONE " + show);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        } else if (id == android.R.id.home) {
            //hideKeyboard(this);
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }
    }

    @Override
    public void loadingIsInProgress(boolean loadingStarted) {
        showProgressBar(loadingStarted);
    }
}


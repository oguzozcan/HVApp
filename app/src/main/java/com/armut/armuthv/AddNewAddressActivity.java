package com.armut.armuthv;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.fragments.AskAddressFragment;
import com.armut.armuthv.fragments.BasicFragment;
import com.squareup.otto.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewAddressActivity extends BaseActivity implements BasicFragment.LoadingProcess{

    private boolean loadingInProgress;
    //private AskAddressFragment fragment;
    private LinearLayout progressBarLayout;

    @Override
    protected void setTag() {
        TAG = "AddNewAddressAct";
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
        toolbarTitle.setText(getString(R.string.update_address));
//        continueButton.setTypeface(ArmutUtils.loadFont(getAssets(), getString(R.string.font_pera_bold)));
//        toolbarTitle.setTypeface(ArmutUtils.loadFont(getAssets(), getString(R.string.font_pera_bold)));
        final AskAddressFragment fragment = new AskAddressFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("EDIT_ADDRESS", 1);
        bundle.putInt("INDEX" , -1);
        bundle.putInt("BACKGROUND_COLOR", Color.WHITE);
        bundle.putInt("SERVICE_ID", -1);
        fragment.setArguments(bundle);
        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        container.setBackgroundColor(Color.WHITE);
//        fragmentOrder = getArguments().getInt("INDEX");
//        serviceId = getArguments().getInt("SERVICE_ID");
//        backgroundColor = getArguments().getString("BACKGROUND_COLOR");

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadingInProgress) {
                    return;
                }
                if (fragment.answersGivenReadyToPassNewPage()) {
                    fragment.postNewAddressJson();
                    showProgressBar(true);
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

    @Subscribe
    public void onNetworkError(ApiErrorEvent event){
        loadingIsInProgress(false);
    }

    private void showProgressBar(final boolean show){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingInProgress = show;
                progressBarLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exit, menu);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }else if(id == android.R.id.home){
//            QuestionsActivity.hideKeyboard(this);
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
            MainActivity.setBackwardsTranslateAnimation(this);
        }
    }


    @Override
    public void loadingIsInProgress(boolean isLoading) {
        showProgressBar(isLoading);
    }
}

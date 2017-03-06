package com.armut.armuthv.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.BaseActivity;

/**
 * Created by oguzemreozcan on 17/05/16.
 */
public abstract class BasicFragment extends Fragment{ //implements DialogNoSupport.NoSupportCallback { // implements OnLoadingError

    public String TAG = "Basic Fragment";
    public ArmutHVApp app;
    public boolean attached;
    public Activity parentActivity;

    public BasicFragment() {
        setTag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (ArmutHVApp) getActivity().getApplication();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(app == null){
            app = (ArmutHVApp) getActivity().getApplication();
        }
        if (context instanceof BaseActivity){
            parentActivity = (BaseActivity) context;
        }
        app.getBus().register(this);
        attached = true;
    }

    @Override
    public void onDetach() {
        attached = false;
        app.getBus().unregister(this);
        super.onDetach();
        parentActivity = null;
    }

    void showMessage(final String message, final int length) {
        if (attached) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), message, length).show();
                }
            });
        }
    }

//    @Subscribe
//    public void onLoadingError(final EventBusLoadingError eventBusLoadingError) {
//        if (attached) {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    int statusCode = eventBusLoadingError.getStatusCode();
//                    switch (statusCode) {
//                        case 200:
//                        case 201:
//                        case 204:
//                        case 304:
//                            return;
//                        case 306:
//                            Log.d(TAG, "UNSUPPORTED API");
//                            DialogNoSupport dialogNoSupport = new DialogNoSupport();
//                            //dialogNoSupport.setTargetFragment(BasicFragment.this, Constants.NO_SUPPORT);
//                            dialogNoSupport.show(getChildFragmentManager(), "noSupportDialog");
//                            break;
//                        default:
//                            Log.d(TAG, "BF - ON LOADING ERROR " + statusCode);
//                            BasicActivity.showMessage(getActivity(), statusCode, eventBusLoadingError.getErrorMessage());
//                            break;
//                    }
//                }
//            });
//        }
//    }

//    @Override
//    public void versionNotSupported(boolean update) {
//        if (update) {
//            String appPackageName = "com.armut.armutha";//getActivity().getPackageName();//"com.armut.armutha";
//            Log.d(TAG, "VERSION NOT SUPPORTED: " + appPackageName);
//            try {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
//            } catch (android.content.ActivityNotFoundException anfe) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
//            }
//        }
//    }

    protected abstract void setTag();

//    public interface OnFragmentInteractionListener {
//        void onFragmentInteraction(String name, int pathId, int id, int color);
//
//        void onTitleTextChange(String text);
//
//        void displayBottomBar(boolean display);
//
//        void displayTopBar(boolean display);
//
//        void onToolbarColorChange(int color);
//
//        void onTitleColorChange(int color);
//
//        void onToolbarIconEnabled(int imageResource);
//
//        void onToolbarHomeButtonChange(int imageResource);
//
//        void changeTab(int tabIndex);
//
//        void initNotificationView(boolean visible, int badgeCount);
//    }
//
//    public interface AnswersCallback {
//        void getTotalPrice(String price);
//
//        void goToPage(int pageNumber, boolean inBetweenQuestionScreens);
//
//        void problemOnChangingPage();
//
//        void onPageAttached(int pageNumber);
//
//        void recalculatePrice(double value, int unitToChange);
//    }


    public interface LoadingProcess {
        //loading started boolean is just to control loading bar, sending a task with loadingStarted true will start sended task , oterwise just send null as task
        void loadingIsInProgress(boolean isLoading);
    }

//    public interface OnLoginFragmentInteractionListener {
//        void callGetUser(String userId);
//    }

}


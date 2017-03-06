package com.armut.armuthv.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.armut.armuthv.AddNewAddressActivity;
import com.armut.armuthv.AddNewUserInfoActivity;
import com.armut.armuthv.BaseActivity;
import com.armut.armuthv.JobDetailActivity;
import com.armut.armuthv.LoginActivity;
import com.armut.armuthv.MainActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.ReviewsActivity;
import com.armut.armuthv.WebActivity;
import com.armut.armuthv.adapters.AdapterLinearLayout;
import com.armut.armuthv.adapters.AddressListAdapter;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.Address;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.Profile;
import com.armut.armuthv.objects.Review;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.objects.UserInfo;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.PhotoSelectorHelper;
import com.armut.armuthv.utils.TimeUtils;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 12/08/16.
 */
public class UserProfileFragment extends BasicFragment implements AddressListAdapter.AddressClicked,
        DialogLogoutFragment.LogoutCallback,
        PhotoSelectorHelper.PhotoSelector, DialogPasswordCreateFragment.PasswordCreatedCallback {

    private User me;
    private int userUpdatePart;
    private PhotoSelectorHelper helper;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    // @Override
    public static UserProfileFragment newInstance() {
        //UserProfileFragment fragment =
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
        return new UserProfileFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        String token = app.isTokenPresent(TAG);
        //setRetainInstance(true);
        if (token != null) {
            String userId = app.getDataSaver().getString(Constants.USERNAME);//app.getUserId();
            Log.d(TAG, "USER ID: " + userId);
            //if(app.getUser() == null){
            app.getBus().post(new MiscEvents.UserRequest(""));
            //}else{
            app.getBus().post(new MiscEvents.ProfileRequest());
            if (!userId.equals("")) {
                app.getBus().post(new MiscEvents.GetRatingsRequest(userId));
            }
            // }
        }
    }

    @Override
    public void setTag() {
        TAG = "User_Profile_Fragment";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_profile_collapsable_layout, container, false);
        String token = app.isTokenPresent(TAG);
        if (token != null) {
            Log.d(TAG, "ON CREATE VIEW FULFILL USER INFO");
            showLoading(true, rootView);
            switchToChild(1);
        } else {
            switchToChild(0);
            initLoginScreen(rootView);
        }
        return rootView;
    }

    private void initLoginScreen(View rootView) {
        if (rootView == null) {
            return;
        }
        RelativeLayout noProfileLayout = (RelativeLayout) rootView.findViewById(R.id.mainNoProfileLayout);
        noProfileLayout.setVisibility(View.VISIBLE);
//            fillEmptyViews(rootView, assetManager);
//            mListener.onToolbarColorChange(Color.WHITE);
//            mListener.onTitleColorChange(Color.parseColor("#808080"));
        Button loginButton = (Button) rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginActivity();
            }
        });

        Button signUp = (Button) rootView.findViewById(R.id.signUp);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            signUp.setText(Html.fromHtml(getString(R.string.sign_up_link), Html.FROM_HTML_MODE_LEGACY));
        } else {
            signUp.setText(Html.fromHtml(getString(R.string.sign_up_link)));
        }
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(Constants.REGISTER_HV_URL));
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("URL", Constants.REGISTER_HV_URL);
                intent.putExtra("title", "Üye Ol");
                startActivity(intent);
                BaseActivity.setTranslateAnimation(getActivity());
            }
        });
    }

    private void switchToChild(int childNo) {
        if (getView() != null) {
            ViewSwitcher switcher = (ViewSwitcher) getView().findViewById(R.id.viewSwitcher);
            switcher.setDisplayedChild(childNo);
        } else {
            Log.d(TAG, "GET VIEW NULL");
        }
    }

    private void showLoading(boolean show, View rootView) {
        if (rootView != null) {
            View loadingLayout = rootView.findViewById(R.id.loadingLayout);
            View loadingLayoutTransparent = rootView.findViewById(R.id.loadingLayout1);
            if (show) {
                loadingLayout.setVisibility(View.VISIBLE);
                loadingLayoutTransparent.setVisibility(View.VISIBLE);
            } else {
                loadingLayout.setVisibility(View.GONE);
                loadingLayoutTransparent.setVisibility(View.GONE);
            }
        }
    }

    private void logout() {
        View view = getView();
        if (view == null) {
            Log.d(TAG, "VIEW IS NULL");
            return;
        }
        RelativeLayout noProfileLayout = (RelativeLayout) getView().findViewById(R.id.mainNoProfileLayout);
        noProfileLayout.setVisibility(View.VISIBLE);
        switchToChild(0);
        initLoginScreen(getView());
        app.logout();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(this.getActivity(), LoginActivity.class);
        startActivityForResult(intent, Constants.UPDATE_USER_PROFILE_PAGE);
    }

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
                            //Log.d(TAG, "IS DEFAULT PROFILE true : " + p.getBusinessName());
                            break;
                        }
                        //else{
                        //Log.d(TAG, "IS DEFAULT PROFILE false: " + p.getBusinessName());
                        //}
                        defaultProfileIndex++;
                    }
                }
                if (defaultProfileIndex == profiles.size()) {
                    defaultProfileIndex = 0;
                }
                Profile profile = response.getResponse().body().get(defaultProfileIndex);

                Log.d(TAG, "GET PROFILE - fill userInfo: ");
                if (getView() != null) {
                    fillUserInfo(getView(), profile);
                }
            } else if (response.getResponse().code() == 404) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Lütfen geçerli bir hizmet veren hesabıyla giriş yapınız.", Toast.LENGTH_LONG).show();
                    }
                });
                logout();
            }
        }
    }

    @Subscribe
    public void onLoadRatings(final MiscEvents.GetRatingsResponse response) {
        Log.d(TAG, "ON LOAD RATINGS : " + response);
        if (response != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    View fragmentView = getView();
                    if (fragmentView != null) {
                        final ArrayList<Review> reviewList = response.getResponse().body();
                        int size = reviewList.size();
                        //LinearLayout pagerLayout = (LinearLayout) fragmentView.findViewById(R.id.pagerLayout);
                        TextView ratingSubtitle = (TextView) fragmentView.findViewById(R.id.ratingSubtitle);
                        ViewPager ratingsSlider = (ViewPager) fragmentView.findViewById(R.id.ratingsSlider);
                        Button viewMoreButton = (Button) fragmentView.findViewById(R.id.viewMoreButton);
                        if (size > 0) {
                            viewMoreButton.setText("Tümünü Gör ( " + reviewList.size() + " )");
                            viewMoreButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    goToReviewList(reviewList);
                                }
                            });
                            int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics());
                            ratingsSlider.setAdapter(new RatingSliderPagerAdapter(getActivity(), reviewList));
                            ratingsSlider.setPageMargin(margin);
                            ratingsSlider.setClipToPadding(false);
                            ratingsSlider.setPadding(margin * 2, 0, margin * 2, 0);
                            ratingsSlider.setOffscreenPageLimit(1);
                            //pagerLayout.setVisibility(View.VISIBLE);
                            ratingSubtitle.setVisibility(View.GONE);
                            viewMoreButton.setVisibility(View.VISIBLE);
                            ratingsSlider.setVisibility(View.VISIBLE);
                        } else {
                            ratingSubtitle.setVisibility(View.VISIBLE);
                            // pagerLayout.setVisibility(View.GONE);
                            viewMoreButton.setVisibility(View.GONE);
                            ratingsSlider.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "DETACH FRAGMENT");
    }

    private void goToReviewList(ArrayList<Review> reviewList) {
        Intent intent = new Intent(this.getActivity(), ReviewsActivity.class);
        intent.putExtra("PROFILE_ID", me.getUserInfo().getUserId());
        intent.putParcelableArrayListExtra("REVIEW_LIST", reviewList);
        startActivity(intent);
        MainActivity.setTranslateAnimation(getActivity());
    }

    @Subscribe
    public void onLoadUser(final MiscEvents.UserResponse event) {
        Log.d(TAG, "ON LOAD USER");
        if (event != null) {
            Response<User> userResponse = event.getResponse();
            if (userResponse != null) {
                User user = userResponse.body();
                if (user != null) {
                    Log.d(TAG, "USER SUCCESFULLY SAVED ");
                    app.setUser(user);
                    app.getBus().post(new MiscEvents.ProfileRequest());
                    String userID = user.getUserInfo().getUserId();
                    if (!userID.equals("")) {
                        app.getBus().post(new MiscEvents.GetRatingsRequest(userID));
                    }
                    //TODO might be necassary
//                    app.getDataSaver().putString(Constants.USERNAME, user.getUserInfo().getUserId());
//                    app.getDataSaver().save();
                }
            }
        }
//        View loadingLayout = findViewById(R.id.progressBar1);
//        loadingLayout.setVisibility(View.GONE);
    }

    private void fillUserInfo(View rootView, Profile profile) {
        me = app.getUser();
        if (me == null) {
            //TODO
            //useLoader();
            return;
        }
        final UserInfo info = me.getUserInfo();
//        mListener.onToolbarColorChange(Color.TRANSPARENT);
//        mListener.onTitleColorChange(Color.WHITE);
        Button logoutButton = (Button) rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogLogoutFragment logoutDialog = DialogLogoutFragment.newInstance();
                //logoutDialog.setTargetFragment(UserProfileFragment.this, Constants.LOGOUT);
                logoutDialog.show(UserProfileFragment.this.getChildFragmentManager(), "logoutDialog");
            }
        });
        ImageView backgroundBlurredImage = (ImageView) rootView.findViewById(R.id.backgroundImage);
        CircleImageView profileThumbnail = (CircleImageView) rootView.findViewById(R.id.profileThumbnail);
        updateProfileInfo(profile, info);
        profileThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicClicked();
            }
        });
        backgroundBlurredImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePicClicked();
            }
        });

        CardView changePasswordCard = (CardView) rootView.findViewById(R.id.changePasswordCard);
        changePasswordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPasswordCreateFragment passwordDialog = DialogPasswordCreateFragment.newInstance(false);
                //passwordDialog.setTargetFragment(UserProfileFragment.this, Constants.PASSWORD_CREATED);
                passwordDialog.show(UserProfileFragment.this.getChildFragmentManager(), "fragment_create_password");
            }
        });

        Button addNewAddressButton = (Button) rootView.findViewById(R.id.addNewAddressButton);
        addNewAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Intent intent = new Intent(UserProfileFragment.this.getActivity(), AddNewAddressActivity.class);
                startActivityForResult(intent, Constants.UPDATE_ADDRESS_REQUEST_CODE);
                MainActivity.setTranslateAnimation(getActivity());
            }
        });

        AdapterLinearLayout addressListView = (AdapterLinearLayout) rootView.findViewById(R.id.addressListView);
        addressListView.setItemsSelectable(true);
        addressListView.setOnItemClickListener(new AddressClick());

        if (me.getAddresses() != null) {
            if (me.getAddresses().size() == 0) {
                addNewAddressButton.setVisibility(View.VISIBLE);
            } else {
                addNewAddressButton.setVisibility(View.GONE);
            }
        } else {
            addNewAddressButton.setVisibility(View.VISIBLE);
        }

        TextView accountBalanceTv = (TextView) rootView.findViewById(R.id.accountBalance);
        double balance = me.getUserInfo().getAccountBalance();
        if (balance > 0) {
            accountBalanceTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.avocado));
        } else {
            accountBalanceTv.setTextColor(ContextCompat.getColor(getActivity(), R.color.red));
        }
        accountBalanceTv.setText(ArmutUtils.getMoneyPattern(balance));

//        });
//        CardView shareTwitter = (CardView) rootView.findViewById(R.id.shareTwitter);
//        shareTwitter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Share twitter on button click");
//                String application = "com.twitter.android"; // "com.facebook.katana";
//                boolean appInstalled = ArmutUtils.appInstalledOrNot(application, getActivity());
//                if (!appInstalled) {
//                    Log.d(TAG, "Twitter does not exist");
//                    Toast.makeText(getActivity().getApplicationContext(), "Telefonda twitter uygulaması mevcut değil.", Toast.LENGTH_LONG).show();
//                }
//                TweetComposer.Builder builder = new TweetComposer.Builder(getActivity())
//                        .text(getString(R.string.share_text));
//                //.image(FTUtils.getImageUri(getActivity(), image, "tmpImage"));
//                builder.show();
//            }
//        });

        CardView shareButton = (CardView) rootView.findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClick();
            }
        });

        CardView likeButton = (CardView) rootView.findViewById(R.id.likeButton);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogRateUsFragment fragment = DialogRateUsFragment.newInstance(true);
                fragment.show(getActivity().getFragmentManager(), "rateUsDialogFragment");
            }
        });

        Button contactArmut = (Button) rootView.findViewById(R.id.contactArmut);
        contactArmut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogContactFragment contactDialog = DialogContactFragment.newInstance("");
                contactDialog.show(getActivity().getFragmentManager(), "fragment_call_armut");
            }
        });
        CardView personalInfoCard = (CardView) rootView.findViewById(R.id.personalInfoCard);
        personalInfoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileFragment.this.getActivity(), AddNewUserInfoActivity.class);
                Log.d(TAG, "USERPROFILEFRAGMENT - userid : " + info.getUserId());
                startActivityForResult(intent, Constants.UPDATE_USER_INFO_REQUEST_CODE);
                MainActivity.setTranslateAnimation(getActivity());
            }
        });
        addAddressData();
        showLoading(false, rootView);
        rootView.findViewById(R.id.profileParentLayout).setVisibility(View.VISIBLE);
        switchToChild(1);
        showImage(profile.getUserPicUrl());

        ImageView badgeImage = (ImageView) rootView.findViewById(R.id.badgeIcon);
        ImageView badgeImageTop = (ImageView) rootView.findViewById(R.id.badgeIconTop);
        if (profile.getBadges() != null) {
            if (profile.getBadges().length > 0) {
                //Log.d(TAG, "BADGE COUNT MORE THEN 0");
                badgeImageTop.setVisibility(View.VISIBLE);
                badgeImage.setVisibility(View.VISIBLE);
                badgeImage.setBackgroundResource(R.drawable.badge_animation);
                AnimationDrawable frameAnimation = (AnimationDrawable) badgeImage.getBackground();
                frameAnimation.start();

            } else {
                //Log.d(TAG, "BADGE COUNT 0");
                badgeImage.setVisibility(View.GONE);
                badgeImageTop.setVisibility(View.GONE);
            }
        }
        TextView ratingCount = (TextView) rootView.findViewById(R.id.ratingCount);
        TextView rating = (TextView) rootView.findViewById(R.id.profileRating);
        rating.setText(Double.toString(profile.getAverageRating()));
        ratingCount.setText(Integer.toString(profile.getNumberOfComments()));
//        viewMoreButton.setText(Integer.toString(profile.getNumberOfComments()));
        //TODO
        ArrayList commentStarViews = new ArrayList<>(5);
        ImageView star1 = (ImageView) rootView.findViewById(R.id.star1);
        ImageView star2 = (ImageView) rootView.findViewById(R.id.star2);
        ImageView star3 = (ImageView) rootView.findViewById(R.id.star3);
        ImageView star4 = (ImageView) rootView.findViewById(R.id.star4);
        ImageView star5 = (ImageView) rootView.findViewById(R.id.star5);
        commentStarViews.add(star1);
        commentStarViews.add(star2);
        commentStarViews.add(star3);
        commentStarViews.add(star4);
        commentStarViews.add(star5);
        setStarVisibility(profile.getAverageRating(), commentStarViews);
//        loadingLayoutTransparent.setVisibility(View.GONE);
//        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_empty, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "REQUEST CODE: " + requestCode);
        Log.d(TAG, "RESULT CODE: " + resultCode);
        if (resultCode != Activity.RESULT_OK) {
            //TODO handle error
            return;
        }
        switch (requestCode) {
            case PhotoSelectorHelper.REQ_PICK_IMAGE:
                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(helper.createImageFile());
                    PhotoSelectorHelper.copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();
                    inputStream.close();
                    //userUpdatePart = Constants.UPDATE_USER_PROFILE_PAGE;
                } catch (Exception e) {
                    Log.e(TAG, "Error while creating temp file", e);
                }
                break;
            case PhotoSelectorHelper.REQ_CAMERA:
                break;

            case Constants.NEW_LOGIN_UPDATE_ALL:
                Log.d(TAG, "LOAD ALL JOBS");
                String token = app.isTokenPresent(TAG);
                if (token != null) {
                    app.getBus().post(new JobEvents.OpportunitiesRequest(token, ""));
                    app.getBus().post(new JobEvents.DealsRequest(token, ""));
                    app.getBus().post(new JobEvents.QuotesRequest(token, ""));
                }
                break;
        }

        if (requestCode == PhotoSelectorHelper.REQ_CAMERA || requestCode == PhotoSelectorHelper.REQ_PICK_IMAGE) {
            if (helper != null) {
                byte[] imageEncoded = helper.initPhotoForUpload();
                if(imageEncoded != null){
                    showLoading(true, getView());
                    Log.d(TAG, "ON ACTIVITY RESULT IN FRAGMENT : START PHOTO UPLOAD TASK");
                    //TODO
                    app.getBus().post(new MiscEvents.PostProfilePhotoRequest(imageEncoded));
                }else{
                    if(getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(),"Fotoğraf yüklenemedi, lütfen daha sonra tekrar deneyin.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
            return;
        }
        Log.d(TAG, "ON ACTIVITY RESULT IN FRAGMENT : " + requestCode);
        switchToChild(1);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setStarVisibility(final Double rating, final ArrayList<ImageView> viewsToChange) {
        if (rating == null) {
            return;
        }
        try {
            if (attached) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int ratingReal = rating.intValue();
                        double precision = rating - ratingReal;
                        Drawable fullStar = ContextCompat.getDrawable(UserProfileFragment.this.getActivity(), R.drawable.icn_star_full);
                        Drawable halfStar = ContextCompat.getDrawable(UserProfileFragment.this.getActivity(), R.drawable.icn_star_half);
                        Drawable emptyStar = ContextCompat.getDrawable(UserProfileFragment.this.getActivity(), R.drawable.icn_star_empy);
                        Log.d(TAG, "RATING REAL:  " + rating + " - rating: " + ratingReal + " - precision: " + precision);
                        for (int i = 1; i <= 5; i++) {
                            if (i <= ratingReal) {
                                viewsToChange.get(i - 1).setImageDrawable(fullStar);
                            } else if (i == (ratingReal + 1) && precision >= 0.5) {
                                viewsToChange.get(i - 1).setImageDrawable(halfStar);
                            } else if (i > ratingReal) {
                                viewsToChange.get(i - 1).setImageDrawable(emptyStar);
                            }
                        }
//                        if(reviewList != null){
//                            if(commentSummaryLayout.getVisibility() != View.VISIBLE){
//                                commentSummaryLayout.setVisibility(View.VISIBLE);
//                            }
//                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProfileInfo(Profile profile, UserInfo info) {
        String name = info.getFirstName().concat(" ").concat(info.getLastName());
        View view = getView();
        if (view == null) {
            return;
        }
        TextView nameTitle = (TextView) view.findViewById(R.id.profileName);
        TextView nameTv = (TextView) view.findViewById(R.id.name);
        TextView emailTv = (TextView) view.findViewById(R.id.email);
        TextView phoneTv = (TextView) view.findViewById(R.id.phone);
        nameTv.setText(name);
        nameTitle.setText(name);
        emailTv.setText(profile.getUserId());
        Phonenumber.PhoneNumber number;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        String text = profile.getMobilePhoneNumber();
        try {
            number = phoneUtil.parse(text, "TR");
            boolean isValid = phoneUtil.isValidNumber(number);
            if (isValid) {
                text = phoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            } else {
                text = profile.getMobilePhoneNumber();
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
            text = profile.getMobilePhoneNumber();
        }
        phoneTv.setText(text);
    }

    private void addAddressData() {
        if (me == null) {
            return;
        }
        View view = getView();
        if (view == null) {
            return;
        }
        ArrayList<Address> addressArrayList;
        if (me.getAddresses() == null) {
            addressArrayList = new ArrayList<>();
        } else {
            addressArrayList = new ArrayList<>(me.getAddresses());
        }
        Address address = null;
        try {
            address = addressArrayList.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (address == null) {
            Log.d(TAG, "ADDRESS NULL");
            return;
        } else {
            Log.d(TAG, "ADDRESS NOT NULL : " + address.getAddress());
        }
        if (addressArrayList.size() != 0) {
            AdapterLinearLayout addressListView = (AdapterLinearLayout) view.findViewById(R.id.addressListView);
            addressListView.setAdapter(new AddressListAdapter(getActivity(), this, R.layout.profile_address_card, addressArrayList));
        }
    }

    private void showImage(String imageSource) {
        if (imageSource == null) {
            return;
        }
        try {
            ImageView profileThumbnail = (ImageView) getView().findViewById(R.id.profileThumbnail);
            String source = Constants.IMAGE_BASE_URL_REAL + Constants.PROFILE_PICS_POSTFIX + imageSource + ";width=" + Constants.THUMBNAIL_SIZE + ";height=" + Constants.THUMBNAIL_SIZE + ";mode=crop;";
            Picasso.with(getActivity().getApplicationContext()).load(source).placeholder(R.drawable.default_users_7).into(profileThumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAddressClicked(int position) {
        //TODO
//        Intent intent = new Intent(UserProfileFragment.this.getActivity(), AddNewAddressActivity.class);
//        startActivityForResult(intent, Constants.UPDATE_ADDRESS_REQUEST_CODE);
//        MainActivity.setTranslateAnimation(getActivity());
    }

    @Override
    public void logoutSuccessful(boolean success) {
        Log.d(TAG, "LOGOUT SUCCESFUL " + success);
        if (success) {
            logout();
        }
    }

    @Subscribe
    public void photoUploadStatus(final MiscEvents.PostProfilePhotoResponse photoUploadObject) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getView() != null) {
                        View loadingLayoutTransparent = getView().findViewById(R.id.loadingLayout1);
                        if (loadingLayoutTransparent.getVisibility() == View.VISIBLE) {
                            loadingLayoutTransparent.setVisibility(View.GONE);
                        }
                    }
                    String response = photoUploadObject.getResponse();
                    Log.d(TAG, "PHOTO UPLOAD STATUS : is succesfull" + response);
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        String path = jsonObject.getString("img_name");
                        Log.d(TAG, "SHOW IMAGE : path : " + path);
                        if (app != null && path != null) {
                            UserInfo info = app.getUser().getUserInfo();
                            info.setPicUrl(path);
                            app.getUser().setUserInfo(info);
                            showImage(path);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(getString(R.string.profile_photo_upload_problem), Toast.LENGTH_LONG);
                }
            }
        });
    }

    private void profilePicClicked() {
        if (app.requestCardWritePermission(getActivity(), this, getString(R.string.warning_photo_upload_profile))) {
            final DialogUploadPhoto uploadDialog = DialogUploadPhoto.newInstance();
            //uploadDialog.setTargetFragment(UserProfileFragment.this, Constants.UPLOAD_PHOTO_DIALOG);
            uploadDialog.show(UserProfileFragment.this.getChildFragmentManager(), "uploadDialog");
        }
    }

    @Override
    public void uploadMethodSelected(int status) {
        if (attached && getActivity() != null) {
            helper = new PhotoSelectorHelper(getActivity(), UserProfileFragment.this);
            switch (status) {
                case PhotoSelectorHelper.REQ_CAMERA:
                    helper.takePicture();
                    break;
                case PhotoSelectorHelper.REQ_PICK_IMAGE:
                    helper.openGallery();
                    break;
            }
        }
    }

    @Override
    public void refreshJobsScreen(boolean succesful) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ON RESUME " + openPhotoDialog);
        if (openPhotoDialog) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    // EXECUTE ACTIONS (LIKE FRAGMENT TRANSACTION ETC.)
                    final DialogUploadPhoto uploadDialog = DialogUploadPhoto.newInstance();
                    uploadDialog.show(UserProfileFragment.this.getChildFragmentManager(), "uploadDialog");
                    openPhotoDialog = false;
                }
            }, 100);
        }
    }

    boolean openPhotoDialog = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "ON REQUEST PERMISSON RESULT: " + requestCode);
        switch (requestCode) {
            case Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the contacts-related task you need to do.
                    app.isWriteToSdCardPermissionGranted = true;
                    openPhotoDialog = true;
                    Log.d(TAG, "PERMISSION GRANTED POPUP WILL BE OPENED AT ON RESUME : " + openPhotoDialog);
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    app.isWriteToSdCardPermissionGranted = false;
                }
                app.getDataSaver().putBoolean(Constants.PERMISSION_KEY_WRITE_EXTERNAL_STORAGE, app.isWriteToSdCardPermissionGranted);
                app.getDataSaver().save();
                break;
            }
        }
    }

    public void onShareClick() {
        List<Intent> targetShareIntents = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> resInfos = pm.queryIntentActivities(shareIntent, 0);
        if (!resInfos.isEmpty()) {
            for (ResolveInfo resInfo : resInfos) {
                String packageName = resInfo.activityInfo.packageName;
                Log.i("Package Name", packageName);
                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana")
                        || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.plus")
                        || packageName.contains("com.google.android.talk") || packageName.contains("com.slack")
                        || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.orca")
                        || packageName.contains("com.yahoo.mobile") || packageName.contains("com.skype.raider")
                        || packageName.contains("com.android.mms")|| packageName.contains("com.linkedin.android")
                        || packageName.contains("com.google.android.apps.messaging")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
//                    Log.d(TAG, "APP NAME: " +  resInfo.loadLabel(pm).toString());
                    intent.putExtra("AppName", resInfo.loadLabel(pm).toString());
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text));
                    if(packageName.contains("com.twitter.android")){
                        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text_twitter));
                    }
                    //intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text));
                   // if(packageName.contains("com.google.android.gm") || packageName.contains("com.yahoo.mobile") ){
                        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_text_short));
                    //}
                    intent.setPackage(packageName);
                    targetShareIntents.add(intent);
                }
            }
            if (!targetShareIntents.isEmpty()) {
                Collections.sort(targetShareIntents, new Comparator<Intent>() {
                    @Override
                    public int compare(Intent o1, Intent o2) {
                        return o1.getStringExtra("AppName").compareTo(o2.getStringExtra("AppName"));
                    }
                });
                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Paylaşmak için uygulama seçin");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
                startActivity(chooserIntent);
            } else {
                Toast.makeText(getActivity(), "Paylaşmak için uygun uygulama yok.", Toast.LENGTH_LONG).show();
            }
        }
        try {
            MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getString(R.string.mpId));
            mixpanel.track("Reco on Social Media", ArmutUtils.getBasicJson(new BasicNameValuePair("Social Media", "NA")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AddressClick implements AdapterLinearLayout.OnItemClick {

        @Override
        public void onItemClick(int position, Object item) {
            Log.d(TAG, "ADDRESS CLICK");
            Intent intent = new Intent(UserProfileFragment.this.getActivity(), AddNewAddressActivity.class);
            startActivityForResult(intent, Constants.UPDATE_ADDRESS_REQUEST_CODE);
            MainActivity.setTranslateAnimation(getActivity());
        }
    }

    class RatingSliderPagerAdapter extends PagerAdapter {

        final Context mContext;
        final LayoutInflater mLayoutInflater;
        final ArrayList<Review> ratings;
        final Drawable fullStar;
        final Drawable halfStar;
        final Drawable emptyStar;

        RatingSliderPagerAdapter(Context context, ArrayList<Review> ratings) {
            mContext = context;
            this.ratings = ratings;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            fullStar = ContextCompat.getDrawable(UserProfileFragment.this.getActivity(), R.drawable.icn_star_full);
            halfStar = ContextCompat.getDrawable(UserProfileFragment.this.getActivity(), R.drawable.icn_star_half);
            emptyStar = ContextCompat.getDrawable(UserProfileFragment.this.getActivity(), R.drawable.icn_star_empy);
        }

        @Override
        public int getCount() {
            if (ratings == null) {
                return 0;
            }
            return ratings.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.reviews_summary_layout, container, false);
            ArrayList commentStarViews = new ArrayList<>(5);
            TextView commenterNameTv = (TextView) itemView.findViewById(R.id.commenterName);
            TextView commenterDateTv = (TextView) itemView.findViewById(R.id.commentDate);
            TextView commentTv = (TextView) itemView.findViewById(R.id.commentTv);
            ImageView star1 = (ImageView) itemView.findViewById(R.id.star1);
            ImageView star2 = (ImageView) itemView.findViewById(R.id.star2);
            ImageView star3 = (ImageView) itemView.findViewById(R.id.star3);
            ImageView star4 = (ImageView) itemView.findViewById(R.id.star4);
            ImageView star5 = (ImageView) itemView.findViewById(R.id.star5);
            commentStarViews.add(star1);
            commentStarViews.add(star2);
            commentStarViews.add(star3);
            commentStarViews.add(star4);
            commentStarViews.add(star5);
            Review review = ratings.get(position);
            if (review != null) {
                commentTv.setText(review.getReview());
                commenterNameTv.setText(review.getFromUserName());
                try {
                    commenterDateTv.setText(TimeUtils.convertDateTimeFormat(TimeUtils.dfISOMS, TimeUtils.dtfOut, review.getCreateDate()));
                } catch (IllegalArgumentException e) {
                    commenterDateTv.setText(review.getCreateDate());
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
                setStarVisibility(review.getRating(), commentStarViews);
            }
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}

package com.armut.armuthv;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.armut.armuthv.busevents.ApiErrorEvent;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.fragments.DialogNoConnection;
import com.armut.armuthv.objects.Address;
import com.armut.armuthv.objects.ParitusVerifiedList;
import com.armut.armuthv.objects.ParitusVerifyObject;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.services.AuthRestApi;
import com.armut.armuthv.services.AuthenticationService;
import com.armut.armuthv.services.FusedLocationer;
import com.armut.armuthv.services.JobRestApi;
import com.armut.armuthv.services.JobService;
import com.armut.armuthv.services.LocationPopupService;
import com.armut.armuthv.services.LocationRestApi;
import com.armut.armuthv.services.LocationService;
import com.armut.armuthv.services.MessageService;
import com.armut.armuthv.services.MessagesRestApi;
import com.armut.armuthv.services.ParitusRestApi;
import com.armut.armuthv.services.ParitusService;
import com.armut.armuthv.services.UserRestApi;
import com.armut.armuthv.services.UserService;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.armut.armuthv.utils.DataSaver;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by oguzemreozcan on 12/05/16.
 */
public class ArmutHVApp extends Application {

    private static final String TAG = "ArmutHVApp";
    private static Bus mBus;
    private DataSaver dataSaver;
    private ParitusVerifiedList paritusVerifiedAddressList;
    //public static Locale localeTr = new Locale("tr");
    public boolean isWriteToSdCardPermissionGranted;
    public boolean isLocationPremissionGranted = true;
    public boolean isCallPermissionGranted = false;
    public boolean isLocationServiceAvailable = false;
    public boolean isLocationPermissonAsked;
    private User user;
    private JobService jobService;
    private MessageService messageService;
    private UserService userService;
    private AuthenticationService authService;
    private LocationService locationService;
    private FusedLocationer fusedLocationer;
    private ParitusService paritusService;
    private SparseIntArray notificationCountPerJob;
    //public final boolean DEBUG_MODE = true;

    public LocationPopupService popupService;

    @Override
    public void onCreate() {
        super.onCreate();
        Context context = getApplicationContext();
        try {
            ArmutUtils.createClientInfo();
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem on getting Device Info");
            ArmutUtils.clientInfo = "";
        }
        registerRestApi(false);
        //FB
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //NewRelic
        NewRelic.withApplicationToken(getString(R.string.nrToken)).start(this);

        //Mixpanel
        MixpanelAPI mixpanel = MixpanelAPI.getInstance(this, getString(R.string.mpId));
        mixpanel.track("Launched The App");
        // Twitter /change those key and secret for HV app
//        TwitterAuthConfig authConfig = new TwitterAuthConfig("5Ql1uMH43xXDxDZu6Y3RFZM1k", "FjKbJ6tFxm9SrXp4NOIUgZejVF0ZAqPtqMw0KiCxoarXKtktQL"); // key , secret
//        Fabric.with(this, new TwitterCore(authConfig), new TweetComposer());
        //Adjust
        String environment = BuildConfig.DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this, getString(R.string.adjustToken), environment);
        //config.setLogLevel(LogLevel.VERBOSE);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
        AdjustEvent event = new AdjustEvent("opdf9y"); //Launched App
        Adjust.trackEvent(event);

        Picasso picasso = new Picasso.Builder(context).build();
        Picasso.setSingletonInstance(picasso);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/raleway-medium.ttf")
                        .setFontAttrId(R.attr.fontPath)
//                        .addCustomViewWithSetTypeface(CustomViewWithTypefaceSupport.class)
//                        .addCustomStyle(TextField.class, R.attr.textFieldStyle)
                        .build()
        );
        if (!ArmutUtils.isMarshmallow()) {
            isLocationPremissionGranted = true;
            isCallPermissionGranted = true;
            isWriteToSdCardPermissionGranted = true;
            isLocationPermissonAsked = false;
        } else {
            isLocationPremissionGranted = getDataSaver().getBoolean(Constants.PERMISSION_KEY_REQUEST_LOCATION);
            isCallPermissionGranted = getDataSaver().getBoolean(Constants.PERMISSION_KEY_CALL);
            isWriteToSdCardPermissionGranted = getDataSaver().getBoolean(Constants.PERMISSION_KEY_WRITE_EXTERNAL_STORAGE);
            isLocationPermissonAsked = getDataSaver().getBoolean(Constants.PERMISSION_KEY_REQUEST_LOCATION_IS_ASKED);
        }

        popupService = new LocationPopupService(this);
    }

    public void addToNotificationGroup(int jobId) {
        if (notificationCountPerJob == null) {
            notificationCountPerJob = new SparseIntArray();
        }
        Integer count = notificationCountPerJob.get(jobId);
        count += 1;
        notificationCountPerJob.put(jobId, count);
    }

    public void clearAllNotificationGroups(){
        if(notificationCountPerJob != null)
            notificationCountPerJob.clear();
    }

    public int getNotificationGroupCount(int jobId){
        if(notificationCountPerJob != null){
            return notificationCountPerJob.get(jobId);
        }
        return 0;
    }

    public void clearNotificationGroup(int jobId){
        if(notificationCountPerJob != null){
            notificationCountPerJob.delete(jobId);
        }
    }

    public void registerRestApi(boolean reset) {
        //TODO base stage URL shhould change to live
        String url = getDataSaver().getString(Constants.BASE_URL_KEY, Constants.BASE_LIVE_URL); // Constants.BASE_STAGE_URL;
        String authUrl = getDataSaver().getString(Constants.BASE_AUTH_URL_KEY, Constants.AUTH_URL);
        Log.d("APP", "URL: " + url);
        //Log.d("APP", "AUTH URL: " + authUrl);
        Log.d("TOKEN", "Token: " + isTokenPresent(TAG));
        if (reset) {
            unregisterRestApi();
        }
//        String url = getDataSaver().getString(Constants.BASE_URL_KEY, Constants.BASE_STAGE_URL);
        Retrofit retrofit = createRetrofitObject(url);
        Retrofit authRetrofit = createRetrofitObject(authUrl);

        Retrofit paritusRetrofit = null;
        try {
            paritusRetrofit = createAdapter(getBaseContext(), Constants.BASE_PARITUS_URL);
        } catch (Exception e) {
            e.printStackTrace();
        }

        authService = new AuthenticationService(getBus(), authRetrofit.create(AuthRestApi.AuthenticationRestApi.class), authRetrofit.create(AuthRestApi.LiveAuthenticationRestApi.class));
        jobService = new JobService(this, retrofit.create(JobRestApi.JobOpportunitiesRestApi.class),
                retrofit.create(JobRestApi.JobQuotesRestApi.class),
                retrofit.create(JobRestApi.JobDealsRestApi.class),
                retrofit.create(JobRestApi.JobDetailsRestApi.class),
                retrofit.create(JobRestApi.PostQuoteRestApi.class),
                retrofit.create(JobRestApi.LeadPriceRestApi.class),
                retrofit.create(JobRestApi.JobOpportunityRejectApi.class),
                retrofit.create(JobRestApi.RejectReasonsApi.class),
                retrofit.create(JobRestApi.JobQuoteCommissionApi.class));
        messageService = new MessageService(this, retrofit.create(MessagesRestApi.JobMessagesRestApi.class), retrofit.create(MessagesRestApi.PostJobMessagesRestApi.class),
                retrofit.create(MessagesRestApi.PostDeviceTokenRestApi.class), retrofit.create(MessagesRestApi.DeleteDeviceTokenRestApi.class));

        userService = new UserService(this, retrofit.create(UserRestApi.GetUserRestApi.class), retrofit.create(UserRestApi.GetUserProfilesRestApi.class),
                retrofit.create(UserRestApi.GetUserCalendar.class), retrofit.create(UserRestApi.PatchUserCalendar.class), retrofit.create(UserRestApi.PostUserPhoto.class),
                retrofit.create(UserRestApi.PatchUser.class), retrofit.create(UserRestApi.PatchUserAddress.class), retrofit.create(UserRestApi.GetRatingsApi.class),
                retrofit.create(UserRestApi.GetAssignedPhoneNumber.class), retrofit.create(UserRestApi.PatchUserPassword.class), retrofit.create(UserRestApi.PostUserAppReview.class));

        locationService = new LocationService(this, retrofit.create(LocationRestApi.GetStatesRestApi.class), retrofit.create(LocationRestApi.GetCitiesRestApi.class),
                retrofit.create(LocationRestApi.GetDistrictsRestApi.class), retrofit.create(LocationRestApi.PostCoordinateRestApi.class), retrofit.create(LocationRestApi.PostCoordinatesRestApi.class));

        paritusService = new ParitusService(this, paritusRetrofit.create(ParitusRestApi.VerifyAddress.class));

        getBus().register(this);
        getBus().register(authService);
        getBus().register(jobService);
        getBus().register(messageService);
        getBus().register(userService);
        getBus().register(locationService);
        getBus().register(paritusService);
    }

    private void unregisterRestApi() {
        getBus().unregister(jobService);
        getBus().unregister(authService);
        getBus().unregister(messageService);
        getBus().unregister(userService);
        getBus().unregister(locationService);
        getBus().unregister(paritusService);
        getBus().unregister(this);
    }

    public User getUser() {
        return user;
    }

    public Address getUserAddress() {
        if (user == null) {
            return null;
        }
        if (user.getAddresses() == null) {
            return null;
        }
        ArrayList<Address> addressList = new ArrayList<>(user.getAddresses());
        return addressList.get(0);
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            String userId = user.getUserInfo().getUserId();
            NewRelic.setAttribute("UserName", userId);
            MixpanelAPI mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.mpId));
            mixpanelAPI.identify(userId);
            mixpanelAPI.getPeople().identify(userId);
            mixpanelAPI.getPeople().set("Email", userId);
            mixpanelAPI.getPeople().set("Platform", "Android");
            mixpanelAPI.getPeople().set("Account Type", "HV");
        }
    }

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    public Bus getBus() {
        if (mBus == null) {
            mBus = new Bus(ThreadEnforcer.ANY);
        }
        return mBus;
    }

    public FusedLocationer getFusedLocationer() {
        if (fusedLocationer == null) {
            fusedLocationer = new FusedLocationer();
        }
        return fusedLocationer;
    }

    public DataSaver getDataSaver() {
        if (dataSaver == null) {
            dataSaver = new DataSaver(getApplicationContext(), "ArmutHV", false);
        }
        return dataSaver;
    }

    public String isTokenPresent(String TAG) {
        String token = getDataSaver().getString(Constants.ACCESS_TOKEN_KEY);
        if (token.equals("")) {
            Log.d(TAG, "PROBLEM !!!! Token NULL");
            return null;
        }
        return token;
    }

    public String getDeviceToken() {
        String deviceToken = getDataSaver().getString("DEVICE_TOKEN", "");
        return deviceToken == null ? "" : deviceToken;
    }

    public String getUserId() {
        String userId;
        try {
            userId = getUser().getUserInfo().getUserId();
        } catch (Exception e) {
            e.printStackTrace();
            userId = getDataSaver().getString(Constants.USERNAME);
        }
        return userId;
    }

    public void logout() {
        String token = getDeviceToken();
        if (!token.isEmpty())
            getBus().post(new MiscEvents.DeleteDeviceTokenRequest(token));
        getDataSaver().putString(Constants.ACCESS_TOKEN_KEY, "");
        getDataSaver().putString(Constants.USERNAME, "");
        getDataSaver().putInt(Constants.BUSINESS_MODEL_ID, Constants.BUSINESS_MODEL_REQUEST);
        getDataSaver().putString(Constants.DEVICE_TOKEN, "");
        saveParitusVerifiedList(true);
        getDataSaver().save();
        MainActivity.defaultBusinessModelId = 0;
        setUser(null);
        getDataSaver().save();
    }

    private Retrofit createRetrofitObject(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
    }

    public static Retrofit createAdapter(Context context, String url) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, KeyManagementException {
        // loading CAs from an InputStream
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        InputStream cert = context.getResources().openRawResource(R.raw.gtux);
        Certificate ca;
        try {
            ca = cf.generateCertificate(cert);
        } finally {
            cert.close();
        }

        // creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // creating a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        // creating a RestAdapter using the custom client
//        return new RestAdapter.Builder()
//                .setEndpoint(UrlRepository.API_BASE)
//                .setClient(new OkClient(okHttpClient))
//                .build();
        OkHttpClient client = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) tmf.getTrustManagers()[0]).build();
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private void saveParitusVerifiedList(boolean asNull) {
        Gson gson = new Gson();
        String listJson = paritusVerifiedAddressList != null ? gson.toJson(paritusVerifiedAddressList) : null;
        if (asNull) {
            listJson = null;
        }
        getDataSaver().putString(Constants.PARITUS_VERIFIED_ADDRESS_LIST, listJson);
        getDataSaver().save();
        Log.d(TAG, "SAVED PARITUS LIST: " + listJson);
    }

    public void addToParitusVerifiedList(ParitusVerifyObject verifyObject) {
        if (verifyObject == null) {
            Log.d(TAG, "PARITUS OBJECT IS NULL");
            return;
        }
        if (paritusVerifiedAddressList == null) {
            String verifiedAddressListJson = getParitusListJson();
            if (verifiedAddressListJson == null) {
                paritusVerifiedAddressList = new ParitusVerifiedList();
            } else {
                Gson gson = new Gson();
                paritusVerifiedAddressList = gson.fromJson(verifiedAddressListJson, ParitusVerifiedList.class);
            }
        }
        addParitusObject(verifyObject);
    }

    private void addParitusObject(ParitusVerifyObject verifyObject) {
        Log.d(TAG, "PARITUS ADD OBJECT: " + verifyObject.jobId + " lat : " + verifyObject.getResult().getLatitude() + " - lon : " + verifyObject.getResult().getLongitude());
        if (isParitusObjectContained(verifyObject.jobId) == null) {
            paritusVerifiedAddressList.getVerifiedAddressList().add(verifyObject);
            saveParitusVerifiedList(false);
        } else {
            Log.d(TAG, "Object already in the list");
        }
    }

    private String getParitusListJson() {
        return getDataSaver().getString(Constants.PARITUS_VERIFIED_ADDRESS_LIST, null);
    }

    public ParitusVerifyObject isParitusObjectContained(long jobId) {
        Log.d(TAG, "PARITUS IS OBJECT PRESENT: " + jobId);
        try {

            if (paritusVerifiedAddressList == null) {
                Log.d(TAG, "PARITUS LOCAL ARRAY IS EMPTY: ");
                String verifiedAddressListJson = getParitusListJson();
                if (verifiedAddressListJson == null) {
                    paritusVerifiedAddressList = new ParitusVerifiedList();
                    Log.d(TAG, "PARITUS JSON DATA IS EMPTY ");
                    return null;
                } else {
                    Gson gson = new Gson();
                    paritusVerifiedAddressList = gson.fromJson(verifiedAddressListJson, ParitusVerifiedList.class);
                    if (paritusVerifiedAddressList == null) {
                        paritusVerifiedAddressList = new ParitusVerifiedList();
                        return null;
                    } else {
                        for (ParitusVerifyObject object : paritusVerifiedAddressList.getVerifiedAddressList()) {
                            if (object.jobId == jobId) {
                                Log.d(TAG, "PARITUS IS OBJECT PRESENT true in json array: " + jobId);
                                return object;
                            }
                        }
                        return null;
                    }
                }
            } else {
                ArrayList<ParitusVerifyObject> list = paritusVerifiedAddressList.getVerifiedAddressList();
                if (list == null) {
                    return null;
                }
                if (list.size() == 0) {
                    return null;
                }
                for (ParitusVerifyObject object : list) {
                    if (object.jobId == jobId) {
                        Log.d(TAG, "PARITUS IS OBJECT PRESENT true in local array: " + jobId);
                        return object;
                    }
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Subscribe
    public void onApiError(ApiErrorEvent event) {
        if (event.getErrorMessage() == null) {
            if (ArmutUtils.isNetworkAvailable(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "Bir sorun var, lütfen tekrar deneyin.", Toast.LENGTH_LONG).show(); // + event.getStatusCode()
            } else {
                Toast.makeText(getApplicationContext(), "İnternetinizi kontrol edin ve tekrar deneyin.", Toast.LENGTH_LONG).show();
            }
        } else {
            if (event.getStatusCode() == 401) {
                Toast.makeText(getApplicationContext(), "Lütfen giriş yapın.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), event.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        }
        Log.d(TAG, "TOKEN: " + event.getStatusCode() + " - " + isTokenPresent(TAG));
    }

    public void openOKDialog(final AppCompatActivity activity, final Fragment fragment) {
        if (activity == null) {
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogNoConnection dialog = new DialogNoConnection();
                Bundle args = new Bundle();
                //if(dialogType.equals("no_connection")){
                args.putString("title", getString(R.string.no_connection_title));
                args.putString("message", getString(R.string.no_connection_with_button));
                // }
                String tag;
                dialog.setArguments(args);
                if (fragment != null) {
                    //dialog.setTargetFragment(fragment, Constants.NO_CONNECTION);
                    tag = fragment.getTag();
                    try {
                        dialog.show(fragment.getChildFragmentManager(), tag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
//                else {
//                    if (activity instanceof MainActivity) {
//                        dialog.setTargetActivity((MainActivity) activity, Constants.NO_CONNECTION);
//                    }
//                    try{
//                        dialog.show(activity.getSupportFragmentManager(), tag);
//                    }catch(Exception e){
//                        e.printStackTrace();
//                    }
//                }
            }
        }, 50);
    }

    public boolean requestCardWritePermission(Activity activity, String message) {
        if (!ArmutUtils.isMarshmallow()) {
            isWriteToSdCardPermissionGranted = true;
            return true;
        }
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            isWriteToSdCardPermissionGranted = false;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessage(activity, message, Toast.LENGTH_LONG);
            }
//            else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE);
//            }
        } else {
            isWriteToSdCardPermissionGranted = true;
        }
        getDataSaver().putBoolean(Constants.PERMISSION_KEY_WRITE_EXTERNAL_STORAGE, isWriteToSdCardPermissionGranted);
        getDataSaver().save();
        return isWriteToSdCardPermissionGranted;
    }

    public boolean requestCardWritePermission(Activity activity, Fragment fragment, String message) {
        if (!ArmutUtils.isMarshmallow()) {
            isWriteToSdCardPermissionGranted = true;
            return true;
        }
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            isWriteToSdCardPermissionGranted = false;
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessage(activity, message, Toast.LENGTH_LONG);
            }
//            else {
            // No explanation needed, we can request the permission.
            fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE);
//            }
        } else {
            isWriteToSdCardPermissionGranted = true;
        }
        getDataSaver().putBoolean(Constants.PERMISSION_KEY_WRITE_EXTERNAL_STORAGE, isWriteToSdCardPermissionGranted);
        getDataSaver().save();
        return isWriteToSdCardPermissionGranted;
    }

    public boolean requestLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPremissionGranted = true;
            Log.d(TAG, "LOCATION PERMISSION GRANTED");
        } else {
            Log.d(TAG, "LOCATION PERMISSION istiyoruzzzzzzz");
            String message = "Lokasyonunu yollayabilmemiz için telefon ayarlarından buna izin vermen gerekli.";
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessage(activity, message, Toast.LENGTH_LONG);
            } else {
                showMessage(activity, "Lokasyonunu otomatik olarak alabilmemiz için telefon ayarlarından buna izin vermen gerekli.", Toast.LENGTH_LONG);
            }
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.PERMISSIONS_REQUEST_LOCATION);
            return false;

        }
        dataSaver.putBoolean(Constants.PERMISSION_KEY_REQUEST_LOCATION, isLocationPremissionGranted);
        dataSaver.save();
        return isLocationPremissionGranted;
    }

    public void showMessage(final Activity activity, final String message, final int length) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, length).show();
            }
        });
    }

    public void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        //Log.d(TAG, "HIDE KEYBOARD : ");
        if (v == null)
            return;
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static final class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}

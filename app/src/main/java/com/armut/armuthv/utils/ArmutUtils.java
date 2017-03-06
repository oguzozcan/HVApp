package com.armut.armuthv.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.armut.armuthv.BuildConfig;
import com.armut.armuthv.R;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 12/05/16.
 */
public class ArmutUtils {

    private static String deviceName = "";
    private static String deviceOs = "";
    public static String clientInfo = "";
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"); // ^\w+\@\w+\.\w+$

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static boolean checkPlayServices(Activity activity) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(activity, result,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;
    }

    public static int getActionBarSize(Activity activity) {
        final TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarSize = ((int) styledAttributes.getDimension(0, 0));
        styledAttributes.recycle();
        return actionBarSize;
    }

    public static void createClientInfo() throws JSONException {
        ArmutUtils.deviceName = ArmutUtils.getDeviceName();
        ArmutUtils.deviceOs = ArmutUtils.getAndroidVersion();
        clientInfo = getBasicJsonAsString(new BasicNameValuePair("client_os", "Android"), new BasicNameValuePair("client_os_version", deviceOs), new BasicNameValuePair("client", deviceName),
                new BasicNameValuePair("app_version_code", Integer.toString(BuildConfig.VERSION_CODE)),
                new BasicNameValuePair("app_version", BuildConfig.VERSION_NAME), new BasicNameValuePair("application_type", Integer.toString(1)));
        //0 stands for ha app, 1 for hv
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getErrorMessage(Response response) {
        String message = response.message();
        try {
            InputStream stream = response.errorBody().byteStream();//bytes()
            byte[] buffer = new byte[4096]; //8192
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = stream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            String json = new String(output.toByteArray());
            Log.i("ERROR!", " Error RESPONSE json: " + json + "- message: " + message);
            //Log.d("", "ON RESPONSE postquote: " + ArmutUtils.bodyToString(call.request().body()));
            message = new JSONObject(json).getString("message");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String getAuthErrorMessage(Response response) {
        String message = response.message();
        try {
            InputStream stream = response.errorBody().byteStream();//bytes()
            byte[] buffer = new byte[4096]; //8192
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            while ((bytesRead = stream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            String json = new String(output.toByteArray());
            Log.i("ERROR!", " Error RESPONSE json: " + json + "- message: " + message);
            //Log.d("", "ON RESPONSE postquote: " + ArmutUtils.bodyToString(call.request().body()));
            message = new JSONObject(json).getString("error_description");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static boolean isMarshmallow() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public static String getBasicJsonAsString(BasicNameValuePair... params) throws JSONException {
        JSONObject object = new JSONObject();
        for (BasicNameValuePair param : params) {
            object.put(param.getName(), param.getValue());
        }
        Log.d("JSON", "JSON: " + object.toString());
        return object.toString();
    }

    public static JSONObject getBasicJson(BasicNameValuePair... params) {
        JSONObject object = new JSONObject();
        try {
            for (BasicNameValuePair param : params) {
                object.put(param.getName(), param.getValue());
            }
            Log.d("JSON", "JSON: " + object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return object;
    }

    public static void setImage(ImageView imageView, byte[] imageArray, int defaultSampleSize) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = defaultSampleSize;
        boolean imageSet = false;
        while (!imageSet) {
            try {
                imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length, bitmapOptions));
                imageSet = true;
            } catch (OutOfMemoryError e) {
                bitmapOptions.inSampleSize *= 2;
            }
        }
    }

    public static int[] getScreenSize(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;
        int[] sizes = {width, height};
        Log.d("SCREEN_SIZE", "WIDTH: " + width);
        Log.d("SCREEN_SIZE", "HEIGHT: " + height);
        Log.d("SCREEN_SIZE", "Density: " + activity.getResources().getDisplayMetrics().density);
        return sizes;
    }

    public static float dpFromPx(int px, Context context) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(int dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    /**
     * Returns the consumer friendly device name
     */
    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase += c;
        }
        return phrase;
    }

    private static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return sdkVersion + " (" + release + ")";
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static void openHVAppPageFromPlayStore(Activity activity) {
        //final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.armut.armuthv")));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.armut.armuthv")));
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        if (context == null) {
            return false;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    public static void openApplicationSettings(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static void sendMail(String email, String recipient, String subject, String imagePath, Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:")); // .concat(recipient)
        emailIntent.setType("message/rfc822");
        //emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
        emailIntent.putExtra(Intent.EXTRA_TEXT, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (imagePath != null) {
            Uri pngUri = Uri.parse("file://" + imagePath);//Uri.parse(imagePath);
            emailIntent.putExtra(android.content.Intent.EXTRA_STREAM, pngUri);
            //emailIntent.setType("application/image");
        }
        //i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(pic));
        // i.setType("image/png");
        try {
            activity.startActivity(Intent.createChooser(emailIntent, activity.getString(R.string.send_mail)));
            // finish();
            Log.i("Email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, activity.getString(R.string.no_mail_app),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void callArmut(Activity activity) {
        if(isTelephonyEnabled(activity)){
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Constants.CALL_ARMUT));
            try {
                activity.startActivity(intent);
            } catch (android.content.ActivityNotFoundException ex) {
                ex.printStackTrace();
                Toast.makeText(activity, activity.getString(R.string.no_call_app),
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity, activity.getString(R.string.no_call_app),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean isTelephonyEnabled(Activity activity){
        TelephonyManager telephonyManager = (TelephonyManager)activity.getSystemService(Activity.TELEPHONY_SERVICE);
        return telephonyManager != null && telephonyManager.getSimState()==TelephonyManager.SIM_STATE_READY;
    }

    public static void callNumber(Activity activity, String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
        try {
            activity.startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, activity.getString(R.string.no_call_app),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean appInstalledOrNot(String uri, Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static String getMoneyPattern(double money) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return formatter.format(money).replace(',', '.').concat(" TL ");
        //String s = String.format(TimeUtil.localeTr,"%,f", money).replace(',','.').concat(" TL");
    }

    public static String getMoneyPatternWithoutCurrency(double money) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        return formatter.format(money).replace(',', '.');
        //String s = String.format(TimeUtil.localeTr,"%,f", money).replace(',','.').concat(" TL");
    }

    public static String getCallPreferenceText(Activity activity, int callPreference) {
        switch (callPreference) {
            case Constants.CALL_PREF_NO_CALL:
                return activity.getString(R.string.call_preference_no_call);
            case Constants.CALL_PREF_CAN_CALL:
                return activity.getString(R.string.call_preference_can_call);
            case Constants.CALL_PREF_SECRET_NUMBER:
                return activity.getString(R.string.call_preference_secret_number);
            default:
                return activity.getString(R.string.call_preference_can_call);
        }
    }

    public static BasicNameValuePair getJobStartDatePair(Activity activity, String formattedStartDate, int selectedJobStartDateTypeId){
        BasicNameValuePair timePair;
        switch (selectedJobStartDateTypeId){
            case Constants.JOB_START_DATE_TYPE_SPECIFIC_TIME:
                timePair = new BasicNameValuePair("Time", activity.getString(R.string.work_start_specific_date)); //formattedStartDate
                break;
            case Constants.JOB_START_DATE_TYPE_LOOKING_PRICE:
                timePair = new BasicNameValuePair("Time", activity.getString(R.string.work_start_not_specified));
                break;
            case Constants.JOB_START_DATE_TYPE_SIX_MONTHS:
                timePair = new BasicNameValuePair("Time", activity.getString(R.string.work_start_in_six_months));
                break;
            case Constants.JOB_START_DATE_TYPE_TWO_MONTHS:
                timePair = new BasicNameValuePair("Time", activity.getString(R.string.work_start_in_two_months));
                break;
            default:
                timePair = new BasicNameValuePair("Time", formattedStartDate);
                break;
        }
        return timePair;
    }

//    public static void getKeyHash(Activity activity){
//        try {
//            PackageInfo info = activity.getPackageManager().getPackageInfo(
//                    "com.armut.armuthv",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash", "KeyHash:"+ Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT));
//                Toast.makeText(activity.getApplicationContext(), Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//    }
}

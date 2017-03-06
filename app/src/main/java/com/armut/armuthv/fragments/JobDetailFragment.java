package com.armut.armuthv.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.objects.BasicNameValuePair;
import com.armut.armuthv.objects.ControlJobValue;
import com.armut.armuthv.objects.JobDetails;
import com.armut.armuthv.objects.ParitusVerifyObject;
import com.armut.armuthv.objects.QAData;
import com.armut.armuthv.objects.QuestionAnswerView;
import com.armut.armuthv.utils.ArmutUtils;
import com.armut.armuthv.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 27/06/16.
 */
public class JobDetailFragment extends BasicFragment implements OnMapReadyCallback {

    private int height = 0;
    private LoadingProcess loadingCallback;
    private GoogleMap gMap;
    private MapView mapView;
    private JobDetailsReceivedCallback jobDetailsCallback;
    private boolean dataLoaded = false;
    private long jobId;
    private String jobAddressDetail;

    public JobDetailFragment() {
        // Required empty public constructor
    }

    public void initializeMapView() {
        try {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTag() {
        TAG = "JOB_DETAIL_FRAGMENT";
    }

//    public static JobDetailFragment newInstance(int index, int jobId, int color) {
//        JobDetailFragment fragment = new JobDetailFragment();
//        Bundle args = new Bundle();
//        args.putInt("INDEX", index);
//        args.putInt("COLOR", color);
//        args.putInt("JOB_ID", jobId);
//        Log.d("JOB DETAILS FRAGMENT", "JOB ID: " + jobId);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static JobDetailFragment newInstance(long jobId, int sectionNumber, String jobAddressDetail, boolean jobHasAddressDetails) {
        JobDetailFragment fragment = new JobDetailFragment();
        Bundle args = new Bundle();
        args.putLong(JobEvents.PARAM_JOB_ID, jobId);
        args.putInt(JobEvents.PARAM_SECTION_NUMBER, sectionNumber);
        args.putString(JobEvents.PARAM_JOB_ADDRESS, jobAddressDetail);
        args.putBoolean(JobEvents.PARAM_JOB_HAS_ADDRESS_DETAILS, jobHasAddressDetails);
        Log.d("JOB DETAILS FRAGMENT", "JOB ID: " + jobId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            loadingCallback = (LoadingProcess) context;
            jobDetailsCallback = (JobDetailsReceivedCallback) context;
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        long jobId = getArguments().getLong(JobEvents.PARAM_JOB_ID);
        Log.d(TAG, "JOB_ID : " + jobId);
        if (!dataLoaded) {
            app.getBus().post(new JobEvents.JobDetailRequest(jobId));
            if (loadingCallback != null) {
                loadingCallback.loadingIsInProgress(true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.job_details_layout, container, false);
        mapView = (MapView) rootView.findViewById(R.id.mapImageView);
        Bundle bundle = getArguments();
        jobId = bundle.getLong(JobEvents.PARAM_JOB_ID);
        jobAddressDetail = bundle.getString(JobEvents.PARAM_JOB_ADDRESS, "");

        int sectionNumber = bundle.getInt(JobEvents.PARAM_SECTION_NUMBER);
        Log.d(TAG, "JOB DETAILS FRAGMENT: JOBID:" + jobId + " jobaddressDetail : " + jobAddressDetail);
        Button cancelJob = (Button) rootView.findViewById(R.id.cancelJob);
        if (sectionNumber == Constants.OPPORTUNITY_SECTION_INDEX) {
            cancelJob.setVisibility(View.VISIBLE);
            cancelJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "CLICK ON Cancel Job LAYYOUT;");
                    try {
                        DialogCancelJob cancelJob = DialogCancelJob.newInstance("");
                        cancelJob.show(getActivity().getFragmentManager(), "cancelJob");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            cancelJob.setVisibility(View.INVISIBLE);
        }
        initializeMapView();
        return rootView;
    }

    private void setMapView(final double lat, final double lon, int sectionNumber) {
        if ((sectionNumber - 1) == Constants.DEALS_SECTION) {
            Log.d(TAG, "LATITUDE: " + lat);
            LatLng latlng = new LatLng(lat, lon);
            mapView.setTag(latlng);
            mapView.setVisibility(View.VISIBLE);
            if(getView() != null){
                LinearLayout directionsLayout = (LinearLayout) getView().findViewById(R.id.directionLayout);
                directionsLayout.setVisibility(View.VISIBLE);
                Button carDirectionButton = (Button) directionsLayout.findViewById(R.id.getDirectionForCar);
                Button busDirectionButton = (Button) directionsLayout.findViewById(R.id.getDirectionForBusses);
                carDirectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            final MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getActivity().getString(R.string.mpId));
                            mixpanel.track("Tapped Get Directions", ArmutUtils.getBasicJson(new BasicNameValuePair("Navigation Type", "car")));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        final String latlon = "http://maps.google.com/maps?daddr=" + lat + "," + lon + "&mode=driving&dirflg=d";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(latlon));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });
                busDirectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            final MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getActivity().getString(R.string.mpId));
                            mixpanel.track("Tapped Get Directions", ArmutUtils.getBasicJson(new BasicNameValuePair("Navigation Type", "public")));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        final String latlon = "http://maps.google.com/maps?daddr=" + lat + "," + lon + "&mode=transit";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(latlon));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });
            }
            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
            // when the callback is received.
            if (gMap != null) {
                // The map is already ready to be used
                setMapLocation(gMap, latlng);
            }
        }else{
            mapView.setVisibility(View.GONE);
            if(getView() != null){
                LinearLayout directionsLayout = (LinearLayout) getView().findViewById(R.id.directionLayout);
                directionsLayout.setVisibility(View.GONE);
            }
        }
    }

    @Subscribe
    public void getJobDetails(JobEvents.JobDetailResponse response) {
        JobDetails job = response.getResponse().body();//getArguments().getParcelable("JOB");
        if (response.getResponse().code() == 401) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getActivity() != null){
                        Toast.makeText(getActivity(), "İş mevcut değil, iş iptal edilmiş ya da çoktan randevu ayarlanmış olabilir.", Toast.LENGTH_LONG).show();
                        getActivity().setResult(Constants.UPDATE_JOBS_PAGE);
                        getActivity().finish();
                    }
                }
            }, 350);
        }
        View fragmentView = getView();
        int sectionNumber = getArguments().getInt(JobEvents.PARAM_SECTION_NUMBER);
        if (fragmentView == null || job == null) {
            Log.d(TAG, "FRAGMENT VIEWW IS NULL or jobdetails null ");
            return;
        }
        if (job.getLatitude() != null) {
            setMapView(job.getLatitude(), job.getLongitude(), sectionNumber);
        } else {
            if(getView() != null){
                LinearLayout directionsLayout = (LinearLayout) getView().findViewById(R.id.directionLayout);
                directionsLayout.setVisibility(View.GONE);
            }
            mapView.setVisibility(View.GONE);
        }

        final LinearLayout layout = (LinearLayout) fragmentView.findViewById(R.id.linearLayout);
        layout.setFocusableInTouchMode(true);
        layout.setFocusable(true);
        layout.requestFocus();

        ArrayList<ControlJobValue> controlJobValues = new ArrayList<>(job.getControlValues());
        ArrayList<QAData> pairs = initQuestionAnswers(controlJobValues);

        QAData jobNoPair = new QAData(0, 0, Long.toString(jobId), new BasicNameValuePair("İş Numarası:", Long.toString(jobId)));
        pairs.add(jobNoPair);

        int textSize = (int) (getResources().getDimension(R.dimen.font_size_checkbox) / getResources().getDisplayMetrics().density);
        int index = 0;
        for (QAData pair : pairs) {
            index++;
            QuestionAnswerView qa = new QuestionAnswerView(getActivity(), null, layout, pair.getPage(), pair.getControlId(), false, index == pairs.size());
            BasicNameValuePair bp = pair.getPair();
            qa.setAnswerText(bp.getValue());
            //TODO check if stress address not already is in the field
            if (pair.getControlId() == 9000002 && !jobAddressDetail.isEmpty()) {
                //TODO
                Log.d(TAG, "PARITUS: ADDRESS: " + jobAddressDetail);
                ParitusVerifyObject verifyObject = app.isParitusObjectContained(jobId);
                boolean jobHasAddressDetails = false;
                if (getArguments() != null)
                    jobHasAddressDetails = getArguments().getBoolean(JobEvents.PARAM_JOB_HAS_ADDRESS_DETAILS, false);

                if (verifyObject == null && jobHasAddressDetails) {
                    app.getBus().post(new MiscEvents.ParitusVerifyAddressRequest(jobAddressDetail));
                } else  if(verifyObject != null){
                    if (verifyObject.getResult() != null) {
                        setMapView(verifyObject.getResult().getLatitude(), verifyObject.getResult().getLongitude(), sectionNumber);
                    }
                }
                qa.setAnswerText(jobAddressDetail); // bp.getValue() + " / "
            }
            qa.setQuestionText(bp.getName());
            Log.d(TAG, "QA:" + bp.getName() + "-answer:" + bp.getValue() + "." + " - id: " + pair.getControlId());
            qa.setAnswerTextColor(ContextCompat.getColor(getActivity(), R.color.blackish));
            qa.setQuestionTextColor(ContextCompat.getColor(getActivity(), R.color.warm_grey2));
            qa.setBackground(ContextCompat.getColor(getActivity(), R.color.transparent));
            qa.setAnswerTextSize(textSize);
            qa.setQuestionTextSize(textSize);
        }
        //mainLayout.setBackgroundColor(color);
        layout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.whitish_background));
        //setContactLayout(inflater, layout);
        final ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                    //viewWidth = layout.getWidth();
                    height = layout.getHeight();
                    Log.d(TAG, "LAYOUT HEIGHT : " + height);
                }
            });
        }
        if (jobDetailsCallback != null) {
            jobDetailsCallback.onJobDetailsReceived(job);
        }

        if (loadingCallback != null) {
            loadingCallback.loadingIsInProgress(false);
        }
        dataLoaded = true;
    }

    @Subscribe
    public void getVerifiedAddressFromParitus(MiscEvents.ParitusVerifyAddressResponse response) {
        if (response != null) {
            ParitusVerifyObject.Result result = response.getResponse().body().getResult();
            ParitusVerifyObject object = new ParitusVerifyObject();
            if (result != null) {
                object.setResult(result);
                if (jobId != 0) {
                    object.setJobId(jobId);
                }
                Log.d(TAG, "PARITUS VERIFIED ADDRESS RESPONSE : " + result.getVerificationScore() + " - result lat: " + result.getLatitude());
                if (result.getVerificationScore() > 700) {
                    int sectionNumber = getArguments().getInt(JobEvents.PARAM_SECTION_NUMBER);
                    setMapView(result.getLatitude(), result.getLongitude(), sectionNumber);
                    app.addToParitusVerifiedList(object);
                }
            } else {
                Log.d(TAG, "PARITUS VERIFIED ADDRESS RESULT IS NULL");
            }
        }
    }

    private void setJobStartTypeAndDate(String text, String[] jobStartTypeAndDate) {
        Log.d(TAG, "JOB START DATE TYPE: " + text);
        if (jobStartTypeAndDate[0] == null) {
            jobStartTypeAndDate[0] = text;
        } else {
            jobStartTypeAndDate[1] = text;
            if (jobDetailsCallback != null) {
                jobDetailsCallback.onJobStartDateTypeCalculated(jobStartTypeAndDate);
            }
        }
    }

    private ArrayList<QAData> initQuestionAnswers(ArrayList<ControlJobValue> values) {
        ArrayList<QAData> pairs = new ArrayList<>();
        String[] jobStartTypeAndDate = new String[2];
        String previousKey = "";
        for (ControlJobValue value : values) {
            String keyText = value.getLabel();
            String valueText = value.getValue();
            //TODO
            int controlId = value.getControlId();
            if (controlId == 9000004) {
                setJobStartTypeAndDate(valueText, jobStartTypeAndDate);
            }
            Log.d(TAG, "CONTROL JOB DETAIL  Control Value - Key: " + keyText + " - value: " + valueText);
            keyText = keyText != null ? keyText : "-";
            if (previousKey.equals(keyText)) {
                if (valueText == null || pairs.size() == 0) {
                    continue;
                }
                QAData pair = pairs.get(pairs.size() - 1);
                pairs.remove(pairs.size() - 1);
                String prevValue = pair.getPair().getValue();
                String tempValue = !ArmutUtils.isNumeric(prevValue) ? prevValue.concat(" / ").concat(valueText) : valueText;
                pair = new QAData(0, value.getControlId(), valueText, new BasicNameValuePair(keyText, tempValue));
                pairs.add(pair);
            } else {
                QAData pair = new QAData(0, value.getControlId(), valueText, new BasicNameValuePair(keyText, valueText != null ? valueText : "-"));
                pairs.add(pair);
            }
            previousKey = keyText;
        }
        return pairs;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (getActivity() != null) {
            MapsInitializer.initialize(getActivity());
            gMap = googleMap;
            LatLng data = (LatLng) mapView.getTag();
            if (data != null) {
                setMapLocation(gMap, data);
            }
        }
    }

    private void setMapLocation(GoogleMap map, LatLng data) {
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data, 12f));
        map.addMarker(new MarkerOptions().position(data));
        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                try{
                    final MixpanelAPI mixpanel = MixpanelAPI.getInstance(getActivity(), getActivity().getString(R.string.mpId));
                    mixpanel.track("Tapped MapView");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public interface JobDetailsReceivedCallback {
        void onJobDetailsReceived(JobDetails details);

        void onJobStartDateTypeCalculated(String[] typeDateArray);
    }
}


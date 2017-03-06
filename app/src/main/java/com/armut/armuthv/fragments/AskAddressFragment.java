package com.armut.armuthv.fragments;

/**
 * Created by oguzemreozcan on 12/08/16.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.armut.armuthv.R;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.components.AdditionalInfoEditText;
import com.armut.armuthv.components.AddressSelector;
import com.armut.armuthv.objects.Address;
import com.armut.armuthv.objects.City;
import com.armut.armuthv.objects.District;
import com.armut.armuthv.objects.State;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

public class AskAddressFragment extends BasicFragment { // implements AddNewAddressTask.NewAddressCalback

    private int editAddress = 0;
    //private boolean answersReady;
    private AddressSelector addressSelector;
    private AdditionalInfoEditText longEditText;
    private BasicFragment.LoadingProcess loadingCallback;
//    private String newAddressJson;
   // public static boolean addressAsked = false;

    public AskAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void setTag() {
        TAG = "AskAddressFragment";
    }

//    @Override
    public boolean answersGivenReadyToPassNewPage() {
        return addressSelector.isAnswered();
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Log.d(TAG, "ASK ADDRESS FRAGMENT ON ATTACH");
        try {
            loadingCallback = (BasicFragment.LoadingProcess) activity;
            if (loadingCallback != null) {
                loadingCallback.loadingIsInProgress(false);
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.d(TAG, "ASK ADDRESS FRAGMENT ON DETACH");
        app.hideKeyboard(getActivity());
        super.onDetach();
        loadingCallback = null;

//        if (longEditText != null) {
//            longEditText.setClickable(true);
//            longEditText.setFocusable(true);
//            longEditText.requestFocus();
//        }
//        if (getActivity() != null) {
//            QuestionsActivity.hideKeyboardFrom(getActivity().getApplicationContext(), longEditText);
//            QuestionsActivity.hideKeyboard(getActivity());
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ask_address, container, false);
//        int serviceId = getArguments().getInt("SERVICE_ID");
        int backgroundColor = getArguments().getInt("BACKGROUND_COLOR");
        editAddress = getArguments().getInt("EDIT_ADDRESS");
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.linearLayout);
        layout.setFocusableInTouchMode(true);
        layout.setFocusable(true);
        layout.requestFocus();
        layout.setBackgroundColor(backgroundColor);
        //hideKeyboard();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.requestFocus();
                //QuestionsActivity.hideKeyboard(getActivity());
            }
        });

        // ArrayList<City> cities = QuestionProvider.getInstance().getClosestCities();
//        ServiceQuestion question = new ServiceQuestion();
//        question.setQuestion(editAddress == 1 ? " " : getString(R.string.service_address));
//        question.setControlTypeId(Constants.CONTROL_TYPE__WHERE);
        //View divider = rootView.findViewById(R.id.divider);
        //question.setRequired(true);

        addressSelector = new AddressSelector(getActivity(), loadingCallback, stateArrayList,cityArrayList, null, false, editAddress == 1);
        layout.addView(addressSelector);
        longEditText = new AdditionalInfoEditText(getActivity(),app, getActivity().getString(R.string.address_detail), "",
                false, true, false, false); // editAddress == 1
        app.getBus().post(new MiscEvents.GetStatesRequest());
        loadingCallback.loadingIsInProgress(true);
        return rootView;
    }

    public Address prepareNewAddressJson() {
        Address address = new Address();
        String[] ids = addressSelector.getAnswerToSend().split("\\" + Constants.SEPERATOR);
        Log.d(TAG, "GET ANSWER TO SEND: " + addressSelector.getAnswerToSend());
        String addressDetail = longEditText.getSelectedValue();
        address.setStateId(Integer.parseInt(ids[0]));
        address.setCityId(Integer.parseInt(ids[1]));
        address.setDistrictId(Integer.parseInt(ids[2]));
        address.setAddress(addressDetail);
//        // GET ANSWER TO SEND: 34*444*5973
//        Gson gson = new GsonBuilder().create();
//        Type type = new TypeToken<Address>() {
//        }.getType();
//        newAddressJson = gson.toJson(address, type);
//        Log.d(TAG, "JSON : " + newAddressJson);
        return address;
    }

    public void postNewAddressJson() {
        //TODO
        Log.d(TAG, "POST REQUEST TO SERVER");
        String token = app.isTokenPresent(TAG);
        if (token != null) {
            app.getBus().post(new MiscEvents.PatchAddressRequest(prepareNewAddressJson()));
        }
    }

//    public void addChildren(View view, ViewGroup parentLayout) {
//        if (view instanceof QuestionView) {
//            parentLayout.addView(view);
//        } else {
//            throw new IllegalStateException("View should implement QuestionView Interface");
//        }
//    }

    @Subscribe
    public void onAddressUpdated(MiscEvents.PatchAddressResponse response) {
        //creditCardsCallback.newCreditCardAdded(succesful);
        //Address address = response.getResponse().body();
        loadingCallback.loadingIsInProgress(false);
        if (response != null) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            Log.d(TAG, "NEW ADDRESS ADDED RESULT OK, CLOSE ACTIVITY AND SEND MESSAGE");

        } else {
            showMessage(getString(R.string.problem_on_updating_address), Toast.LENGTH_SHORT);
        }
    }

    @Subscribe
    public void getStatesList(MiscEvents.GetStatesResponse response){
        Log.d(TAG, "STATE LIST RECEIVED : ");
        if(response != null){
            stateArrayList = response.getResponse().body();
            Log.d(TAG, "STATE LIST RECEIVED : " + stateArrayList.size());
            addressSelector.addAdditionalInfoText(longEditText);
            if (editAddress == 1) {
                //addressSelector.addChildren(longEditText);
                if (app != null) {
                    Address address = app.getUserAddress();
                    if (address != null) {
                        longEditText.setText(address.getAddress());
                        Log.d(TAG, "SET DEFINED ADDRESS: " + address.getStateId());
                        addressSelector.initSpinnersForPredefinedValues(address.getStateId(), Constants.STATE_FIELD, stateArrayList);
                       // app.getBus().post(new MiscEvents.GetCitiesRequest(address.getStateId()));
                    }
                }
            }else{
                addressSelector.initSpinnersForPredefinedValues(34, Constants.STATE_FIELD, stateArrayList);
                loadingCallback.loadingIsInProgress(false);
//                app.getBus().post(new MiscEvents.GetCitiesRequest(34));
            }
        }
    }

    ArrayList<City> cityArrayList;
    ArrayList<State> stateArrayList;

    @Subscribe
    public void getCitiesList(MiscEvents.GetCitiesResponse response){
        Log.d(TAG, "City LIST RECEIVED for state: " + response.getStateId());
        if(response != null){
            cityArrayList = response.getResponse().body();
            String stateName = cityArrayList.get(0).getStateName();
            Log.d(TAG, "City LIST RECEIVED : " + cityArrayList.size() + " - State of the City: " + cityArrayList.get(0).getStateName() );
            Address address = app.getUserAddress();
            if (address != null) {
                if(stateName.equals(address.getStateName())){
                    addressSelector.initSpinnersForPredefinedValues(address.getCityId(), Constants.CITY_FIELD, cityArrayList);
                }else{
                    addressSelector.initSpinnersForPredefinedValues(0, Constants.CITY_FIELD, cityArrayList);
                    loadingCallback.loadingIsInProgress(false);
                    longEditText.setText("");
                }
            }else{
                addressSelector.initSpinnersForPredefinedValues(0, Constants.CITY_FIELD, cityArrayList);
            }
        }
    }

    @Subscribe
    public void getDistrictList(MiscEvents.GetDistrictsResponse response){
        Log.d(TAG, "District LIST RECEIVED for state: " + response.getCityId());
        if(response != null){
            ArrayList<District> districtArrayList = response.getResponse().body();
            String cityName = districtArrayList.get(0).getCityName();
            Log.d(TAG, "District LIST RECEIVED : " + districtArrayList.size());
            Address address = app.getUserAddress();
            if (address != null) {
                if(cityName.equals(address.getCityName())){
                    addressSelector.initSpinnersForPredefinedValues(address.getDistrictId(), Constants.DISTRICT_FIELD, districtArrayList);
                }else{
                    addressSelector.initSpinnersForPredefinedValues(0, Constants.DISTRICT_FIELD, districtArrayList);
                }
            }else{
                addressSelector.initSpinnersForPredefinedValues(0, Constants.DISTRICT_FIELD, districtArrayList);
            }
        }

        loadingCallback.loadingIsInProgress(false);
    }

}


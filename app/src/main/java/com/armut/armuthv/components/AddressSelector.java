package com.armut.armuthv.components;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.R;
import com.armut.armuthv.adapters.NothingSelectedSpinnerAdapter;
import com.armut.armuthv.busevents.MiscEvents;
import com.armut.armuthv.fragments.BasicFragment;
import com.armut.armuthv.objects.Address;
import com.armut.armuthv.objects.City;
import com.armut.armuthv.objects.District;
import com.armut.armuthv.objects.State;
import com.armut.armuthv.utils.Constants;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by oguzemreozcan on 21/09/16.
 */

public class AddressSelector extends LinearLayout implements QuestionView { // , CityTask.CitiesTaskCallback, DistrictTask.DistrictTaskCallback

    private final Activity context;
    private final ArmutHVApp app;
    private QuestionTextView textView;
    //    private final ServiceQuestion question;
    private final String TAG = "Address_Selector";
    private int order;
    private ArrayList<State> stateList;
    private ArrayList<City> cityList;
    private ArrayList<District> districtList;

    private boolean taskRunning = false;
    private DropdownSpinnerWithPopup stateDropdown;
    private DropdownSpinnerWithPopup cityDropdown;
    private DropdownSpinnerWithPopup districtDropdown;
    //private ChoiceGetLocationButton useLocationButton;
    private final BasicFragment.LoadingProcess loadingCallback;
    private final static int STATE_SELECTED = 0;
    private final static int CITY_SELECTED = 1;
    private final static int DISTRICT_SELECTED = 2;
    //Confirmation Screens has already has address info, user can change in between closest cities, districts. But spinners come with already selected values
    //Now it changed - user can not change address values in confirmation screens all spinners get blocked
//    private final boolean isConfirmationScreen;
    //Independent address selector has no get location button, user manually select address from spinners and add addtional info to the textbox
    private final boolean independentSelector;
    //This is to automatically reset spinner values - after user change state forex other spinners gets ready to be selected again
    private boolean spinnersReadyToBeSelected = true;
    //    //This is for user to be able to dismiss closest districts and cities and reselect from start again in confirmation screens
//    private boolean reselectionEnabled = false;
    //Predefined state - user address is present fill the edit adress screen
    private boolean predefinedState;
    //Predefined city - user address is present fill the edit adress screen
    private boolean predefinedCity;
    //Predefined district - user address is present fill the edit adress screen
    private boolean predefinedDistrict;

    private int addressRow = R.layout.address_list_view_white_colored;
    private int addressNothingStateRow = R.layout.nothing_selected_state_row_white_colored;
    private int addressNothingCityRow = R.layout.nothing_selected_city_row_white_colored;
    private int addressNothingDistrictRow = R.layout.nothing_selected_district_row_white_colored;
    private AdditionalInfoEditText detailEditText;

    //, ServiceQuestion question
    public AddressSelector(Activity context, BasicFragment.LoadingProcess loadingCallback, ArrayList<State> stateList, ArrayList<City> cityList, ArrayList<District> districtList, boolean isConfirmationScreen, boolean isIndepentedSelector) {
        super(context);
        setLayoutTransition(new LayoutTransition());
        this.context = context;
        app = (ArmutHVApp) context.getApplication();
//        this.question = question;
        this.stateList = stateList;
        this.cityList = cityList;
        this.districtList = districtList;
        this.independentSelector = isIndepentedSelector;
        predefinedState = false;
        predefinedDistrict = false;
        predefinedCity = false;
        this.loadingCallback = loadingCallback;
        init();
        app.getBus().register(this);
    }

    public void addAdditionalInfoText(AdditionalInfoEditText additionalInfoEditText) {
        this.detailEditText = additionalInfoEditText;
        if (detailEditText != null) {
            if(this.getChildCount() != 0){
                this.removeAllViews();
            }
            this.addChildren(detailEditText);
        }
    }

    public AdditionalInfoEditText getInfoText() {
        return detailEditText;
    }

    public boolean isInfoTextAvailable() {
        return detailEditText != null;
    }

//    public void setServiceId(int serviceId) {
//        this.serviceId = serviceId;
//    }

    private void init() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        if (independentSelector) {
            addressRow = R.layout.address_list_view_row;
            addressNothingStateRow = R.layout.nothing_selected_state_row;
            addressNothingCityRow = R.layout.nothing_selected_city_row;
            addressNothingDistrictRow = R.layout.nothing_selected_district_row;
        }
        //setBackgroundColor(getResources().getColor(R.color.accent_material_dark));
        //setGravity(Gravity.CENTER_VER);
        int marginBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        int marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        int paddingVertical = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
        params.setMargins(0, 0, 0, marginTop);
        setPadding(marginBottom, paddingVertical, marginBottom, paddingVertical);
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(params);
//        if (question != null) {
            textView = new QuestionTextView(context, R.dimen.font_size_questions);
//            //TODO this if is new
////            cityDropdown.setClickable(false);
//            if(isConfirmationScreen && !independentSelector){
                setQuestionText(context.getString(R.string.address_detail));
//            }else{
//                setQuestionText(question.getQuestion());
//            }
//        }
        stateDropdown = getAddressView(context, null, Constants.STATE_FIELD);
        stateDropdown.setOnItemSelectedListener(new SpinnerItemSelector(STATE_SELECTED));
        cityDropdown = getAddressView(context, null, Constants.CITY_FIELD);
        cityDropdown.setOnItemSelectedListener(new SpinnerItemSelector(CITY_SELECTED));
        districtDropdown = getAddressView(context, null, Constants.DISTRICT_FIELD);
        if (districtList == null) {
            districtDropdown.setClickable(false);
        }
        districtDropdown.setOnItemSelectedListener(new SpinnerItemSelector(DISTRICT_SELECTED));
        addChildren(stateDropdown);
        addChildren(cityDropdown);
        addChildren(districtDropdown);
    }

//    public void initSpinnersForPredefinedValues(boolean setAccordingToLocation, ArrayList<State> stateList, ArrayList<City> cityList, ArrayList<District> districtList) {
//        this.stateList = new ArrayList<>(stateList);
//        this.cityList = new ArrayList<>(cityList);
//        this.districtList = new ArrayList<>(districtList);
//        stateDropdown = getAddressView(context, stateDropdown, Constants.STATE_FIELD);
//        stateDropdown.setOnItemSelectedListener(new SpinnerItemSelector(STATE_SELECTED));
//        cityDropdown = getAddressView(context, cityDropdown, Constants.CITY_FIELD);
//        cityDropdown.setOnItemSelectedListener(new SpinnerItemSelector(CITY_SELECTED));
//        districtDropdown = getAddressView(context, districtDropdown, Constants.DISTRICT_FIELD);
//        districtDropdown.setOnItemSelectedListener(new SpinnerItemSelector(DISTRICT_SELECTED));
//        districtDropdown.setClickable(true);
//        //isConfirmationScreen = true;
//        spinnersReadyToBeSelected = false;
//        // reselectionEnabled = false;
////        if (setAccordingToLocation) {
////            stateDropdown.setSelection((QuestionProvider.getInstance().getClosestStatePosition() + 1));
////            //cityDropdown.setClickable(false); // true
////            cityDropdown.setSelection(1);
////            //districtDropdown.setClickable(false); // true
////            districtDropdown.setSelection(1);
////            cityDropdown.setClickable(true);
////        }
//        Log.d(TAG, "INIT SPINNER SET PREDEFINED VALUES");
//    }

    public void initSpinnersForPredefinedValues(int id, int fieldId, ArrayList list) {
        switch (fieldId) {
            case Constants.STATE_FIELD:
               // if (stateList == null) {
                    stateList = list;
                    stateDropdown = getAddressView(getContext(), stateDropdown, Constants.STATE_FIELD);
               // }
                if (stateDropdown != null && stateList != null) {
                    int index = 1;
                    if(id != 0){
                        for (State state : stateList) {
                            if (state.getId() == id) {
                                Log.d(TAG, "PREDEFINED STATE : " + id + "-index: " + index);
                                break;
                            } else {
                                Log.d(TAG, "PREDEFINED STATE index: " + index);
                                index++;
                            }
                        }
                        predefinedState = true;
                        try {
                            stateDropdown.setSelection(index - 1 < stateList.size() ? index : 0);
                            //cityDropdown.setClickable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        predefinedState = false;
                        try {
                            stateDropdown.setSelection(0);
                            //cityDropdown.setClickable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case Constants.CITY_FIELD:
                //if (cityList == null) {
                    cityList = list;
                    cityDropdown = getAddressView(getContext(), cityDropdown, Constants.CITY_FIELD);
                //}
                if (cityDropdown != null && cityList != null) {
                    int index = 1;
                    if(id != 0){
                        for (City city : cityList) {
                            if (city.getId() == id) {
                                Log.d(TAG, "PREDEFINED CITY : " + id + "-index: " + index + " - cityname: " + city.getName() + " - cityList.size() : " + cityList.size());
                                break;
                            } else {
                                Log.d(TAG, "PREDEFINED CITY index: " + index + " - id: " + id);
                                index++;
                            }
                        }
                        predefinedCity = true;
                        try {
                            cityDropdown.setSelection(index - 1 < cityList.size() ? index : 0);
                            Log.d(TAG, "SET SELECTION : " + (index));
                            //diDropdown.setClickable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        predefinedCity = false;
                        try {
                            cityDropdown.setSelection(0);
                            //diDropdown.setClickable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case Constants.DISTRICT_FIELD:
               // if (districtList == null) {
                    districtList = list;
                    districtDropdown = getAddressView(getContext(), districtDropdown, Constants.DISTRICT_FIELD);
               // }
                if (districtDropdown != null && districtList != null) {
                    int index = 1;
                    if(id != 0){
                        for (District district : districtList) {
                            if (district.getDistrictId() == id) {
                                Log.d(TAG, "PREDEFINED District : " + id + "-index: " + index);
                                break;
                            } else {
                                Log.d(TAG, "PREDEFINED District index: " + index + "- district.getDistrictId(): " + district.getDistrictId() + "- id: " + id);
                                index++;
                            }
                        }
                        predefinedDistrict = true;
                        try {
                            districtDropdown.setSelection(index - 1 < districtList.size() ? index : 0);
                            //diDropdown.setClickable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        predefinedDistrict = false;
                        try {
                            districtDropdown.setSelection(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    public DropdownSpinnerWithPopup getAddressView(Context context, DropdownSpinnerWithPopup spinner, int fieldId) {
        //String[] names = null;
        AddressItem[] names;
        int index = 0;
        ArrayAdapter adapter = null;
        NothingSelectedSpinnerAdapter nothingAdapter = null;
        DropdownSpinnerWithPopup spinnerWithPopup;

        switch (fieldId) {
            case Constants.STATE_FIELD:
                if (stateList != null) {
                    names = new AddressItem[stateList.size()];
                    for (State state : stateList) {
                        names[index] = new AddressItem(state.getId(), state.getName());
                        index++;
                    }
                } else {
                    names = new AddressItem[1];
                    names[0] = new AddressItem(0, "Lütfen Seçiniz");
                }
                adapter = new ArrayAdapterItem(context, addressRow, names);
                nothingAdapter = new NothingSelectedSpinnerAdapter(adapter,
                        addressNothingStateRow,
                        context);

                break;
            case Constants.CITY_FIELD:
                if (cityList != null) {
                    names = new AddressItem[cityList.size()];
                    for (City city : cityList) {
                        names[index] = new AddressItem(city.getId(), city.getName());
                        index++;
                    }
                } else {
                    names = new AddressItem[1];
                    names[0] = new AddressItem(0, "Lütfen Seçiniz");
                }
                adapter = new ArrayAdapterItem(context, addressRow, names);
                nothingAdapter = new NothingSelectedSpinnerAdapter(adapter,
                        addressNothingCityRow,
                        context);


                break;
            case Constants.DISTRICT_FIELD:
                if (districtList != null) {
                    names = new AddressItem[districtList.size()];
                    for (District district : districtList) {
                        names[index] = new AddressItem(district.getDistrictId(), district.getDistrictName());
                        index++;
                    }
                } else {
                    names = new AddressItem[1];
                    names[0] = new AddressItem(0, "Lütfen Seçiniz");
                }

                adapter = new ArrayAdapterItem(context, addressRow, names);
                nothingAdapter = new NothingSelectedSpinnerAdapter(adapter,
                        addressNothingDistrictRow,
                        context);
                break;

        }
        adapter.setDropDownViewResource(R.layout.address_list_view_row);
        if (spinner == null) {
            spinnerWithPopup = new DropdownSpinnerWithPopup(context);
        } else {
            spinnerWithPopup = spinner;
        }

        spinnerWithPopup.setAdapter(nothingAdapter);
        //spinnerWithPopup.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, names));
        spinnerWithPopup.setBackgroundResource(R.drawable.spinner_selector_shape);
        if (independentSelector) {
            spinnerWithPopup.setBackgroundResource(R.drawable.dropdown);
        }
//        if (question != null)
//            spinnerWithPopup.setRequired(question.isRequired());
        return spinnerWithPopup;
    }

    private void setQuestionText(String text) {
        Log.d("ADDRESS_SELECTOR", "SET QUESTION TEXT: " + text);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(textView);
    }

    private void addChildren(View view) {
        if (view instanceof QuestionView) {
            this.addView(view);
        } else {
            throw new IllegalStateException("View should implement QuestionView Interface");
        }
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public String getSelectedValue() {
        String value = "";
        for (int i = 0; i < getChildCount(); i++) {
            View qv = getChildAt(i);
            if (qv instanceof DropdownSpinnerWithPopup) {
                if (value.equals("")) {
                    value = ((DropdownSpinnerWithPopup) qv).getSelectedValue();
                } else {
                    value = value.concat(" - ").concat(((DropdownSpinnerWithPopup) qv).getSelectedValue());
                }
            } else if (qv instanceof AdditionalInfoEditText) {
                value = value.concat("\n").concat(((AdditionalInfoEditText) qv).getSelectedValue());
            }
        }
        return value;
    }

    @Override
    public String getAnswerToSend() {
        if (!isInfoTextAvailable()) {
            return stateDropdown.getAnswerToSend().concat(Constants.SEPERATOR).concat(cityDropdown.getAnswerToSend())
                    .concat(Constants.SEPERATOR).concat(districtDropdown.getAnswerToSend());
        } else {
            return stateDropdown.getAnswerToSend().concat(Constants.SEPERATOR).concat(cityDropdown.getAnswerToSend())
                    .concat(Constants.SEPERATOR).concat(districtDropdown.getAnswerToSend().concat(Constants.SEPERATOR).concat(getInfoText().getAnswerToSend()));
        }
    }

    @Override
    public void setRequired(boolean answerRequired) {
    }

    @Override
    public boolean getRequired() {
        return true;
    }

    @Override
    public boolean isAnswered() {
//        if (useLocationButton.isSelected()) {
//            return true;
//        }
        return stateDropdown.isAnswered() && cityDropdown.isAnswered() && districtDropdown.isAnswered();
    }

    private int controlId;

    @Override
    public int getControlId() {
        return controlId;
    }

    @Override
    public void setControlId(int controlId) {
        this.controlId = controlId;
        stateDropdown.setControlId(controlId);
        cityDropdown.setControlId(controlId);
        districtDropdown.setControlId(controlId);
    }

    @Subscribe
    public void getCityList(final ArrayList<City> cityArrayList) {
        taskRunning = false;
        if (cityArrayList == null) {
            //TODO that means there is a problem -
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                if (loadingCallback != null) {
                    loadingCallback.loadingIsInProgress(false);
                }
//                final Collator trCollator = Collator.getInstance(TimeUtil.localeTr); //Your locale here
//                trCollator.setStrength(Collator.PRIMARY);
//                Collections.sort(cityArrayList, new Comparator<City>() {
//                    @Override
//                    public int compare(City lhs, City rhs) {
//                        return trCollator.compare(lhs.getName(), rhs.getName());//lhs.getName().compareTo(rhs.getName());
//                    }
//                });
                final AddressItem[] names = new AddressItem[cityArrayList.size()];
                for (City city : cityArrayList) {
                    names[index] = new AddressItem(city.getId(), city.getName());
                    index++;
                }
                cityList = cityArrayList;
                ArrayAdapterItem adapter = new ArrayAdapterItem(context, addressRow, names);
                NothingSelectedSpinnerAdapter nothingAdapter = new NothingSelectedSpinnerAdapter(adapter,
                        addressNothingCityRow,// R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        context);
                adapter.setDropDownViewResource(R.layout.address_list_view_row);
                cityDropdown.setAdapter(nothingAdapter);
                Log.d(TAG, "GET CITY LIST");
                cityDropdown.setClickable(true);
                districtDropdown.setClickable(false);
                if (predefinedState) {
                    if (app != null) {
                        Address address = app.getUserAddress();
                        if (address != null) {
                            int cityId = address.getCityId();
                            initSpinnersForPredefinedValues(cityId, Constants.CITY_FIELD, cityArrayList);
                            predefinedState = false;
                        }
                    }
                }
            }
        });
    }

    @Subscribe
    public void getDistrictList(final ArrayList<District> districtArrayList) {
        taskRunning = false;
        if (districtArrayList == null) {
            return;
        }
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                if (loadingCallback != null) {
                    // if (!isConfirmationScreen) {
//                    double lat = districtArrayList.get(0).getLatitude();
//                    double lon = districtArrayList.get(0).getLongitude();
                    Log.d(TAG, "GET SERVICE START TASK: ");// + isServiceChecked
                    if (!independentSelector) {
                        loadingCallback.loadingIsInProgress(true); // , new ServiceTask("", serviceId, lat, lon)
                        //TODO  QuestionProvider.getInstance().setClosestDistrictList(districtArrayList);
                    } else {
                        loadingCallback.loadingIsInProgress(false);
                    }
                    //   }
                    //else {
                    //loadingCallback.loadingIsInProgress(false, null);
                    // }
                }
                //if(isConfirmationScreen){
                Log.d(TAG, "SPINNERS READY TO BE SELECTED : is confirmation screen: " + false);
                spinnersReadyToBeSelected = true;
                //}
                districtList = districtArrayList;
                final AddressItem[] names = new AddressItem[districtArrayList.size()];
                for (District district : districtArrayList) {
                    names[index] = new AddressItem(district.getDistrictId(), district.getDistrictName());
                    index++;
                }
                ArrayAdapterItem adapter = new ArrayAdapterItem(context, addressRow, names);
                Log.d(TAG, "District list length : " + names.length);
                NothingSelectedSpinnerAdapter nothingAdapter = new NothingSelectedSpinnerAdapter(adapter,
                        addressNothingDistrictRow,// R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        context);

                adapter.setDropDownViewResource(R.layout.address_list_view_row);
                Log.d(TAG, "GET DISTRICT LIST");
                districtDropdown.setClickable(true);
                cityDropdown.setClickable(true);
                if (predefinedCity) {
                    if (app != null) {
                        Address address = app.getUserAddress();
                        if (address != null) {
                            int districtId = address.getDistrictId();
                            Log.d(TAG, "USER DISTRICT ID: " + districtId);
                            initSpinnersForPredefinedValues(districtId, Constants.DISTRICT_FIELD, districtArrayList);
                            predefinedCity = false;
                            predefinedDistrict = false;
                        }
                    }
                }
                if (loadingCallback != null)//&isConfirmationScreen
                    loadingCallback.loadingIsInProgress(false);
            }
        });
    }

    public class ArrayAdapterItem extends ArrayAdapter<AddressItem> {

        final Context mContext;
        final int layoutResourceId;
        AddressItem data[] = null;

        public ArrayAdapterItem(Context mContext, int layoutResourceId, AddressItem[] data) {
            super(mContext, layoutResourceId, R.id.textViewItem, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AddressItem objectItem;
            ViewHolder holder;
            try {
                objectItem = data[position];
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
                objectItem = data[0];
            }
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
                holder.textViewItem.setTag(objectItem.itemId);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textViewItem.setText(objectItem.itemName);
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final AddressItem objectItem = data[position];
            if (convertView == null) {
                // inflate the layout
                convertView = ((Activity) mContext).getLayoutInflater().inflate(R.layout.address_list_view_row, parent, false);
                holder = new ViewHolder();
                holder.textViewItem = (TextView) convertView.findViewById(R.id.textViewItem);
                holder.textViewItem.setTag(objectItem.itemId);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // object item based on the position
            holder.textViewItem.setText(objectItem.itemName);
            return convertView;
        }
    }

    public static class ViewHolder {
        TextView textViewItem;
    }

    public class AddressItem {

        final int itemId;
        final String itemName;

        AddressItem(int itemId, String itemName) {
            this.itemId = itemId;
            this.itemName = itemName;
        }

        @Override
        public String toString() {
            return itemName;
        }
    }

    class SpinnerItemSelector implements AdapterView.OnItemSelectedListener {
        int selection = -1;

        SpinnerItemSelector(int selection) {
            this.selection = selection;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            if (selection == STATE_SELECTED) {
//                if (!spinnersReadyToBeSelected && !reselectionEnabled && (!isConfirmationScreen || (isConfirmationScreen && !instantBookingModelAvailable))) { // isConfirmationScreen
//                    reselectionEnabled = true;
//                } else if (reselectionEnabled) {
//                    spinnersReadyToBeSelected = true;
//                }
//            }

            if (position > 0) {
                Log.d(TAG, "ON ITEM SELECTED: taskRunning: " + taskRunning + " - spinnersReadyToBeSelected : " + spinnersReadyToBeSelected + " - selection : " + selection);
                if (!taskRunning && spinnersReadyToBeSelected) {
                    if (selection == STATE_SELECTED) {
                        if (stateList == null) {
                            return;
                        }
                        State state = stateList.get(position - 1);
                        Log.d(TAG, "On STATE SELECTED : position: " + position + " - stateName: " + state.getName()); //TODO nullpointer
                        app.getBus().post(new MiscEvents.GetCitiesRequest(state.getId()));
                        //CityTask cityTask = new CityTask(AddressSelector.this, stateList.get(position - 1).getId());
                        //cityTask.execute();
                        districtDropdown.setSelection(0);
                        cityDropdown.setClickable(true);
                        districtDropdown.setClickable(false);
                        if (loadingCallback != null) {
                            loadingCallback.loadingIsInProgress(true);
                        }
                        if (position > 0) {
//                            if (controlTypeId == Constants.CONTROL_TYPE__WHERE) {
                            //TODO  QuestionProvider.getInstance().selectedStateIndex = position - 1;
//                            }
//                            else if (controlTypeId == Constants.CONTROL_TYPE__WHERE_TO) {
//                                QuestionProvider.getInstance().selectedStateIndexWhereTo = position - 1;
//                            }
                        }
                        //taskRunning = true;
                    } else if (selection == CITY_SELECTED) {
                        City city = cityList.get(position > 0 ? position - 1 : position);
                        Log.d(TAG, "**On CITY SELECTED : position: " + position + " - cityName: " + city.getName());
                        //DistrictTask districtTask = new DistrictTask(AddressSelector.this, city.getId());
                        //app.executeAsyncTask(districtTask, null);
                        app.getBus().post(new MiscEvents.GetDistrictsRequest(cityList.get(position - 1).getId()));
                        districtDropdown.setSelection(0);
                        districtDropdown.setClickable(true);

                        if (loadingCallback != null) {
                            loadingCallback.loadingIsInProgress(true);
                        }
//                        ArrayList<City> citySelected = new ArrayList<>();
//                        citySelected.add(city);
//                        QuestionProvider.getInstance().setClosestCityList(citySelected);
                        if (position > 0) { //&& !isConfirmationScreen
//                            if (controlTypeId == Constants.CONTROL_TYPE__WHERE) {
                            //TODO  QuestionProvider.getInstance().selectedCityIndex = position - 1;
                            Log.d(TAG, "**On CITY SELECTED : position: " + (position - 1) + " - cityName: " + city.getName());
//                            }
//                            else if (controlTypeId == Constants.CONTROL_TYPE__WHERE_TO) {
//                                QuestionProvider.getInstance().selectedCityIndexWhereTo = position - 1;
//                            }
                        }
                        //taskRunning = true;
                    } else if (selection == DISTRICT_SELECTED) {
                        try {
                            District district = districtList.get(position > 0 ? position - 1 : position);
                            Log.d(TAG, "ON DISTRICT SELECTED: position : " + position + " - districtName: " + district.getDistrictName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        districtDropdown.setClickable(true);
//                        ArrayList<District> districtSelected = new ArrayList<>();
//                        districtSelected.add(district);
//                        QuestionProvider.getInstance().setClosestDistrictList(districtSelected);
                        if (position > 0) {
//                            if (controlTypeId == Constants.CONTROL_TYPE__WHERE) {
                            //TODO     QuestionProvider.getInstance().selectedDistrictIndex = position - 1;
//                            }
//                            else if (controlTypeId == Constants.CONTROL_TYPE__WHERE_TO) {
//                                QuestionProvider.getInstance().selectedDistrictIndexWhereTo = position - 1;
//                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}


package com.armut.armuthv.busevents;

import android.app.Activity;
import android.widget.EditText;

import com.armut.armuthv.objects.Address;
import com.armut.armuthv.objects.City;
import com.armut.armuthv.objects.Coordinate;
import com.armut.armuthv.objects.CustomerReview;
import com.armut.armuthv.objects.DeviceToken;
import com.armut.armuthv.objects.District;
import com.armut.armuthv.objects.Job;
import com.armut.armuthv.objects.Message;
import com.armut.armuthv.objects.ParitusVerifyObject;
import com.armut.armuthv.objects.Profile;
import com.armut.armuthv.objects.Review;
import com.armut.armuthv.objects.State;
import com.armut.armuthv.objects.User;
import com.armut.armuthv.objects.UserCalendarItem;
import com.armut.armuthv.objects.UserInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by oguzemreozcan on 25/07/16.
 */
public class MiscEvents {

    public static class MessagesRequest {

        private final int jobQuotesId;

        public MessagesRequest(int jobQuotesId) {
            this.jobQuotesId = jobQuotesId;
        }

        public int getJobQuotesId() {
            return jobQuotesId;
        }
    }

    public static class MessagesResponse {
        private final Response<ArrayList<Message>> response;

        public MessagesResponse(Response<ArrayList<Message>> response) {
            this.response = response;
        }

        public Response<ArrayList<Message>> getResponse() {
            return response;
        }
    }

    public static class PostMessageRequest {

        private final Message message;
        private final WeakReference<EditText> editTextWeakReference;

        public PostMessageRequest(Message message, EditText editText) {
            this.message = message;
            editTextWeakReference = new WeakReference<EditText>(editText);
        }

        public Message getMessage() {
            return message;
        }

        public WeakReference<EditText> getEditTextWeakReference() {
            return editTextWeakReference;
        }
    }

    public static class PostMessageResponse {
        private final Response<Message> response;
        private final WeakReference<EditText> editTextWeakReference;
        private final Message message;

        public PostMessageResponse(Response<Message> response, WeakReference<EditText> editTextWeakReference) {
            this.response = response;
            this.message = response.body();
            this.editTextWeakReference = editTextWeakReference;
        }

        public Response<Message> getResponse() {
            return response;
        }

        public WeakReference<EditText> getEditTextWeakReference() {
            return editTextWeakReference;
        }

        public Message getMessage() {
            return message;
        }
    }


    public static class PostDeviceTokenRequest {
        private final DeviceToken deviceToken;
        public PostDeviceTokenRequest(DeviceToken deviceToken) {
            this.deviceToken = deviceToken;
        }
        public DeviceToken getToken() {
            return deviceToken;
        }
    }

    public static class PostDeviceTokenResponse {
        private final Response<DeviceToken> response;
        private DeviceToken deviceToken;

        public PostDeviceTokenResponse(Response<DeviceToken> response) {
            this.response = response;
            this.deviceToken = response.body();
        }

        public Response<DeviceToken> getResponse() {
            return response;
        }

        public DeviceToken getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(DeviceToken token){
            deviceToken = token;
        }
    }

    public static class DeleteDeviceTokenRequest {
        private final String deviceToken;
        public DeleteDeviceTokenRequest(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getDeviceToken() {
            return deviceToken;
        }
    }

    public static class DeleteDeviceTokenResponse {
        private final Response<ResponseBody> response;

        public DeleteDeviceTokenResponse(Response<ResponseBody> response) {
            this.response = response;
        }
        public Response<ResponseBody> getResponse() {
            return response;
        }

    }

    public static class UserRequest {

        private final String userId;

        public UserRequest(String userId) {
            this.userId = userId;
        }

        public String getUserId() {
            return userId;
        }
    }

    public static class UserResponse {

        private final Response<User> response;

        public UserResponse(Response<User> response) {
            this.response = response;
        }

        public Response<User> getResponse() {
            return response;
        }
    }

    public static class ProfileRequest {
    }

    public static class ProfileResponse {

        private final Response<ArrayList<Profile>> response;

        public ProfileResponse(Response<ArrayList<Profile>> response) {
            this.response = response;
        }

        public Response<ArrayList<Profile>> getResponse() {
            return response;
        }
    }

    public static class GetCalendarRequest{

    }

    public static class GetCalendarResponse {

        private final Response<ArrayList<UserCalendarItem>> response;

        public GetCalendarResponse(Response<ArrayList<UserCalendarItem>> response) {
            this.response = response;
        }

        public Response<ArrayList<UserCalendarItem>> getResponse() {
            return response;
        }
    }

    public static class PatchCalendarRequest{

        private final ArrayList<UserCalendarItem> calendarList;

        public PatchCalendarRequest(ArrayList<UserCalendarItem> calendarList){
            this.calendarList = calendarList;
        }

        public ArrayList<UserCalendarItem>  getCalendarItem() {
            return calendarList;
        }
    }

    public static class PatchCalendarResponse {

        private final Response<ResponseBody> response;

        public PatchCalendarResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class PatchAddressRequest{

        private final Address address;

        public PatchAddressRequest(Address address){
            this.address = address;
        }

        public Address getAddress() {
            return address;
        }
    }

    public static class PatchAddressResponse {

        private final Response<ResponseBody> response;

        public PatchAddressResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class PatchUserRequest{

        private final UserInfo user;

        public PatchUserRequest(UserInfo user){
            this.user = user;
        }

        public UserInfo getUser() {
            return user;
        }
    }

    public static class PatchUserResponse {

        private final Response<ResponseBody> response;

        public PatchUserResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class GetRatingsRequest {

        private final String forUserId;

        public GetRatingsRequest(String forUserId) {
            this.forUserId = forUserId;
        }

        public String getForUserId() {
            return forUserId;
        }
    }

    public static class GetRatingsResponse {
        private final Response<ArrayList<Review>> response;

        public GetRatingsResponse(Response<ArrayList<Review>> response) {
            this.response = response;
        }

        public Response<ArrayList<Review>> getResponse() {
            return response;
        }
    }

    public static class PostProfilePhotoRequest{
        private final byte[] imageEncoded;

        public PostProfilePhotoRequest(byte[] imageEncoded){
            this.imageEncoded = imageEncoded;
        }

        public byte[] getImageEncoded() {
            return imageEncoded;
        }
    }

    public static class PostProfilePhotoResponse{

        private final String response;

        public PostProfilePhotoResponse(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }

    public static class GetStatesRequest{

    }

    public static class GetStatesResponse {

        private final Response<ArrayList<State>> response;

        public GetStatesResponse(Response<ArrayList<State>> response) {
            this.response = response;
        }

        public Response<ArrayList<State>> getResponse() {
            return response;
        }
    }

    public static class GetCitiesRequest{
        private final int stateId;

        public GetCitiesRequest(int stateId) {
            this.stateId = stateId;
        }

        public int getStateId() {
            return stateId;
        }
    }

    public static class GetCitiesResponse {

        private final Response<ArrayList<City>> response;
        private final int stateId;

        public GetCitiesResponse(Response<ArrayList<City>> response, int stateId) {
            this.response = response;
            this.stateId = stateId;
        }

        public Response<ArrayList<City>> getResponse() {
            return response;
        }

        public int getStateId() {
            return stateId;
        }
    }

    public static class GetDistrictsRequest{
        private final int cityId;

        public GetDistrictsRequest(int cityId) {
            this.cityId = cityId;
        }

        public int getCityId() {
            return cityId;
        }
    }

    public static class GetDistrictsResponse {

        private final Response<ArrayList<District>> response;
        private final int cityId;

        public GetDistrictsResponse(Response<ArrayList<District>> response, int cityId) {
            this.response = response;
            this.cityId = cityId;
        }

        public Response<ArrayList<District>> getResponse() {
            return response;
        }

        public int getCityId() {
            return cityId;
        }
    }

    public static class GetAssignedPhoneNumberRequest{

        private final String userId;
        private final long jobId;

        public GetAssignedPhoneNumberRequest(String userId, long jobId){
            this.userId = userId;
            this.jobId = jobId;
        }

        public String getUserId() {
            return userId;
        }

        public long getJobId() {
            return jobId;
        }
    }

    public static class GetAssignedPhoneNumberResponse {

        private final Response<ResponseBody> response;

        public GetAssignedPhoneNumberResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class PatchUserPasswordRequest{

        private final User.ModifyPassword modifyPassObject;

        public PatchUserPasswordRequest(User.ModifyPassword modifyPassObject){
            this.modifyPassObject = modifyPassObject;
        }

        public User.ModifyPassword getModifyPassObject() {
            return modifyPassObject;
        }
    }

    public static class PatchUserPasswordResponse {

        private final Response<ResponseBody> response;

        public PatchUserPasswordResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class PostCoordinateRequest{

        private final Coordinate coordinate;

        public PostCoordinateRequest(Coordinate coordinate){
            this.coordinate = coordinate;
        }

        public Coordinate getCoordinate() {
            return coordinate;
        }
    }

    public static class PostCoordinateResponse {

        private final Response<Coordinate> response;

        public PostCoordinateResponse(Response<Coordinate> response) {
            this.response = response;
        }

        public Response<Coordinate> getResponse() {
            return response;
        }
    }

    public static class PostCoordinatesRequest{

        private final ArrayList<Coordinate> coordinates;

        public PostCoordinatesRequest(ArrayList<Coordinate> coordinates ){
            this.coordinates = coordinates;
        }

        public ArrayList<Coordinate>  getCoordinates() {
            return coordinates;
        }
    }

    public static class PostCoordinatesResponse {

        private final Response<ResponseBody> response;

        public PostCoordinatesResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class DisplayTurnOnLocationServicePopupRequest{

    }

    public static class DisplayBlockingLocationPopupRequest {

    }

    public static class ParitusVerifyAddressRequest{

        private final String address;

        public ParitusVerifyAddressRequest(String address){
            this.address = address;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class ParitusVerifyAddressResponse{

        private final Response<ParitusVerifyObject> response;

        public ParitusVerifyAddressResponse(Response<ParitusVerifyObject> response) {
            this.response = response;
        }

        public Response<ParitusVerifyObject> getResponse() {
            return response;
        }
    }


    public static class PostUserAppReviewRequest{

        private final CustomerReview review;

        public PostUserAppReviewRequest(CustomerReview review){
            this.review = review;
        }

        public CustomerReview getReview() {
            return review;
        }
    }

    public static class PostUserAppReviewResponse {

        private final Response<ResponseBody> response;

        public PostUserAppReviewResponse(Response<ResponseBody> response) {
            this.response = response;
        }

        public Response<ResponseBody> getResponse() {
            return response;
        }
    }

    public static class FillJobHashMapRequest{
        public int sectionNumber;
        public ArrayList<Job> jobArrayList;

        public FillJobHashMapRequest(int sectionNumber, ArrayList<Job> jobArrayList) {
            this.sectionNumber = sectionNumber;
            this.jobArrayList = jobArrayList;
        }
    }

    public static class ItemClickedRequest{
        public int sectionNumber;
        public Job job;
        public Activity activity;

        public ItemClickedRequest(Activity activity, Job job, int sectionNumber) {
            this.sectionNumber = sectionNumber;
            this.activity = activity;
            this.job = job;
        }
    }

    public static class NotificationReceivedEvent{
        public int jobId;
        public int jobQuoteId;
        public String action;

        public NotificationReceivedEvent(int jobId, int jobQuoteId, String action) {
            this.jobId = jobId;
            this.jobQuoteId = jobQuoteId;
            this.action = action;
        }
    }

}

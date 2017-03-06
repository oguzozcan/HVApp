package com.armut.armuthv.services;

import com.armut.armuthv.objects.City;
import com.armut.armuthv.objects.Coordinate;
import com.armut.armuthv.objects.District;
import com.armut.armuthv.objects.State;
import com.armut.armuthv.utils.Constants;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by oguzemreozcan on 22/09/16.
 */

public class LocationRestApi {

    public interface GetStatesRestApi{
        @GET(Constants.STATES_POSTFIX)
        Call<ArrayList<State>> getStates(@Header("Authorization") String token, @Header("client_info") String clientInfo);
    }

    public interface GetCitiesRestApi{
        @GET(Constants.STATES_POSTFIX + "{state_id}" + "/cities")
        Call<ArrayList<City>> getCities(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("state_id") int stateId);
    }

    public interface GetDistrictsRestApi{
        @GET(Constants.STATES_POSTFIX + Constants.CITIES_POSTFIX + "{city_id}" + Constants.DISTRICTS_POSTFIX)
        Call<ArrayList<District>> getDistricts(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Path("city_id") int cityId);
    }

    public interface PostCoordinateRestApi{
        @POST(Constants.COORDINATE_POSTFIX)
        Call<Coordinate> postCoordinate(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body Coordinate coordinateModel);
    }

    public interface PostCoordinatesRestApi{
        @POST(Constants.COORDINATE_POSTFIX)
        Call<ResponseBody> postCoordinates(@Header("Authorization") String token, @Header("client_info") String clientInfo, @Body ArrayList<Coordinate> coordinateModel);
    }

}

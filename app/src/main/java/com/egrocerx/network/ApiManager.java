package com.egrocerx.network;


import android.app.ProgressDialog;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.crashlytics.android.Crashlytics;
import com.egrocerx.core.MyApplication;
import com.egrocerx.util.AppUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author rahul
 */
public class ApiManager implements JSONObjectRequestListener {
    public final static String PROJECT_ROOT_URL = "http://grocery.forwardcode.in/";
    public final static String BASE_URL = PROJECT_ROOT_URL + "public/index.php/v1/";

    private static ApiManager apiManager;
    private ApiResponse apiResponse;
    private ApiMode mode;
    private ApiMode cancelledApiMode;
    private static ProgressDialog progressDialog;
    private static JsonParser jsonParser;
    private boolean showLoader = false;

    public static void init() {
        apiManager = new ApiManager();
        jsonParser = new JsonParser();
    }

//    private static void initProgress(Activity context) {
//        progressDialog = new ProgressDialog(context);
//
//        progressDialog.show();
//    }

    public static ApiManager getInstance() {
        if (apiManager == null) {
            init();
        }
        return apiManager;
    }

    @Override
    public void onResponse(JSONObject response) {
        dismissDialog();
//        if (this.mode == cancelledApiMode)
//            return;
        try {

            if (response.has("ErrorMessage")) {
                if (response.getString("ErrorMessage").equals("Success")) {
                    apiResponse.onSuccess(getSerializedJson(response.toString()), mode);
                } else {
                    apiResponse.onFailure(getSerializedJson(response.toString()), mode);
                }
                return;
            }


            if (response.getInt("status") == 200) {
                apiResponse.onSuccess(getSerializedJson(response.toString()), mode);
            } else if (response.getInt("status") == 401) {
                AppUtils.shortToast(response.getString("message"));
                MyApplication.instance.logoutUser();
            } else {
                apiResponse.onFailure(getSerializedJson(response.toString()), mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResponse.onException(e, mode);
        }
    }

    private void dismissDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onError(ANError anError) {
        dismissDialog();
//        if (this.mode == cancelledApiMode)
//            return;

        if (anError.getErrorCode() != 0) {
            apiResponse.onFailure(getSerializedJson(anError.getErrorBody()), mode);
        } else {
            Crashlytics.logException(anError);
            apiResponse.onException(anError, mode);
        }
    }

    private JsonObject getSerializedJson(String jsonObject) {
        //        Log.e("API_RESPONSE", jsonObject);
        return jsonParser.parse(jsonObject).getAsJsonObject();
    }

    public void requestApi(ApiMode mode,
                           HashMap<String, Object> requestBody,
                           boolean showLoader, ApiResponse response,
                           String requestType) {
//        MyApplication.instance.getContext();

        if (cancelledApiMode == mode)
            cancelledApiMode = null;


        if (this.cancelledApiMode != null)
            cancelApi(cancelledApiMode);
        this.apiResponse = response;
        this.mode = mode;
        this.showLoader = showLoader;
        launchLoader();
        switch (mode) {
            default:
                startApi(requestBody, mode.getName(), requestType);
                break;
        }
    }

    private void launchLoader() {
        if (showLoader && !MyApplication.instance.getContext().isFinishing()) {
            progressDialog = new ProgressDialog(MyApplication.instance.getContext());
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    private void startApi(HashMap<String, Object> requestBody, String path,
                          String requestType) {

        switch (requestType) {
            case "post":
            case "POST":
                postRequestBody(requestBody, path).getAsJSONObject(this);
                break;
            case "get":
            case "GET":
                getRequestBody(requestBody, path).getAsJSONObject(this);
                break;
            default:
                break;
        }
    }

    private synchronized ANRequest getRequestBody(HashMap<String, Object> params, String path) {
        ANRequest.GetRequestBuilder builder = new ANRequest.GetRequestBuilder(BASE_URL + path);
        return builder.build();
    }

    private synchronized ANRequest postRequestBody(HashMap<String, Object> params, String path) {
        ANRequest.PostRequestBuilder builder = new ANRequest.PostRequestBuilder(BASE_URL + path);

        /*common param for all api*/
        if (params == null) {
            params = new HashMap<>();
        }

        try {
            builder.addJSONObjectBody(new JSONObject(new Gson().toJson(params)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }


    public void cancelApi(ApiMode mode) {
        cancelledApiMode = mode;
        AndroidNetworking.cancel(mode.getName());
    }

    public void sendMessage(String msg, String mobile, ApiMode mode, ApiResponse response) {
        this.apiResponse = response;
        this.mode = mode;
        launchLoader();

        AndroidNetworking.post(
                "http://rsms.antikinfotech.com/api/mt/SendSMS?APIKey=bPo7MnnLkEqHmIKtuxP4vg" +
                        "&senderid=KARLOF&channel=2&DCS=0&flashsms=0")
                .addQueryParameter("text", msg)
                .addQueryParameter("number", mobile)
                .build()
                .getAsJSONObject(this);
    }

}

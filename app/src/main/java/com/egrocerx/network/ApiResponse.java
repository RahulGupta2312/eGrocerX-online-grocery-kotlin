package com.egrocerx.network;

import com.google.gson.JsonObject;


public interface ApiResponse {
    void onSuccess(JsonObject jsonObject, ApiMode mode);

    void onFailure(JsonObject errorObject, ApiMode mode);

    void onException(Exception e, ApiMode mode);
}


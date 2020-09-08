package com.travels.searchtravels.api;

import com.google.api.services.vision.v1.model.LatLng;

public interface OnVisionApiListener {
    public void onSuccess(LatLng latLng);
    public void onErrorPlace(String category);
    public void onError();
}

package com.foxconn.bandon.label.detail.model;


import com.foxconn.bandon.http.ResponseStr;

import okhttp3.RequestBody;
import retrofit2.Callback;

public interface ILabelDetailDataSource {

    interface LabelDetailCallback extends Callback<ResponseStr> {

    }

    interface GetLabelDetailCallback extends Callback<LabelDetail>{

    }

    interface LabelItemCallback extends Callback<LabelItem> {

    }

    void saveLabelDetail(RequestBody body, LabelDetailCallback callback);

    void deleteLabelDetail(RequestBody body, LabelDetailCallback callback);

    void getLabelItem(String name, LabelItemCallback callback);

    void getLabelDetail(int id, GetLabelDetailCallback callback);




}

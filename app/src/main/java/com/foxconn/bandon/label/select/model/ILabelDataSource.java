package com.foxconn.bandon.label.select.model;


import retrofit2.Callback;

public interface ILabelDataSource {

    interface GetLabelCallback extends Callback<Label> {

    }
    void getLabel(GetLabelCallback callback);
}

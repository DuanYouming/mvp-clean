package com.foxconn.bandon.base;


public interface BaseView<T extends BasePresenter> {
    void setPresenter(T presenter);
}

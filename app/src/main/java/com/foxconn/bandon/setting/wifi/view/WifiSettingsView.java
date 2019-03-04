package com.foxconn.bandon.setting.wifi.view;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.StatusSwitch;
import com.foxconn.bandon.setting.BaseSettingView;
import com.foxconn.bandon.setting.wifi.WifiContract;
import com.foxconn.bandon.setting.wifi.model.WifiDevice;
import com.foxconn.bandon.setting.wifi.presenter.WifiPresenter;
import com.foxconn.bandon.setting.wifi.model.WifiDeviceRepository;
import com.foxconn.bandon.utils.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class WifiSettingsView extends FrameLayout implements WifiContract.View {

    public static final String TAG = WifiSettingsView.class.getSimpleName();
    private RecyclerView mListView;
    private Context mContext;
    private WifiListAdapter mAdapter;
    private List<WifiDevice> mResults = new ArrayList<>();
    private BaseSettingView.DismissCallback mDismissCallback;
    private WifiContract.Presenter mPresenter;
    private StatusSwitch mSwitchBar;
    private WifiConnectDialog mDialog;

    public WifiSettingsView(@NonNull Context context) {
        super(context);
    }

    public WifiSettingsView(@NonNull Context context, BaseSettingView.DismissCallback callback) {
        this(context);
        this.mDismissCallback = callback;
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setup();
    }

    protected void setup() {
        inflate(mContext, R.layout.layout_wifi_view, this);

        WifiManager manager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        new WifiPresenter(manager, this, WifiDeviceRepository.getInstance(new AppExecutors(), getContext().getFilesDir()));

        View btnClose = findViewById(R.id.button_close);
        btnClose.setOnClickListener(clickListener);

        mSwitchBar = findViewById(R.id.status_switch);
        mSwitchBar.setOnCheckedChangeListener(changeListener);

        mListView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mAdapter = new WifiListAdapter(getContext(), mResults);
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(mAdapter);

        mDialog = findViewById(R.id.connect_dialog);
        mDialog.setClickCallback(new WifiConnectDialog.ClickCallback() {
            @Override
            public void cancel() {
                hideDialog();
            }

            @Override
            public void join(WifiDevice device) {
                mPresenter.startConnecting(device);
            }
        });

        mAdapter.setCallback(new WifiListAdapter.ClickCallback() {
            @Override
            public void onClick(int position) {
                mDialog.setData(mResults.get(position));
                showDialog();

            }
        });

        if (null != mPresenter) {
            mPresenter.addCallback();
            mPresenter.startScan();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mPresenter) {
            mPresenter.removeCallback();
        }
        WifiDeviceRepository.destroyInstance();
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mDismissCallback.onDismiss(TAG);
        }
    };

    CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
            if (null != mPresenter) {
                mPresenter.setWifiState(enable);
            }
        }
    };

    @Override
    public void notifyDataChanged(List<WifiDevice> devices) {
        mResults.clear();
        mResults.addAll(devices);
        mAdapter.notifyDataSetChanged();
        mListView.scrollToPosition(0);
    }

    @Override
    public void showDevicesList() {
        mListView.setVisibility(VISIBLE);
    }

    @Override
    public void hideDevicesList() {
        mListView.setVisibility(INVISIBLE);
    }

    @Override
    public void showDialog() {
        mDialog.setVisibility(VISIBLE);
    }

    @Override
    public void hideDialog() {
        mDialog.setVisibility(INVISIBLE);
    }

    @Override
    public void setDialogMessage(String message) {
        mDialog.setMessage(message);
    }

    @Override
    public void setSwitchBarState(boolean isChecked) {
        mSwitchBar.setChecked(isChecked);
    }

    @Override
    public void setPresenter(WifiContract.Presenter presenter) {
        if (null != presenter) {
            mPresenter = presenter;
        }
    }
}

package com.foxconn.bandon.label.add;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.label.select.LabelSelectFragment;
import com.foxconn.bandon.zixing.activity.CaptureFragment;


public class AddLabelFragment extends BaseFragment {
    public static final String TAG = AddLabelFragment.class.getName();

    public AddLabelFragment() {
    }


    public static AddLabelFragment newInstance() {
        return new AddLabelFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_label, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.scan).setOnClickListener(v -> {
            Bundle bundle = initBundle(CaptureFragment.TAG);
            bundle.putInt(CaptureFragment.KEY_REQUEST_CODE, CaptureFragment.REQUEST_CODE_LABEL);
            mListener.startFragment(bundle);
        });

        view.findViewById(R.id.key_in).setOnClickListener(v -> {
            Bundle bundle = initBundle(LabelSelectFragment.TAG);
            mListener.startFragment(bundle);
        });

    }
}

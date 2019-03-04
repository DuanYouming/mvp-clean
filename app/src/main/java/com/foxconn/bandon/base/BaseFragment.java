package com.foxconn.bandon.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.foxconn.bandon.MainActivity;
import com.foxconn.bandon.utils.FragmentsUtils;
import com.foxconn.bandon.utils.LogUtils;


public class BaseFragment extends Fragment {
    public static final String TAG = BaseFragment.class.getName();
    public static final String KEY_FRAG_TAG = "KEY_FRAG_TAG";
    protected Callback mListener;
    protected Bundle bundle = new Bundle();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mListener = (Callback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView()");
        return new TextView(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d(TAG, "onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.d(TAG, "onResume()");
        mListener.showNavigationBar();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "onDestroyView()");
        unbindDrawables(getView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.d(TAG, "onDetach()");
        mListener = null;
    }

    public void finish() {
        FragmentsUtils utils = FragmentsUtils.getInstance(getActivity().getSupportFragmentManager());
        utils.back();
    }

    public interface Callback {

        void home();

        void startFragment(Bundle bundle);

        void StartFragmentAndFinish(Bundle bundle);

        void showNavigationBar();

        void hideNavigationBar();

        void addView(View view);

        void removeView(View view);

        void destroyView();

        void canSleep();

        void cancelSleep();

        void registNavigationCallback(MainActivity.NavigationCallback callback);

        void unregistNavigationCallback(MainActivity.NavigationCallback callback);

    }

    protected Bundle initBundle(String tag) {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_FRAG_TAG, tag);
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    protected Bundle getBundle() {
        return bundle;
    }

    private void unbindDrawables(View view) {
        if (null == view) {
            LogUtils.d(TAG, "unbindDrawables view is null");
            return;
        }
        if (null != view.getBackground()) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }


}

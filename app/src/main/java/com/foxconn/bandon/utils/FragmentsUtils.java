package com.foxconn.bandon.utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.standby.StandbyFragment;
import com.foxconn.bandon.main.view.MainFragment;
import com.foxconn.bandon.tinker.BandonApplication;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class FragmentsUtils {
    private static final String TAG = FragmentsUtils.class.getSimpleName();
    private static FragmentsUtils instance;
    private List<String> fragmentTags = new ArrayList<>();
    private List<String> commonFrags = new ArrayList<>();
    private final FragmentManager manager;

    private FragmentsUtils(FragmentManager manager) {
        this.manager = manager;
        commonFrags.add(StandbyFragment.TAG);
        commonFrags.add(MainFragment.TAG);
        push(StandbyFragment.TAG);
    }

    public static FragmentsUtils getInstance(FragmentManager manager) {
        if (null == instance) {
            synchronized (FragmentsUtils.class) {
                if (null == instance) {
                    instance = new FragmentsUtils(manager);
                }
            }
        }
        return instance;
    }

    public void initMain(Bundle bundle) {
        String tag = bundle.getString(BaseFragment.KEY_FRAG_TAG);
        if (TextUtils.equals(tag, MainFragment.TAG)) {
            getFragment(tag);
            push(tag);
        }
    }

    public void startFragment(Bundle bundle) {
        stop(top());
        start(bundle);
    }

    public void StartFragmentAndFinish(Bundle bundle) {
        if (TextUtils.equals(top(), StandbyFragment.TAG)) {
            return;
        }
        stop(top());
        toBack();
        start(bundle);
    }

    public void back() {
        if (TextUtils.equals(top(), StandbyFragment.TAG)) {
            return;
        }
        stop(top());
        toBack();
        LogUtils.d(TAG, "show fragment " + top());
        Bundle bundle = new Bundle();
        bundle.putString(BaseFragment.KEY_FRAG_TAG, top());
        start(bundle);
    }

    private void start(Bundle bundle) {
        String fragTag = bundle.getString(BaseFragment.KEY_FRAG_TAG);
        BaseFragment fragment = getFragment(fragTag);
        if (null == fragment) {
            return;
        }
        fragment.setBundle(bundle);
        if (commonFrags.contains(fragTag)) {
            showFragment(fragment);
        } else {
            attachFragment(fragment);
        }
        push(fragTag);
    }

    private void stop(String fragTag) {
        BaseFragment fragment = getFragment(fragTag);
        if (null == fragment) {
            return;
        }
        if (commonFrags.contains(fragTag)) {
            hideFragment(fragment);
        } else {
            detachFragment(fragment);
        }
    }

    public void home() {
        if (TextUtils.equals(top(), MainFragment.TAG)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(BaseFragment.KEY_FRAG_TAG, MainFragment.TAG);
        startFragment(bundle);
    }

    private void addFragment(Fragment fragment, String fragTag) {
        if (null == fragment) {
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.main_container, fragment, fragTag);
        transaction.commit();
    }

    private void showFragment(Fragment fragment) {
        if (null == fragment) {
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    private void hideFragment(Fragment fragment) {
        if (null == fragment) {
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();

    }

    private void attachFragment(Fragment fragment) {
        if (null == fragment) {
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.attach(fragment);
        transaction.commit();
    }

    private void detachFragment(Fragment fragment) {
        if (null == fragment) {
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.detach(fragment);
        transaction.remove(fragment);
        transaction.commit();
    }


    private String top() {
        if (null == fragmentTags || fragmentTags.size() == 0) {
            return null;
        }
        return fragmentTags.get(fragmentTags.size() - 1);
    }

/*    private void toHome() {
        int index = fragmentTags.indexOf(MainFragment.TAG);
        for (int i = fragmentTags.size() - 1; i > index; i--) {
            String tag = fragmentTags.get(i);
            pop(tag);
            stop(tag);
            LogUtils.d(TAG, "pop fragment " + tag);
        }
        LogUtils.d(TAG, "fragmentTags top fragment:" + top());
    }*/

    private void toBack() {
        pop(top());
        LogUtils.d(TAG, "fragmentTags top fragment:" + top());
    }

    private void push(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        if (!fragmentTags.contains(tag)) {
            fragmentTags.add(tag);
        } else {
            //clear tags which above this tag
            int top = fragmentTags.indexOf(tag);
            for (int i = fragmentTags.size() - 1; i > top; i--) {
                fragmentTags.remove(i);
            }
        }
    }

    private void pop(String tag) {
        if (TextUtils.isEmpty(tag) || TextUtils.equals(tag, StandbyFragment.TAG)) {
            return;
        }
        if (fragmentTags.contains(tag)) {
            fragmentTags.remove(tag);
        } else {
            LogUtils.d(TAG, "fragment manager not contains " + tag);
        }
    }

    private BaseFragment getFragment(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return null;
        }
        Fragment fragment = manager.findFragmentByTag(tag);
        if (null != fragment && (fragment instanceof BaseFragment)) {
            return (BaseFragment) fragment;
        }

        try {
            Class<?> clazz = BandonApplication.getInstance().getClassLoader().loadClass(tag);
            Method method = clazz.getMethod("newInstance");
            method.setAccessible(true);
            Object instance = method.invoke(null);
            if (instance instanceof BaseFragment) {
                fragment = (BaseFragment) instance;
                addFragment(fragment, tag);
                return (BaseFragment) fragment;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(TAG, "getFragment() Exception:" + e.toString());
        }
        return null;
    }
}

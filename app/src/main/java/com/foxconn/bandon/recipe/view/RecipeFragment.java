package com.foxconn.bandon.recipe.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foxconn.bandon.MainActivity;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.custom.SearchEditView;
import com.foxconn.bandon.recipe.RecipeContract;
import com.foxconn.bandon.recipe.model.CategoryInfo;
import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.recipe.presenter.RecipePresenter;
import com.foxconn.bandon.speech.SpeechFragment;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends BaseFragment implements RecipeContract.View {
    public static final String TAG = RecipeFragment.class.getName();
    private static final int COUNTS = 4;
    private RecipeContract.Presenter mPresenter;
    private SearchEditView mSearchView;
    private View mSearchContainer;
    private View mFragContainer;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<CategoryList.ListBean> mValues = new ArrayList<>();
    private RecipeAdapter mRecipeAdapter;
    private TextView mExceptionView;
    private TextView mTextCategory;
    private Bundle mBundle;

    public RecipeFragment() {

    }


    public static RecipeFragment newInstance() {
        return new RecipeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mListener.registNavigationCallback(navigationCallback);
        new RecipePresenter(this, RecipeRepository.getInstance(new AppExecutors()));
        mBundle = getBundle();
        return inflater.inflate(R.layout.fragment_recipe, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initSearchView(view);
        initView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != bundle) {
            String result = bundle.getString(Constant.KEY_SEARCH_RESULT);
            listener.go(result);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.unregistNavigationCallback(navigationCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mFragments) {
            mFragments.clear();
        }
    }

    @Override
    public void update(CategoryList list) {
        mExceptionView.setVisibility(View.GONE);
        mValues.clear();
        mValues.addAll(list.getResult().getList());
        mRecipeAdapter.notifyDataSetChanged();
    }

    @Override
    public void showExceptionView() {
        mValues.clear();
        mRecipeAdapter.notifyDataSetChanged();
        mExceptionView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(RecipeContract.Presenter presenter) {
        mPresenter = presenter;
    }

    private void initData() {
        for (int i = 0; i < COUNTS; i++) {
            CategoryFragment fragment = CategoryFragment.newInstance(i);
            fragment.setListener(itemClickListener);
            mFragments.add(fragment);
        }
    }

    private void initSearchView(View view) {
        mSearchContainer = view.findViewById(R.id.search_container);
        mSearchView = view.findViewById(R.id.search_view);
        mSearchView.setHintString(getString(R.string.label_cat_search_hit));
        mSearchView.setSearchListener(listener);

        mTextCategory = view.findViewById(R.id.category);
        mExceptionView = view.findViewById(R.id.exception_view);
        RecyclerView listView = view.findViewById(R.id.list_view);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        mRecipeAdapter = new RecipeAdapter(getContext(), mValues);
        listView.setAdapter(mRecipeAdapter);
    }

    private void initView(View view) {
        mFragContainer = view.findViewById(R.id.frag_container);
        TabLayout tab = view.findViewById(R.id.tabs);
        tab.addTab(tab.newTab().setText(getContext().getText(R.string.recipe_sharp)));
        tab.addTab(tab.newTab().setText(getContext().getText(R.string.recipe_recommend)));
        tab.addTab(tab.newTab().setText(getContext().getText(R.string.recipe_meals)));
        tab.addTab(tab.newTab().setText(getContext().getText(R.string.recipe_food_category)));
        tab.setSelectedTabIndicatorHeight(0);

        ViewPager pager = view.findViewById(R.id.recipe_pager);
        ViewPageAdapter adapter = new ViewPageAdapter(getActivity().getSupportFragmentManager(), mFragments);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));
        tab.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.setOverScrollMode(View.OVER_SCROLL_NEVER);

    }

    private void resetSearchView() {
        mTextCategory.setVisibility(View.GONE);
        mSearchContainer.setVisibility(View.GONE);
        mFragContainer.setVisibility(View.VISIBLE);
        mSearchView.setVisibility(View.VISIBLE);
        mSearchView.clearInput();
        mSearchView.setHintString(getString(R.string.label_cat_search_hit));
        mSearchView.clearFocus();
        mValues.clear();
    }


    class ViewPageAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;

        ViewPageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return null == mFragments ? 0 : mFragments.size();
        }

    }

    SearchEditView.SearchListener listener = new SearchEditView.SearchListener() {
        @Override
        public void go(String keyword) {
            if (TextUtils.isEmpty(keyword)) {
                return;
            }
            if (mSearchContainer.getVisibility() == View.GONE) {
                mSearchContainer.setVisibility(View.VISIBLE);
                mFragContainer.setVisibility(View.GONE);
            }
            mPresenter.getCategoryListByName(keyword);
        }

        @Override
        public void voiceInput() {
            Bundle bundle = initBundle(SpeechFragment.TAG);
            bundle.putString(SpeechFragment.KEY_FROM, RecipeFragment.TAG);
            mListener.StartFragmentAndFinish(bundle);
        }
    };

    MainActivity.NavigationCallback navigationCallback = new MainActivity.NavigationCallback() {
        @Override
        public boolean onBackClick() {
            if (mSearchContainer.getVisibility() == View.VISIBLE || mTextCategory.getVisibility() == View.VISIBLE) {
                resetSearchView();
                return true;
            }
            return false;
        }

        @Override
        public boolean onVoiceClick() {
            return true;
        }
    };

    CategoryFragment.ItemClickListener itemClickListener = new CategoryFragment.ItemClickListener() {
        @Override
        public void onClick(View v) {
            if ((null != v.getTag()) && (v.getTag() instanceof CategoryInfo)) {
                CategoryInfo info = (CategoryInfo) v.getTag();
                mTextCategory.setVisibility(View.VISIBLE);
                mSearchContainer.setVisibility(View.VISIBLE);
                mSearchView.setVisibility(View.GONE);
                mFragContainer.setVisibility(View.GONE);
                mTextCategory.setText(info.getName());
                mPresenter.getCategoryListByID(info.getCtgId());
            }
        }
    };


}

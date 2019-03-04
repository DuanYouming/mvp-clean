package com.foxconn.bandon.label.select;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.foxconn.bandon.MainActivity;
import com.foxconn.bandon.R;
import com.foxconn.bandon.base.BaseFragment;
import com.foxconn.bandon.custom.SearchEditView;
import com.foxconn.bandon.label.detail.view.LabelDetailFragment;
import com.foxconn.bandon.label.select.model.Label;
import com.foxconn.bandon.label.select.model.LabelCategory;
import com.foxconn.bandon.label.select.model.LabelRepository;
import com.foxconn.bandon.label.select.presenter.LabelSelectPresenter;
import com.foxconn.bandon.speech.SpeechFragment;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.Constant;
import com.foxconn.bandon.utils.LogUtils;
import com.foxconn.bandon.zixing.activity.CaptureFragment;

import java.util.ArrayList;
import java.util.List;


public class LabelSelectFragment extends BaseFragment implements ILabelSelectContract.View {
    public static final String TAG = LabelSelectFragment.class.getName();
    private static final int Category_Of_Row = 5;
    private static String[] foods = new String[]{"萝卜", "苹果", "可乐", "猪蹄", "西瓜", "秋刀鱼", "番茄沙司", "牛肉丸", "酸奶"};
    private ILabelSelectContract.Presenter mPresenter;
    private SubAdaptor mSubAdaptor;
    private Label mLabel;
    private List<String> mSubTags;
    private List<String> mTotalTags = new ArrayList<>();
    private View mSearchContainer;
    private View mSelectContainer;
    private SearchEditView mSearchView;
    private View mLoadFailureView;
    private View noResultView;
    private SubAdaptor subAdaptor;
    private RecyclerView recyclerView;
    private List<String> searchValues;
    private Bundle mBundle;

    public LabelSelectFragment() {
    }


    public static LabelSelectFragment newInstance() {
        return new LabelSelectFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        new LabelSelectPresenter(this, LabelRepository.getInstance(new AppExecutors()));
        mListener.registNavigationCallback(navigationCallback);
        mBundle = getBundle();
        return inflater.inflate(R.layout.fragment_label_select, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.load();
        initLabelView(view);
        initSubLabelView(view);
        initSearchView(view);
        mListener.showNavigationBar();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener.unregistNavigationCallback(navigationCallback);
        if (null != searchValues) {
            searchValues.clear();
        }
        LabelRepository.destroyInstance();
    }

    MainActivity.NavigationCallback navigationCallback = new MainActivity.NavigationCallback() {
        @Override
        public boolean onBackClick() {
            if (mSearchContainer.getVisibility() == View.VISIBLE) {
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

    private void resetSearchView() {
        mSearchContainer.setVisibility(View.GONE);
        mSelectContainer.setVisibility(View.VISIBLE);
        mSearchView.setInputString("");
        mSearchView.setHintString(getString(R.string.label_cat_search_hit));
        mSearchView.clearFocus();
    }

    private void initLabelView(View view) {
        List<LabelCategory> labels = new ArrayList<>();
        labels.add(0, new LabelCategory(R.drawable.ic_cat_veggie, getString(R.string.label_cat_vegetable), getString(R.string.label_tag_vegetable)));
        labels.add(1, new LabelCategory(R.drawable.ic_cat_fruits, getString(R.string.label_cat_fruit), getString(R.string.label_tag_fruit)));
        labels.add(2, new LabelCategory(R.drawable.ic_cat_poultry, getString(R.string.label_cat_poultry), getString(R.string.label_tag_poultry)));
        labels.add(3, new LabelCategory(R.drawable.ic_cat_meat, getString(R.string.label_cat_meat), getString(R.string.label_tag_meat)));
        labels.add(4, new LabelCategory(R.drawable.ic_cat_seafood, getString(R.string.label_cat_seafood), getString(R.string.label_tag_seafood)));
        labels.add(5, new LabelCategory(R.drawable.ic_cat_drinks, getString(R.string.label_cat_beverage), getString(R.string.label_tag_beverage)));
        labels.add(6, new LabelCategory(R.drawable.ic_cat_condiment, getString(R.string.label_cat_spice), getString(R.string.label_tag_spice)));
        labels.add(7, new LabelCategory(R.drawable.ic_cat_frozen_food, getString(R.string.label_cat_frozen), getString(R.string.label_tag_frozen)));

        mSelectContainer = view.findViewById(R.id.select_container);
        mLoadFailureView = view.findViewById(R.id.load_failure_view);
        RecyclerView listView = view.findViewById(R.id.list_labels);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        listView.setLayoutManager(layoutManager);
        Adapter adapter = new Adapter(getContext(), labels);
        listView.setAdapter(adapter);
    }


    private void initSubLabelView(View view) {
        RecyclerView listView = view.findViewById(R.id.list_sub_label);
        ChipsLayoutManager layoutManager = ChipsLayoutManager.newBuilder(getContext()).setMaxViewsInRow(Category_Of_Row).build();
        listView.setLayoutManager(layoutManager);
        mSubTags = new ArrayList<>();
        mSubAdaptor = new SubAdaptor(getContext(), mSubTags);
        listView.setAdapter(mSubAdaptor);
    }

    private void showSubLabelView(String subTag) {
        if (null == mLabel || mLabel.getFoodTags().size() == 0) {
            LogUtils.d(TAG, "Load label fail");
            showExceptionView();
        } else {
            LogUtils.d(TAG, "Load label success");
            mLoadFailureView.setVisibility(View.GONE);
            List<Label.FoodTagsBean> labelTags = mLabel.getFoodTags();
            for (Label.FoodTagsBean labelTag : labelTags) {
                if (TextUtils.equals(subTag, labelTag.getFoodBigTag())) {
                    mSubTags.clear();
                    mSubTags.addAll(labelTag.getFoodSmallTag());
                    mSubAdaptor.notifyDataSetChanged();
                }
            }
        }
    }

    private void initSearchView(View view) {
        LogUtils.d(TAG, "initSearchView");
        mSearchContainer = view.findViewById(R.id.search_container);
        recyclerView = view.findViewById(R.id.list_search_view);
        ChipsLayoutManager layoutManager = ChipsLayoutManager.newBuilder(getContext()).setMaxViewsInRow(Category_Of_Row).build();
        recyclerView.setLayoutManager(layoutManager);
        searchValues = new ArrayList<>();
        subAdaptor = new SubAdaptor(getContext(), searchValues);
        recyclerView.setAdapter(subAdaptor);
        noResultView = view.findViewById(R.id.no_result_hint);
        mSearchView = view.findViewById(R.id.search);
        mSearchView.setHintString(getString(R.string.label_cat_search_hit));
        mSearchView.setSearchListener(listener);
    }

    @Override
    public void update(Label label) {
        mLabel = label;
        List<Label.FoodTagsBean> labelTags = mLabel.getFoodTags();
        for (Label.FoodTagsBean labelTag : labelTags) {
            mTotalTags.addAll(labelTag.getFoodSmallTag());
        }
        if (null != mBundle && !TextUtils.isEmpty(mBundle.getString(CaptureFragment.KEY_CAPTURE_RESULT))) {
            LogUtils.d(TAG, "update SearchView ");
            int index = (int) (Math.random() * foods.length);
            mSearchView.setInputString(foods[index]);
            listener.go(foods[index]);
        }
        if (null != mBundle) {
            LogUtils.d(TAG, "update SearchView ");
            String result = mBundle.getString(Constant.KEY_SEARCH_RESULT);
            mSearchView.setInputString(result);
            listener.go(result);
        }
    }


    private void showExceptionView() {
        mLoadFailureView.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(ILabelSelectContract.Presenter presenter) {
        mPresenter = presenter;
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private List<LabelCategory> mLabels;
        private LayoutInflater mInflater;
        private int mSelectedIndex = -1;

        Adapter(Context context, List<LabelCategory> labels) {
            this.mLabels = labels;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.item_label_select, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final int res = mLabels.get(position).getIcon();
            final String name = mLabels.get(position).getName();
            final String subTag = mLabels.get(position).getTag();
            holder.imageView.setImageResource(res);
            holder.textView.setText(name);
            if (mSelectedIndex != position) {
                holder.view.setSelected(false);
            } else {
                holder.view.setSelected(true);
            }
            holder.view.setOnClickListener(v -> {
                if (!holder.view.isSelected()) {
                    mSelectedIndex = holder.getAdapterPosition();
                    notifyDataSetChanged();
                    showSubLabelView(subTag);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mLabels.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;
            View view;

            public ViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                imageView = itemView.findViewById(R.id.image_label_icon);
                textView = itemView.findViewById(R.id.text_label_name);
            }
        }
    }

    SearchEditView.SearchListener listener = new SearchEditView.SearchListener() {

        @Override
        public void go(String keyword) {
            List<String> searchResult = new ArrayList<>();
            if (TextUtils.isEmpty(keyword)) {
                return;
            }
            if (mSearchContainer.getVisibility() == View.GONE) {
                mSearchContainer.setVisibility(View.VISIBLE);
                mSelectContainer.setVisibility(View.GONE);
            }

            for (String tag : mTotalTags) {
                if (tag.contains(keyword) && !searchResult.contains(tag)) {
                    searchResult.add(tag);
                }
            }
            if (searchResult.size() > 0) {
                noResultView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                noResultView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
            searchValues.clear();
            searchValues.addAll(searchResult);
            subAdaptor.notifyDataSetChanged();
        }

        @Override
        public void voiceInput() {
            Bundle bundle = initBundle(SpeechFragment.TAG);
            bundle.putString(SpeechFragment.KEY_FROM, LabelSelectFragment.TAG);
            mListener.StartFragmentAndFinish(bundle);
        }
    };

    private class SubAdaptor extends RecyclerView.Adapter<SubAdaptor.ViewHolder> {

        private List<String> mValues;
        private LayoutInflater mInflater;

        SubAdaptor(Context context, List<String> mValues) {
            this.mValues = mValues;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.sub_label_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            int padding = 15;
            if (position % Category_Of_Row == 0) {
                holder.view.setPadding(0, padding, padding, padding);
            } else {
                holder.view.setPadding(padding, padding, padding, padding);
            }

            holder.subCat.setText(mValues.get(position));
            holder.subCat.setOnClickListener(v -> {
                Bundle bundle = initBundle(LabelDetailFragment.TAG);
                bundle.putString(LabelDetailFragment.KEY_ITEM_NAME, mValues.get(holder.getAdapterPosition()));
                mListener.startFragment(bundle);
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView subCat;
            private View view;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                subCat = view.findViewById(R.id.sub_cat);
            }
        }

    }

}

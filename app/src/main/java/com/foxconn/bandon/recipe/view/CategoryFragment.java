package com.foxconn.bandon.recipe.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.viewpager.CBViewHolderCreator;
import com.foxconn.bandon.custom.viewpager.ConvenientBanner;
import com.foxconn.bandon.custom.viewpager.Holder;
import com.foxconn.bandon.recipe.model.CategoryFactory;
import com.foxconn.bandon.recipe.CategoryFragmentContract;
import com.foxconn.bandon.recipe.model.BannerInfo;
import com.foxconn.bandon.recipe.model.CategoryInfo;
import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.recipe.model.RecipeRepository;
import com.foxconn.bandon.recipe.presenter.CategoryFragmentPresenter;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment implements CategoryFragmentContract.View {
    private static final String TAG = CategoryFragment.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final int SIZE = 3;
    private List<CategoryInfo> mCategories = new ArrayList<>();
    private List<CategoryList> mLists = new ArrayList<>();
    private List<BannerInfo> mBannerInfo = new ArrayList<>();
    private CategoryFragmentContract.Presenter mPresenter;
    private Adapter mAdapter;
    private ItemClickListener listener;
    private int index;
    private int nowIndex = 0;

    public CategoryFragment() {

    }

    public static CategoryFragment newInstance(int index) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate");
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_PARAM1);
            mCategories = CategoryFactory.initCategories(index);
            mBannerInfo = CategoryFactory.initBannerInfo(index);
            new CategoryFragmentPresenter(RecipeRepository.getInstance(new AppExecutors()), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager(view);
        initRecyclerView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.d(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy");
    }

    @Override
    public void update(CategoryList list, String name) {
        list.setCategoryName(name);
        mLists.add(nowIndex, list);
        nowIndex++;
        if (nowIndex == mCategories.size()) {
            mAdapter.notifyDataSetChanged();
            nowIndex = 0;
        } else {
            initData();
        }
        LogUtils.d(TAG, "update list size:" + mLists.size());
    }

    @Override
    public void showExceptionView() {

    }

    @Override
    public void setPresenter(CategoryFragmentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }

    private void initData() {
        if (null != mLists && mLists.size() == mCategories.size()) {
            return;
        }
        CategoryInfo categoryInfo = mCategories.get(nowIndex);
        mPresenter.getCategoryList(categoryInfo.getName(), categoryInfo.getCtgId(), 2, SIZE);

    }

    private void initViewPager(View view) {
        ConvenientBanner<BannerInfo> banner = view.findViewById(R.id.banner);
        if (index == CategoryFactory.INDEX_MORE) {
            banner.setVisibility(View.GONE);
            return;
        }
        banner.setPages(creator, mBannerInfo);
        banner.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT);
        banner.setPageIndicator(new int[]{R.drawable.banner_default, R.drawable.banner_selected});
        banner.setOnItemClickListener(position -> {

        });
        banner.setCanLoop(true);
        banner.startTurning(3000);
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        mAdapter = new Adapter(mLists);
        recyclerView.setAdapter(mAdapter);
    }


    class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private LayoutInflater inflater;
        private List<CategoryList> mValues;

        public Adapter(List<CategoryList> mValues) {
            this.mValues = mValues;
            this.inflater = LayoutInflater.from(getContext());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.category_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.title.setText(mValues.get(position).getCategoryName());
            holder.setValues(mValues.get(position).getResult().getList());
            holder.more.setTag(mCategories.get(position));
            holder.more.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
            return null == mValues ? 0 : mValues.size();
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView more;
        RecyclerView recyclerView;
        RecipeAdapter mRecipeAdapter;
        List<CategoryList.ListBean> mValues = new ArrayList<>();

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            more = itemView.findViewById(R.id.more);

            recyclerView = itemView.findViewById(R.id.recipe_list);
            GridLayoutManager manager = new GridLayoutManager(getContext(), 3, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
            mRecipeAdapter = new RecipeAdapter(getContext(), mValues);
            recyclerView.setAdapter(mRecipeAdapter);
        }

        public void setValues(List<CategoryList.ListBean> values) {
            mValues.clear();
            mValues.addAll(values);
            mRecipeAdapter.notifyDataSetChanged();
        }
    }

    CBViewHolderCreator creator = new CBViewHolderCreator() {
        @Override
        public Holder createHolder(View itemView) {
            return new LocalImageHolderView(itemView);
        }

        @Override
        public int getLayoutId() {
            return R.layout.banner_item;
        }
    };

    class LocalImageHolderView extends Holder<BannerInfo> {
        private ImageView imageView;
        private TextView hint;

        LocalImageHolderView(View itemView) {
            super(itemView);
        }

        @Override
        protected void initView(View itemView) {
            imageView = itemView.findViewById(R.id.show);
            hint = itemView.findViewById(R.id.banner_hint);
        }

        @Override
        public void updateUI(BannerInfo info) {
            imageView.setImageResource(info.getRes());
            hint.setText(info.getHint());
        }
    }

    interface ItemClickListener extends View.OnClickListener {
        @Override
        void onClick(View v);
    }

}

package com.foxconn.bandon.setting.user.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.signature.ObjectKey;
import com.foxconn.bandon.R;
import com.foxconn.bandon.custom.SeparateGridItemDecoration;
import com.foxconn.bandon.setting.user.UserCreateView;
import com.foxconn.bandon.setting.user.model.UserInfo;
import com.foxconn.bandon.setting.user.model.UserInfoRepository;
import com.foxconn.bandon.setting.user.UserSettingsView;
import com.foxconn.bandon.utils.AppExecutors;
import com.foxconn.bandon.utils.GlideApp;
import com.foxconn.bandon.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class UsersListView extends FrameLayout implements IUserManagerContract.View {
    private static final String TAG = UsersListView.class.getSimpleName();
    private UserSettingsView.Callback mClickCallback;
    private IUserManagerContract.Presenter mPresenter;
    private List<UserInfo> mUsers;
    private List<String> mDeleteUsers = new ArrayList<>();
    private Adapter mAdapter;
    private View mBtnManager;
    private View mBtnContainer;
    private boolean mEditModel;

    public UsersListView(@NonNull Context context) {
        this(context, null);
    }


    public UsersListView(@NonNull Context context, UserSettingsView.Callback callback) {
        super(context);
        this.mClickCallback = callback;
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.users_list_view, this);

        findViewById(R.id.back).setOnClickListener(v -> mClickCallback.back());

        mBtnManager = findViewById(R.id.btn_manager);
        mBtnManager.setOnClickListener(v -> enterEditModel());

        mBtnContainer = findViewById(R.id.delete_member_btn_container);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> exitEditModel());

        findViewById(R.id.btn_delete).setOnClickListener(v -> {
            for (String name : mDeleteUsers) {
                mPresenter.deleteUser(name);
            }
            exitEditModel();
        });

        RecyclerView listView = findViewById(R.id.member_recycler_view);
        mUsers = new ArrayList<>();
        mAdapter = new Adapter(mUsers, getContext());
        listView.setAdapter(mAdapter);
        listView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        listView.addItemDecoration(new SeparateGridItemDecoration(getContext()));
        new UserManagerPresenter(this, UserInfoRepository.getInstance(new AppExecutors()));
        mPresenter.getUser();
    }

    private void enterEditModel() {
        mBtnContainer.setVisibility(VISIBLE);
        mBtnManager.setVisibility(INVISIBLE);
        mEditModel = true;
        mAdapter.notifyDataSetChanged();
    }

    private void exitEditModel() {
        mBtnContainer.setVisibility(INVISIBLE);
        mBtnManager.setVisibility(VISIBLE);
        mEditModel = false;
        mAdapter.notifyDataSetChanged();
        mDeleteUsers.clear();
    }

    @Override
    public void notifyDataChanged(List<UserInfo> users) {
        if (null != users) {
            mUsers.clear();
            mUsers.addAll(users);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(IUserManagerContract.Presenter presenter) {
        mPresenter = presenter;
    }


    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private final int VIEW_TYPE_BUTTON = 0;
        private final int VIEW_TYPE_USER = 1;
        private List<UserInfo> users;
        private LayoutInflater mInflater;


        public Adapter(List<UserInfo> users, Context context) {
            this.users = users;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == VIEW_TYPE_USER) {
                view = mInflater.inflate(R.layout.user_list_item, parent, false);
            } else {
                view = mInflater.inflate(R.layout.user_list_button, parent, false);
            }
            return new ViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_TYPE_USER) {
                final UserInfo user = users.get(position);
                String url = user.getUrl();
                ObjectKey key = new ObjectKey(user.getCreated() + "");
                holder.textUserName.setText(user.getName());
                GlideApp.with(getContext())
                        .asBitmap()
                        .override(258, 258)
                        .signature(key)
                        .load(url)
                        .into(holder.imageUserIcon);
                if (mEditModel) {
                    holder.item.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.bg_item_settings_member));
                    holder.selectImage.setVisibility(View.VISIBLE);
                    if (mDeleteUsers.contains(user.getName())) {
                        holder.selectImage.setImageResource(R.drawable.ic_member_choose);
                    } else {
                        holder.selectImage.setImageResource(R.drawable.ic_member_un_choose);
                    }
                } else {
                    holder.item.setBackground(null);
                    holder.selectImage.setVisibility(View.INVISIBLE);
                }
                holder.item.setOnClickListener(v -> {
                    if (mEditModel) {
                        String name = mUsers.get(holder.getAdapterPosition()).getName();
                        if (mDeleteUsers.contains(name)) {
                            mDeleteUsers.remove(name);
                        } else {
                            mDeleteUsers.add(name);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });

            } else {
                holder.item.setOnClickListener(v -> {
                    LogUtils.d(TAG, "add user");
                    UserCreateView createView = new UserCreateView(getContext(), mClickCallback);
                    mClickCallback.back();
                    mClickCallback.toOtherView(createView);
                });
            }
        }

        @Override
        public int getItemCount() {
            int count = null != users ? users.size() : 0;
            if (mEditModel) {
                return count;
            }
            if (count < 5) {
                return count + 1;
            } else {
                return 6;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mUsers.size() >= 6) {
                return VIEW_TYPE_USER;
            } else if (position < mUsers.size()) {
                return VIEW_TYPE_USER;
            } else {
                return VIEW_TYPE_BUTTON;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            View item;
            TextView textUserName;
            ImageView imageUserIcon;
            ImageView selectImage;

            public ViewHolder(View itemView, int type) {
                super(itemView);
                this.item = itemView;
                if (type == VIEW_TYPE_USER) {
                    textUserName = itemView.findViewById(R.id.name);
                    imageUserIcon = itemView.findViewById(R.id.image);
                    selectImage = itemView.findViewById(R.id.select_image);

                }
            }

        }
    }


}

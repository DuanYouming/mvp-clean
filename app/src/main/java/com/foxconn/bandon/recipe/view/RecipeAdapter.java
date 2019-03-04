package com.foxconn.bandon.recipe.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foxconn.bandon.R;
import com.foxconn.bandon.recipe.model.CategoryList;
import com.foxconn.bandon.utils.GlideApp;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<CategoryList.ListBean> mValues;
    private LayoutInflater inflater;
    private Context context;

    RecipeAdapter(Context context, List<CategoryList.ListBean> values) {
        this.mValues = values;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override

    public RecipeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecipeAdapter.ViewHolder holder, int position) {
        GlideApp.with(context).asBitmap().load(mValues.get(position).getThumbnail()).into(holder.imageThumb);
        holder.textName.setText(mValues.get(position).getName());
        holder.parent.setOnClickListener(v -> Toast.makeText(context, "recipe:" + mValues.get(holder.getAdapterPosition()).getMenuId(), Toast.LENGTH_LONG).show());
    }

    @Override
    public int getItemCount() {
        return null == mValues ? 0 : mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageThumb;
        TextView textName;
        View parent;

        public ViewHolder(View itemView) {
            super(itemView);
            this.parent = itemView;
            this.imageThumb = itemView.findViewById(R.id.thumb);
            this.textName = itemView.findViewById(R.id.name);
        }
    }
}

package com.example.bakingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.recipe.DisplayTexable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
    public static final String ARG_ITEM_ID = "item_id";
    public static final String STEP_INDEX_KEY = ARG_ITEM_ID + "index";

    private final FragmentActivity mParentActivity;
    private final List<DisplayTexable> mValues;
    private final boolean mTwoPane;
    private final View.OnClickListener mOnClickListener;

    SimpleItemRecyclerViewAdapter(FragmentActivity parent, List<DisplayTexable> list, boolean twoPane, View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        if(list.get(0) != null) {
            list.add(0, null);
        }
        mValues = list;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
        String text = position > 0 ? "Step " + position + ": " + mValues.get(position).getDisplayText() : "Ingredients";
        holder.infoText.setText(text);
        holder.itemView.setTag(new Integer(position));
        if(mOnClickListener != null) {
            holder.itemView.setOnClickListener(mOnClickListener);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.content)
        TextView infoText;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

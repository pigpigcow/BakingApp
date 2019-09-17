package com.example.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.idling.CountingIdlingResource;

import com.example.bakingapp.Utils.NetworkUtils;
import com.example.bakingapp.recipe.Recipe;
import com.example.bakingapp.recipe.RecipeContent;

import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.ARG_ITEM_ID;

public class MainActivity extends AppCompatActivity {
    private CountingIdlingResource mIdlingResource;

    @BindView(R.id.item_list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Object o = findViewById(R.id.tv_error_message_display);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            new RecipteTask().execute();
        } else {
            updateUI();
        }
    }

    private void updateUI() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        float valueInPixels = getResources().getDimension(R.dimen.card_width_landscape) / getResources().getDisplayMetrics().density;
        int mNoOfColumns = calNumOfColumns(valueInPixels);
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, mNoOfColumns, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        assert recyclerView != null;
        List<Recipe> list = RecipeContent.getInstance().getITEMS();
        recyclerView.setAdapter(new MainActivity.SimpleItemRecyclerViewAdapter(this, list));
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<MainActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {
        private final MainActivity mParentActivity;
        private final List<Recipe> mValues;
        private final View.OnClickListener mOnClickListener = (View view) -> {
            Recipe item = (Recipe) view.getTag();
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID, String.valueOf(item.getId()));
            Context context = view.getContext();
            Intent intent = new Intent(context, ItemListActivity.class);
            intent.putExtras(arguments);
            context.startActivity(intent);
        };

        SimpleItemRecyclerViewAdapter(MainActivity parent, List<Recipe> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public MainActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MainActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.header.setText(mValues.get(position).getName());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.content)
            TextView header;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    public int calNumOfColumns(float columnWidthDp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (screenWidthDp / columnWidthDp + 0.5);
    }

    public class RecipteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            recyclerView.setVisibility(View.INVISIBLE);
            mLoadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            URL url = NetworkUtils.buildUrl();
            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(url);
                RecipeContent.getInstance(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateUI();
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new CountingIdlingResource(MainActivity.class.getSimpleName());
        }
        return mIdlingResource;
    }
}

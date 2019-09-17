package com.example.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bakingapp.recipe.DisplayTexable;
import com.example.bakingapp.recipe.Navable;
import com.example.bakingapp.recipe.Recipe;
import com.example.bakingapp.recipe.RecipeContent;
import com.example.bakingapp.recipe.Step;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.ARG_ITEM_ID;
import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.STEP_INDEX_KEY;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity implements View.OnClickListener, Navable {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.item_list)
    View recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private boolean mTwoPane;
    private int key;
    private int stepIndex;

    View fragContainer;
    ItemDetailFragment detailFragment;
    int fragID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);
        ButterKnife.bind(this);

        mTwoPane = findViewById(R.id.item_detail_container) != null;
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent == null) {
                closeOnError();
            }

            try {
                key = Integer.parseInt(intent.getStringExtra(ARG_ITEM_ID));
                stepIndex = 0;
            } catch (NullPointerException e) {
                e.printStackTrace();
                closeOnError();
                return;
            };
        } else {
            key = savedInstanceState.getInt(this.getClass().getSimpleName());
            stepIndex = savedInstanceState.getInt(this.getClass().getSimpleName() + "steps", stepIndex);
            if(mTwoPane) {
                fragID = savedInstanceState.getInt(this.getClass().getSimpleName() + "fragID", R.id.item_detail_container);
            }
        }

        toolbar.setTitle(getRecipe().getName());
        setSupportActionBar(toolbar);

        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    public Recipe getRecipe() {
        return RecipeContent.getInstance().getItemMap().get(key);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        List<DisplayTexable> list = new ArrayList<>();
        for(Step s : getRecipe().getSteps()) {
            list.add(s);
        }
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, list, mTwoPane, this));
        if(mTwoPane) {
            fragContainer = findViewById(R.id.item_detail_container);
        }
    }

    private void closeOnError() {
        Toast.makeText(this, R.string.load_error, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(this.getClass().getSimpleName(), key);
        outState.putInt(this.getClass().getSimpleName() + "steps", stepIndex);
        if(fragContainer != null) {
            outState.putInt(this.getClass().getSimpleName() + "fragID", fragContainer.getId());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();
        Bundle arguments = new Bundle();
        arguments.putString(STEP_INDEX_KEY, String.valueOf(index));
        arguments.putString(ARG_ITEM_ID, String.valueOf(getRecipe().getId()));
        if (mTwoPane) {
            if ((detailFragment = (ItemDetailFragment) getSupportFragmentManager().findFragmentById(fragID)) == null) {
                detailFragment = new ItemDetailFragment();
                detailFragment.setNavable(this);
                detailFragment.setArguments(arguments);
                fragID = fragContainer.getId();
                getSupportFragmentManager().beginTransaction()
                        .add(fragID, detailFragment)
                        .commit();
            } else {
                detailFragment.setupIndex(getRecipe().getId(), index);
                detailFragment.onDestroy();
                detailFragment.setUI();
            }
        } else {
            Intent intent = new Intent(this, ItemDetailActivity.class);
            intent.putExtras(arguments);
            startActivity(intent);
        }
    }

    @OnClick(R.id.fab)
    public void showIngredientListInWidget(View view) {
        Snackbar.make(view, "Ingredients list updated", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Recipe r = RecipeContent.getInstance().getItemMap().get(key);
        String msg = r.getName() + "\n\n" + r.getIngredientListString();
        IngredientList.setMsg(msg);
        Intent intent = new Intent(this, IngredientList.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), IngredientList.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    @Override
    public void naveLeft(FloatingActionButton fabLeft, FloatingActionButton fabRight) {
        if(stepIndex > 0) {
            detailFragment.setupIndex(key, --stepIndex);
            if(stepIndex == 0) {
                fabLeft.hide();
            }
            fabRight.show();
            detailFragment.onDestroy();
            detailFragment.setUI();
        }
    }

    @Override
    public void navRight(FloatingActionButton fabLeft, FloatingActionButton fabRight) {
        if(stepIndex < getRecipe().getSteps().size()) {
            detailFragment.setupIndex(key, ++stepIndex);
            if(stepIndex == getRecipe().getSteps().size()) {
                fabRight.hide();
            }
            fabLeft.show();
            detailFragment.onDestroy();
            detailFragment.setUI();
        }
    }
}

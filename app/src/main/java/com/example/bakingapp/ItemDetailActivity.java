package com.example.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.widget.NestedScrollView;

import com.example.bakingapp.recipe.Navable;
import com.example.bakingapp.recipe.Recipe;
import com.example.bakingapp.recipe.RecipeContent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.ARG_ITEM_ID;
import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.STEP_INDEX_KEY;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.item_detail_container)
    View fragContainer;
    @BindView(R.id.fab_left)
    FloatingActionButton fabLeft;
    @BindView(R.id.fab_right)
    FloatingActionButton fabRight;

    ItemDetailFragment detailFragment;
    int fragID;

    private int key;
    private int stepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            key = Integer.valueOf(getIntent().getStringExtra(ARG_ITEM_ID));
            stepIndex = Integer.valueOf( getIntent().getStringExtra(STEP_INDEX_KEY));
        } else {
            key = savedInstanceState.getInt(this.getClass().getSimpleName());
            stepIndex = savedInstanceState.getInt(this.getClass().getSimpleName() + "steps");
            fragID = savedInstanceState.getInt(this.getClass().getSimpleName() + "fragID", R.id.item_detail_container);
        }
        toolbar.setTitle(getRecipe().getName());
        setSupportActionBar(toolbar);
        setupFragment();
    }

    public Recipe getRecipe() {
        return RecipeContent.getInstance().getItemMap().get(key);
    }

    public void setupFragment() {
        if ((detailFragment = (ItemDetailFragment) getSupportFragmentManager().findFragmentById(fragID)) == null) {
            Bundle arguments = new Bundle();
            arguments.putString(ARG_ITEM_ID, String.valueOf(key));
            arguments.putString(STEP_INDEX_KEY,String.valueOf(stepIndex));
            detailFragment = new ItemDetailFragment();
            detailFragment.setArguments(arguments);
            fragID = fragContainer.getId();
            getSupportFragmentManager().beginTransaction()
                    .add(fragID, detailFragment)
                    .commit();
        }
    }

    @OnClick(R.id.fab_left)
    public void naveLeft(View view) {
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

    @OnClick(R.id.fab_right)
    public void navRight(View view) {
        if(stepIndex < getRecipe().getSteps().size()) {
            detailFragment.setupIndex(key, ++stepIndex);
            if(stepIndex == getRecipe().getSteps().size() - 1) {
                fabRight.hide();
            }
            fabLeft.show();
            detailFragment.onDestroy();
            detailFragment.setUI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(this.getClass().getSimpleName(), key);
        outState.putInt(this.getClass().getSimpleName() + "steps", stepIndex);
        outState.putInt(this.getClass().getSimpleName() + "fragID", fragContainer.getId());
        super.onSaveInstanceState(outState);
    }
}

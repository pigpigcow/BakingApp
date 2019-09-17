package com.example.bakingapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bakingapp.helpers.MediaHelper;
import com.example.bakingapp.helpers.StringHelper;
import com.example.bakingapp.recipe.Ingredient;
import com.example.bakingapp.recipe.Navable;
import com.example.bakingapp.recipe.Recipe;
import com.example.bakingapp.recipe.RecipeContent;
import com.example.bakingapp.recipe.Step;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.ARG_ITEM_ID;
import static com.example.bakingapp.SimpleItemRecyclerViewAdapter.STEP_INDEX_KEY;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment implements View.OnClickListener {
    private Navable navable;
    private int key = 0;
    private int stepIndex = 0;
    private MediaHelper mHelper;

    @BindView(R.id.playerView)
    SimpleExoPlayerView mPlayerView;
    @BindView(R.id.detail_image)
    ImageView image;
    @BindView(R.id.fab_left)
    FloatingActionButton fabLeft;
    @BindView(R.id.fab_right)
    FloatingActionButton fabRight;

    @BindView(R.id.media_holder)
    FrameLayout mediaHolder;
    @BindView(R.id.detail_fragment_content)
    TextView content;
    private int playerState = -1;
    private long playerPostion = -1;
    private boolean playerWhenReady = true;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {}

    public void setNavable(Navable navable) {
        this.navable = navable;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            key = savedInstanceState.getInt(this.getClass().getSimpleName());
            stepIndex = savedInstanceState.getInt(this.getClass().getSimpleName() + "steps");
        }
    }


    public void setUI() {
        Activity activity = this.getActivity();
        ButterKnife.bind(this, activity);

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        Recipe item = RecipeContent.getInstance().getItemMap().get(key);
        Step s = getCurrentStep();
        boolean isIngredients = stepIndex == 0;

        if (item == null) {
            Toast.makeText(getActivity(), activity.getString(R.string.recipe_not_found), Toast.LENGTH_SHORT).show();
            return;
        } else if (appBarLayout != null) {
            String title = !isIngredients ? s.getShortDescription() : "Ingredients";
            appBarLayout.setTitle(title);
        }

        if(navable != null) {
            if(stepIndex == getRecipe().getSteps().size()) {
                fabRight.hide();
            } else {
                fabRight.show();
            }

            if(stepIndex == 0) {
                fabLeft.hide();
            } else {
                fabLeft.show();
            }
        } else {
            fabLeft.hide();
            fabRight.hide();
        }

        String contextText = "";
        if (!isIngredients) {
            if (s != null && StringHelper.isValid(s.getVideoURL()) || StringHelper.isValid(s.getThumbnailURL())) {
                mediaHolder.setVisibility(View.VISIBLE);
                mHelper = new MediaHelper(s, mPlayerView, image, activity);
                mHelper.setupPlayer(playerWhenReady,playerState, playerPostion);
            } else {
                mediaHolder.setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
            contextText = s.getDescription();
        } else {
            mediaHolder.setVisibility(View.GONE);
            content.setVisibility(View.VISIBLE);
            contextText = Ingredient.getIngredientListString("", RecipeContent.getInstance().getItemMap().get(key).getIngredients());
        }
        content.setText(contextText);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail, container, false);
        if (savedInstanceState == null && getArguments().containsKey(ARG_ITEM_ID)) {
            key = Integer.parseInt(getArguments().getString(ARG_ITEM_ID));
            stepIndex = Integer.parseInt(getArguments().getString(STEP_INDEX_KEY));
        } else {
            key = savedInstanceState.getInt(this.getClass().getSimpleName());
            stepIndex = savedInstanceState.getInt(this.getClass().getSimpleName() + "steps", stepIndex);
            playerPostion = savedInstanceState.getLong(MediaHelper.POSITION_KEY, -1);
            playerState = savedInstanceState.getInt(MediaHelper.STATE_KEY, -1);
            playerWhenReady = savedInstanceState.getBoolean(MediaHelper.PLAY_WHEN_READY_KEY, true);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(this.getClass().getSimpleName(), key);
        outState.putInt(this.getClass().getSimpleName() + "steps", stepIndex);
        if( mHelper != null && mHelper.mediaExist()) {
            playerPostion =  mHelper.getPlayer().getCurrentPosition();
            playerState = mHelper.getPlayer().getPlaybackState();
            playerWhenReady = mHelper.getPlayer().getPlayWhenReady();
            outState.putLong(MediaHelper.POSITION_KEY, playerPostion );
            outState.putInt(MediaHelper.STATE_KEY, playerState );
            outState.putBoolean(MediaHelper.PLAY_WHEN_READY_KEY, playerWhenReady);
        }
    }

    public void setKey(int mListIndex) {
        this.key = mListIndex;
    }

    public Step getCurrentStep() {
        Recipe item = RecipeContent.getInstance().getItemMap().get(key);
        return stepIndex == 0 ? null : item.getSteps().get(stepIndex - 1);
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    public void setupIndex(int key, int stepIndex) {
        this.key = key;
        this.stepIndex = stepIndex;
    }

    @Override
    public void onClick(View v) {
    }

    @OnClick(R.id.fab_left)
    public void naveLeft(View view) {
        navable.naveLeft(fabLeft, fabRight);
    }

    @OnClick(R.id.fab_right)
    public void navRight(View view) {
        navable.navRight(fabLeft, fabRight);
    }

    public Recipe getRecipe() {
        return RecipeContent.getInstance().getItemMap().get(key);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            setUI();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mHelper == null || !mHelper.mediaExist())) {
            setUI();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    public void releasePlayer() {
        if (mHelper != null) {
            mHelper.onDestroy();
        }
    }
}

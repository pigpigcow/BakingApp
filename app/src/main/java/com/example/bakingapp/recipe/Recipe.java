package com.example.bakingapp.recipe;

import com.example.bakingapp.helpers.ListHelper;
import com.example.bakingapp.helpers.StringHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private static final String ID_KEY = "id";
    private static final String NAME_KEY = "name";
    private static final String INGREDIENTS_KEY = "ingredients";
    private static final String STEPS_KEY = "steps";
    private static final String SERVINGS_KEY = "servings";
    private static final String IMAGE_KEY = "image";

    private final int id;
    private final String name;
    private final List<Ingredient> ingredients;
    private final List<Step> steps;
    private final int servings;
    private final String image;

    public Recipe(int id, String name, List<Ingredient> ingredients, List<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public static Recipe getFromJSON(JSONObject json) throws JSONException {
        Recipe result = null;
        if (json != null) {
            final int id = json.getInt(ID_KEY);
            final String name = json.getString(NAME_KEY);
            final List<Ingredient> ingredients = Ingredient.getListFromJSONList(json.getJSONArray(INGREDIENTS_KEY));
            final List<Step> steps = Step.getListFromJSONList(json.getJSONArray(STEPS_KEY));
            final int servings = json.getInt(SERVINGS_KEY);
            final String image = json.getString(IMAGE_KEY);
            result = new Recipe(id, name, ingredients, steps, servings, image);
        }
        return result;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public String getIngredientListString() {
        return Ingredient.getIngredientListString(null, ingredients);
    }
}

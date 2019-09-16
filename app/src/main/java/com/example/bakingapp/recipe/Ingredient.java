package com.example.bakingapp.recipe;

import com.example.bakingapp.helpers.ListHelper;
import com.example.bakingapp.helpers.StringHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Ingredient implements DisplayTexable {
    private static final String QUANTITY_KEY = "quantity";
    private static final String MEASURE_KEY = "measure";
    private static final String INGREDIENT_KEY = "ingredient";

    private final int quantity;
    private final String measure;
    private final String ingredient;

    public Ingredient(int quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public static List<Ingredient> getListFromJSONList(JSONArray list) throws JSONException {
        List<Ingredient> result = new ArrayList<>();
        if(list != null) {
            for(int i = 0; i < list.length(); i++) {
                final JSONObject json = list.getJSONObject(i);
                ListHelper.addObjectToLIstIfNotNull(result, getFromJSON(json));
            }
        }
        return result;
    }

    private static Ingredient getFromJSON(JSONObject json) throws JSONException {
        Ingredient result = null;
        if(json != null) {
            final int quantity = json.getInt(QUANTITY_KEY);
            final String measure = json.getString(MEASURE_KEY);
            final String ingredient = json.getString(INGREDIENT_KEY);
            result = new Ingredient(quantity, measure, ingredient);
        }
        return result;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    @Override
    public String getDisplayText() {
        // 1 1/2 cups (225 g) unbleached all-purpose flour
        return getQuantity() + " " + getMeasure() + " " + getIngredient();
    }

    public static String getIngredientListString(String startText, List<Ingredient> list) {
        String result = StringHelper.isValid(startText) ? startText : "";
        if(list != null) {
            for (Ingredient i : list) {
                result += i.getDisplayText() + "\n";
            }
        }
        return result;
    }
}

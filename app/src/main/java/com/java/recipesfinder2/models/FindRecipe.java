package com.java.recipesfinder2.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FindRecipe {

    @SerializedName("recipe")
    @Expose
    private Recipe recipe;

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

}

package com.java.recipesfinder2.singleton;

import com.java.recipesfinder2.models.SearchRecipes;

import java.util.HashMap;
import java.util.Map;

public class ListOfRecipes {

    private static final ListOfRecipes instance = new ListOfRecipes();

    private SearchRecipes searchRecipes;
    private Map<String,String> ingredients;

    public static ListOfRecipes getInstance() {
        return instance;
    }

    public void setSearchRecipes(SearchRecipes searchRecipes){
        this.searchRecipes = searchRecipes;
    }

    public SearchRecipes getSearchRecipes(){
        return searchRecipes;
    }

    public void setIngredients(String position, String ingredients){
        if (this.ingredients == null){
            this.ingredients = new HashMap<>();
        }
        this.ingredients.put(position, ingredients);
    }

    public Map<String, String> getIngredients(){
        return ingredients;
    }

    public void deleteIngredients(){
        if (ingredients != null) {
            ingredients.clear();
        }
    }
}

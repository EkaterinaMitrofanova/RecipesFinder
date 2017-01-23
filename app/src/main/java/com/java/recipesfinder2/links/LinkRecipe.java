package com.java.recipesfinder2.links;

import com.java.recipesfinder2.models.FindRecipe;
import com.java.recipesfinder2.models.SearchRecipes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LinkRecipe {

//    @POST("api/get")
//    Call<FindRecipe> getRecipeByID(@QueryMap Map<String,String> params);

    @GET("/api/get")
    Call<FindRecipe> getRecipeByID(@Query("key") String key, @Query("rId") String id);

    @GET("/api/search")
    Call<SearchRecipes> searchRecipes(@Query("key") String key, @Query("q") String ingredients, @Query("page") int page);
}

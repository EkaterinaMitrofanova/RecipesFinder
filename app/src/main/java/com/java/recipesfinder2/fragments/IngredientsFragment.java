package com.java.recipesfinder2.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.java.recipesfinder2.MainActivity;
import com.java.recipesfinder2.links.LinkRecipe;
import com.java.recipesfinder2.models.FindRecipe;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IngredientsFragment extends Fragment{

    private MainActivity activity;
    private String key, id;
    private Retrofit client;
    private String position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
    }

    public void findIngredients(String key, String id, Retrofit client, String position){
        this.key = key;
        this.id = id;
        this.position = position;
        this.client = client;
        FindIngredientsAsync findIngredientsAsync = new FindIngredientsAsync();
        findIngredientsAsync.execute();
    }


    public class FindIngredientsAsync extends AsyncTask<Void, String, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.searchStarted();
        }

        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> result = null;
            LinkRecipe service = client.create(LinkRecipe.class);
            Call<FindRecipe> call = service.getRecipeByID(key, id);
            try {
                Response<FindRecipe> response = call.execute();
                if (response.body() != null) {
                    result = response.body().getRecipe().getIngredients();
                }
            } catch (UnknownHostException e){
                publishProgress("internet");
            } catch (SocketTimeoutException e){
                publishProgress("server");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0].equals("server")){
                Toast.makeText(activity, "Server error. Please, try again later", Toast.LENGTH_SHORT).show();
            } else if (values[0].equals("internet")){
                Toast.makeText(activity, "Please, check Internet connection", Toast.LENGTH_SHORT).show();
            }
            onCancelled();
        }


        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            activity.ingredientsFound(result, position);
        }
    }

}

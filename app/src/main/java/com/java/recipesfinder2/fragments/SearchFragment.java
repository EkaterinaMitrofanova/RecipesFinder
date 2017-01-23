package com.java.recipesfinder2.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.java.recipesfinder2.MainActivity;
import com.java.recipesfinder2.links.LinkRecipe;
import com.java.recipesfinder2.models.SearchRecipes;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchFragment extends Fragment{

    private SearchRecipes searchRecipes;
    private MainActivity activity;
    private String key, ingredients;
    private int page;
    private Retrofit client;
    private SearchingAsync searchingAsync;

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

    public void startSearch(String key, String ingredients, int page, Retrofit client){
        if (searchingAsync != null && searchingAsync.getStatus() == AsyncTask.Status.RUNNING){
            searchingAsync.cancel(true);
        }
        this.key = key;
        this.ingredients = ingredients;
        this.page = page;
        this.client = client;
        searchingAsync = new SearchingAsync();
        searchingAsync.execute();
    }

    public class SearchingAsync extends AsyncTask<Void, String, SearchRecipes> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            activity.searchStarted();
        }

        @Override
        protected SearchRecipes doInBackground(Void... params) {
            LinkRecipe service = client.create(LinkRecipe.class);
            Call<SearchRecipes> call = service.searchRecipes(key, ingredients, page);
            try {
                Response<SearchRecipes> response = call.execute();

                searchRecipes = response.body();

            } catch (UnknownHostException e){
                publishProgress("internet");
            } catch (SocketTimeoutException e){
                publishProgress("server");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return searchRecipes;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values[0].equals("server")){
                Toast.makeText(activity, "Server error. Please, try again later", Toast.LENGTH_SHORT).show();
            } else if (values[0].equals("internet")){
                Toast.makeText(activity, "Please, check Internet connection", Toast.LENGTH_SHORT).show();
            }
            cancel(true);
        }

        @Override
        protected void onPostExecute(SearchRecipes searchRecipes) {
            super.onPostExecute(searchRecipes);
            activity.searchFinished(searchRecipes, ingredients, page);
        }

        @Override
        public void onCancelled() {
            super.onCancelled();
            activity.searchCanceled();
        }
    }
}

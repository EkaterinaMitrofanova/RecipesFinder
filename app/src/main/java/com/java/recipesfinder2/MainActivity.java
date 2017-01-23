package com.java.recipesfinder2;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.java.recipesfinder2.adapters.RecyclerAdapter;
import com.java.recipesfinder2.fragments.IngredientsFragment;
import com.java.recipesfinder2.fragments.ListFragment;
import com.java.recipesfinder2.fragments.SearchFragment;
import com.java.recipesfinder2.models.SearchRecipes;
import com.java.recipesfinder2.singleton.ListOfRecipes;

import java.io.File;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecyclerAdapter.ButtonListener{

    EditText ingredientsEdit;
    Button searchButton, prevButton, nextButton;
    TextView pageView;
    ProgressBar progressBar;
    Toolbar toolbar;
    ListFragment listFragment;

    private static final String  BASE_URL = "http://food2fork.com";
    private static final String KEY = "80bb2940a9a7d5a21146b13206876f1e";
    private Retrofit client = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static final String PROGRESS_BAR_STATUS = "progressBar";
    private static final String EDIT_TEXT = "editText";
    private static final String PREVIOUS_BUTTON = "prevButton";
    private static final String NEXT_BUTTON = "nextButton";
    private static final String PAGE_NUMBER = "pageNumber";
    private static final String INGREDIENTS = "ingredients";
    private static final String SEARCH_TAG = "tag1";
    private static final String INGREDIENTS_TAG = "tag2";

    String ingredients = "";

    SearchFragment searchFragment;
    IngredientsFragment ingredientsFragment;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        fragmentManager = getSupportFragmentManager();

        listFragment = (ListFragment) fragmentManager.findFragmentById(R.id.fragmentList);
        listFragment.scrollToPosition();

        searchFragment = (SearchFragment) fragmentManager.findFragmentByTag(SEARCH_TAG);
        if (searchFragment == null){
            searchFragment = new SearchFragment();
            getSupportFragmentManager().beginTransaction().add(searchFragment, SEARCH_TAG).commit();
        }

        ingredientsFragment = (IngredientsFragment) fragmentManager.findFragmentByTag(INGREDIENTS_TAG);
        if (ingredientsFragment == null){
            ingredientsFragment = new IngredientsFragment();
            fragmentManager.beginTransaction().add(ingredientsFragment, INGREDIENTS_TAG).commit();
        }

        if (savedInstanceState != null){
            progressBar.setIndeterminate(savedInstanceState.getBoolean(PROGRESS_BAR_STATUS));
            ingredientsEdit.setText(savedInstanceState.getString(EDIT_TEXT));
            prevButton.setEnabled(savedInstanceState.getBoolean(PREVIOUS_BUTTON));
            nextButton.setEnabled(savedInstanceState.getBoolean(NEXT_BUTTON));
            pageView.setText(savedInstanceState.getString(PAGE_NUMBER));
            ingredients = savedInstanceState.getString(INGREDIENTS);
        }
    }

    private void initViews(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.icon);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        ingredientsEdit = (EditText)findViewById(R.id.ingredientsEdit);
        searchButton = (Button)findViewById(R.id.searchButton);
        prevButton = (Button)findViewById(R.id.previousButton);
        nextButton = (Button)findViewById(R.id.nextButton);
        pageView = (TextView)findViewById(R.id.pageView);

        prevButton.setEnabled(false);
        nextButton.setEnabled(false);

        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == searchButton.getId()){
            startNewSearch(1, ingredientsEdit.getText().toString());
        } else {
            if (id == prevButton.getId()){
                startNewSearch(Integer.valueOf(pageView.getText().toString()) - 1, ingredients);
            } else {
                if (id == nextButton.getId()){
                    startNewSearch(Integer.valueOf(pageView.getText().toString()) + 1, ingredients);
                }
            }
        }
    }

    private void startNewSearch(int page, String ingredients){
        searchFragment.startSearch(KEY, ingredients, page, client);
    }

    public void searchStarted() {
        progressBar.setIndeterminate(true);
    }

    public void searchFinished(SearchRecipes searchRecipes, String ingredients, int page) {
        progressBar.setIndeterminate(false);
        if (searchRecipes != null) {
            if (searchRecipes.getCount() != 0) {
                this.ingredients = ingredients;
                ListOfRecipes.getInstance().setSearchRecipes(searchRecipes);
                ListOfRecipes.getInstance().deleteIngredients();
                listFragment.updateAdapter();
                if (page == 1) {
                    prevButton.setEnabled(false);
                } else {
                    prevButton.setEnabled(true);
                }
                nextButton.setEnabled(true);
                pageView.setText(String.valueOf(page));
            } else {
                System.out.println(searchRecipes.getCount());
                Toast.makeText(this, "Please, check ingredients", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void searchCanceled(){
        progressBar.setIndeterminate(false);
    }

    @Override
    public void getIngredients(String position, String id) {
        ingredientsFragment.findIngredients(KEY, id, client, position);
    }

    @Override
    public void showPreparation(String url){
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

    public void ingredientsFound(List<String> ingredientsList, String position){
        progressBar.setIndeterminate(false);
        if (ingredientsList != null){
            String ingredients = "";
            for (String ingredient : ingredientsList) {
                ingredients = ingredients + ingredient + "\n";
            }
            ListOfRecipes.getInstance().setIngredients(position, ingredients);
            listFragment.updateAdapter(position);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(PROGRESS_BAR_STATUS, progressBar.isIndeterminate());
        outState.putString(EDIT_TEXT, ingredientsEdit.getText().toString());
        outState.putBoolean(PREVIOUS_BUTTON, prevButton.isEnabled());
        outState.putBoolean(NEXT_BUTTON, nextButton.isEnabled());
        outState.putString(PAGE_NUMBER, pageView.getText().toString());
        outState.putString(INGREDIENTS, ingredients);
    }

    @Override
    protected void onDestroy() {
        listFragment.setPosition();
        super.onDestroy();
        try {
            trimCache(this);
            // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).showInWEB();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        return dir.delete();
    }
}

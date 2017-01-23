package com.java.recipesfinder2.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.recipesfinder2.R;
import com.java.recipesfinder2.models.Recipe;
import com.java.recipesfinder2.singleton.ListOfRecipes;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{

    private ListOfRecipes listOfRecipes;

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        final Recipe recipe = listOfRecipes.getSearchRecipes().getRecipes().get(position);
        Picasso.with(holder.view.getContext())
                .load(recipe.getImageUrl())
                .error(R.drawable.image_not_found)
                .fit()
                .into(holder.imageView);
        holder.titleView.setText(recipe.getTitle());
        holder.rankView.setText(String.valueOf(new DecimalFormat("#0.00").format(recipe.getSocialRank())));
        Map<String,String> ingredientsList = listOfRecipes.getIngredients();
        if (ingredientsList != null && ingredientsList.containsKey(String.valueOf(holder.getAdapterPosition()))) {
            holder.ingredView.setText(ingredientsList.get(String.valueOf(holder.getAdapterPosition())));
        } else {
            holder.ingredView.setText("");
        }
        holder.ingredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonListener buttonListener = (ButtonListener) holder.view.getContext();
                buttonListener.getIngredients(String.valueOf(holder.getAdapterPosition()), recipe.getRecipeId());
            }
        });
        holder.prepareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ButtonListener buttonListener = (ButtonListener) holder.view.getContext();
                buttonListener.showPreparation(recipe.getSourceUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        listOfRecipes = ListOfRecipes.getInstance();
        if (listOfRecipes.getSearchRecipes() == null) {
            return 0;
        } else {
            return listOfRecipes.getSearchRecipes().getCount();
        }
    }

    public interface ButtonListener {
        void getIngredients(String position, String id);
        void showPreparation(String url);
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, rankImage;
        TextView titleView, rankView, ingredView;
        Button prepareButton, ingredButton;
        View view;

        RecyclerViewHolder(View view) {
            super(view);
            this.view = view;
            imageView = (ImageView) view.findViewById(R.id.imageOfRecipe);
            rankImage = (ImageView) view.findViewById(R.id.rankIcon);
            titleView = (TextView) view.findViewById(R.id.titleOfRecipe);
            rankView = (TextView) view.findViewById(R.id.rank);
            prepareButton = (Button) view.findViewById(R.id.showPreparation);
            ingredView = (TextView)view.findViewById(R.id.ingredView);
            ingredButton = (Button)view.findViewById(R.id.igredButton);
        }
    }
}

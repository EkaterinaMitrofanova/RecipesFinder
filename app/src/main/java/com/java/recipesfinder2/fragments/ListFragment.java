package com.java.recipesfinder2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.recipesfinder2.R;
import com.java.recipesfinder2.adapters.RecyclerAdapter;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ListFragment extends Fragment{

    RecyclerAdapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        if (adapter == null) {
            adapter = new RecyclerAdapter();
        }
        recyclerView.setAdapter(adapter);
        if (position != 0) {
            linearLayoutManager.scrollToPosition(position);
        }
    }

    public void updateAdapter(){
        adapter.notifyDataSetChanged();
    }

    public void updateAdapter(String position){
        adapter.notifyItemChanged(Integer.valueOf(position));
    }

    @Override
    public void onDetach() {
        position = linearLayoutManager.findFirstVisibleItemPosition();
        super.onDetach();
    }
}

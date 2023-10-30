package com.example.cpen321mappost;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private ArrayList<String> tagList;
    private Set<String> tagSet;
    private static final String TAG = "TagAdapter";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBoxTag;

        public ViewHolder(View view) {
            super(view);

            checkBoxTag = view.findViewById(R.id.tag_name_id);

        }
    }

    public TagAdapter(ArrayList<String> tagList, Set<String> tagSet) {

        this.tagList = tagList;
        this.tagSet = tagSet;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_item, parent, false);
        return new TagAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {

        String tag = tagList.get(position);

        holder.checkBoxTag.setText(tag);

        holder.checkBoxTag.setOnCheckedChangeListener(null);
        holder.checkBoxTag.setChecked(tagSet.contains(tag));
        holder.checkBoxTag.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                tagSet.add(tag);

            } else {

                tagSet.remove(tag);

            }
        });

    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

}

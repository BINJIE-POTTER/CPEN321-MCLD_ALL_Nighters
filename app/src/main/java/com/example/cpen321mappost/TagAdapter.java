package com.example.cpen321mappost;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Set;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private final ArrayList<String> tagList;
    private final Set<String> tagSet;
    private static final String TAG = "TagAdapter";

    //ChatGPT usage: Partial
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBoxTag;

        public ViewHolder(View view) {
            super(view);

            checkBoxTag = view.findViewById(R.id.tag_name_id);

        }
    }

    //ChatGPT usage: No
    public TagAdapter(ArrayList<String> tagList, Set<String> tagSet) {

        this.tagList = tagList;
        this.tagSet = tagSet;

    }

    //ChatGPT usage: Partial
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_item, parent, false);
        return new TagAdapter.ViewHolder(itemView);
    }

    //ChatGPT usage: Partial
    @Override
    public void onBindViewHolder(@NonNull TagAdapter.ViewHolder holder, int position) {

        String tag = tagList.get(position);

        holder.checkBoxTag.setText(tag);

        holder.checkBoxTag.setOnCheckedChangeListener(null);
        holder.checkBoxTag.setChecked(tagSet.contains(tag));
        holder.checkBoxTag.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                Log.d(TAG, tag + " is selected.");

                tagSet.add(tag);

            } else {

                Log.d(TAG, tag + " is released.");

                tagSet.remove(tag);

            }
        });

    }

    //ChatGPT usage: No
    @Override
    public int getItemCount() {
        return tagList.size();
    }

}

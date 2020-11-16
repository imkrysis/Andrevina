package com.example.andrevina;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    static ArrayList<Record> recordsList;

    RecyclerViewAdapter(ArrayList<Record> recordsList) {

        this.recordsList = recordsList;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewRVNickname;
        public TextView textViewRVAttempts;
        public TextView textViewRVTimeInfo;
        public ImageView imageViewRVPhotoThumbnail;

        public ViewHolder(View itemView) {

            super(itemView);

            this.textViewRVNickname = itemView.findViewById(R.id.textViewRVNickname);
            this.textViewRVAttempts = itemView.findViewById(R.id.textViewRVAttempts);
            this.textViewRVTimeInfo = itemView.findViewById(R.id.textViewRVTimeInfo);
            this.imageViewRVPhotoThumbnail = itemView.findViewById(R.id.imageViewRVPhotoThumbnail);

        }

        public void setData(Record recordData) {

            this.textViewRVNickname.setText(recordData.getNickname());
            this.textViewRVAttempts.setText(String.valueOf(recordData.getAttempts()));
            this.textViewRVTimeInfo.setText(recordData.getTimeInfo());
            this.imageViewRVPhotoThumbnail.setImageBitmap(recordData.getPhotoBitmap());

        }

    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_records_list, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        holder.setData(recordsList.get(position));

    }

    @Override
    public int getItemCount() {

        return recordsList.size();

    }

}

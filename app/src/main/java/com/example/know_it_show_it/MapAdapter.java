package com.example.know_it_show_it;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.UserViewHolder>{
    private Map<String, Object> mData;
    public MapAdapter(Map<String, Object> data) {
        mData = data;
    }
    @NonNull
    @Override
    public MapAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapAdapter.UserViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.nickname.setText(getHashMapKeyFromIndex(mData, position));
    }

    private String getHashMapKeyFromIndex(Map<String, Object> mData, int index) {
        String key = null;
        HashMap<String, Object> hs = (HashMap<String, Object>) mData;
        int pos = 0;
        for (Map.Entry<String, Object> entry : hs.entrySet()) {
            if (index == pos) {
                key = entry.getKey();
            }
            pos++;
        }
        return key;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView nickname;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.user_list_item_nickname);
        }
    }
}

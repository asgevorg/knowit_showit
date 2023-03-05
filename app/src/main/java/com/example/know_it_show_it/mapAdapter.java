package com.example.know_it_show_it;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class mapAdapter extends RecyclerView.Adapter<mapAdapter.UserViewHolder>{
    private Map<String, Object> mData;

    public mapAdapter(Map<String, Object> data) {
        mData = data;
    }
    @NonNull
    @Override
    public mapAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mapAdapter.UserViewHolder holder, int position) {
        //Error not getting all
//        Log.i("TTTT", mData.get(holder.nickname).toString());
//        holder.nickname.setText(mData.get("nickname").toString());
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

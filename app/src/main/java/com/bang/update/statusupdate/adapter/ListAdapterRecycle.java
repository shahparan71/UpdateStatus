package com.bang.update.statusupdate.adapter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bang.update.statusupdate.R;
import com.bang.update.statusupdate.model.UpdateInformation;

import java.util.ArrayList;

/**
 * Created by Quoc Nguyen on 13-Dec-16.
 */

public class ListAdapterRecycle extends RecyclerView.Adapter<ListAdapterRecycle.ViewHolder>{

    private ArrayList<UpdateInformation> statusList;

    // RecyclerView recyclerView;
    public ListAdapterRecycle(ArrayList<UpdateInformation> statusList2) {
        this.statusList = statusList2;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UpdateInformation status = statusList.get(position);
        holder.textView.setText(status.getName());

        byte[] statusTemp = status.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(statusTemp, 0, statusTemp.length);
        holder.imageView.setImageBitmap(bitmap);
    }


    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.myImage);
            this.textView = (TextView) itemView.findViewById(R.id.status_update);
        }
    }
}


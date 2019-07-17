package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aponjon.lifechordlaunch.R;
import com.pojo.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.CustomMyOrder> {

    public Context context;
    public List<Post> postList;

    public MyOrderAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }


    @NonNull
    @Override
    public CustomMyOrder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, null);
        CustomMyOrder viewHolder = new CustomMyOrder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomMyOrder holder, int position) {
        Post post = postList.get(position);
        holder.name.setText("" + post.getLaunchDate());

        if (post.isAlreadySelect.equals("1")) {
            holder.designation.setText("Order success");
            holder.profileImage.setImageResource(R.drawable.ic_done);
        } else {
            holder.profileImage.setImageResource(R.drawable.ic_clear);
            holder.designation.setText("Order Cancel");
        }

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class CustomMyOrder extends RecyclerView.ViewHolder {

        @BindView(R.id.letter_image)
        ImageView profileImage;

        @BindView(R.id.contact_name)
        TextView name;

        @BindView(R.id.contact_number_item)
        TextView designation;

        public CustomMyOrder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

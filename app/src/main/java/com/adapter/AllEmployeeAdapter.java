package com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.aponjon.lifechordlaunch.R;
import com.pojo.Post;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AllEmployeeAdapter extends RecyclerView.Adapter<AllEmployeeAdapter.CustomEmployee> {

    public Context context;
    public List<Post> postList;

    public AllEmployeeAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public CustomEmployee onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_employee, null);
        CustomEmployee viewHolder = new CustomEmployee(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomEmployee holder, int position) {

        Post post = postList.get(position);
        holder.name.setText("" + post.getName());
        holder.designation.setText("" + post.getDesignation());

        String firstLetter = String.valueOf(post.getName().charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        //int color = generator.getColor(getItem(position));
        int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color);

        holder.profileImage.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    public class CustomEmployee extends RecyclerView.ViewHolder {

        @BindView(R.id.letter_image)
        ImageView profileImage;

        @BindView(R.id.contact_name)
        TextView name;

        @BindView(R.id.contact_number_item)
        TextView designation;

        public CustomEmployee(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

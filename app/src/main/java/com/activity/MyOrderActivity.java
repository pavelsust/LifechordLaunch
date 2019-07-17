package com.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.MyOrderAdapter;
import com.aponjon.lifechordlaunch.R;
import com.com.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pojo.Post;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MyOrderActivity extends AppCompatActivity {

    @BindView(R.id.order_recycleview)
    RecyclerView recyclerView;

    public ProgressDialog progressDialog;
    public MyOrderAdapter myOrderAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(MyOrderActivity.this);
        progressDialog.setMessage("Loading ...");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        databaseQuary();
    }


    public void databaseQuary() {
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        databaseReference
                .child(Constant.ORDER)
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Post> postList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);

                            /*
                            if (post.getLaunchDate().equals("" + getCurrentDate())) {
                                if (post.getIsAlreadySelect().equals("1")) {
                                    //postList.add(new Post("" + post.getName(), "" + post.getDesignation(), "" + post.getLaunchDate(), "" + post.getIsAlreadySelect(), "" + post.getUserID()));
                                    postList.add(post);

                                }
                            }
                            */
                            postList.add(post);
                        }

                        progressDialog.dismiss();
                        showDataIntoView(postList);


                        Timber.d("firebase data " + dataSnapshot.toString());
                        Timber.d("list data " + postList.toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }

    private void showDataIntoView(List<Post> postList) {
        myOrderAdapter = new MyOrderAdapter(getApplicationContext(), postList);
        recyclerView.setAdapter(myOrderAdapter);
    }

}

package com.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adapter.AllEmployeeAdapter;
import com.aponjon.lifechordlaunch.R;
import com.com.utils.Constant;
import com.com.utils.LoginState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pojo.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AllEmployeeActivity extends AppCompatActivity {

    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    public AllEmployeeAdapter allEmployeeAdapter;
    private DatabaseReference databaseReference;
    public ProgressDialog progressDialog;
    public LoginState loginState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_employee);
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

        loginState = new LoginState(getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(AllEmployeeActivity.this);
        progressDialog.setMessage("Loading ....");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseQuary();

    }


    public void databaseQuary() {
        progressDialog.show();
        databaseReference
                .child(Constant.DATA)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Post> postList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);

                            if (post.getLaunchDate().equals("" + convertToOnlyDate(loginState.getCurrentDate()))) {
                                if (post.getIsAlreadySelect().equals("1")) {
                                    postList.add(post);

                                }
                            }

                        }

                        Timber.d("data: " + dataSnapshot.toString());

                        progressDialog.dismiss();
                        Timber.d("data: " + postList.toString());
                        dataSetIntoAdapter(postList);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }


    public void dataSetIntoAdapter(List<Post> postList) {
        allEmployeeAdapter = new AllEmployeeAdapter(getApplicationContext(), postList);
        recyclerView.setAdapter(allEmployeeAdapter);
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd hh:mm a zzz");
        Date date = new Date();
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+6:00"));
        return sdf.format(date);
    }

    public String convertToOnlyDate(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String dateString = "";
        try {
            date = dateFormat.parse(currentDate);
            System.out.println(date.toString()); // Wed Dec 04 00:00:00 CST 2013

            dateString = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Timber.d("main date " + loginState.getCurrentDate());
        Timber.d("Convert date: " + dateString);
        return dateString;
    }

}

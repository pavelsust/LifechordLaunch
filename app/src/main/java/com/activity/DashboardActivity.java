package com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aponjon.lifechordlaunch.R;
import com.com.utils.Constant;
import com.com.utils.LoginState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pojo.MyResult;
import com.pojo.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private LoginState loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loginState = new LoginState(getApplicationContext());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postNewData();
            }
        });

        databaseQuary();
    }

    private void postNewData() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = databaseReference.push().getKey();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        Post post = new Post("" + loginState.getDataFromSharedPreferance(Constant.NAME), "" + loginState.getDataFromSharedPreferance(Constant.DESIGNATION), "" + currentDateandTime, "0" , userID);
        databaseReference
                .child(Constant.DATA)
                .child(key)
                .setValue(post)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d("" + e.toString());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(DashboardActivity.this, "Success", Toast.LENGTH_LONG).show();
                        } else {
                            Timber.d("" + task.getException().toString());
                        }
                    }
                });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_log_out:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
                loginState.saveDataIntoSharePreferance(Constant.IS_LOGIN, false);
                finish();
                return true;

            case R.id.menu_get_data:
                databaseQuary();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void databaseQuary() {

        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference
                .child(Constant.DATA).orderByChild("userID").equalTo(""+userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Post> postArrayList = new ArrayList<>();

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);
                            postArrayList.add(post);
                        }

                        Timber.d("firebase data: "+dataSnapshot.toString());
                        Timber.d("list data"+postArrayList.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

}

package com.activity;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aponjon.lifechordlaunch.R;
import com.com.utils.CheckPermission;
import com.com.utils.Constant;
import com.com.utils.LoginState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pojo.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = DashboardActivity.class.getSimpleName();
    private DatabaseReference databaseReference;
    private LoginState loginState;

    Animation atg, atgtwo, atgthree;

    @BindView(R.id.food_image)
    ImageView foodImage;

    @BindView(R.id.pagesubtitle)
    TextView pageSubtitle;

    @BindView(R.id.button_launch_order)
    Button launchOrder;

    @BindView(R.id.food_order_title)
    TextView foodOrderTitle;
    public boolean isLaunchOrder = false;

    public ProgressDialog progressDialog;

    @BindView(R.id.layout_log_out)
    LinearLayout logOut;

    @BindView(R.id.text_user_name)
    TextView userName;

    @BindView(R.id.text_user_designation)
    TextView userDesignation;
    public CheckPermission checkPermission;
    LocationManager locationManager;

    boolean isGPS = false;
    boolean isNetwork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        loginState = new LoginState(getApplicationContext());
        progressDialog = new ProgressDialog(DashboardActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        checkPermission = new CheckPermission(getApplicationContext());

        atg = AnimationUtils.loadAnimation(this, R.anim.atg);
        atgtwo = AnimationUtils.loadAnimation(this, R.anim.atgtwo);
        atgthree = AnimationUtils.loadAnimation(this, R.anim.atgthree);

        if (loginState.getDataFromSharedPreferance(Constant.NAME) != null && !loginState.getDataFromSharedPreferance(Constant.NAME).isEmpty()) {
            userName.setText("" + loginState.getDataFromSharedPreferance(Constant.NAME));
        }

        if (loginState.getDataFromSharedPreferance(Constant.DESIGNATION) != null && !loginState.getDataFromSharedPreferance(Constant.DESIGNATION).isEmpty()) {
            userDesignation.setText("" + loginState.getDataFromSharedPreferance(Constant.DESIGNATION));
        }

        databaseQuary();
    }


    @OnClick(R.id.layout_order)
    public void openOrder() {
        startActivity(new Intent(DashboardActivity.this, MyOrderActivity.class));
    }


    @OnClick(R.id.layout_log_out)
    public void logout() {
        loginState.clearSharedPreferance();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
        finish();
    }


    @OnClick(R.id.button_launch_order)
    public void orderButtonClick() {

        Timber.d("Current date: " + loginState.getCurrentDate());
        Timber.d("Fixed date: " + loginState.finalDate());
        Timber.d("Result date: " + loginState.compareTwoDate());


        if (loginState.compareTwoDate()) {
            if (!isLaunchOrder) {
                postNewData();
                createOrderList("1");
            } else {
                updateData("0");
                createOrderList("0");
            }

        } else {
            showDialog();
        }
    }

    @OnClick(R.id.layout_ell_employee)
    public void allEmployee() {
        startActivity(new Intent(DashboardActivity.this, AllEmployeeActivity.class));
    }

    public void postNewData() {
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //String key = databaseReference.push().getKey();

        String date = convertToOnlyDate(loginState.getCurrentDate());

        Timber.d("convert date main: " + date);

        Post post = new Post("" + loginState.getDataFromSharedPreferance(Constant.NAME), "" + loginState.getDataFromSharedPreferance(Constant.DESIGNATION), "" +date , "1", userID);
        databaseReference
                .child(Constant.DATA)
                .child(userID)
                .setValue(post)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Timber.d("" + e.toString());
                        progressDialog.dismiss();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            isLaunchOrder = true;
                            updateUI();
                        } else {
                            isLaunchOrder = false;
                            updateUI();
                            Timber.d("" + task.getException().toString());
                        }
                    }
                });

    }


    private void createOrderList(String order) {
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String key = databaseReference.push().getKey();
        Post post = new Post("" + convertToAm(loginState.getCurrentDate()), order);

        databaseReference
                .child(Constant.ORDER)
                .child(userID)
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
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        databaseReference
                .child(Constant.DATA)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Post post = dataSnapshot1.getValue(Post.class);

                            if (post.getUserID().equals("" + userID)) {
                                if (post.getLaunchDate().equals("" + loginState.getCurrentDate())) {
                                    if (post.getIsAlreadySelect().equals("1")) {
                                        isLaunchOrder = true;
                                    } else {
                                        isLaunchOrder = false;
                                    }
                                } else {
                                    isLaunchOrder = false;
                                }
                            }
                        }
                        updateUI();
                        Timber.d("firebase data: "+dataSnapshot.toString());
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });
    }


    private void updateData(String order) {
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child(Constant.DATA).child(userID).child("isAlreadySelect").setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    isLaunchOrder = false;
                    updateUI();
                }
                progressDialog.dismiss();
            }
        });
    }

    public void updateUI() {
        if (isLaunchOrder) {
            foodOrderTitle.setText("Enjoy your launch");
            launchOrder.setText("Cancel My Launch");
        } else {
            foodOrderTitle.setText("You haven't booked your today's launch");
            launchOrder.setText("Book My Launch");
        }
    }

    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Notice");
        alertDialogBuilder.setMessage("Your can't change order after 10:45 AM");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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


    public String convertToAm(String currentDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date date;
        String dateString = null;
        try {
            date = dateFormat.parse(currentDate);
            System.out.println(date.toString()); // Wed Dec 04 00:00:00 CST 2013

            dateString = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateString;
    }
}

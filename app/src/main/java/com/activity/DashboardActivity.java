package com.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.ActivityCompat;

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
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DashboardActivity extends AppCompatActivity implements LocationListener {

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
    public static final int PERMISSION_REQUEST_CODE = 200;
    public static String gpsTimeDate;


    LocationManager locationManager;
    Location loc;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;


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

        if (checkPermission.isPermissionGranted()) {
            getLocation();
        } else {
            requestForPermission();
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
        try {
            if (compareDate()) {
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
        } catch (ParseException e) {
            e.printStackTrace();
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
        Post post = new Post("" + loginState.getDataFromSharedPreferance(Constant.NAME), "" + loginState.getDataFromSharedPreferance(Constant.DESIGNATION), "" + getCurrentDate(), "1", userID);
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
                            Toast.makeText(DashboardActivity.this, "Success", Toast.LENGTH_LONG).show();
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
        Post post = new Post("" + getCurrentDateAndTime(), order);

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
                                if (post.getLaunchDate().equals("" + getCurrentDate())) {
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

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    private String getCurrentDateAndTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String currentDate = sdf.format(new Date());
        return currentDate;
    }

    public void updateUI() {
        if (isLaunchOrder) {
            foodOrderTitle.setText("Your order complete");
            launchOrder.setText("Cancel Order");
        } else {
            foodOrderTitle.setText("Order is not placed yet");
            launchOrder.setText("Launch Order");
        }
    }

    public boolean compareDate() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        Date currentDateTime = sdf.parse(gpsTimeDate);
        Date setDateTime = sdf.parse(setDateTime());

        Timber.d("gps time: " + gpsTimeDate);

        if (currentDateTime.before(setDateTime)) {
            return true;
        } else {
            return false;
        }
    }

    private String setDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(new Date());
        return currentDate + " 03:35 PM";
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

    public void requestForPermission() {
        ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                        showMessageOKCancel("You need to allow access to both the permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS,
                                                            Manifest.permission.READ_PHONE_STATE,
                                                            Manifest.permission.READ_CALL_LOG,
                                                            Manifest.permission.MODIFY_PHONE_STATE,
                                                            Manifest.permission.CALL_PHONE},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    }
                                });
                        return;
                    }
                }
        }

        if (checkPermission.isPermissionGranted()) {
            getLocation();
        } else {
            Toasty.error(getApplicationContext(), "Need location permission", Toast.LENGTH_SHORT, false).show();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.app.AlertDialog.Builder(getApplicationContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void getLocation() {
        progressDialog.show();
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            DashboardActivity.MIN_TIME_BW_UPDATES,
                            DashboardActivity.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            progressDialog.dismiss();
                        upDateTime(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            progressDialog.dismiss();
                        upDateTime(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    progressDialog.dismiss();
                    upDateTime(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //updateUI(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public void upDateTime(Location location) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String currentDate = sdf.format(location.getTime());
        Timber.d("time " + currentDate);
        gpsTimeDate = currentDate;
    }
}

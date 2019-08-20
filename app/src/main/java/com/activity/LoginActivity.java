package com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.aponjon.lifechordlaunch.R;
import com.com.utils.Constant;
import com.com.utils.LoginState;
import com.com.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pojo.MyUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.password)
    EditText inputPassword;
    @BindView(R.id.btn_login)
    AppCompatButton btnLogin;
    @BindView(R.id.btn_reset_password)
    AppCompatButton btnResetPassword;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    FirebaseAuth auth;

    public DatabaseReference databaseReference;
    public LoginState loginState;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Log In");
        auth = FirebaseAuth.getInstance();
        loginState = new LoginState(getApplicationContext());
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading ...");
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USER);


        if (isNetworkAvailable(getApplicationContext())) {
            if (loginState.getBooleanDataFromSharedPreferance(Constant.IS_LOGIN)) {
                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            showDialog();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utils.isValidText(inputEmail) && Utils.isValidText(inputPassword)) {
                    String email = inputEmail.getText().toString();
                    final String password = inputPassword.getText().toString();

                    progressDialog.show();

                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            inputPassword.setError(getString(R.string.minimum_password));
                                        } else {
                                            Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        getUserData();
                                    }
                                }
                            });
                }
            }
        });
    }

    public void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Alert!!");
        alertDialogBuilder.setMessage("You have no internet connection.");
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @OnClick(R.id.btn_signup)
    public void openRegistrationActivity() {
        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(intent);
    }

    private void getUserData() {
        progressDialog.show();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference.child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        MyUser myUser = dataSnapshot.getValue(MyUser.class);

                        Timber.d("name" + myUser.getName());
                        Timber.d("designation: " + myUser.getName());

                        loginState.saveDataIntoSharePreferance(Constant.IS_LOGIN, true);
                        loginState.saveDataIntoSharePreferance(Constant.NAME, "" + myUser.getName());
                        loginState.saveDataIntoSharePreferance(Constant.DESIGNATION, "" + myUser.getDesignation());

                        progressDialog.dismiss();

                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public boolean isNetworkAvailable(Context ctxContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctxContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnected()
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE).isConnected()) {
            return true;
        }
        return false;
    }

    @OnClick(R.id.btn_reset_password)
    public void showForgotPasswordDialog() {
        // load the dialog_promt_user.xml layout and inflate to view
        LayoutInflater layoutinflater = LayoutInflater.from(LoginActivity.this);
        View promptUserView = layoutinflater.inflate(R.layout.dialog_prompt_user, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
                .setView(promptUserView)
                .setTitle("Enter your email address")
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();


        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog) {
                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText userEmail = (EditText) alertDialog.findViewById(R.id.username);
                        if (userEmail.getText().toString().length() > 0) {
                            sendResetEmail(alertDialog, userEmail.getText().toString());
                        } else {
                            userEmail.setError("please fill this");
                        }
                    }
                });
            }
        });
        alertDialog.show();

    }

    public void sendResetEmail(AlertDialog alertDialog, String email) {
        progressDialog.show();
        auth.sendPasswordResetEmail(email)

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT, false).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            alertDialog.dismiss();
                            Toasty.success(getApplicationContext(), "Check your email", Toast.LENGTH_SHORT, false).show();
                        }
                    }
                });


    }
}

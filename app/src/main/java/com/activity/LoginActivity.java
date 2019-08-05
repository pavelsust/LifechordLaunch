package com.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.aponjon.lifechordlaunch.R;
import com.com.utils.Constant;
import com.com.utils.LoginState;
import com.com.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
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

        if (loginState.getBooleanDataFromSharedPreferance(Constant.IS_LOGIN)) {
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
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

}

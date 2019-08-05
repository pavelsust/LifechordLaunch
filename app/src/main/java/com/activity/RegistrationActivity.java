package com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.database.FirebaseDatabase;
import com.pojo.MyUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.password)
    EditText inputPassword;
    @BindView(R.id.sign_up_button)
    AppCompatButton buttonSignUp;
    @BindView(R.id.btn_reset_password)
    AppCompatButton btnResetPassword;
    @BindView(R.id.sign_in_button)
    AppCompatButton buttonSignIn;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.registration_name)
    EditText inputName;
    @BindView(R.id.registration_designation)
    EditText inputDesignation;
    private FirebaseAuth mAuth;
    public LoginState loginState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Registration");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        loginState = new LoginState(getApplicationContext());
    }

    @OnClick(R.id.sign_up_button)
    public void registerUser() {
        if (isCheckValid()) {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                MyUser user = new MyUser(
                                        inputName.getText().toString(),
                                        inputEmail.getText().toString(),
                                        inputDesignation.getText().toString()
                                );

                                FirebaseDatabase.getInstance().getReference(Constant.USER)
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressBar.setVisibility(View.GONE);
                                        if (task.isSuccessful()) {
                                            openDashboard(inputName.getText().toString(), inputDesignation.getText().toString());
                                        } else {
                                            //display a failure message
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    private void openDashboard(String name, String designation) {
       loginState.saveDataIntoSharePreferance(Constant.IS_LOGIN, true);
        loginState.saveDataIntoSharePreferance(Constant.NAME, "" + name);
        loginState.saveDataIntoSharePreferance(Constant.DESIGNATION, "" + designation);
        Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean isCheckValid() {
        boolean err = false;
        err = Utils.isValidText(inputEmail);
        err &= Utils.isValidText(inputPassword);
        err &= Utils.isValidText(inputName);
        err &= Utils.isValidText(inputDesignation);
        return err;
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //FirebaseAuth.getInstance().signOut();
    }
}

package com.spectators.hungerfree;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ChangePassword extends AppCompatActivity  implements View.OnClickListener{

    private TextInputLayout oldPassLayout;
    private TextInputLayout newPassLayout;
    private TextInputLayout confirmPassLayout;

    private TextInputEditText oldPassText;
    private TextInputEditText newPassText;
    private TextInputEditText confirmPassText;

    private AppCompatButton changePassButton;

    private NestedScrollView nestedScrollView;
    private AppCompatTextView changePassBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().hide();

        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);

        oldPassLayout = (TextInputLayout) findViewById(R.id.oldPassLayout);
        newPassLayout = (TextInputLayout) findViewById(R.id.newPassLayout);
        confirmPassLayout = (TextInputLayout) findViewById(R.id.confirmPassLayout);

        oldPassText = (TextInputEditText) findViewById(R.id.oldPassText);
        newPassText = (TextInputEditText) findViewById(R.id.newPassText);
        confirmPassText = (TextInputEditText) findViewById(R.id.confirmPassText);

        changePassButton = (AppCompatButton) findViewById(R.id.changePassButton);
        changePassBack = (AppCompatTextView) findViewById(R.id.changePassBack);

        changePassButton.setOnClickListener(this);
        changePassBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.changePassButton:
                changePass();
                break;
            case R.id.changePassBack:
                Intent intentRegister = new Intent(getApplicationContext(), DashboardActivity.class);
                startActivity(intentRegister);
                break;
        }
    }

    private String TAG = "myApp";
    private DatabaseReference database;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user;
    private String password;
    private String newPass;
    private String confirmPass;

    private void changePass(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        password = oldPassText.getText().toString();
        newPass = newPassText.getText().toString();
        confirmPass = confirmPassText.getText().toString();

        mAuth.signInWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(newPass.equals(confirmPass)){
                                if(newPass.length() < 8){
                                    Snackbar.make(nestedScrollView, "Password should be atleast 8 characters.", Snackbar.LENGTH_LONG).show();
                                    emptyInputEditText();
                                    return;
                                }
                                change();
                            }
                            else{
                                Snackbar.make(nestedScrollView, "Passwords do not match.", Snackbar.LENGTH_LONG).show();
                                emptyInputEditText();
                                return;
                            }
                        } else {
                            Snackbar.make(nestedScrollView, "Current Password doesn't match.", Snackbar.LENGTH_LONG).show();
                            emptyInputEditText();
                            return;
                        }
                    }
                });
    }

    private void change(){
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                        Snackbar.make(nestedScrollView, "Password updated.", Snackbar.LENGTH_LONG).show();
                                        emptyInputEditText();
                                        return;
                                    } else {
                                        Log.d(TAG, "Error updating password");
                                        Snackbar.make(nestedScrollView, "Error Updating Password.", Snackbar.LENGTH_LONG).show();
                                        emptyInputEditText();
                                        return;
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                            Snackbar.make(nestedScrollView, "Error Updating Password.", Snackbar.LENGTH_LONG).show();
                            emptyInputEditText();
                            return;
                        }
                    }
                });
    }

    private void emptyInputEditText() {
        oldPassText.setText(null);
        newPassText.setText(null);
        confirmPassText.setText(null);
    }

}

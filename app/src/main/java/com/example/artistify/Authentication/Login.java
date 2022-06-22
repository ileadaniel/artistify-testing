package com.example.artistify.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.artistify.Admin.MainActivity;
import com.example.artistify.ContestCreator.ContestCreator;
import com.example.artistify.Evaluator.Evaluator;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.NormalUser.NormalUser;
import com.example.artistify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private Button login_btn;
    private TextView register_btn,forgotPassword;
    private EditText editTextEmail,editTextPassword;

    private ProgressBar progressBar;

    private FirebaseAuth myAuth;
    private DatabaseReference reference;


    private String userID;

    private FirebaseUser firebaseUser;


    //    @Override
//    public void onBackPressed() {
//
//        startActivity(new Intent(Login.this,Welcome.class));
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        register_btn =(TextView) findViewById(R.id.register_btn);
        login_btn=(Button)findViewById(R.id.login_btn);

        progressBar=(ProgressBar)findViewById(R.id.progressBarLogin);

        login_btn.setOnClickListener(this);

        register_btn.setOnClickListener(this);

        myAuth=FirebaseAuth.getInstance();

        editTextEmail=(EditText)findViewById(R.id.editTextTextEmailAddress);
        editTextPassword=(EditText)findViewById(R.id.editTextTextPassword);
        forgotPassword = (TextView)findViewById(R.id.forgot_password);

        forgotPassword.setOnClickListener(this);
        reference = FirebaseDatabase.getInstance().getReference("Users");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_btn:
                startActivity(new Intent(this, Register.class));
                break;
            case R.id.login_btn:
                userLogin();
                break;

            case R.id.forgot_password:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
        }
    }

    private void userLogin() {
        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();


        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length()<6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();

                    if(user.isEmailVerified()){
                        //redirect to user profile
                        //startActivity(new Intent(Login.this,UploadPicture.class));
                        userID = user.getUid();
                        getSignedInUserProfile();
                    }
                    else
                    {
                        progressBar.setVisibility(View.GONE);
                        user.sendEmailVerification();
                        Toast.makeText(Login.this,"Check your email to verify your account",Toast.LENGTH_LONG).show();
                    }


                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void getSignedInUserProfile() {

        DatabaseReference newReference = reference;//

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseUser actualFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert actualFirebaseUser != null;
            userID = actualFirebaseUser.getUid();

            newReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if(userProfile!=null){
                        //String fullName=userProfile.fullName;
                        //String email=userProfile.email;
                        int user_level = userProfile.authentication_level;
                        String user_status=userProfile.status;

                        // emailTextView.setText(email);
                        //fullNameTextView.setText(fullName);


                        if(user_level==0){
                            if(user_status.equals("Approved")){
                                Intent intent = new Intent(Login.this, NormalUser.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(Login.this, UserDefault.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                        else if(user_level==1){
                            if(user_status.equals("Approved")){
                                Intent intent = new Intent(Login.this, ContestCreator.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(Login.this,UserDefault.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                        else if(user_level==2){
                            if(user_status.equals("Approved")){
                                Intent intent = new Intent(Login.this, Evaluator.class);
                                // Intent intent = new Intent(Login.this,UploadPicture.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(Login.this,UserDefault.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                        else if(user_level==3){
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this,"Something wrong happened!",Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}


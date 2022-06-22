package com.example.artistify.Authentication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.example.artistify.TestInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private Button register_button;
    private EditText full_nameText, emailText, passwordText;
    private TextView need_help;
    private ProgressBar progressBar;

    private int auth_level;
    private String user_pic_url="https://firebasestorage.googleapis.com/v0/b/artistify-2f529.appspot.com/o/1618408780232.png?alt=media&token=c7373b87-19ea-4b42-ace9-d8fe90a5f461";
    private String cover_pic="https://firebasestorage.googleapis.com/v0/b/artistify-2f529.appspot.com/o/1618669658267.jpg?alt=media&token=22eb2430-432f-4100-afdf-fd5ee0e5d6b7";
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        register_button=findViewById(R.id.register_btn2);
        need_help=findViewById(R.id.need_help_register);
        progressBar=findViewById(R.id.progressBarRegister);

        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Register.this,
                R.layout.color_spinner_layout,getResources().getStringArray(R.array.login_levels));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);


        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        auth_level = 0;
                        break;
                    case 1:
                        auth_level = 1;
                        break;
                    case 2:
                        auth_level = 2;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        need_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog();
            }

            private void showHelpDialog() {
                AlertDialog.Builder builder=new AlertDialog.Builder(need_help.getContext(),R.style.MyDialogTheme);
                builder.setTitle("Registration information!");
                builder.setMessage("Welcome to Artistify, the art contest platform.\n\n" +
                        "Here are some tips and information for you:\n\n" +
                        "1. You can create an account using one of the 3 types of users (Normal user, Contest creator, Evaluator).\n\n" +
                        "2. If you want to compete with other artists around the world who are in the Artistify app you can assign yourself the Normal user role.\n" +
                        "Your account will be approved right away after you finish the whole registration procedure.\n\n" +
                        "3. If you want to create and hold contests you can assign yourself the Contest creator role.\n" +
                        "You will be redirected to a section where you will see your current status, your account will be reviewed by admin and the status will be updated.\n\n" +
                        "4. Same thing if you want to be an evaluator.\n\n" +
                        "5. For additional information, you can send an email to:\nartistify.official@gmail.com");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //builder.show();


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                alertDialog.getWindow().setAttributes(layoutParams);
            }
        });


        register_button.setOnClickListener(this);

        full_nameText=(EditText)findViewById(R.id.editTextTextFullName);
        emailText=(EditText)findViewById(R.id.editTextTextEmailAddress);
        passwordText=(EditText)findViewById(R.id.editTextTextPassword);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register_btn2:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String email=emailText.getText().toString().trim();
        String password=passwordText.getText().toString().trim();
        String full_name=full_nameText.getText().toString().trim();

        if(full_name.isEmpty()){
            full_nameText.setError("Full name is required!");
            full_nameText.requestFocus();
            return;
        }

        if(email.isEmpty()){
            emailText.setError("Email is required!");
            emailText.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please provide valid email!");
            emailText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordText.setError("Password is required!");
            passwordText.requestFocus();
            return;
        }

        if(password.length()<6){
            passwordText.setError("Min password length should be 6 characters!");
            passwordText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        long startTime = System.nanoTime();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(auth_level==1||auth_level==2){
                                status="Pending";
                            }
                            else if(auth_level==0){
                                status="Approved";
                            }
                            User user = new User(full_name,email,auth_level,user_pic_url,cover_pic,status);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(Register.this,"You have been registered successfully!",Toast.LENGTH_LONG).show();
                                        long elapsedTime = System.nanoTime() - startTime;
                                        TestInfo.showTestUserInfoLog("Test user sign up processing time",elapsedTime/1000000);
                                        FirebaseAuth.getInstance().signOut();
                                        startActivity(new Intent(Register.this, Login.class));
                                    }
                                    else{
                                        Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });


                        }
                        else{
                            Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });


    }
}
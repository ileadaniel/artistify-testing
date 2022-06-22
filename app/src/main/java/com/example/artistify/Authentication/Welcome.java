package com.example.artistify.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.artistify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Welcome extends AppCompatActivity {

    Button loginButton;
    TextView register_btn,textview_register;
    private DatabaseReference reference;
    private ProgressBar progressBar;

    private long backPressedTime;
    private Toast backToast;


    @Override
    public void onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()){
            backToast.cancel();

            moveTaskToBack(true);
            //android.os.Process.killProcess(android.os.Process.myPid());  //not necessary
            finish();
            System.exit(1);
        }
        else{
            backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        register_btn=findViewById(R.id.register_btn1);
        loginButton=findViewById(R.id.login_btn);
        textview_register=findViewById(R.id.textView);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        loginButton.setVisibility(View.GONE);
        textview_register.setVisibility(View.GONE);
        register_btn.setVisibility(View.GONE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); //set window not clickable

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");


        if(firebaseUser==null) {
            progressBar.setVisibility(View.GONE);

            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE); //set window clickable

            loginButton.setVisibility(View.VISIBLE);
            textview_register.setVisibility(View.VISIBLE);
            register_btn.setVisibility(View.VISIBLE);
        }


        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
                    public void onClick(View view){
                Intent intent = new Intent(Welcome.this, Login.class);
                startActivity(intent);
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Welcome.this, Register.class);
                startActivity(intent);
            }
        });
    }
}
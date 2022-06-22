package com.example.artistify.Authentication;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.artistify.Admin.MainActivity;
import com.example.artistify.ContestCreator.ContestCreator;
import com.example.artistify.Evaluator.Evaluator;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.NormalUser.NormalUser;
import com.example.artistify.TestInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home  extends Application {

    private DatabaseReference reference;

    private String userID;


    @Override
    public void onCreate() {
        super.onCreate();


        TestInfo.showLog();

        long startTime = System.nanoTime();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        if(firebaseUser!=null){

            if(firebaseUser.isEmailVerified()){
               // Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                userID = firebaseUser.getUid();

            reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);

                    if(userProfile!=null){
                        //String fullName=userProfile.fullName;
                        //String email=userProfile.email;
                        int user_level = userProfile.getAuthentication_level();
                        String user_status=userProfile.getStatus();

                        if(user_level==0){
                            if(user_status.equals("Approved")){
                                Intent intent = new Intent(Home.this, NormalUser.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(Home.this, UserDefault.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            long elapsedTime = System.nanoTime() - startTime;

                            TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

                        }
                        else if(user_level==1){
                            if(user_status.equals("Approved")){
                                Intent intent = new Intent(Home.this, ContestCreator.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(Home.this,UserDefault.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            long elapsedTime = System.nanoTime() - startTime;

                            TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

                        }
                        else if(user_level==2){
                            if(user_status.equals("Approved")){
                                Intent intent = new Intent(Home.this, Evaluator.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(Home.this,UserDefault.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            long elapsedTime = System.nanoTime() - startTime;

                            TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

                        }
                        else if(user_level==3){
                            long elapsedTime = System.nanoTime() - startTime;

                            TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

                            Intent intent = new Intent(Home.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Home.this,"Something wrong happened!",Toast.LENGTH_LONG).show();
                    long elapsedTime = System.nanoTime() - startTime;

                    TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

                }
            });
            }else{
                long elapsedTime = System.nanoTime() - startTime;

                TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

            }

        }
        else{
            long elapsedTime = System.nanoTime() - startTime;

            TestInfo.showTestUserInfoLog("Test if user logged in and details",elapsedTime/1000000);

        }


    }
}

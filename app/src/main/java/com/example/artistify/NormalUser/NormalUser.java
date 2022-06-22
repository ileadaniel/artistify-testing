package com.example.artistify.NormalUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.artistify.Authentication.UserDefault;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.example.artistify.UploadPicture;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NormalUser extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    private FirebaseUser user;
    private String userID="";
    private DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users");


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
        setContentView(R.layout.activity_normal_user);

        BottomNavigationView bottomNavigationViewUser = findViewById(R.id.bottomNavigationViewUser);

        NavController navController = Navigation.findNavController(this, R.id.fragment2);

        NavigationUI.setupWithNavController(bottomNavigationViewUser, navController);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.contestsUserFragment, R.id.exhibitionsUserFragment, R.id.profileUserFragment)
                .build();

        user= FirebaseAuth.getInstance().getCurrentUser();
        userID=user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user1=snapshot.getValue(User.class);
                if(user1!=null){
                    String status=user1.getStatus();
                    if(status.equals("Rejected")||status.equals("Banned")){
                        finish();
                        Intent intent=new Intent(NormalUser.this, UserDefault.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
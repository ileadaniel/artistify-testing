package com.example.artistify.ContestCreator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.widget.Toast;

import com.example.artistify.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContestCreator extends AppCompatActivity {


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
        setContentView(R.layout.activity_contest_creator);

        BottomNavigationView bottomNavigationViewContCreator = findViewById(R.id.bottomNavigationViewContestCreator);

        NavController navController = Navigation.findNavController(this, R.id.fragment3);

        NavigationUI.setupWithNavController(bottomNavigationViewContCreator, navController);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.contestsContCreatorFragment, R.id.exhibitionsContCreatorFragment, R.id.profileContCreatorFragment)
                .build();
    }
}
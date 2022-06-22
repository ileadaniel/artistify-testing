package com.example.artistify.ContestEntries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.artistify.Admin.MainActivity;
import com.example.artistify.ContestCreator.ContestCreator;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewEntriesCreatorAdmin extends AppCompatActivity {

    RecyclerView recyclerView;
    ViewEntriesCreatorAdminAdapter viewEntriesCreatorAdminAdapter;
    private DatabaseReference user_info=FirebaseDatabase.getInstance().getReference().child("Users");
    List<ContestEntry> imageList;

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    @Override
    public void onBackPressed() {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID=firebaseUser.getUid();
        user_info.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    int auth_level=user.authentication_level;
                    if(auth_level==1){
                        finish();
                        Intent intent=new Intent(ViewEntriesCreatorAdmin.this, ContestCreator.class);
                        startActivity(intent);
                    }
                    else if(auth_level==3){
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_entries_creator);

        recyclerView=findViewById(R.id.myContest_creator_entries_RecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList=new ArrayList<>();

        viewEntriesCreatorAdminAdapter = new ViewEntriesCreatorAdminAdapter(this,imageList);
        recyclerView.setAdapter(viewEntriesCreatorAdminAdapter);

        Intent intent = getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);
        contestEntries(message);
    }

    private void contestEntries(String message) {
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Contest_entries");
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ContestEntry contestEntry = snapshot1.getValue(ContestEntry.class);
                    {
                        assert contestEntry != null;
                        if(contestEntry.getContestID().equals(message)&&contestEntry.getEntry_status().equals("Approved")&&contestEntry.getOffTopic_count()<3){
                            imageList.add(contestEntry);
                        }
                    }
                    Collections.sort(imageList, new Comparator<ContestEntry>() {
                        @Override
                        public int compare(ContestEntry o1, ContestEntry o2) {
                            return o1.getOrder()-o2.getOrder();
                        }
                    });
                    //Collections.reverse(imageList);
                    viewEntriesCreatorAdminAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
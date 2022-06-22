package com.example.artistify.ContestCreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.artistify.ContestEntries.ViewEntriesCreatorAdmin;
import com.example.artistify.ContestEntries.ViewEntriesCreatorAdminAdapter;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContestReportsCreator extends AppCompatActivity {


    RecyclerView recyclerView;
    ViewReportsCreatorAdapter viewReportsCreatorAdapter;
    List<ContestEntry> imageList;

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    @Override
    public void onBackPressed() {
        finish();
        Intent intent=new Intent(ContestReportsCreator.this, ContestCreator.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest_reports_creator);

        recyclerView=findViewById(R.id.myContest_creator_reports_RecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList=new ArrayList<>();

        viewReportsCreatorAdapter = new ViewReportsCreatorAdapter(this,imageList);
        recyclerView.setAdapter(viewReportsCreatorAdapter);

        Intent intent = getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);
        contestReports(message);
    }

    private void contestReports(String message) {
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Contest_entries");
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ContestEntry contestEntry = snapshot1.getValue(ContestEntry.class);
                    {
                        assert contestEntry != null;
                        if(contestEntry.getContestID().equals(message)&&(contestEntry.getOffTopic_count()==3)){
                            imageList.add(contestEntry);
                        }
                    }
                    Collections.reverse(imageList);
                    viewReportsCreatorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
package com.example.artistify.ContestCreator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

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
import java.util.Comparator;
import java.util.List;

public class UserVoteRank extends AppCompatActivity {


    RecyclerView recyclerView;
    ViewVoteRankAdapter viewVoteRankAdapter;
    List<ContestEntry> imageList;

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vote_rank);

        recyclerView=findViewById(R.id.myUsers_vote_rank_RecycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList=new ArrayList<>();

        viewVoteRankAdapter = new ViewVoteRankAdapter(this,imageList);
        recyclerView.setAdapter(viewVoteRankAdapter);

        Intent intent = getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);
        usersRank(message);
    }

    private void usersRank(String message) {
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
                            return o2.getVote_count()-o1.getVote_count();
                        }
                    });
                    //Collections.reverse(imageList);
                    viewVoteRankAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
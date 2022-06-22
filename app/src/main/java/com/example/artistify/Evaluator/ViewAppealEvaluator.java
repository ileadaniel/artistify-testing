package com.example.artistify.Evaluator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.example.artistify.ContestEntries.ViewEntriesEvaluatorUserAdapter;
import com.example.artistify.ModelClasses.AppealRequest;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.Contests;
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

public class ViewAppealEvaluator extends AppCompatActivity {


    RecyclerView recyclerView1;
    RecyclerView recyclerView2;

    ViewEntriesEvaluatorUserAdapter viewPendingAppealsAdapter;
    List<ContestEntry> pendingList;
    ViewEntriesEvaluatorUserAdapter viewReviewedAppealsAdapter;
    List<ContestEntry> reviewedList;

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    private FirebaseUser firebaseUser;
    private String userID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appeal_evaluator);

        Intent intent = getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);
        String[] message_received = message.split(",");

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userID=firebaseUser.getUid();

        recyclerView1=findViewById(R.id.myAppeal_view_evaluator_RecycleView);
        recyclerView1.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,2);
        recyclerView1.setLayoutManager(linearLayoutManager);

        pendingList=new ArrayList<>();

        viewPendingAppealsAdapter= new ViewEntriesEvaluatorUserAdapter(this,pendingList);
        recyclerView1.setAdapter(viewPendingAppealsAdapter);

        pendingAppeals(message_received[0],"Pending");

        //--------------------------------------------------------------------------------//

        recyclerView2=findViewById(R.id.myAppeal_view_evaluator_answered_RecycleView);
        recyclerView2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(this,2);
        recyclerView2.setLayoutManager(linearLayoutManager1);

        reviewedList=new ArrayList<>();

        viewReviewedAppealsAdapter= new ViewEntriesEvaluatorUserAdapter(this,reviewedList);
        recyclerView2.setAdapter(viewReviewedAppealsAdapter);

        reviewedAppeals(message_received[0],"Reviewed");
    }

    private void reviewedAppeals(String s, String reviewed) {
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Contest_entries");
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewedList.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ContestEntry contestEntry = snapshot1.getValue(ContestEntry.class);
                    {
                        String contestEntryID=snapshot1.getKey();
                        assert contestEntry != null;
                        if(contestEntry.getContestID().equals(s)&&contestEntry.getEntry_status().equals("Approved")&&contestEntry.getOffTopic_count()<3){

                            DatabaseReference contest_reference=FirebaseDatabase.getInstance().getReference().child("Contests");
                            DatabaseReference appeal_reference = FirebaseDatabase.getInstance().getReference("Appeal_requests");


                            contest_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        String contestID=dataSnapshot.getKey();
                                        Contests contests=dataSnapshot.getValue(Contests.class);
                                        if(contests!=null){
                                            if(contestID.equals(s)){
                                                if(contests.getAppeal_evaluator1_id().equals(userID)||
                                                        contests.getAppeal_evaluator2_id().equals(userID)||
                                                        contests.getAppeal_evaluator3_id().equals(userID)){

                                                    appeal_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.exists()){
                                                                for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                                    AppealRequest appealRequest=dataSnapshot1.getValue(AppealRequest.class);
                                                                    if(appealRequest!=null){
                                                                        String contest_id=appealRequest.getContestID();
                                                                        String user_id=appealRequest.getUserID();
                                                                        String entry_id=appealRequest.getEntryID();


                                                                        if(user_id.equals(contestEntry.getUserID())&&entry_id.equals(contestEntryID)){
                                                                            //Toast.makeText(ViewAppealEvaluator.this, contestEntry.getUserID()+" "+user_id + " | "+contestEntryID +" "+entry_id, Toast.LENGTH_SHORT).show();
                                                                            if(contests.getAppeal_evaluator1_id().equals(userID)&&appealRequest.getEvaluation1_status().equals(reviewed)){
                                                                                reviewedList.add(contestEntry);
                                                                            }
                                                                            else if(contests.getAppeal_evaluator2_id().equals(userID)&&appealRequest.getEvaluation2_status().equals(reviewed)){
                                                                                reviewedList.add(contestEntry);
                                                                                //Toast.makeText(ViewAppealEvaluator.this, contests.getAppeal_evaluator1_id()+" | "+ userID+" | "+appealRequest.getEvaluation1_status(), Toast.LENGTH_SHORT).show();

                                                                            }
                                                                            else if(contests.getAppeal_evaluator3_id().equals(userID)&&appealRequest.getEvaluation3_status().equals(reviewed)){
                                                                                reviewedList.add(contestEntry);

                                                                            }
                                                                        }
                                                                    }
                                                                    Collections.sort(reviewedList, new Comparator<ContestEntry>() {
                                                                        @Override
                                                                        public int compare(ContestEntry o1, ContestEntry o2) {
                                                                            return o1.getOrder()-o2.getOrder();
                                                                        }
                                                                    });
                                                                    //Collections.reverse(imageList);
                                                                    viewReviewedAppealsAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }





                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });




                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void pendingAppeals(String message, String pending) {
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Contest_entries");
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingList.clear();

                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ContestEntry contestEntry = snapshot1.getValue(ContestEntry.class);
                    {
                        String contestEntryID=snapshot1.getKey();
                        assert contestEntry != null;
                        if(contestEntry.getContestID().equals(message)&&contestEntry.getEntry_status().equals("Approved")&&contestEntry.getOffTopic_count()<3){

                            DatabaseReference contest_reference=FirebaseDatabase.getInstance().getReference().child("Contests");
                            DatabaseReference appeal_reference = FirebaseDatabase.getInstance().getReference("Appeal_requests");

                            //pendingList.add(contestEntry);

                            contest_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        String contestID=dataSnapshot.getKey();
                                        Contests contests=dataSnapshot.getValue(Contests.class);
                                        if(contests!=null){
                                            if(contestID.equals(message)){
                                                if(contests.getAppeal_evaluator1_id().equals(userID)||
                                                        contests.getAppeal_evaluator2_id().equals(userID)||
                                                        contests.getAppeal_evaluator3_id().equals(userID)){

                                                    appeal_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(snapshot.exists()){
                                                                for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                                    AppealRequest appealRequest=dataSnapshot1.getValue(AppealRequest.class);
                                                                    if(appealRequest!=null){
                                                                        String contest_id=appealRequest.getContestID();
                                                                        String user_id=appealRequest.getUserID();
                                                                        String entry_id=appealRequest.getEntryID();


                                                                        if(user_id.equals(contestEntry.getUserID())&&entry_id.equals(contestEntryID)){
                                                                            //Toast.makeText(ViewAppealEvaluator.this, contestEntry.getUserID()+" "+user_id + " | "+contestEntryID +" "+entry_id, Toast.LENGTH_SHORT).show();
                                                                             if(contests.getAppeal_evaluator1_id().equals(userID)&&appealRequest.getEvaluation1_status().equals(pending)){
                                                                                pendingList.add(contestEntry);
                                                                             }
                                                                            else if(contests.getAppeal_evaluator2_id().equals(userID)&&appealRequest.getEvaluation2_status().equals(pending)){
                                                                                pendingList.add(contestEntry);
                                                                                 //Toast.makeText(ViewAppealEvaluator.this, contests.getAppeal_evaluator1_id()+" | "+ userID+" | "+appealRequest.getEvaluation1_status(), Toast.LENGTH_SHORT).show();

                                                                            }
                                                                            else if(contests.getAppeal_evaluator3_id().equals(userID)&&appealRequest.getEvaluation3_status().equals(pending)){
                                                                                pendingList.add(contestEntry);

                                                                            }
                                                                        }
                                                                    }
                                                                    Collections.sort(pendingList, new Comparator<ContestEntry>() {
                                                                        @Override
                                                                        public int compare(ContestEntry o1, ContestEntry o2) {
                                                                            return o1.getOrder()-o2.getOrder();
                                                                        }
                                                                    });
                                                                    //Collections.reverse(imageList);
                                                                    viewPendingAppealsAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }





                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });




                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
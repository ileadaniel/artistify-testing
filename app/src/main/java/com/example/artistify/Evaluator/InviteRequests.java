package com.example.artistify.Evaluator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.artistify.ContestCreator.myContestCreatorAdapter;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.EvaluationRequest;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InviteRequests extends AppCompatActivity {

    private String userID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private RecyclerView InvitesList;
    private DatabaseReference mDatabase;
    myEvaluationRequestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_requests);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        InvitesList=(RecyclerView)findViewById(R.id.myEvaluationRequestsRecycleView);
        InvitesList.setHasFixedSize(true);

        InvitesList.setLayoutManager(new LinearLayoutManager(this));

        processFilter(userID);

    }

    private void processFilter(String userID) {
        FirebaseRecyclerOptions<EvaluationRequest> options =
                new FirebaseRecyclerOptions.Builder<EvaluationRequest>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Evaluation_requests").orderByChild("evaluator_id")
                                .startAt(userID).endAt(userID+"\uf8ff"),EvaluationRequest.class).build();

        adapter=new myEvaluationRequestsAdapter(options);
        adapter.startListening();
        InvitesList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
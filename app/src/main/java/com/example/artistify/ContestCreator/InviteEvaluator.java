package com.example.artistify.ContestCreator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.artistify.Evaluator.InviteRequests;
import com.example.artistify.ModelClasses.EvaluationRequest;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InviteEvaluator extends AppCompatActivity {

    private DatabaseReference reference_evaluator;

    private RecyclerView EvaluatorsList;
    myEvaluatorInviteAdapter adapter;

    myInvitedEvaluatorAdapter inv_adapter;
    private RecyclerView InvitedEvaluatorsList;
    private DatabaseReference mDatabase;

    public final static String MESSAGE_KEY ="com.example.message_key";

    private String message="";



    @Override
    public void onBackPressed() {

        finish();
        Intent intent=new Intent(InviteEvaluator.this,ContestCreator.class);
        startActivity(intent);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_evaluator);

        reference_evaluator= FirebaseDatabase.getInstance().getReference().child("Users");

        reference_evaluator.keepSynced(true);

        EvaluatorsList=(RecyclerView)findViewById(R.id.myInvitedEvaluatorRecycleView);
        EvaluatorsList.setHasFixedSize(true);

        EvaluatorsList.setLayoutManager(new LinearLayoutManager(this));


        processFilter(2);
        reference_evaluator.keepSynced(true);
        mDatabase=FirebaseDatabase.getInstance().getReference().child("Evaluation_requests");
        mDatabase.keepSynced(true);

        InvitedEvaluatorsList=(RecyclerView)findViewById(R.id.selected_evaluatorsRecyclerView);
        InvitedEvaluatorsList.setHasFixedSize(true);

        InvitedEvaluatorsList.setLayoutManager(new LinearLayoutManager(this));


        Intent intent = getIntent();
        message = intent.getStringExtra(MESSAGE_KEY);


        processFilter1(message);
       // adapter.notifyDataSetChanged();
       // inv_adapter.notifyDataSetChanged();

    }

    private void processFilter(int s) {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("authentication_level")
                                .equalTo(s),User.class).build();

        adapter=new myEvaluatorInviteAdapter(options);
        adapter.startListening();
        EvaluatorsList.setAdapter(adapter);



    }

    private void processFilter1(String s){

        FirebaseRecyclerOptions<EvaluationRequest> options1 =
                new FirebaseRecyclerOptions.Builder<EvaluationRequest>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Evaluation_requests").orderByChild("contest_id")
                                .startAt(s).endAt(s+"\uf8ff"),EvaluationRequest.class).build();

        inv_adapter = new myInvitedEvaluatorAdapter(options1);
        inv_adapter.startListening();
        InvitedEvaluatorsList.setAdapter(inv_adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        //adapter.notifyDataSetChanged();
        inv_adapter.startListening();
        //inv_adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        //adapter.notifyDataSetChanged();
        inv_adapter.stopListening();
       // inv_adapter.notifyDataSetChanged();
    }

}
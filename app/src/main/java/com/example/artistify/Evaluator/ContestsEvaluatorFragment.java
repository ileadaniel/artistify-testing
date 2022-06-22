package com.example.artistify.Evaluator;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.artistify.ContestCreator.myContestCreatorAdapter;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContestsEvaluatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContestsEvaluatorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button viewRequests;

    private RecyclerView ContestsEvaluatedList;
    private DatabaseReference mDatabase;
    myContestEvaluatorAdapter adapter;
    private FirebaseUser user;
    private String userID;

    public ContestsEvaluatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContestsEvaluatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContestsEvaluatorFragment newInstance(String param1, String param2) {
        ContestsEvaluatorFragment fragment = new ContestsEvaluatorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_contests_evaluator, container, false);


        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Contests");
        mDatabase.keepSynced(true);

        ContestsEvaluatedList=(RecyclerView)view.findViewById(R.id.myEvaluatorContestsRecycleView);
        ContestsEvaluatedList.setHasFixedSize(true);

        ContestsEvaluatedList.setLayoutManager(new LinearLayoutManager(getContext()));

        processFilter("visible");


        viewRequests=(Button)view.findViewById(R.id.view_contest_evaluation_requests);

        viewRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),InviteRequests.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void processFilter(String userID) {
        FirebaseRecyclerOptions<Contests> options =
                new FirebaseRecyclerOptions.Builder<Contests>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Contests").orderByChild("visibility")
                                .startAt(userID).endAt(userID+"\uf8ff"),Contests.class).build();

        adapter=new myContestEvaluatorAdapter(options);
        adapter.startListening();
        ContestsEvaluatedList.setAdapter(adapter);

    }
}
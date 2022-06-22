package com.example.artistify.ContestCreator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContestsContCreatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContestsContCreatorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String userID;
    private DatabaseReference reference_user;

    private RecyclerView ContestsList;
    private DatabaseReference mDatabase;
    myContestCreatorAdapter adapter;
    private Button create_new_contest;

    public ContestsContCreatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContestsContCreatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContestsContCreatorFragment newInstance(String param1, String param2) {
        ContestsContCreatorFragment fragment = new ContestsContCreatorFragment();
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
       View view = inflater.inflate(R.layout.fragment_contests_cont_creator, container, false);


        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference_user = FirebaseDatabase.getInstance().getReference("Users");

        userID = user.getUid();
       


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Contests");
        mDatabase.keepSynced(true);
        ContestsList=(RecyclerView)view.findViewById(R.id.myContest_creator_RecycleView);
        ContestsList.setHasFixedSize(true);

        ContestsList.setLayoutManager(new LinearLayoutManager(getContext()));


            processFilter(userID);

        create_new_contest=(Button)view.findViewById(R.id.button_create_new_contest);
        create_new_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
            private void showDialog(){
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext(),R.style.MyDialogTheme);
                builder.setTitle("Important informations");
                builder.setMessage("1. After you create the contest you need to select contest start date, contest end date, evaluation duration and appeals duration!" +
                        "\n"+"2. If the contest is not approved by the admin the status will be changed to Rejected, which requires small changes to the contest, and after pressing the update button the contest automatically goes to Pending status and admin will review the contest again.");
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Contests contest = new Contests("","","Not assigned","Draft","Not assigned","Not assigned","Not assigned",
                                "Not assigned", "Not assigned","Not assigned",userID,"invisible",0,0,0,0);
                        String contestID=mDatabase.push().getKey();
                        mDatabase.child(contestID).setValue(contest);

                        Toast.makeText(getContext(), "Successfully created", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

       return view;
    }

    private void processFilter(String s) {
        FirebaseRecyclerOptions<Contests> options =
                new FirebaseRecyclerOptions.Builder<Contests>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Contests").orderByChild("contest_creator_id")
                                .startAt(s).endAt(s+"\uf8ff"),Contests.class).build();

        adapter=new myContestCreatorAdapter(options);
        adapter.startListening();
        ContestsList.setAdapter(adapter);
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

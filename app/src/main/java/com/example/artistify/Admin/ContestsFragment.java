package com.example.artistify.Admin;

import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.SearchView;


import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.NewCategory;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private DatabaseReference mDatabase;
    private SearchView search;
    private Button filter;

    private RecyclerView contestsList;
    myContestAdminAdapter adapter;


    public ContestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContestsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContestsFragment newInstance(String param1, String param2) {
        ContestsFragment fragment = new ContestsFragment();
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
        View view= inflater.inflate(R.layout.fragment_contests, container, false);

        mDatabase=FirebaseDatabase.getInstance().getReference().child("Contests");
        mDatabase.keepSynced(true);

        contestsList = (RecyclerView) view.findViewById(R.id.myContestsRecyclerView);

        contestsList.setLayoutManager(new LinearLayoutManager(getContext()));


        FirebaseRecyclerOptions<Contests> options =
                new FirebaseRecyclerOptions.Builder<Contests>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Contests"),Contests.class).build();

        adapter = new myContestAdminAdapter(options);
        contestsList.setAdapter(adapter);

        filter= (Button)view.findViewById(R.id.filter_btn);

        search=(SearchView)view.findViewById(R.id.search_contest);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setIconified(false);
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSearch(newText);
                return false;
            }
        });


        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog();
            }

            private void showOptionDialog() {
                int position = -1;
                String[] options = {"All","Draft", "Pending", "Open", "Rejected", "Evaluation","Appeals", "Finished", "Add new category"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogTheme);
                builder.setTitle("Filter contests");
                builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (which == 0) {

                            FirebaseRecyclerOptions<Contests> options =
                                    new FirebaseRecyclerOptions.Builder<Contests>()
                                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Contests").orderByChild("status"),Contests.class).build();

                            adapter=new myContestAdminAdapter(options);
                            adapter.startListening();
                            contestsList.setAdapter(adapter);
                            dialog.dismiss();

                        }
                        if(which == 1){
                            processFilter("Draft");
                            dialog.dismiss();
                        }
                        if(which == 2){
                            processFilter("Pending");
                            dialog.dismiss();
                        }
                        if(which == 3){
                            processFilter("Open");
                            dialog.dismiss();
                        }
                        if(which == 4){
                            processFilter("Rejected");
                            dialog.dismiss();
                        }
                        if(which == 5){
                            processFilter("Evaluation");
                            dialog.dismiss();
                        }
                        if(which == 6){
                            processFilter("Appeals");
                            dialog.dismiss();
                        }
                        if(which == 7){
                            processFilter("Finished");
                            dialog.dismiss();
                        }
                        if(which == 8){
                            startActivity(new Intent(getContext(), NewCategory.class));
                            dialog.dismiss();
                        }
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

    private void processSearch(String s) {
        FirebaseRecyclerOptions<Contests> options =
                new FirebaseRecyclerOptions.Builder<Contests>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Contests").orderByChild("title")
                                .startAt(s).endAt(s+"\uf8ff"),Contests.class).build();

        adapter=new myContestAdminAdapter(options);
        adapter.startListening();
        contestsList.setAdapter(adapter);
    }
    private void processFilter(String s) {
        FirebaseRecyclerOptions<Contests> options =
                new FirebaseRecyclerOptions.Builder<Contests>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Contests").orderByChild("status")
                                .startAt(s).endAt(s+"\uf8ff"),Contests.class).build();

        adapter=new myContestAdminAdapter(options);
        adapter.startListening();
        contestsList.setAdapter(adapter);
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
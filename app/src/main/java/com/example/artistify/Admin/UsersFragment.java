package com.example.artistify.Admin;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.example.artistify.TestInfo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView UsersList;
    private DatabaseReference mDatabase;
    private SearchView search;
    myUsersAdapter adapter;
    private Button status;



    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase.keepSynced(true);
        UsersList=(RecyclerView)view.findViewById(R.id.myRecycleView);
        UsersList.setHasFixedSize(true);

        UsersList.setLayoutManager(new LinearLayoutManager(getContext()));

        long startTime = System.nanoTime();

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(mDatabase,User.class).build();

        adapter = new myUsersAdapter(options);
        UsersList.setAdapter(adapter);



        long elapsedTime = System.nanoTime() - startTime;
        //Toast.makeText(getContext(), ""+elapsedTime, Toast.LENGTH_SHORT).show();

        TestInfo.showTestUserInfoLog("Test data collection: get all users",elapsedTime/1000000);

        search=(SearchView)view.findViewById(R.id.search_user);

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



        status= (Button)view.findViewById(R.id.status_btn);

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog();
            }

            private void showOptionDialog() {
                int position = -1;
                String[] options = {"All", "Pending", "Approved", "Rejected","Banned","Deleted"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.MyDialogTheme);
                builder.setTitle("Filter users");
                builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (which == 0) {

                            FirebaseRecyclerOptions<User> options =
                                    new FirebaseRecyclerOptions.Builder<User>()
                                            .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("status"),User.class).build();

                            adapter=new myUsersAdapter(options);
                            adapter.startListening();
                            UsersList.setAdapter(adapter);

                            dialog.dismiss();

                        }
                        if (which == 1) {
                            processFilter("Pending");
                            dialog.dismiss();


                        }
                        if (which == 2) {
                            processFilter("Approved");
                            dialog.dismiss();


                        }
                        if (which == 3) {
                            processFilter("Rejected");
                            dialog.dismiss();
                        }
                        if (which == 4) {
                            processFilter("Banned");
                            dialog.dismiss();
                        }
                        if (which == 5) {
                            processFilter("Deleted");
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
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("fullName")
                                .startAt(s).endAt(s+"\uf8ff"),User.class).build();

        adapter=new myUsersAdapter(options);
        adapter.startListening();
        UsersList.setAdapter(adapter);
    }
    private void processFilter(String s) {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("status")
                                .startAt(s).endAt(s+"\uf8ff"),User.class).build();

        adapter=new myUsersAdapter(options);
        adapter.startListening();
        UsersList.setAdapter(adapter);
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
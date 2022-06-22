package com.example.artistify.NormalUser;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.artistify.ExhibitionsViewAdapter;
import com.example.artistify.ModelClasses.ContestResults;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExhibitionsUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExhibitionsUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView ContestsListExhibition;
    private DatabaseReference mDatabase;
    ExhibitionsViewAdapter adapter;

    public ExhibitionsUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExhibitionsUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExhibitionsUserFragment newInstance(String param1, String param2) {
        ExhibitionsUserFragment fragment = new ExhibitionsUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_exhibitions_user, container, false);

        mDatabase= FirebaseDatabase.getInstance().getReference().child("Contest_results");
        mDatabase.keepSynced(true);

        ContestsListExhibition=(RecyclerView)view.findViewById(R.id.myUserExhibitionsRecycleView);
        ContestsListExhibition.setHasFixedSize(true);

        ContestsListExhibition.setLayoutManager(new LinearLayoutManager(getContext()));

        processFilter("visible");

        return view;
    }

    private void processFilter(String visibility) {
        FirebaseRecyclerOptions<ContestResults> options =
                new FirebaseRecyclerOptions.Builder<ContestResults>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Contest_results").orderByChild("visibility")
                                .startAt(visibility).endAt(visibility+"\uf8ff"),ContestResults.class).build();

        adapter=new ExhibitionsViewAdapter(options);
        adapter.startListening();
        ContestsListExhibition.setAdapter(adapter);
    }
}
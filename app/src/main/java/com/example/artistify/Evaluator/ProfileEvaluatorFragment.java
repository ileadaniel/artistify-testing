package com.example.artistify.Evaluator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.artistify.Authentication.Login;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.example.artistify.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileEvaluatorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileEvaluatorFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private DatabaseReference reference;
    private DatabaseReference contest_Reference=FirebaseDatabase.getInstance().getReference().child("Contests");

    private String userID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private CircleImageView img;
    private ProgressBar myProgressbar;
    private ProgressBar myProgressbarCover;
    private ImageView cover_pic,evaluator_settings;
    private TextView full_name_evaluator,authentication_evaluator,email_ev,nr_contest_evaluated;

    public ProfileEvaluatorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileEvaluatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileEvaluatorFragment newInstance(String param1, String param2) {
        ProfileEvaluatorFragment fragment = new ProfileEvaluatorFragment();
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
    public void onResume() {
        super.onResume();
        reference.keepSynced(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_evaluator, container, false);



        img=(CircleImageView)view.findViewById(R.id.profile_image_evaluator);
        cover_pic=(ImageView)view.findViewById(R.id.cover_pic_evaluator);
        myProgressbar=(ProgressBar)view.findViewById(R.id.progress_image_loader);
        myProgressbarCover=(ProgressBar)view.findViewById(R.id.progress_image_loader_cover);
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        userID = user.getUid();
        full_name_evaluator=(TextView)view.findViewById(R.id.fullName_evaluator);
        authentication_evaluator=(TextView)view.findViewById(R.id.authentication_evaluator);

        authentication_evaluator.setText("Evaluator");

        email_ev=(TextView)view.findViewById(R.id.email_ev);
        nr_contest_evaluated=(TextView)view.findViewById(R.id.nr_contests_ev);



        evaluator_settings=(ImageView)view.findViewById(R.id.evaluator_settings);


        evaluator_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserSettings.class));
            }
        });


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
                    String fullName=userProfile.getFullName();

                    int user_level = userProfile.getAuthentication_level();
                    String profile_img_url=userProfile.getUser_pic_url();
                    String cover_pic_url=userProfile.getCover_pic_url();
                    String email=userProfile.getEmail();

//                    Glide.with(getContext()).load(cover_pic_url)
//                            .placeholder(R.drawable.progress_animation_profile).centerCrop().into(cover_pic);
//                    Glide.with(getContext()).load(profile_img_url)
//                            .placeholder(R.drawable.progress_animation_profile).into(img);


                    Glide.with(getContext()).load(cover_pic_url).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop()
                            .dontAnimate()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    myProgressbarCover.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            })
                            .into(cover_pic);


                    Glide.with(getContext()).load(profile_img_url).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop()
                            .dontAnimate()
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    myProgressbar.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            })
                            .into(img);

                    full_name_evaluator.setText(fullName);

                    email_ev.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Something wrong happened!",Toast.LENGTH_LONG).show();
            }
        });

        final int[] contests_counter = {0};
        contest_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Contests contests=dataSnapshot.getValue(Contests.class);
                        if(contests!=null){
                            String ev1=contests.getEvaluator1_id();
                            String ev2=contests.getEvaluator2_id();
                            String ev3=contests.getEvaluator3_id();
                            String a_ev1=contests.getAppeal_evaluator1_id();
                            String a_ev2=contests.getAppeal_evaluator2_id();
                            String a_ev3=contests.getAppeal_evaluator3_id();

                            if((ev1.equals(userID)||ev2.equals(userID)||ev3.equals(userID)||a_ev1.equals(userID)||a_ev2.equals(userID)||a_ev3.equals(userID))&&contests.getStatus().equals("Finished")){
                                contests_counter[0] = contests_counter[0] +1;

                            }


                        }
                    }
                    nr_contest_evaluated.setText(String.valueOf(contests_counter[0]));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}
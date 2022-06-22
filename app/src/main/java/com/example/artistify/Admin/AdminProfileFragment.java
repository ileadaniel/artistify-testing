package com.example.artistify.Admin;

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
import com.example.artistify.R;
import com.example.artistify.ModelClasses.User;
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
 * Use the {@link AdminProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button logout_admin;

    private DatabaseReference reference;

    private String userID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private CircleImageView img;
    private ProgressBar myProgressbar;
    private ProgressBar myProgressbarCover;
    private ImageView cover_pic,admin_settings;
    private TextView full_name_admin,authentication_admin,email_admin;


    public AdminProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminProfileFragment newInstance(String param1, String param2) {
        AdminProfileFragment fragment = new AdminProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        reference.keepSynced(true);
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
        View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        img=(CircleImageView)view.findViewById(R.id.profile_image_admin);
        myProgressbar=(ProgressBar)view.findViewById(R.id.progress_image_loader);
        myProgressbarCover=(ProgressBar)view.findViewById(R.id.progress_image_loader_cover);
        cover_pic=(ImageView)view.findViewById(R.id.cover_pic_admin);

        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        userID = user.getUid();
        full_name_admin=(TextView)view.findViewById(R.id.fullName_admin);
        authentication_admin=(TextView)view.findViewById(R.id.authentication_admin);
        email_admin=(TextView)view.findViewById(R.id.email_admin);
        authentication_admin.setText("Administrator");

        admin_settings=(ImageView)view.findViewById(R.id.admin_settings);


        admin_settings.setOnClickListener(new View.OnClickListener() {
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



//                    Glide.with(getContext()).load(profile_img_url)
//                            .placeholder(R.drawable.progress_animation_profile).into(img);

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


                    full_name_admin.setText(fullName);
                    email_admin.setText(email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Something wrong happened!",Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
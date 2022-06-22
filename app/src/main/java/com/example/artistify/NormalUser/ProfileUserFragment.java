package com.example.artistify.NormalUser;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.example.artistify.UploadPicture;
import com.example.artistify.UserSettings;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Button logout_normal_user;

    private DatabaseReference reference;

    private String userID;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private CircleImageView img;
    private ProgressBar myProgressbar;
    private ProgressBar myProgressbarCover;
    private ImageView cover_pic,user_settings,upload_new_image;
    private TextView full_name_n_user,user_level_auth;
    private final static String MESSAGE_KEY="com.example.message_key";

    RecyclerView recyclerView;
    PhotoAdapter photoAdapter;
    List<Image> imageList;

    public ProfileUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileUserFragment newInstance(String param1, String param2) {
        ProfileUserFragment fragment = new ProfileUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile_user, container, false);
        img=(CircleImageView)view.findViewById(R.id.profile_image_normal_user);
        cover_pic=(ImageView)view.findViewById(R.id.cover_pic_normal_user);
        myProgressbar=(ProgressBar)view.findViewById(R.id.progress_image_loader);
        myProgressbarCover=(ProgressBar)view.findViewById(R.id.progress_image_loader_cover);
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        userID = user.getUid();
        full_name_n_user=(TextView)view.findViewById(R.id.fullName_normal_user);
        logout_normal_user=(Button) view.findViewById(R.id.logout_normal_user);
        user_level_auth=(TextView)view.findViewById(R.id.user_level_auth);

        upload_new_image=(ImageView)view.findViewById(R.id.upload_new_image);

        upload_new_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UploadPicture.class);
                intent.putExtra(MESSAGE_KEY,"image");
                startActivity(intent);
            }
        });

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList=new ArrayList<>();

        photoAdapter= new PhotoAdapter(getContext(),imageList);
        recyclerView.setAdapter(photoAdapter);

        userPhotos();


        user_settings=(ImageView)view.findViewById(R.id.settings_user);


        user_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), UserSettings.class));
            }
        });


//        logout_normal_user.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getContext(),Login.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//                if(getActivity()!=null){
//                    getActivity().finish();
//                }
//            }
//        });


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
                    String fullName=userProfile.fullName;

                    int user_level = userProfile.authentication_level;
                    String profile_img_url=userProfile.user_pic_url;
                    String cover_pic_url=userProfile.cover_pic_url;

                    user_level_auth.setText("Normal user");

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

                    full_name_n_user.setText(fullName);
                    reference.keepSynced(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Something wrong happened!",Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    private void userPhotos(){
        DatabaseReference DBreference = FirebaseDatabase.getInstance().getReference("Image");
        DBreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    Image image = snapshot1.getValue(Image.class);
                    {
                        assert image != null;
                        if(image.getUserId().equals(userID)){
                            imageList.add(image);
                        }
                    }
                    //Collections.reverse(imageList);
                    Collections.sort(imageList, new Comparator<Image>() {
                        @Override
                        public int compare(Image o1, Image o2) {
                            return Long.compare(o2.getTimestamp_upload(),o1.getTimestamp_upload());
                        }
                    });
                    photoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
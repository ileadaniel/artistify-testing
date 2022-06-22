package com.example.artistify.NormalUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.artistify.ModelClasses.Image;
import com.example.artistify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExistingPhotos extends AppCompatActivity {

    private String userID;


    private FirebaseUser user;

    RecyclerView recyclerView;
    ExistingPhotosAdapter existingPhotosAdapter;
    List<Image> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_photos);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        recyclerView=findViewById(R.id.recycler_view_existing_images);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList=new ArrayList<>();

        existingPhotosAdapter= new ExistingPhotosAdapter(this,imageList);
        recyclerView.setAdapter(existingPhotosAdapter);

        userPhotos();


    }

    private void userPhotos() {
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
                    Collections.reverse(imageList);
                    existingPhotosAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
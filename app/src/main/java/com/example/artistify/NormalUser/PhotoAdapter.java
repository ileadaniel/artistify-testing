package com.example.artistify.NormalUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.ContestResults;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.NewCategory;
import com.example.artistify.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    private Context context;
    private List<Image> mImage;
    //ImageView imageSelected;
    Dialog dialog_photo;

    private String category_selected="";

    private DatabaseReference image_reference= FirebaseDatabase.getInstance().getReference().child("Image");
    private DatabaseReference contest_result_reference=FirebaseDatabase.getInstance().getReference().child("Contest_results");
    private DatabaseReference contest_entry_reference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private TextView image_title,image_category,date_submit;

    private int flag_winner_photo=0;
    private int flag_contest_entry=0;
    private String entry_id_selected;

    public PhotoAdapter(Context context, List<Image> mImage) {
        this.context = context;
        this.mImage = mImage;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.photos_item,parent,false);

        return new PhotoAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Image image = mImage.get(position);
//        Glide.with(context).load(image.getImageUrl())
//                .placeholder(R.drawable.progress_animation_profile).centerCrop().into(holder.uploaded_image);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(context).load(image.getImageUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop()
                .dontAnimate()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.myProgressbar.setVisibility(View.INVISIBLE);
                        return false;
                    }
                })
                .into(holder.uploaded_image);

        String [] categories_items = holder.uploaded_image.getContext().getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(holder.uploaded_image.getContext(),
                R.layout.color_spinner_layout_user_photo,categories_items);

        holder.uploaded_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_photo.setContentView(R.layout.image_layout_dialog);
                dialog_photo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                image_title = dialog_photo.findViewById(R.id.image_title_selected_user);
                image_category= dialog_photo.findViewById(R.id.image_category_user);
                date_submit= dialog_photo.findViewById(R.id.date_submit_photo);

                TextView deleteButton= dialog_photo.findViewById(R.id.delete_user_photo);
                TextView editButton= dialog_photo.findViewById(R.id.edit_user_photo);
                TextView saveChanges=dialog_photo.findViewById(R.id.save_changes);
                TextView cancelEdit=dialog_photo.findViewById(R.id.cancel_edit);
                TextView contest_category_show=dialog_photo.findViewById(R.id.contest_category_spinner_show);

                ImageView undo_contest_spinner=dialog_photo.findViewById(R.id.undo_contest_spinner);

                EditText editImage_title=dialog_photo.findViewById(R.id.edit_image_title_user);
                //EditText editImage_category=dialog_photo.findViewById(R.id.edit_image_category_user);
                Spinner spinner=dialog_photo.findViewById(R.id.category_change_spinner);




                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(myAdapter);




                String title=image.getTitle();
                if(!title.equals("")){
                   image_title.setText(image.getTitle());
                }
                else{
                    image_title.setText("No title");
                }

               image_category.setText(image.getCategory());

                long date_submitted=image.getTimestamp_upload();

                String date = DateFormat.format("EEEE, MMMM dd, yyyy \n( HH:mm )", date_submitted).toString();

                date_submit.setText(date);

                String url=image.getImageUrl();



                ImageView imageSelected= dialog_photo.findViewById(R.id.imageViewSelected);
                ProgressBar myProgressbar=dialog_photo.findViewById(R.id.progress_image_loader);

               int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

               int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;

//                Glide.with(context).load(url)
//                        .placeholder(R.drawable.progress_animation_profile)
//                        .fitCenter().override(width_screen,height_screen).into(imageSelected);

                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter().override(width_screen,height_screen)
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
                        .into(imageSelected);


                ImageView imageViewClose= dialog_photo.findViewById(R.id.imageViewClose);

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteImageDialog();
                    }

                    private void showDeleteImageDialog() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(deleteButton.getContext(),R.style.MyDialogTheme);
                        builder.setTitle("Are you sure?");
                        builder.setMessage("After you perform this action, you can't access this photo anymore.\nIf your photo is winner in a contest you can't remove it!");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                            builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //flag_winner_photo =0;
                                image_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            String image_id=dataSnapshot.getKey();
                                            Image image1=dataSnapshot.getValue(Image.class);
                                            if(image1.getImageUrl().equals(image.getImageUrl())){
                                                //Toast.makeText(deleteButton.getContext(), image_id, Toast.LENGTH_SHORT).show();
                                                if(image.getWinner_photo()==0){
                                                    deletePhoto(image_id,image.getImageUrl());
                                                }
                                                else{
                                                    Toast.makeText(context, "This photo is winner in a contest, you can't delete it", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        }

                                    }

                                    private void deletePhoto(String image_id, String imageUrl) {
                                        FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
                                        StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(imageUrl);
                                        Toast.makeText(context, "deleting", Toast.LENGTH_SHORT).show();
                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // File deleted successfully
                                                    image_reference.child(image_id).removeValue();
                                                    contest_entry_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                                String entry_id=dataSnapshot1.getKey();
                                                                ContestEntry contestEntry1=dataSnapshot1.getValue(ContestEntry.class);
                                                                if(contestEntry1!=null) {
                                                                    if(contestEntry1.getImageID().equals(image_id)){
                                                                        contest_entry_reference.child(entry_id).removeValue();
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                    Toast.makeText(deleteButton.getContext(), "Photo removed successfully!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    dialog_photo.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // An error occurred!
                                                    Toast.makeText(deleteButton.getContext(), "An error occurred, please try again!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                        //flag_winner_photo=0;
                                        //flag_contest_entry=0;
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });



                            }
                        });
                        builder.show();
                    }

                    private void verifyFlag() {

                        if(flag_winner_photo ==1){
                            Toast.makeText(deleteButton.getContext(), "This photo is winner in a contest, you can't delete it", Toast.LENGTH_LONG).show();
                            //flag_winner_photo=0;
                            flag_contest_entry=0;
                        }
                        else if(flag_winner_photo==0){
                            FirebaseStorage mFirebaseStorage=FirebaseStorage.getInstance();
                            StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(image.getImageUrl());
                            Toast.makeText(context, "deleting", Toast.LENGTH_SHORT).show();
//                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    // File deleted successfully
//                                                    image_reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                            for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
//                                                                String image_id = dataSnapshot.getKey();
//                                                                Image image1 = dataSnapshot.getValue(Image.class);
//                                                                if (image1.getImageUrl().equals(image.getImageUrl())) {
//                                                                    image_reference.child(image_id).removeValue();
//                                                                    contest_entry_reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                        @Override
//                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                            for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
//                                                                                String entry_id=dataSnapshot1.getKey();
//                                                                                ContestEntry contestEntry1=dataSnapshot1.getValue(ContestEntry.class);
//                                                                                if(contestEntry1!=null) {
//                                                                                    if(contestEntry1.getImageID().equals(image_id)){
//                                                                                        contest_entry_reference.child(entry_id).removeValue();
//                                                                                    }
//                                                                                }
//                                                                            }
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                        }
//                                                    });
//
//                                                    Toast.makeText(deleteButton.getContext(), "Photo removed successfully!", Toast.LENGTH_SHORT).show();
//                                                    dialog.dismiss();
//                                                    dialog_photo.dismiss();
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception exception) {
//                                                    // An error occurred!
//                                                    Toast.makeText(deleteButton.getContext(), "An error occurred, please try again!", Toast.LENGTH_SHORT).show();
//                                                    dialog.dismiss();
//                                                }
//                                            });
                            flag_winner_photo=0;
                            flag_contest_entry=0;
                        }
                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image_title.setVisibility(View.GONE);
                        image_category.setVisibility(View.GONE);
                        editImage_title.setVisibility(View.VISIBLE);
                        contest_category_show.setVisibility(View.VISIBLE);
                        saveChanges.setVisibility(View.VISIBLE);
                        cancelEdit.setVisibility(View.VISIBLE);
                        editButton.setVisibility(View.GONE);
                        deleteButton.setVisibility(View.GONE);
                    }
                });

                cancelEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image_title.setVisibility(View.VISIBLE);
                        image_category.setVisibility(View.VISIBLE);
                        editImage_title.setVisibility(View.GONE);
                        contest_category_show.setVisibility(View.GONE);
                        saveChanges.setVisibility(View.GONE);
                        cancelEdit.setVisibility(View.GONE);
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.GONE);
                        undo_contest_spinner.setVisibility(View.GONE);
                    }
                });

                contest_category_show.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contest_category_show.setVisibility(View.GONE);
                        spinner.setVisibility(View.VISIBLE);
                        undo_contest_spinner.setVisibility(View.VISIBLE);
                    }
                });
                undo_contest_spinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contest_category_show.setVisibility(View.VISIBLE);
                        spinner.setVisibility(View.GONE);
                        undo_contest_spinner.setVisibility(View.GONE);
                    }
                });

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        category_selected= spinner.getSelectedItem().toString();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                saveChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    String image_id=dataSnapshot.getKey();
                                    Image image1=dataSnapshot.getValue(Image.class);
                                    if(image1!=null){
                                        String user_id=image1.getUserId();
                                        String image_url=image1.getImageUrl();
                                        if(user_id.equals(image.getUserId())&&image_url.equals(image.getImageUrl())){
                                            if(spinner.getVisibility()==View.VISIBLE){
                                                image_reference.child(image_id).child("category").setValue(category_selected);
                                            }
                                            else{
                                                image_reference.child(image_id).child("category").setValue("Profile");
                                            }
                                            if(!editImage_title.getText().toString().equals("")){
                                                image_reference.child(image_id).child("title").setValue(editImage_title.getText().toString());
                                            }
                                            dialog_photo.dismiss();
                                            Toast.makeText(saveChanges.getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });




                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_photo.dismiss();
                    }
                });
                dialog_photo.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog_photo.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_photo.getWindow().setAttributes(layoutParams);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView uploaded_image;
        public ProgressBar myProgressbar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            uploaded_image=itemView.findViewById(R.id.uploaded_image);
            myProgressbar=itemView.findViewById(R.id.progress_image_loader);

            dialog_photo =new Dialog(itemView.getContext());
        }
    }
}

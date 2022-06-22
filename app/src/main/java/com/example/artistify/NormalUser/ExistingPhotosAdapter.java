package com.example.artistify.NormalUser;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.R;
import com.example.artistify.UploadPicture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ExistingPhotosAdapter extends RecyclerView.Adapter<ExistingPhotosAdapter.ViewHolder> {

    private Context context;
    private List<Image> mImage;
    Dialog dialog;
    private FirebaseUser user;
    private String userID="";

    private DatabaseReference contest_reference= FirebaseDatabase.getInstance().getReference().child("Contests");
    private DatabaseReference contest_submit_reference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference image_selected=FirebaseDatabase.getInstance().getReference().child("Image");

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    private TextView image_title_existing,image_category_existing,date_submit_existing,pick_image;

    public ExistingPhotosAdapter(Context context,List<Image> mImage){
        this.context=context;
        this.mImage=mImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.photos_item,parent,false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExistingPhotosAdapter.ViewHolder holder, int position) {
        Image image = mImage.get(position);
//        Glide.with(context).load(image.getImageUrl())
//                .placeholder(R.drawable.progress_animation_profile).centerCrop().into(holder.uploaded_image);

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

        holder.uploaded_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.existing_image_layout_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                image_title_existing = dialog.findViewById(R.id.image_title_selected_user_existing);
                image_category_existing= dialog.findViewById(R.id.image_category_user_existing);
                date_submit_existing= dialog.findViewById(R.id.date_submit_photo_existing);
                pick_image=dialog.findViewById(R.id.pick_image_user);

                ImageView imageSelected_existing=dialog.findViewById(R.id.imageViewSelected_existing);
                ProgressBar myProgressbar=dialog.findViewById(R.id.progress_image_loader);

                String title=image.getTitle();
                if(!title.equals("")){
                    image_title_existing.setText(image.getTitle());
                }
                else{
                    image_title_existing.setText("No title");
                }

                image_category_existing.setText(image.getCategory());

                long date_submitted=image.getTimestamp_upload();

                String date = DateFormat.format("EEEE, MMMM dd, yyyy", date_submitted).toString();

                date_submit_existing.setText(date);

                String url=image.getImageUrl();


                int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

                int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;


//                Glide.with(context).load(url)
//                        .placeholder(R.drawable.progress_animation_profile)
//                        .fitCenter().override(width_screen,height_screen).into(imageSelected_existing);

                Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).fitCenter().override(width_screen,height_screen)
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
                        .into(imageSelected_existing);


                ImageView imageViewClose=dialog.findViewById(R.id.imageViewClose_existing);


                pick_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = ((Activity) context).getIntent();

                        message = intent.getStringExtra(MESSAGE_KEY);

                        contest_reference.child(message).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Contests contests=snapshot.getValue(Contests.class);
                                if(contests!=null){
                                    String category=contests.getCategory();

                                    if(category.equals(image.getCategory())){
                                       // Toast.makeText(pick_image.getContext(), key, Toast.LENGTH_LONG).show();
                                   image_selected.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                               String image_id=dataSnapshot.getKey();
                                               Image image1=dataSnapshot.getValue(Image.class);
                                               if(image1.getImageUrl().equals(image.getImageUrl())){
                                                   //Toast.makeText(pick_image.getContext(), image_id, Toast.LENGTH_SHORT).show();
                                                   user = FirebaseAuth.getInstance().getCurrentUser();
                                                   userID = user.getUid();
                                                   ContestEntry contestEntry=new ContestEntry(message,userID,image_id,category,0,"Approved",0,0,0,0,1000000000,1000000000,1000000000);
                                                   String contest_entry_id=contest_submit_reference.push().getKey();
                                                   contest_submit_reference.child(contest_entry_id).setValue(contestEntry);
                                                   contest_submit_reference.child(contest_entry_id).child("date_submit").setValue(ServerValue.TIMESTAMP);

                                                   Toast.makeText(pick_image.getContext(), "Entry submitted successfully, redirecting to contests page", Toast.LENGTH_SHORT).show();


                                                   dialog.dismiss();
                                                   ((Activity)context).finish();
                                                   Intent intent=new Intent(pick_image.getContext(), NormalUser.class);
                                                   context.startActivity(intent);

                                               }
                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {

                                       }
                                   });
                                    }
                                    else{
                                        Toast.makeText(pick_image.getContext(), "Image category is different than the contest category.\nPlease select another one", Toast.LENGTH_LONG).show();

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
                        dialog.dismiss();
                    }
                });
                dialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.getWindow().setAttributes(layoutParams);
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

            dialog=new Dialog(itemView.getContext());
        }
    }
}

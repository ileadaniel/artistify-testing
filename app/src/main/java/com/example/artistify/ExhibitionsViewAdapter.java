package com.example.artistify;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.artistify.ModelClasses.Categories;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.ContestResults;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.NormalUser.myContestNormalUserAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ExhibitionsViewAdapter extends FirebaseRecyclerAdapter<ContestResults,ExhibitionsViewAdapter.myViewHolder> {

    private DatabaseReference contest_Reference= FirebaseDatabase.getInstance().getReference().child("Contests");
    private DatabaseReference contestCategory_Reference=FirebaseDatabase.getInstance().getReference().child("Categories");
    private DatabaseReference contestEntry_Reference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference users_Reference=FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference image_Reference=FirebaseDatabase.getInstance().getReference().child("Image");

    Dialog dialog_first;
    Dialog dialog_second;
    Dialog dialog_special;


    public ExhibitionsViewAdapter(@NonNull FirebaseRecyclerOptions<ContestResults> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull ContestResults model) {
        String contest_id=model.getContestID();

        contest_Reference.child(contest_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Contests contests=snapshot.getValue(Contests.class);
                if(contests!=null){
                    String contest_title=contests.getTitle();
                    String contest_description=contests.getDescription();
                    long contest_end_time=contests.getTimestamp_end();

                    String date = DateFormat.format("EEEE, MMMM dd, yyyy , HH:mm:ss", contest_end_time).toString();

                    String contest_category=contests.getCategory();

                    holder.contest_title_exhibition.setText(contest_title);
                    holder.contest_description_exhibition.setText(contest_description);
                    holder.contest_category_exhibition.setText(contest_category);
                    holder.contest_end_time_exhibition.setText(date);

                    contestCategory_Reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                Categories categories=dataSnapshot.getValue(Categories.class);
                                if(categories!=null) {
                                    String category_nm = categories.getCategory_name();
                                    if (category_nm.equals(contest_category)) {
                                        String category_image_url = categories.getCover_image_url();
//                                        Glide.with(holder.contest_cover_pic_exhibition.getContext()).load(category_image_url)
//                                                .placeholder(R.drawable.progress_animation).centerCrop().into(holder.contest_cover_pic_exhibition);

                                        Glide.with(holder.contest_cover_pic_exhibition.getContext()).load(category_image_url).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop()
                                                .dontAnimate().centerCrop()
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
                                                .into(holder.contest_cover_pic_exhibition);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String first_place_id=model.getFirstPlace();

        contestEntry_Reference.child(first_place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                if(contestEntry1!=null){
                    String users_id=contestEntry1.getUserID();
                    String image_id=contestEntry1.getImageID();

                    users_Reference.child(users_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if(user!=null){
                                String name=user.getFullName();
                                holder.first_place_name.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    image_Reference.child(image_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Image image = snapshot.getValue(Image.class);
                            if(image!=null){
                                String image_url=image.getImageUrl();
//                                Glide.with(holder.first_place_photo.getContext()).load(image_url)
//                                        .placeholder(R.drawable.progress_animation).into(holder.first_place_photo);
//
                                Glide.with(holder.first_place_photo.getContext()).load(image_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .dontAnimate()
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                holder.myProgressbar1.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(holder.first_place_photo);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String second_place_id=model.getSecondPlace();

        contestEntry_Reference.child(second_place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                if(contestEntry1!=null){
                    String users_id=contestEntry1.getUserID();
                    String image_id=contestEntry1.getImageID();

                    users_Reference.child(users_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if(user!=null){
                                String name=user.getFullName();
                                holder.second_place_name.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    image_Reference.child(image_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Image image = snapshot.getValue(Image.class);
                            if(image!=null){
                                String image_url=image.getImageUrl();
//                                Glide.with(holder.second_place_photo.getContext()).load(image_url)
//                                        .placeholder(R.drawable.progress_animation).into(holder.second_place_photo);

                                Glide.with(holder.second_place_photo.getContext()).load(image_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .dontAnimate()
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                holder.myProgressbar2.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(holder.second_place_photo);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        String special_place_id=model.getSpecialPlace();

        contestEntry_Reference.child(special_place_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                if(contestEntry1!=null){
                    String users_id=contestEntry1.getUserID();
                    String image_id=contestEntry1.getImageID();

                    users_Reference.child(users_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if(user!=null){
                                String name=user.getFullName();
                                holder.special_place_name.setText(name);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    image_Reference.child(image_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Image image = snapshot.getValue(Image.class);
                            if(image!=null){
                                String image_url=image.getImageUrl();
//                                Glide.with(holder.special_place_photo.getContext()).load(image_url)
//                                        .placeholder(R.drawable.progress_animation).into(holder.special_place_photo);

                                Glide.with(holder.special_place_photo.getContext()).load(image_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .dontAnimate()
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                holder.myProgressbar3.setVisibility(View.INVISIBLE);
                                                return false;
                                            }
                                        })
                                        .into(holder.special_place_photo);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        holder.view_hide_winners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expand_winners.getVisibility()==View.GONE){
                    holder.expand_winners.setVisibility(View.VISIBLE);
                    holder.view_hide_winners.setText("Hide winners");
                    holder.view_hide_winners.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.collapse_card,0);
                }
                else{
                    holder.expand_winners.setVisibility(View.GONE);
                    holder.view_hide_winners.setText("View winners");
                    holder.view_hide_winners.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expand_card,0);

                }

            }
        });

        holder.expand_first_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.first_place_photo.getVisibility()==View.GONE){
                    holder.frame1.setVisibility(View.VISIBLE);
                    holder.first_place_photo.setVisibility(View.VISIBLE);
                    holder.imageView_expand_1st_winner.setImageResource(R.drawable.collapse_card);
                }
                else{
                    holder.frame1.setVisibility(View.GONE);
                    holder.first_place_photo.setVisibility(View.GONE);
                    holder.imageView_expand_1st_winner.setImageResource(R.drawable.expand_card);
                }

            }
        });


        holder.first_place_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_first.setContentView(R.layout.image_layout_dialog);
                dialog_first.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView image_title = dialog_first.findViewById(R.id.image_title_selected_user);
                TextView image_category= dialog_first.findViewById(R.id.image_category_user);
                TextView date_submit= dialog_first.findViewById(R.id.date_submit_photo);
                LinearLayout linear_image_layout_buttons=dialog_first.findViewById(R.id.linear_image_layout_buttons);

                if(linear_image_layout_buttons.getVisibility()==View.VISIBLE){
                    linear_image_layout_buttons.setVisibility(View.GONE);
                }
                ImageView imageSelected=dialog_first.findViewById(R.id.imageViewSelected);
                ProgressBar myProgressbar=dialog_first.findViewById(R.id.progress_image_loader);

                contestEntry_Reference.child(first_place_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                        if(contestEntry1!=null) {
                            String image_id = contestEntry1.getImageID();
                            image_Reference.child(image_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Image image = snapshot.getValue(Image.class);
                                    if(image!=null){
                                        int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

                                        int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;

                                        String image_url=image.getImageUrl();
//                                        Glide.with(holder.first_place_photo).load(image_url)
//                                                .placeholder(R.drawable.progress_animation_profile)
//                                                .fitCenter().override(width_screen,height_screen).into(imageSelected);

                                        Glide.with(holder.first_place_photo).load(image_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .dontAnimate().fitCenter().override(width_screen,height_screen)
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


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                ImageView imageViewClose=dialog_first.findViewById(R.id.imageViewClose);

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_first.dismiss();
                    }
                });
                dialog_first.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog_first.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_first.getWindow().setAttributes(layoutParams);
            }
        });


        holder.second_place_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_second.setContentView(R.layout.image_layout_dialog);
                dialog_second.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView image_title = dialog_second.findViewById(R.id.image_title_selected_user);
                TextView image_category= dialog_second.findViewById(R.id.image_category_user);
                TextView date_submit= dialog_second.findViewById(R.id.date_submit_photo);
                LinearLayout linear_image_layout_buttons=dialog_second.findViewById(R.id.linear_image_layout_buttons);

                if(linear_image_layout_buttons.getVisibility()==View.VISIBLE){
                    linear_image_layout_buttons.setVisibility(View.GONE);
                }

                ImageView imageSelected=dialog_second.findViewById(R.id.imageViewSelected);
                ProgressBar myProgressbar=dialog_second.findViewById(R.id.progress_image_loader);

                contestEntry_Reference.child(second_place_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                        if(contestEntry1!=null) {
                            String image_id = contestEntry1.getImageID();
                            image_Reference.child(image_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Image image = snapshot.getValue(Image.class);
                                    if(image!=null){
                                        int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

                                        int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;

                                        String image_url=image.getImageUrl();
//                                        Glide.with(holder.second_place_photo).load(image_url)
//                                                .placeholder(R.drawable.progress_animation_profile)
//                                                .fitCenter().override(width_screen,height_screen).into(imageSelected);

                                        Glide.with(holder.second_place_photo).load(image_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .dontAnimate().fitCenter().override(width_screen,height_screen)
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


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                ImageView imageViewClose=dialog_second.findViewById(R.id.imageViewClose);

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_second.dismiss();
                    }
                });
                dialog_second.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog_second.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_second.getWindow().setAttributes(layoutParams);
            }
        });



        holder.special_place_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_special.setContentView(R.layout.image_layout_dialog);
                dialog_special.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView image_title = dialog_special.findViewById(R.id.image_title_selected_user);
                TextView image_category= dialog_special.findViewById(R.id.image_category_user);
                TextView date_submit= dialog_special.findViewById(R.id.date_submit_photo);
                LinearLayout linear_image_layout_buttons=dialog_special.findViewById(R.id.linear_image_layout_buttons);

                if(linear_image_layout_buttons.getVisibility()==View.VISIBLE){
                    linear_image_layout_buttons.setVisibility(View.GONE);
                }

                ImageView imageSelected=dialog_special.findViewById(R.id.imageViewSelected);
                ProgressBar myProgressbar=dialog_special.findViewById(R.id.progress_image_loader);

                contestEntry_Reference.child(special_place_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                        if(contestEntry1!=null) {
                            String image_id = contestEntry1.getImageID();
                            image_Reference.child(image_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Image image = snapshot.getValue(Image.class);
                                    if(image!=null){
                                        int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

                                        int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;

                                        String image_url=image.getImageUrl();
//                                        Glide.with(holder.special_place_photo).load(image_url)
//                                                .placeholder(R.drawable.progress_animation_profile)
//                                                .fitCenter().override(width_screen,height_screen).into(imageSelected);


                                        Glide.with(holder.special_place_photo).load(image_url).diskCacheStrategy(DiskCacheStrategy.NONE)
                                                .dontAnimate().fitCenter().override(width_screen,height_screen)
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


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                ImageView imageViewClose=dialog_special.findViewById(R.id.imageViewClose);

                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_special.dismiss();
                    }
                });
                dialog_special.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog_special.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_special.getWindow().setAttributes(layoutParams);
            }
        });




        holder.expand_second_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.second_place_photo.getVisibility()==View.GONE){
                    holder.frame2.setVisibility(View.VISIBLE);
                    holder.second_place_photo.setVisibility(View.VISIBLE);
                    holder.imageView_expand_2nd_winner.setImageResource(R.drawable.collapse_card);
                }
                else{
                    holder.frame2.setVisibility(View.GONE);
                    holder.second_place_photo.setVisibility(View.GONE);
                    holder.imageView_expand_2nd_winner.setImageResource(R.drawable.expand_card);
                }

            }
        });

        holder.expand_special_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.special_place_photo.getVisibility()==View.GONE){
                    holder.frame3.setVisibility(View.VISIBLE);
                    holder.special_place_photo.setVisibility(View.VISIBLE);
                    holder.imageView_expand_special_place_winner.setImageResource(R.drawable.collapse_card);
                }
                else{
                    holder.frame3.setVisibility(View.GONE);
                    holder.special_place_photo.setVisibility(View.GONE);
                    holder.imageView_expand_special_place_winner.setImageResource(R.drawable.expand_card);
                }

            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exhibition_view,parent,false);
        return new myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder{

        CardView card_view_exhibitions;

        private LinearLayout expand_winners,expand_first_place,expand_second_place,expand_special_place;

        ImageView contest_cover_pic_exhibition,imageView_expand_1st_winner,imageView_expand_2nd_winner,imageView_expand_special_place_winner;
        ImageView first_place_photo,second_place_photo,special_place_photo;

        TextView contest_title_exhibition,contest_description_exhibition,contest_category_exhibition,contest_end_time_exhibition,
        first_place_name,second_place_name,special_place_name;
        TextView view_hide_winners;
        ProgressBar myProgressbar,myProgressbar1,myProgressbar2,myProgressbar3;

        FrameLayout frame1,frame2,frame3;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            contest_cover_pic_exhibition=(ImageView)itemView.findViewById(R.id.contest_image_exhibition);
            myProgressbar=(ProgressBar)itemView.findViewById(R.id.progress_image_loader);
            myProgressbar1=(ProgressBar)itemView.findViewById(R.id.progress_image_loader_pic1);
            myProgressbar2=(ProgressBar)itemView.findViewById(R.id.progress_image_loader_pic2);
            myProgressbar3=(ProgressBar)itemView.findViewById(R.id.progress_image_loader_pic3);
            frame1=(FrameLayout)itemView.findViewById(R.id.frame_1);
            frame2=(FrameLayout)itemView.findViewById(R.id.frame_2);
            frame3=(FrameLayout)itemView.findViewById(R.id.frame_3);

            frame1.setVisibility(View.GONE);
            frame2.setVisibility(View.GONE);
            frame3.setVisibility(View.GONE);


            imageView_expand_1st_winner=(ImageView)itemView.findViewById(R.id.imageView_expand_1st_winner);
            imageView_expand_2nd_winner=(ImageView)itemView.findViewById(R.id.imageView_expand_2nd_winner);
            imageView_expand_special_place_winner=(ImageView)itemView.findViewById(R.id.imageView_expand_special_place_winner);
            first_place_photo=(ImageView)itemView.findViewById(R.id.first_place_photo);
            second_place_photo=(ImageView)itemView.findViewById(R.id.second_place_photo);
            special_place_photo=(ImageView)itemView.findViewById(R.id.special_place_photo);
            contest_title_exhibition=(TextView)itemView.findViewById(R.id.contest_title_exhibition);
            contest_description_exhibition=(TextView)itemView.findViewById(R.id.contest_description_exhibition);
            contest_category_exhibition=(TextView)itemView.findViewById(R.id.contest_category_exhibition);
            contest_end_time_exhibition=(TextView)itemView.findViewById(R.id.contest_end_time_exhibition);
            first_place_name=(TextView)itemView.findViewById(R.id.first_place_winner);
            second_place_name=(TextView)itemView.findViewById(R.id.second_place_winner);
            special_place_name=(TextView)itemView.findViewById(R.id.special_place_winner);
            view_hide_winners=(TextView)itemView.findViewById(R.id.view_hide_winners);
            expand_winners=(LinearLayout)itemView.findViewById(R.id.expandable_view_winners);
            expand_first_place=(LinearLayout)itemView.findViewById(R.id.linear_first_place);
            expand_second_place=(LinearLayout)itemView.findViewById(R.id.linear_second_place);
            expand_special_place=(LinearLayout)itemView.findViewById(R.id.linear_special_place);

            dialog_first=new Dialog(itemView.getContext());
            dialog_second=new Dialog(itemView.getContext());
            dialog_special=new Dialog(itemView.getContext());

            card_view_exhibitions=(CardView)itemView.findViewById(R.id.card_view_exhibitions);
        }
    }
}

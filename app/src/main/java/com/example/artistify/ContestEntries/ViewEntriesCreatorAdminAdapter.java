package com.example.artistify.ContestEntries;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ViewEntriesCreatorAdminAdapter extends RecyclerView.Adapter<ViewEntriesCreatorAdminAdapter.ViewHolder> {

    private Context context;
    private List<ContestEntry> mImage;
    private DatabaseReference userReference=FirebaseDatabase.getInstance().getReference().child("Users");
    Dialog dialog_submission;

    private FirebaseUser user;
    private String userID="";

    private String user_id="";

    private DatabaseReference contestEntryReference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");

    private DatabaseReference contestReference = FirebaseDatabase.getInstance().getReference().child("Contests");

    private int flag_normal_user_evaluator=0; // 0 - > contest creator | 1 - > admin

    private int flag_option_selected=0;

    public ViewEntriesCreatorAdminAdapter(Context context, List<ContestEntry> mImage) {
        this.context = context;
        this.mImage = mImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.photos_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewEntriesCreatorAdminAdapter.ViewHolder holder, int position) {
        ContestEntry contestEntry = mImage.get(position);
        String imageID=contestEntry.getImageID();


        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Image");
        databaseReference.child(imageID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Image image=snapshot.getValue(Image.class);
                if(image!=null){
//                    Glide.with(context).load(image.getImageUrl())
//                            .placeholder(R.drawable.progress_animation_profile).centerCrop().into(holder.uploaded_image);
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        contestEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String contestEntryID=dataSnapshot.getKey();
                    ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                    if(contestEntry1!=null){
                        String cont_id=contestEntry1.getContestID();
                        String user_id=contestEntry1.getUserID();
                        if(cont_id.equals(contestEntry.getContestID())&&user_id.equals(contestEntry.getUserID())){ //change with contestEntry.getUserID() if not working well
                            contestEntryReference.child(contestEntryID).child("rank").setValue(position+1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.uploaded_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_submission.setContentView(R.layout.contest_entry_cc_layout_dialog);
                dialog_submission.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView image_title_CC= dialog_submission.findViewById(R.id.image_title_selected_contest_creator);
                TextView entry_category_CC= dialog_submission.findViewById(R.id.image_category_contest_creator);
                TextView date_submit_CC= dialog_submission.findViewById(R.id.date_submit_photo_contest_creator);
                TextView manage_submission_CC= dialog_submission.findViewById(R.id.manage_submission_contest_creator);
                TextView actual_place_CC=dialog_submission.findViewById(R.id.actual_place_contest_creator);
                TextView points_CC=dialog_submission.findViewById(R.id.points_contest_creator);

                if(contestEntry.getEvaluation_points()>0||contestEntry.getAppeals_points()>0){
                    actual_place_CC.setText(String.valueOf(contestEntry.getRank()));
                }

                if(contestEntry.getAppeals_points()==0)
                {
                    points_CC.setText(String.valueOf(contestEntry.getEvaluation_points()));
                }
                else{
                    points_CC.setText(String.valueOf(contestEntry.getAppeals_points()));

                }

                contestReference.child(contestEntry.getContestID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Contests contests=snapshot.getValue(Contests.class);

                        if(contests!=null){
                            String status=contests.getStatus();
                            if(status.equals("Appeals")||status.equals("Finished")){
                                manage_submission_CC.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                TextView participant_name_CC= dialog_submission.findViewById(R.id.participant_name_contest_creator);

                String participant_id=contestEntry.getUserID();

                userReference.child(participant_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        if(user!=null){
                            String participant_name=user.fullName;
                            participant_name_CC.setText(participant_name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                ImageView imageSelected_CC= dialog_submission.findViewById(R.id.imageViewSelected_contest_creator);
                ProgressBar myProgressbar=dialog_submission.findViewById(R.id.progress_image_loader);

                ImageView imageViewClose= dialog_submission.findViewById(R.id.imageViewClose_contest_creator);

                long date_submitted=contestEntry.getDate_submit();

                String date = DateFormat.format("EEEE, MMMM dd, yyyy \nHH:mm:ss", date_submitted).toString();

                date_submit_CC.setText(date);

                databaseReference.child(imageID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Image image=snapshot.getValue(Image.class);
                        if(image!=null){
                            String title=image.getTitle();
                            if(!title.equals("")){
                                image_title_CC.setText(image.getTitle());
                            }
                            else{
                                image_title_CC.setText("No title");
                            }

                            String url=image.getImageUrl();


                            int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

                            int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;

//
//                            Glide.with(context).load(url)
//                                    .placeholder(R.drawable.progress_animation_profile)
//                                    .fitCenter().override(width_screen,height_screen).into(imageSelected_CC);

                            Glide.with(context).load(url).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop()
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
                                    .into(imageSelected_CC);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                entry_category_CC.setText(contestEntry.getCategory());

                if(flag_normal_user_evaluator==0){
                    manage_submission_CC.setText("Reject entry ");
                    manage_submission_CC.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.remove_vote_evaluator,0);

                }


                manage_submission_CC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag_normal_user_evaluator==0){
                            contestEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        String contestEntryID=dataSnapshot.getKey();
                                        ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                                        if(contestEntry1!=null){
                                            String cont_id=contestEntry1.getContestID();
                                            String user_id=contestEntry1.getUserID();
                                            if(cont_id.equals(contestEntry.getContestID())&&user_id.equals(contestEntry.getUserID())){
                                                showStatusEntryRejectedDialog(contestEntryID);
                                            }
                                        }
                                    }
                                }

                                private void showStatusEntryRejectedDialog(String contestEntryID) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(manage_submission_CC.getContext(),R.style.MyDialogTheme);
                                    builder.setTitle("Important!");
                                    builder.setMessage("Do you want to set status of this entry to rejected?\nAfter this action the reported entry will be invisible for the users");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contestEntryReference.child(contestEntryID).child("entry_status").setValue("Rejected");
                                            dialog.dismiss();
                                            dialog_submission.dismiss();

                                            Toast.makeText(manage_submission_CC.getContext(), "Status changed successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else if(flag_normal_user_evaluator==1){
                            contestEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        String contestEntryID=dataSnapshot.getKey();
                                        ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                                        if(contestEntry1!=null){
                                            String cont_id=contestEntry1.getContestID();
                                            user_id=contestEntry1.getUserID();
                                            if(cont_id.equals(contestEntry.getContestID())&&user_id.equals(contestEntry.getUserID())){
                                                showManageSubmissionDialog(contestEntryID);
                                            }
                                        }
                                    }
                                }

                                private void showManageSubmissionDialog(String contestEntryID) {
                                    int position = -1;
                                    String[] options = {"Set entry status: Rejected", "Set entry status and user status to Rejected"};
                                    AlertDialog.Builder builder = new AlertDialog.Builder(manage_submission_CC.getContext(), R.style.MyDialogTheme);
                                    builder.setTitle("Choose option");
                                    builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(which==0){
                                                flag_option_selected=1;
                                            }
                                            if(which==1) {
                                                flag_option_selected=2;
                                            }
                                        }
                                    });
                                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                           if(flag_option_selected==1){
                                               contestEntryReference.child(contestEntryID).child("entry_status").setValue("Rejected");
                                               Toast.makeText(manage_submission_CC.getContext(), "Status changed successfully", Toast.LENGTH_SHORT).show();
                                               dialog.dismiss();
                                               dialog_submission.dismiss();

                                           }
                                            if(flag_option_selected==2){
                                                contestEntryReference.child(contestEntryID).child("entry_status").setValue("Rejected");
                                                userReference.child(user_id).child("status").setValue("Rejected");
                                                Toast.makeText(manage_submission_CC.getContext(), "Status changed successfully", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                                dialog_submission.dismiss();
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                });


                imageViewClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_submission.dismiss();
                    }
                });
                dialog_submission.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog_submission.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_submission.getWindow().setAttributes(layoutParams);
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
            dialog_submission =new Dialog(itemView.getContext());


            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();

            userReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    if(user!=null){
                        int auth_level=user.authentication_level;
                        if(auth_level==3){
                            flag_normal_user_evaluator=1; //1 for admin
                        }
                        else if(auth_level==1){
                            flag_normal_user_evaluator=0; //0 for contest creator
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }
}

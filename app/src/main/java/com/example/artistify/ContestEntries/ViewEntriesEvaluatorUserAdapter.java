package com.example.artistify.ContestEntries;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.example.artistify.Authentication.UserDefault;
import com.example.artistify.Evaluator.ViewAppealEvaluator;
import com.example.artistify.ModelClasses.AppealRequest;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.ContestReports;
import com.example.artistify.ModelClasses.ContestResults;
import com.example.artistify.ModelClasses.ContestVotes;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.EvaluationResult;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.ModelClasses.ReviewRequest;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class ViewEntriesEvaluatorUserAdapter extends RecyclerView.Adapter<ViewEntriesEvaluatorUserAdapter.ViewHolder> {

    private Context context;
    private List<ContestEntry> mImage;
    private DatabaseReference contestEntryReference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference contestResultsReference=FirebaseDatabase.getInstance().getReference().child("Contest_results");
    private DatabaseReference contestVoteReference=FirebaseDatabase.getInstance().getReference().child("Contest_votes");
    private DatabaseReference contestReportReference=FirebaseDatabase.getInstance().getReference().child("Contest_reports");
    private DatabaseReference userReference=FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference contestReference=FirebaseDatabase.getInstance().getReference().child("Contests");
    private DatabaseReference contestEvaluationResult=FirebaseDatabase.getInstance().getReference().child("Evaluation_results");
    private DatabaseReference appeal_reference=FirebaseDatabase.getInstance().getReference().child("Appeal_requests");


    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    HashMap<Integer,String> ranking = new HashMap<Integer,String>();

    Dialog dialog_submission;
    Dialog evaluation_form;
    Dialog appeal_message_dialog;

    private FirebaseUser user;
    private String userID="";


    private int flag_normal_user_evaluator=0; // 0 - > normal user | 1 - > evaluator

    private int flag_evaluator_appeals_evaluator=0; // 0 - > evaluator | 1 - > appeals evaluator

    private int flag_apeal_number=0; // 1 - > appeal_1 | 2 - > appeal_2 | 3 - > appeal_3

    public ViewEntriesEvaluatorUserAdapter(Context context, List<ContestEntry> mImage) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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


        //update ranking of every contest entry when any user access the view entries layout

        Intent intent =((Activity)holder.uploaded_image.getContext()).getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);

        String[] message_received = message.split(",");

        if(contestEntry.getEvaluation_points()>0&&contestEntry.getAppeals_points()==0&&message_received[1].equals("evaluation")){
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
                                    contestEntryReference.child(contestEntryID).child("rank").setValue(position+1);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        }
        if(contestEntry.getEvaluation_points()>0&&contestEntry.getAppeals_points()>0&&message_received[1].equals("evaluation")){
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
                                contestEntryReference.child(contestEntryID).child("rank").setValue(position+1);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        holder.uploaded_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_submission.setContentView(R.layout.contest_entry_ev_nn_layout_dialog);
                dialog_submission.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView image_title_EVNN= dialog_submission.findViewById(R.id.image_title_selected_ev_nn);
                TextView entry_category_EVNN= dialog_submission.findViewById(R.id.image_category_ev_nn);
                TextView date_submit_EVNN= dialog_submission.findViewById(R.id.date_submit_photo_ev_nn);
                TextView vote_submission_EVNN= dialog_submission.findViewById(R.id.vote_entry_ev_nn);
                TextView report_submission_EVNN= dialog_submission.findViewById(R.id.report_entry_ev_nn);
                TextView default_info_EVNN= dialog_submission.findViewById(R.id.default_info_ev_nn);
                TextView participant_name_EVNN= dialog_submission.findViewById(R.id.participant_name_ev_nn);

                LinearLayout layout_place_entry_EVNN=dialog_submission.findViewById(R.id.linear_actual_place_ev);
                TextView actual_place_EVNN=dialog_submission.findViewById(R.id.actual_place_ev_nn);
                TextView points_entry_EVNN=dialog_submission.findViewById(R.id.points_ev_nn);

                if(contestEntry.getEvaluation_points()>0||contestEntry.getAppeals_points()>0){
                    if(message_received[1].equals("evaluation")){
                        actual_place_EVNN.setText(String.valueOf(position+1));
                    }
                    if(message_received[1].equals("appeals")){
                        actual_place_EVNN.setText(String.valueOf(contestEntry.getRank()));
                    }
                }


                Intent intent =((Activity)holder.uploaded_image.getContext()).getIntent();

                message = intent.getStringExtra(MESSAGE_KEY);

                String[] message_received = message.split(",");

                String participant_id=contestEntry.getUserID();

                userReference.child(participant_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user=snapshot.getValue(User.class);
                        if(user!=null){
                            String participant_name=user.fullName;
                            participant_name_EVNN.setText(participant_name);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                ImageView imageSelected_EVNN= dialog_submission.findViewById(R.id.imageViewSelected_ev_nn);
                ProgressBar myProgressbar=dialog_submission.findViewById(R.id.progress_image_loader);

                ImageView imageViewClose= dialog_submission.findViewById(R.id.imageViewClose_ev_nn);

                long date_submitted=contestEntry.getDate_submit();

                String date = DateFormat.format("EEEE, MMMM dd, yyyy \nHH:mm:ss", date_submitted).toString();

                date_submit_EVNN.setText(date);

                databaseReference.child(imageID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Image image=snapshot.getValue(Image.class);
                        if(image!=null){
                            String title=image.getTitle();
                            if(!title.equals("")){
                                image_title_EVNN.setText(image.getTitle());
                            }
                            else{
                                image_title_EVNN.setText("No title");
                            }

                            String url=image.getImageUrl();


                            int width_screen= Resources.getSystem().getDisplayMetrics().widthPixels;

                            int height_screen=Resources.getSystem().getDisplayMetrics().heightPixels;

//
//                            Glide.with(context).load(url)
//                                    .placeholder(R.drawable.progress_animation_profile)
//                                    .fitCenter().override(width_screen,height_screen).into(imageSelected_EVNN);

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
                                    .into(imageSelected_EVNN);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                entry_category_EVNN.setText(contestEntry.getCategory());


                //VOTING AND REPORT SYSTEM


                //verifying if evaluator voted a specific entry and make visible the linear layout with removing entry option

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
                                    contestEvaluationResult.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                EvaluationResult evaluationResult=dataSnapshot1.getValue(EvaluationResult.class);

                                                if(evaluationResult!=null){
                                                    String contest_id=evaluationResult.getContestID();
                                                    String entry_id=evaluationResult.getEntryID();
                                                    String evaluator_id=evaluationResult.getEvaluatorID();
                                                    if(contest_id.equals(contestEntry.getContestID())&&entry_id.equals(contestEntryID)&&evaluator_id.equals(userID)){
                                                        vote_submission_EVNN.setVisibility(View.GONE);
                                                        report_submission_EVNN.setVisibility(View.GONE);
                                                        default_info_EVNN.setText("Voted");
                                                        default_info_EVNN.setVisibility(View.VISIBLE);
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
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //verify if evaluator is not assigned as evaluator or appeals evaluator for this contest and set visibility of vote button to gone

                contestReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot2:snapshot.getChildren()){
                            String c_id=dataSnapshot2.getKey();
                            Contests contests=dataSnapshot2.getValue(Contests.class);
                            if(contests!=null){
                                String ev1_id=contests.getEvaluator1_id();
                                String ev2_id=contests.getEvaluator2_id();
                                String ev3_id=contests.getEvaluator3_id();
                                String a_ev1_id=contests.getAppeal_evaluator1_id();
                                String a_ev2_id=contests.getAppeal_evaluator2_id();
                                String a_ev3_id=contests.getAppeal_evaluator3_id();
                                String contest_status=contests.getStatus();
                                assert c_id != null;
                                if(flag_normal_user_evaluator==1) {
                                    if (c_id.equals(contestEntry.getContestID()) && contests.getStatus().equals("Evaluation")) {
                                        if (c_id.equals(contestEntry.getContestID()) && (ev1_id.equals(userID) || ev2_id.equals(userID) || ev3_id.equals(userID))) {
                                            vote_submission_EVNN.setVisibility(View.VISIBLE);
                                            flag_evaluator_appeals_evaluator=0;
                                        } else {
                                            vote_submission_EVNN.setVisibility(View.GONE);
                                        }
                                    } else if (c_id.equals(contestEntry.getContestID()) && contests.getStatus().equals("Appeals")) {
                                        if (c_id.equals(contestEntry.getContestID()) && (a_ev1_id.equals(userID) || a_ev2_id.equals(userID) || a_ev3_id.equals(userID))) {
                                            //vote_submission_EVNN.setVisibility(View.VISIBLE);
                                            if(message_received[1].equals("appeals")){
                                                vote_submission_EVNN.setVisibility(View.VISIBLE);
                                            }
                                            else{
                                                vote_submission_EVNN.setVisibility(View.GONE);
                                            }

                                            if(a_ev1_id.equals(userID)){
                                                flag_apeal_number=1;
                                            }
                                            else if(a_ev2_id.equals(userID)){
                                                flag_apeal_number=2;
                                            }
                                            else if(a_ev3_id.equals(userID)){
                                                flag_apeal_number=3;
                                            }
                                            flag_evaluator_appeals_evaluator=1;

                                        } else {
                                            vote_submission_EVNN.setVisibility(View.GONE);
                                        }
                                    } else {
                                        vote_submission_EVNN.setVisibility(View.GONE);
                                    }
                                }
                                if(contest_status.equals("Appeals")||contest_status.equals("Finished")){
                                    report_submission_EVNN.setVisibility(View.GONE);
                                }


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //verifying if normal user already voted a specific entry

                contestVoteReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            ContestVotes contestVotes=dataSnapshot.getValue(ContestVotes.class);
                            if(contestVotes!=null){
                                String uID=contestVotes.getUserID();
                                String cID=contestVotes.getContestID();
                                String eID=contestVotes.getEntryID();

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
                                                    if(uID.equals(userID)&&cID.equals(contestEntry.getContestID())&&eID.equals(contestEntryID)){
                                                        vote_submission_EVNN.setVisibility(View.GONE);
                                                        report_submission_EVNN.setVisibility(View.GONE);
                                                        default_info_EVNN.setText("Voted");
                                                        default_info_EVNN.setVisibility(View.VISIBLE);
                                                    }

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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //verifying if normal user or evaluator already reported a specific entry

                contestReportReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            ContestReports contestReports=dataSnapshot.getValue(ContestReports.class);
                            if(contestReports!=null){
                                String uID=contestReports.getUserID();
                                String cID=contestReports.getContestID();
                                String eID=contestReports.getEntryID();

                                contestEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                            String contestEntryID=dataSnapshot1.getKey();
                                            ContestEntry contestEntry1=dataSnapshot1.getValue(ContestEntry.class);
                                            if(contestEntry1!=null){
                                                String cont_id=contestEntry1.getContestID();
                                                String user_id=contestEntry1.getUserID();
                                                if(cont_id.equals(contestEntry.getContestID())&&user_id.equals(contestEntry.getUserID())){
                                                    if(uID.equals(userID)&&cID.equals(contestEntry.getContestID())&&eID.equals(contestEntryID)){
                                                        vote_submission_EVNN.setVisibility(View.GONE);
                                                        report_submission_EVNN.setVisibility(View.GONE);
                                                        default_info_EVNN.setText("Reported");
                                                        default_info_EVNN.setVisibility(View.VISIBLE);
                                                    }

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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                //show points of entry

                //final int[] points_of_entry = {0};
                final String[] contestEntry_ID = {""};
                contestEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            String contestEntryID=dataSnapshot.getKey();
                            //contestEntry_ID[0] =contestEntryID;
                            ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                            if(contestEntry1!=null){
                                String cont_id=contestEntry1.getContestID();
                                String user_id=contestEntry1.getUserID();
                                if(cont_id.equals(contestEntry.getContestID())&&user_id.equals(contestEntry.getUserID())){

                                    if(contestEntry1.getAppeals_points()>0){
                                        points_entry_EVNN.setText(contestEntry1.getAppeals_points()+ " / 75");
                                    }
                                    else if(contestEntry1.getAppeals_points()==0){
                                        points_entry_EVNN.setText(contestEntry1.getEvaluation_points()+ " / 75");
                                    }

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



                contestReference.child(contestEntry.getContestID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Contests contests=snapshot.getValue(Contests.class);
                        if(contests!=null){
                            long timestamp_contest_end=contests.getTimestamp_end();
                            TimeZone tz = TimeZone.getTimeZone("GMT+03");
                            Calendar c = Calendar.getInstance(tz);

                            long current_time=c.getTimeInMillis();

                            if(current_time>=timestamp_contest_end){
                                vote_submission_EVNN.setVisibility(View.GONE);
                            }
                            if((contests.getStatus().equals("Open")||contests.getStatus().equals("Appeals"))&&flag_normal_user_evaluator==0){
                                vote_submission_EVNN.setVisibility(View.GONE);
                                layout_place_entry_EVNN.setVisibility(View.GONE);
                            }
                            else if(contests.getStatus().equals("Evaluation")&&flag_normal_user_evaluator==0){
                                layout_place_entry_EVNN.setVisibility(View.GONE);
                            }
                            else{
                                layout_place_entry_EVNN.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if((contestEntry.getUserID().equals(userID)&&flag_normal_user_evaluator==0)){
                    vote_submission_EVNN.setVisibility(View.GONE);
                    report_submission_EVNN.setVisibility(View.GONE);
                    default_info_EVNN.setText("Your entry");
                    default_info_EVNN.setVisibility(View.VISIBLE);
                    layout_place_entry_EVNN.setVisibility(View.GONE);
                }
                String contest_id=contestEntry.getContestID();

                //contestReference.addListenerForSingleValueEvent();

                report_submission_EVNN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag_normal_user_evaluator==0){
                            int offTopic_count=contestEntry.getOffTopic_count();
                            if(offTopic_count<3){
                                offTopic_count++;
                            }
                            int finalOffTopic_count=offTopic_count;

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
                                                showReportDialog(contestEntryID);


                                            }
                                        }
                                    }
                                }

                                private void showReportDialog(String contestEntryID) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(vote_submission_EVNN.getContext(),R.style.MyDialogTheme);
                                    builder.setTitle("Important!");
                                    builder.setMessage("Do you want to report this entry as off-Topic Content?\nPlease keep in mind that after you report it you can't withdraw");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contestEntryReference.child(contestEntryID).child("offTopic_count").setValue(finalOffTopic_count);
                                            ContestReports contestReport=new ContestReports(userID,contestEntry.getContestID(),contestEntryID);
                                            String contest_report_id=contestReportReference.push().getKey();
                                            contestReportReference.child(contest_report_id).setValue(contestReport);
                                            dialog.dismiss();
                                            dialog_submission.dismiss();
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
                        if(flag_normal_user_evaluator==1){
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
                                                showReportEvaluatorDialog(contestEntryID);


                                            }
                                        }
                                    }
                                }

                                private void showReportEvaluatorDialog(String contestEntryID) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(vote_submission_EVNN.getContext(),R.style.MyDialogTheme);
                                    builder.setTitle("Important!");
                                    builder.setMessage("Do you want to report this entry as off-Topic Content?\nPlease keep in mind that after you report it you can't withdraw");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contestEntryReference.child(contestEntryID).child("offTopic_count").setValue(3);
                                            ContestReports contestReport=new ContestReports(userID,contestEntry.getContestID(),contestEntryID);
                                            String contest_report_id=contestReportReference.push().getKey();
                                            contestReportReference.child(contest_report_id).setValue(contestReport);
                                            dialog.dismiss();
                                            dialog_submission.dismiss();
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
                    }
                });

                vote_submission_EVNN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(flag_normal_user_evaluator==0){
                            int vote_count=contestEntry.getVote_count();

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
                                                showVoteDialog(contestEntryID);


                                            }
                                        }
                                    }
                                }

                                private void showVoteDialog(String contestEntryID) {
                                    AlertDialog.Builder builder=new AlertDialog.Builder(vote_submission_EVNN.getContext(),R.style.MyDialogTheme);
                                    builder.setTitle("Important!");
                                    builder.setMessage("Do you want to vote this entry?\nPlease keep in mind that after you vote you can't withdraw it");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            contestEntryReference.child(contestEntryID).child("vote_count").setValue(vote_count+1);
                                            ContestVotes contestVote=new ContestVotes(userID,contestEntry.getContestID(),contestEntryID);
                                            String contest_vote_id=contestVoteReference.push().getKey();
                                            contestVoteReference.child(contest_vote_id).setValue(contestVote);
                                            dialog.dismiss();
                                            dialog_submission.dismiss();
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
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()) {
                                        String contestEntryID = dataSnapshot.getKey();
                                        ContestEntry contestEntry1 = dataSnapshot.getValue(ContestEntry.class);
                                        if(contestEntry1!=null){
                                            String cont_id=contestEntry1.getContestID();
                                            String user_id=contestEntry1.getUserID();
                                            if(cont_id.equals(contestEntry.getContestID())&&user_id.equals(contestEntry.getUserID())){
                                                showVoteEvaluatorDialog(contestEntryID);


                                            }
                                        }
                                    }
                                }

                                private void showVoteEvaluatorDialog(String contestEntryID) {

                                    evaluation_form.setContentView(R.layout.evaluation_form_dialog);
                                    evaluation_form.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    TextView total_points_evaluation=evaluation_form.findViewById(R.id.nr_points_count_evaluation);
                                    TextView submit_form_button=evaluation_form.findViewById(R.id.submit_form_evaluator);

                                    final int[] total_points = {0};

                                    final int[] question_one_points = {0};
                                    final int[] question_two_points = {0};
                                    final int[] question_three_points = {0};
                                    final int[] question_four_points = {0};
                                    final int[] question_five_points = {0};

                                    //first question radio buttons


                                    RadioButton question_one_weak=evaluation_form.findViewById(R.id.radioButton_question_one_weak);
                                    RadioButton question_one_fair=evaluation_form.findViewById(R.id.radioButton_question_one_fair);
                                    RadioButton question_one_average=evaluation_form.findViewById(R.id.radioButton_question_one_average);
                                    RadioButton question_one_very_good=evaluation_form.findViewById(R.id.radioButton_question_one_very_good);
                                    RadioButton question_one_excellent=evaluation_form.findViewById(R.id.radioButton_question_one_excellent);

                                    question_one_weak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_one_points[0] =1;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_one_fair.setChecked(false);
                                                question_one_average.setChecked(false);
                                                question_one_very_good.setChecked(false);
                                                question_one_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_one_fair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_one_points[0] =2;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_one_weak.setChecked(false);
                                                question_one_average.setChecked(false);
                                                question_one_very_good.setChecked(false);
                                                question_one_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_one_average.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_one_points[0] =3;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_one_weak.setChecked(false);
                                                question_one_fair.setChecked(false);
                                                question_one_very_good.setChecked(false);
                                                question_one_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_one_very_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_one_points[0] =4;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_one_fair.setChecked(false);
                                                question_one_average.setChecked(false);
                                                question_one_weak.setChecked(false);
                                                question_one_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_one_excellent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_one_points[0] =5;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_one_fair.setChecked(false);
                                                question_one_average.setChecked(false);
                                                question_one_very_good.setChecked(false);
                                                question_one_weak.setChecked(false);
                                            }
                                        }
                                    });


                                    //second question radio buttons


                                    RadioButton question_two_weak=evaluation_form.findViewById(R.id.radioButton_question_two_weak);
                                    RadioButton question_two_fair=evaluation_form.findViewById(R.id.radioButton_question_two_fair);
                                    RadioButton question_two_average=evaluation_form.findViewById(R.id.radioButton_question_two_average);
                                    RadioButton question_two_very_good=evaluation_form.findViewById(R.id.radioButton_question_two_very_good);
                                    RadioButton question_two_excellent=evaluation_form.findViewById(R.id.radioButton_question_two_excellent);

                                    question_two_weak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_two_points[0] =1;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_two_fair.setChecked(false);
                                                question_two_average.setChecked(false);
                                                question_two_very_good.setChecked(false);
                                                question_two_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_two_fair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_two_points[0] =2;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_two_weak.setChecked(false);
                                                question_two_average.setChecked(false);
                                                question_two_very_good.setChecked(false);
                                                question_two_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_two_average.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_two_points[0] =3;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_two_weak.setChecked(false);
                                                question_two_fair.setChecked(false);
                                                question_two_very_good.setChecked(false);
                                                question_two_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_two_very_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_two_points[0] =4;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_two_fair.setChecked(false);
                                                question_two_average.setChecked(false);
                                                question_two_weak.setChecked(false);
                                                question_two_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_two_excellent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_two_points[0] =5;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_two_fair.setChecked(false);
                                                question_two_average.setChecked(false);
                                                question_two_very_good.setChecked(false);
                                                question_two_weak.setChecked(false);
                                            }
                                        }
                                    });


                                    //third question radio buttons


                                    RadioButton question_three_weak=evaluation_form.findViewById(R.id.radioButton_question_three_weak);
                                    RadioButton question_three_fair=evaluation_form.findViewById(R.id.radioButton_question_three_fair);
                                    RadioButton question_three_average=evaluation_form.findViewById(R.id.radioButton_question_three_average);
                                    RadioButton question_three_very_good=evaluation_form.findViewById(R.id.radioButton_question_three_very_good);
                                    RadioButton question_three_excellent=evaluation_form.findViewById(R.id.radioButton_question_three_excellent);

                                    question_three_weak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_three_points[0] =1;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_three_fair.setChecked(false);
                                                question_three_average.setChecked(false);
                                                question_three_very_good.setChecked(false);
                                                question_three_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_three_fair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_three_points[0] = 2;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_three_weak.setChecked(false);
                                                question_three_average.setChecked(false);
                                                question_three_very_good.setChecked(false);
                                                question_three_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_three_average.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_three_points[0] = 3;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_three_weak.setChecked(false);
                                                question_three_fair.setChecked(false);
                                                question_three_very_good.setChecked(false);
                                                question_three_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_three_very_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_three_points[0] = 4;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_three_fair.setChecked(false);
                                                question_three_average.setChecked(false);
                                                question_three_weak.setChecked(false);
                                                question_three_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_three_excellent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_three_points[0] = 5;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_three_fair.setChecked(false);
                                                question_three_average.setChecked(false);
                                                question_three_very_good.setChecked(false);
                                                question_three_weak.setChecked(false);
                                            }
                                        }
                                    });


                                    //fourth question radio buttons


                                    RadioButton question_four_weak=evaluation_form.findViewById(R.id.radioButton_question_four_weak);
                                    RadioButton question_four_fair=evaluation_form.findViewById(R.id.radioButton_question_four_fair);
                                    RadioButton question_four_average=evaluation_form.findViewById(R.id.radioButton_question_four_average);
                                    RadioButton question_four_very_good=evaluation_form.findViewById(R.id.radioButton_question_four_very_good);
                                    RadioButton question_four_excellent=evaluation_form.findViewById(R.id.radioButton_question_four_excellent);

                                    question_four_weak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_four_points[0] =1;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_four_fair.setChecked(false);
                                                question_four_average.setChecked(false);
                                                question_four_very_good.setChecked(false);
                                                question_four_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_four_fair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_four_points[0] = 2;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_four_weak.setChecked(false);
                                                question_four_average.setChecked(false);
                                                question_four_very_good.setChecked(false);
                                                question_four_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_four_average.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_four_points[0] = 3;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_four_weak.setChecked(false);
                                                question_four_fair.setChecked(false);
                                                question_four_very_good.setChecked(false);
                                                question_four_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_four_very_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_four_points[0] = 4;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_four_fair.setChecked(false);
                                                question_four_average.setChecked(false);
                                                question_four_weak.setChecked(false);
                                                question_four_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_four_excellent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_four_points[0] = 5;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_four_fair.setChecked(false);
                                                question_four_average.setChecked(false);
                                                question_four_very_good.setChecked(false);
                                                question_four_weak.setChecked(false);
                                            }
                                        }
                                    });

                                    //fifth question radio buttons



                                    RadioButton question_five_weak=evaluation_form.findViewById(R.id.radioButton_question_five_weak);
                                    RadioButton question_five_fair=evaluation_form.findViewById(R.id.radioButton_question_five_fair);
                                    RadioButton question_five_average=evaluation_form.findViewById(R.id.radioButton_question_five_average);
                                    RadioButton question_five_very_good=evaluation_form.findViewById(R.id.radioButton_question_five_very_good);
                                    RadioButton question_five_excellent=evaluation_form.findViewById(R.id.radioButton_question_five_excellent);

                                    question_five_weak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_five_points[0] =1;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_five_fair.setChecked(false);
                                                question_five_average.setChecked(false);
                                                question_five_very_good.setChecked(false);
                                                question_five_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_five_fair.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_five_points[0] = 2;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_five_weak.setChecked(false);
                                                question_five_average.setChecked(false);
                                                question_five_very_good.setChecked(false);
                                                question_five_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_five_average.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_five_points[0] = 3;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_five_weak.setChecked(false);
                                                question_five_fair.setChecked(false);
                                                question_five_very_good.setChecked(false);
                                                question_five_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_five_very_good.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_five_points[0] = 4;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_five_fair.setChecked(false);
                                                question_five_average.setChecked(false);
                                                question_five_weak.setChecked(false);
                                                question_five_excellent.setChecked(false);
                                            }
                                        }
                                    });
                                    question_five_excellent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            if(isChecked){
                                                question_five_points[0] = 5;
                                                total_points[0] =0;
                                                total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];
                                                total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");
                                                question_five_fair.setChecked(false);
                                                question_five_average.setChecked(false);
                                                question_five_very_good.setChecked(false);
                                                question_five_weak.setChecked(false);
                                            }
                                        }
                                    });

                                    //calculate total points
                                    total_points[0] =question_one_points[0]+question_two_points[0]+question_three_points[0]+question_four_points[0]+question_five_points[0];




                                    total_points_evaluation.setText(String.valueOf(total_points[0])+" / 25");

                                    submit_form_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if(question_one_weak.isChecked()||question_one_fair.isChecked()||question_one_average.isChecked()||question_one_very_good.isChecked()||question_one_excellent.isChecked())
                                            {
                                                if(question_two_weak.isChecked()||question_two_fair.isChecked()||question_two_average.isChecked()||question_two_very_good.isChecked()||question_two_excellent.isChecked())
                                                {
                                                    if(question_three_weak.isChecked()||question_three_fair.isChecked()||question_three_average.isChecked()||question_three_very_good.isChecked()||question_three_excellent.isChecked())
                                                    {
                                                        if(question_four_weak.isChecked()||question_four_fair.isChecked()||question_four_average.isChecked()||question_four_very_good.isChecked()||question_four_excellent.isChecked())
                                                        {
                                                            if(question_five_weak.isChecked()||question_five_fair.isChecked()||question_five_average.isChecked()||question_five_very_good.isChecked()||question_five_excellent.isChecked())
                                                            {

                                                                EvaluationResult evaluationResult=new EvaluationResult(contestEntry.getContestID(),contestEntryID,userID,question_one_points[0],question_two_points[0],
                                                                        question_three_points[0],question_four_points[0],question_five_points[0],total_points[0]);
                                                                String contest_evaluation_result_id=contestEvaluationResult.push().getKey();
                                                                contestEvaluationResult.child(contest_evaluation_result_id).setValue(evaluationResult);

                                                                contestEntryReference.child(contestEntryID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        ContestEntry contestEntry1=snapshot.getValue(ContestEntry.class);
                                                                        if(contestEntry1!=null){
                                                                            long order=contestEntry1.getOrder();

                                                                            if(flag_evaluator_appeals_evaluator==0){
                                                                                int evaluation_points=contestEntry1.getEvaluation_points();

                                                                                int total_vote_points=evaluation_points+total_points[0];
                                                                                long order_update=order-total_points[0];

                                                                                contestEntryReference.child(contestEntryID).child("evaluation_points").setValue(total_vote_points);
                                                                                contestEntryReference.child(contestEntryID).child("order").setValue(order_update);
                                                                                evaluation_form.dismiss();
                                                                                dialog_submission.dismiss();
                                                                                ((Activity)holder.uploaded_image.getContext()).finish();
                                                                                Intent intent=new Intent(vote_submission_EVNN.getContext(), ViewEntriesEvaluatorUser.class);
                                                                                intent.putExtra(MESSAGE_KEY,message);
                                                                                v.getContext().startActivity(intent);
                                                                            }
                                                                            else if(flag_evaluator_appeals_evaluator==1){
                                                                                int appeals_points=contestEntry1.getAppeals_points();

                                                                                int total_vote_points=appeals_points+total_points[0];
                                                                                long order_update=1000000000-total_vote_points;

                                                                                contestEntryReference.child(contestEntryID).child("appeals_points").setValue(total_vote_points);
                                                                                contestEntryReference.child(contestEntryID).child("order").setValue(order_update);

                                                                                appeal_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                                                            String appeal_id=dataSnapshot.getKey();
                                                                                            AppealRequest appealRequest=dataSnapshot.getValue(AppealRequest.class);
                                                                                            if(appealRequest!=null){
                                                                                                String contest_id=appealRequest.getContestID();
                                                                                                String entry_id=appealRequest.getEntryID();
                                                                                                String user_id=appealRequest.getUserID();

                                                                                                if(contest_id.equals(contestEntry.getContestID())&&entry_id.equals(contestEntryID)&&user_id.equals(contestEntry.getUserID())){

                                                                                                   if(flag_apeal_number==1){
                                                                                                       appeal_reference.child(appeal_id).child("evaluation1_status").setValue("Reviewed");
                                                                                                       showAppealMessageDialog(contestEntryID,appeal_id);

                                                                                                   }
                                                                                                   else if(flag_apeal_number==2){
                                                                                                       appeal_reference.child(appeal_id).child("evaluation2_status").setValue("Reviewed");
                                                                                                       showAppealMessageDialog(contestEntryID,appeal_id);

                                                                                                   }
                                                                                                   else if(flag_apeal_number==3){
                                                                                                       appeal_reference.child(appeal_id).child("evaluation3_status").setValue("Reviewed");
                                                                                                       showAppealMessageDialog(contestEntryID,appeal_id);
                                                                                                   }
                                                                                                    //appeal_reference.child(appeal_id).child("eval")
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
                                                                    }

                                                                    private void showAppealMessageDialog(String contestEntryID,String appeal_id) {
                                                                        appeal_message_dialog.setContentView(R.layout.appeal_message_dialog);
                                                                        appeal_message_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                                                        TextView cancel_dialog=appeal_message_dialog.findViewById(R.id.appeal_review_cancel_button);
                                                                        TextView send_message=appeal_message_dialog.findViewById(R.id.appeal_review_send_button);
                                                                        EditText appeal_message=appeal_message_dialog.findViewById(R.id.appeal_review_request_message);

                                                                        send_message.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                String message_to_send=appeal_message.getText().toString();

                                                                                if(flag_apeal_number==1){
                                                                                    appeal_reference.child(appeal_id).child("evaluator1_answer").setValue(message_to_send);
                                                                                }
                                                                                else if(flag_apeal_number==2){
                                                                                    appeal_reference.child(appeal_id).child("evaluator2_answer").setValue(message_to_send);
                                                                                }
                                                                                else if(flag_apeal_number==3){
                                                                                    appeal_reference.child(appeal_id).child("evaluator3_answer").setValue(message_to_send);
                                                                                }

                                                                                appeal_reference.child(appeal_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                        AppealRequest appealRequest=snapshot.getValue(AppealRequest.class);
                                                                                        if(appealRequest!=null){
                                                                                            if(!appealRequest.getEvaluator1_answer().equals("Not answered")&&!appealRequest.getEvaluator2_answer().equals("Not answered")&&!appealRequest.getEvaluator3_answer().equals("Not answered")
                                                                                            &&!appealRequest.getEvaluation1_status().equals("Pending")&&!appealRequest.getEvaluation3_status().equals("Pending")&&!appealRequest.getEvaluation3_status().equals("Pending")){
                                                                                                appeal_reference.child(appeal_id).child("status").setValue("Reviewed");
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                    }
                                                                                });
                                                                                Toast.makeText(send_message.getContext(), "Message sent successfully!", Toast.LENGTH_LONG).show();
                                                                                appeal_message_dialog.dismiss();
                                                                                evaluation_form.dismiss();
                                                                                dialog_submission.dismiss();
                                                                                ((Activity)holder.uploaded_image.getContext()).finish();
                                                                                Intent intent=new Intent(vote_submission_EVNN.getContext(), ViewAppealEvaluator.class);
                                                                                intent.putExtra(MESSAGE_KEY,message);
                                                                                v.getContext().startActivity(intent);
                                                                            }
                                                                        });

                                                                        //appeal_reference.child(appeal_id).child()
                                                                        cancel_dialog.setOnClickListener(new View.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(View v) {
                                                                                appeal_message_dialog.dismiss();
                                                                            }
                                                                        });

                                                                        appeal_message_dialog.show();
                                                                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                                                                        layoutParams.copyFrom(appeal_message_dialog.getWindow().getAttributes());
                                                                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                                                        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                                                                        appeal_message_dialog.getWindow().setAttributes(layoutParams);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                   });



                                                            }
                                                            else{
                                                                Toast.makeText(submit_form_button.getContext(), "You did not answer question 5", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        else{
                                                            Toast.makeText(submit_form_button.getContext(), "You did not answer question 4", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else{
                                                        Toast.makeText(submit_form_button.getContext(), "You did not answer question 3", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                else{
                                                    Toast.makeText(submit_form_button.getContext(), "You did not answer question 2", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else{
                                                Toast.makeText(submit_form_button.getContext(), "You did not answer question 1", Toast.LENGTH_SHORT).show();
                                            }





                                        }
                                    });

                                    ImageView imageViewClose= evaluation_form.findViewById(R.id.imageViewClose_form);

                                    imageViewClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            evaluation_form.dismiss();
                                        }
                                    });

                                    evaluation_form.show();
                                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                                    layoutParams.copyFrom(evaluation_form.getWindow().getAttributes());
                                    layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                                    evaluation_form.getWindow().setAttributes(layoutParams);

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
            dialog_submission = new Dialog(itemView.getContext());
            evaluation_form = new Dialog(itemView.getContext());
            appeal_message_dialog = new Dialog(itemView.getContext());

            user = FirebaseAuth.getInstance().getCurrentUser();
            userID = user.getUid();

            userReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    if(user!=null){
                        int auth_level=user.authentication_level;
                        if(auth_level==2){
                            flag_normal_user_evaluator=1; //1 for evaluator
                        }
                        else if(auth_level==0){
                            flag_normal_user_evaluator=0; //0 for normal user
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            //ranking


//            for(Map.Entry me : ranking.entrySet()){
//                Toast.makeText(itemView.getContext(), "Points "+me.getKey()+" entry id: "+me.getValue(), Toast.LENGTH_SHORT).show();
//            }
        }
    }
}

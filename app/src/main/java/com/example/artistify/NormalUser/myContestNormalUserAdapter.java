package com.example.artistify.NormalUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.service.controls.Control;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.artistify.Authentication.Home;
import com.example.artistify.ContestCreator.InviteEvaluator;
import com.example.artistify.ContestEntries.ViewEntriesEvaluatorUser;
import com.example.artistify.ModelClasses.AppealRequest;
import com.example.artistify.ModelClasses.Categories;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.ContestReports;
import com.example.artistify.ModelClasses.ContestVotes;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.R;
import com.example.artistify.UploadPicture;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class myContestNormalUserAdapter extends FirebaseRecyclerAdapter<Contests,myContestNormalUserAdapter.myViewHolder> {

    private DatabaseReference category_id_selected;
    private DatabaseReference contest_selected;

    private DatabaseReference reference_contest_entry=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference reference_contest_votes=FirebaseDatabase.getInstance().getReference().child("Contest_votes");
    private DatabaseReference reference_contest_reports=FirebaseDatabase.getInstance().getReference().child("Contest_reports");
    private DatabaseReference reference_contest=FirebaseDatabase.getInstance().getReference().child("Contests");
    private DatabaseReference reference_appeal_request=FirebaseDatabase.getInstance().getReference().child("Appeal_requests");
    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    private DatabaseReference reference;
    private FirebaseUser user;
    private String userID="";

    private int flag_joined=0;

    Dialog dialog_appeal_info;

    public myContestNormalUserAdapter(@NonNull FirebaseRecyclerOptions<Contests> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Contests model) {
        holder.contest_title_user.setText(model.getTitle());
        holder.contest_description_user.setText(model.getDescription());
        String category=model.getCategory();
        holder.contest_category_user.setText(model.getCategory());
        holder.contest_status_user.setText(model.getStatus());



        category_id_selected= FirebaseDatabase.getInstance().getReference().child("Categories");
        category_id_selected.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Categories categories=dataSnapshot.getValue(Categories.class);
                    if(categories!=null) {
                        String category_nm = categories.getCategory_name();
                        if (category_nm.equals(category)) {
                            String category_image_url = categories.getCover_image_url();
//                            Glide.with(holder.contest_cover_pic_user.getContext()).load(category_image_url)
//                                    .placeholder(R.drawable.progress_animation).centerCrop().into(holder.contest_cover_pic_user);

                            Glide.with(holder.contest_cover_pic_user.getContext()).load(category_image_url)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().dontAnimate()
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
                                    .into(holder.contest_cover_pic_user);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.more_less_info_view_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expand.getVisibility()==View.GONE){
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.more_less_info_view_user.setText("Less info");
                    holder.more_less_info_view_user.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.collapse_card,0);
                }
                else {
                    holder.expand.setVisibility(View.GONE);
                    holder.more_less_info_view_user.setText("More info");
                    holder.more_less_info_view_user.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expand_card,0);

                }
            }
        });



        holder.join_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //contest_selected=FirebaseDatabase.getInstance().getReference().child("Contests").getRef(position).getKey();
                if(holder.join_contest.getText().equals("Join")) {
                    showOptionDialog(getRef(position).getKey());
                }
                else if(holder.join_contest.getText().equals("Submit appeal")){
                    showAppealSubmitDialog();
                }
                else if(holder.join_contest.getText().equals("View appeal")){
                    reference_contest_entry.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String contestEntryID=dataSnapshot.getKey();
                                ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                                if(contestEntry1!=null){
                                    String contestID=contestEntry1.getContestID();
                                    String user_ID=contestEntry1.getUserID();
                                    if(contestID.equals(getRef(position).getKey())&&user_ID.equals(userID)){
                                        showAppealInfoDialog(getRef(position).getKey(),contestEntryID,userID);
                                    }
                                }
                            }
                        }

                        private void showAppealInfoDialog(String key, String contestEntryID, String userID) {
                            dialog_appeal_info.setContentView(R.layout.appeal_request_view);
                            dialog_appeal_info.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            TextView appeal_entry_status=dialog_appeal_info.findViewById(R.id.request_entry_status);
                            TextView evaluator_1_message=dialog_appeal_info.findViewById(R.id.evaluator1_message);
                            TextView evaluator_2_message=dialog_appeal_info.findViewById(R.id.evaluator2_message);
                            TextView evaluator_3_message=dialog_appeal_info.findViewById(R.id.evaluator3_message);
                            TextView cancel_review_dialog=dialog_appeal_info.findViewById(R.id.appeal_review_request_cancel_button);

                            reference_appeal_request.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                            AppealRequest appealRequest=dataSnapshot.getValue(AppealRequest.class);
                                            if(appealRequest!=null){
                                                String status=appealRequest.getStatus();
                                                String message1=appealRequest.getEvaluator1_answer();
                                                String message2=appealRequest.getEvaluator2_answer();
                                                String message3=appealRequest.getEvaluator3_answer();

                                                appeal_entry_status.setText(status);
                                                evaluator_1_message.setText(message1);
                                                evaluator_2_message.setText(message2);
                                                evaluator_3_message.setText(message3);

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            cancel_review_dialog.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_appeal_info.dismiss();
                                }
                            });
                            dialog_appeal_info.show();
                            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                            layoutParams.copyFrom(dialog_appeal_info.getWindow().getAttributes());
                            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                            //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                            dialog_appeal_info.getWindow().setAttributes(layoutParams);


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            private void showAppealSubmitDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.join_contest.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Appeal request");
                builder.setCancelable(false);
                builder.setMessage("Do you want to send an appeal request for your entry?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reference_contest_entry.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    String contestEntryID=dataSnapshot.getKey();
                                    ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                                    if(contestEntry1!=null){
                                        String contestID=contestEntry1.getContestID();
                                        String user_ID=contestEntry1.getUserID();
                                        if(contestID.equals(getRef(position).getKey())&&user_ID.equals(userID)){
                                            AppealRequest appealRequest = new AppealRequest(getRef(position).getKey(),userID,contestEntryID,"Pending","Pending","Not answered"
                                                    ,"Pending","Not answered","Pending","Not answered");
                                            String contest_appeal_request_id = reference_appeal_request.push().getKey();
                                            reference_appeal_request.child(contest_appeal_request_id).setValue(appealRequest);
                                            holder.join_contest.setText("View appeal");
                                            dialog.dismiss();
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
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                builder.show();
            }

            private void showOptionDialog(String contest) {
                int position = -1;
                String[] options = {"Existing photos in account", "This device"};
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.join_contest.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Choose from where to pick the photo:");
                builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            Intent intent=new Intent(holder.join_contest.getContext(),ExistingPhotos.class);
                            intent.putExtra(MESSAGE_KEY,contest);
                            holder.join_contest.getContext().startActivity(intent);
                            dialog.dismiss();
                        }
                        if(which==1){
                            Intent intent=new Intent(holder.join_contest.getContext(), UploadPicture.class);
                            intent.putExtra(MESSAGE_KEY,contest);
                            holder.join_contest.getContext().startActivity(intent);
                            dialog.dismiss();
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
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference_contest_entry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                    if(contestEntry1!=null) {
                        String parent=dataSnapshot.getKey();
                        String contestid = contestEntry1.getContestID();
                        String userid = contestEntry1.getUserID();
                        if (contestid.equals(getRef(position).getKey()) && userid.equals(userID)) {

                            if(model.getStatus().equals("Open")||model.getStatus().equals("Evaluation")||model.getStatus().equals("Finished")) {
                                //make join button gone
                                holder.join_contest.setVisibility(View.GONE);
                                holder.join_ended.setText("Joined");
                                holder.join_ended.setPadding(40, 0, 40, 0);
                                holder.join_ended.setVisibility(View.VISIBLE);
                            }
                            else if(model.getStatus().equals("Appeals")){
                                if(holder.contest_status_user.getText().equals("Appeals")){
                                    holder.join_contest.setVisibility(View.VISIBLE);
                                    holder.join_ended.setVisibility(View.GONE);
                                    holder.join_contest.setText("Submit appeal");
                                }
                                else if(holder.contest_status_user.getText().equals("Finish stage")){
                                    holder.join_contest.setVisibility(View.GONE);
                                    holder.join_ended.setVisibility(View.VISIBLE);
                                    holder.join_ended.setText("Joined");

                                }


                                if(holder.join_contest.getText().equals("Submit appeal")){
                                    reference_appeal_request.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                                    AppealRequest appealRequest=dataSnapshot.getValue(AppealRequest.class);
                                                    if(appealRequest!=null){
                                                        String contest_id=appealRequest.getContestID();
                                                        String users_id=appealRequest.getUserID();
//                                                        String entry_id=appealRequest.getEntryID();
//                                                        Toast.makeText(holder.join_contest.getContext(), contest_id+" "+users_id, Toast.LENGTH_SHORT).show();
                                                        if(contest_id.equals(getRef(position).getKey())&&users_id.equals(userID)){
                                                            //Toast.makeText(holder.join_contest.getContext(), "wow", Toast.LENGTH_SHORT).show();
                                                            holder.join_contest.setText("View appeal");
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



                            //Toast.makeText(holder.join_contest.getContext(), String.valueOf(timestamp_submited), Toast.LENGTH_SHORT).show();
                            reference_contest.child(Objects.requireNonNull(getRef(position).getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Contests contests = snapshot.getValue(Contests.class);
                                    if (contests != null) {
                                        long timestamp_submited = contestEntry1.getDate_submit();
                                        long timestamp_end = contests.getTimestamp_end();
                                        long diff = timestamp_submited - timestamp_end;

                                        if (diff > 0) {
                                            //Toast.makeText(holder.join_contest.getContext(), "Problem at: "+parent+", timestamp end "+timestamp_submited, Toast.LENGTH_SHORT).show();
                                            reference.child(userID).child("status").setValue("Rejected");
                                            assert parent != null;
                                            reference_contest_entry.child(parent).child("entry_status").setValue("Rejected");
                                            final Handler handler = new Handler(Looper.getMainLooper());
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if(!((Activity) holder.join_contest.getContext()).isFinishing())
                                                    {
                                                        showAllertAccountRejected();
                                                    }

                                                }
                                            }, 1000);

                                        } else {

                                            //do nothing, everything ok
                                        }
                                    }
                                }

                                private void showAllertAccountRejected() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.join_contest.getContext(), R.style.MyDialogTheme);
                                    builder.setTitle("Alert!");
                                    builder.setCancelable(false);
                                    builder.setMessage("You tried to enter contest after time ended by changing device hour, we don't allow cheating here. Your account is now Rejected, admin will review your account!" +
                                            "\nPress -Cancel- button to close the app!");
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();

                                            ((Activity) holder.join_contest.getContext()).finish();

                                            System.exit(1);

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
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        if(model.getTimestamp_start()>System.currentTimeMillis()&&model.getStatus().equals("Open")){
            TimeZone tz = TimeZone.getTimeZone("GMT+03");
            Calendar c = Calendar.getInstance(tz);

            holder.contest_status_user.setText("Starting soon");

            Timestamp timestamp1=new Timestamp(model.getTimestamp_start());
            Timestamp timestamp2=new Timestamp(c.getTime().getTime());

            long milliseconds=timestamp1.getTime()-timestamp2.getTime();


            new CountDownTimer(milliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int seconds = (int) millisUntilFinished / 1000;


                    int days = seconds/(24*3600);

                    seconds = seconds % (24*3600);
                    int hours = seconds / 3600;

                    int minutes = (seconds % 3600) / 60;

                    seconds = (seconds % 3600) % 60;

                    String time_formatted;
                    if(days>0){
                        time_formatted = String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds",days, hours, minutes, seconds);
                    }
                    else{
                        if(hours>0){
                            time_formatted = String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds);
                        }
                        else{
                            if(minutes>0){
                                time_formatted = String.format(Locale.getDefault(), "%02dm %02ds", minutes, seconds);
                            }
                            else{
                                time_formatted = String.format(Locale.getDefault(), "%02ds", seconds);
                            }
                        }
                    }

                    holder.contest_time_left_user.setText(time_formatted);
                    holder.join_contest.setVisibility(View.GONE);
                    holder.join_ended.setText("Join pending");
                    holder.join_ended.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {

                }
            }.start();

        }
        else if(model.getTimestamp_start()<System.currentTimeMillis()&&(model.getStatus().equals("Open")||model.getStatus().equals("Evaluation")||model.getStatus().equals("Appeals"))){

            TimeZone tz = TimeZone.getTimeZone("GMT+03");
            Calendar c = Calendar.getInstance(tz);



            Timestamp timestamp1=new Timestamp(model.getTimestamp_end());
            Timestamp timestamp2=new Timestamp(c.getTime().getTime());

            long milliseconds=timestamp1.getTime()-timestamp2.getTime();


            new CountDownTimer(milliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int seconds = (int) millisUntilFinished / 1000;


                    int days = seconds/(24*3600);

                    seconds = seconds % (24*3600);
                    int hours = seconds / 3600;

                    int minutes = (seconds % 3600) / 60;

                    seconds = (seconds % 3600) % 60;

                    String time_formatted;
                    if(days>0){
                        time_formatted = String.format(Locale.getDefault(), "%02dd %02dh %02dm %02ds",days, hours, minutes, seconds);
                    }
                    else{
                        if(hours>0){
                            time_formatted = String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds);
                        }
                        else{
                            if(minutes>0){
                                time_formatted = String.format(Locale.getDefault(), "%02dm %02ds", minutes, seconds);
                            }
                            else{
                                time_formatted = String.format(Locale.getDefault(), "%02ds", seconds);
                            }
                        }
                    }
                    holder.contest_time_left_user.setText(time_formatted);
                }

                @Override
                public void onFinish() {
                    holder.contest_time_left_user.setText("time over");
                    if(holder.join_contest.getText().equals("Submit appeal")||holder.join_contest.getText().equals("View appeal")){

                    }
                    else{
                        holder.join_contest.setVisibility(View.GONE);
                        holder.join_ended.setText("Joining ended");
                        holder.join_ended.setPadding(40, 20, 40, 20);
                        holder.join_ended.setVisibility(View.VISIBLE);
                    }

                    if(model.getStatus().equals("Open")){
                        holder.contest_status_user.setText("Pending evaluation");
                        holder.join_contest.setVisibility(View.GONE);
                        holder.join_ended.setText("Joining ended");
                        holder.join_ended.setPadding(40, 20, 40, 20);
                        holder.join_ended.setVisibility(View.VISIBLE);
                    }
                    else if(model.getStatus().equals("Evaluation")){
                        holder.contest_status_user.setText("Pending appeals");
                        holder.join_contest.setVisibility(View.GONE);
                        holder.join_ended.setText("Joining ended");
                        holder.join_ended.setPadding(40, 20, 40, 20);
                        holder.join_ended.setVisibility(View.VISIBLE);
                    }
                    else if(model.getStatus().equals("Appeals")){
                        holder.contest_status_user.setText("Finish stage");
                    }

                }
            }.start();

        }
        else{
            holder.contest_time_left_user.setText("N/A");
            holder.join_contest.setVisibility(View.GONE);
            holder.join_ended.setText("Joining ended");
            holder.join_ended.setVisibility(View.VISIBLE);
        }
        if(model.getStatus().equals("Evaluation")){
            holder.join_contest.setVisibility(View.GONE);
            holder.join_ended.setText("Joining ended");
            holder.join_ended.setPadding(40, 20, 40, 20);
            holder.join_ended.setVisibility(View.VISIBLE);
        }

        reference_contest_entry.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);
                    if(contestEntry1!=null){
                        String contestID=contestEntry1.getContestID();
                        String users_id=contestEntry1.getUserID();
                        if(contestID.equals(getRef(position).getKey())&&users_id.equals(userID)){
                            String status=contestEntry1.getEntry_status();
                            int evaluation_points=contestEntry1.getEvaluation_points();
                            int rank=contestEntry1.getRank();
                            if(evaluation_points>0&&(model.getStatus().equals("Appeals")||model.getStatus().equals("Finished"))){
                               holder.rank_points.setVisibility(View.VISIBLE);

                                if(contestEntry1.getAppeals_points()>0){
                                    holder.evaluation_points.setText(contestEntry1.getAppeals_points()+ " / 75");
                                }
                                else if(contestEntry1.getAppeals_points()==0){
                                    holder.evaluation_points.setText(contestEntry1.getEvaluation_points()+ " / 75");
                                }

                               if(rank!=1000000000){
                                   holder.entry_rank.setText(String.valueOf(rank));
                               }
                               else{
                                   holder.entry_rank.setText("Not set yet");
                               }
                            }
                            holder.entry_status_user.setText(status);
                            if(status.equals("Rejected")){
                                holder.remove_rejected_submission.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.remove_rejected_submission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference_contest_entry.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                            String entryID=dataSnapshot.getKey();
                            ContestEntry contestEntry1=dataSnapshot.getValue(ContestEntry.class);

                            if(contestEntry1!=null){
                                String contestID=contestEntry1.getContestID();
                                String users_id=contestEntry1.getUserID();
                                if(contestID.equals(getRef(position).getKey())&&users_id.equals(userID)){
                                    String status=contestEntry1.getEntry_status();
                                    holder.entry_status_user.setText(status);
                                    if(status.equals("Rejected")){
                                        showRemoveRejectedSubmissionDialog(entryID);
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

            private void showRemoveRejectedSubmissionDialog(String entryID) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.remove_rejected_submission.getContext(),R.style.MyDialogTheme);
                builder.setTitle("Important!");
                builder.setMessage("After removing this rejected entry you can submit another one using the Join button\nChoose Yes if you want to do it!");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        reference_contest_entry.child(entryID).removeValue();

                        reference_contest_reports.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    String contest_report_id=dataSnapshot.getKey();
                                    ContestReports contestReports=dataSnapshot.getValue(ContestReports.class);
                                    if(contestReports!=null){
                                        String contestID=contestReports.getContestID();
                                        String entryID=contestReports.getEntryID();
                                        if(contestID.equals(getRef(position).getKey())&&entryID.equals(entryID)){
                                            reference_contest_reports.child(contest_report_id).removeValue();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        dialog.dismiss();
                        ((Activity)holder.remove_rejected_submission.getContext()).finish();
                        Intent intent=new Intent(holder.remove_rejected_submission.getContext(), NormalUser.class);
                        holder.remove_rejected_submission.getContext().startActivity(intent);
                        Toast.makeText(holder.remove_rejected_submission.getContext(), "Entry removed successfully", Toast.LENGTH_SHORT).show();
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
        });



        holder.view_vote_entries_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.view_vote_entries_user.getContext(), ViewEntriesEvaluatorUser.class);
                String message_to_send=getRef(position).getKey()+","+"evaluation";
                intent.putExtra(MESSAGE_KEY,message_to_send);
                v.getContext().startActivity(intent);
            }
        });


    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_view_normal_user,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout expand,rank_points;

        ImageView contest_cover_pic_user;
        ProgressBar myProgressbar;
        TextView contest_title_user,contest_description_user,contest_category_user,contest_time_left_user,contest_status_user,entry_status_user,remove_rejected_submission,entry_rank,evaluation_points;
        TextView more_less_info_view_user;
        TextView join_ended;
        Button join_contest;
        Button view_vote_entries_user;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            contest_cover_pic_user=(ImageView)itemView.findViewById(R.id.contest_image_normal_user);
            myProgressbar=(ProgressBar)itemView.findViewById(R.id.progress_image_loader);
            contest_title_user=(TextView)itemView.findViewById(R.id.contest_title_normal_user);
            contest_description_user=(TextView)itemView.findViewById(R.id.contest_description_normal_user);
            contest_category_user=(TextView)itemView.findViewById(R.id.contest_category_normal_user);
            contest_time_left_user=(TextView)itemView.findViewById(R.id.contest_time_left_normal_user);
            contest_status_user=(TextView)itemView.findViewById(R.id.contest_status_normal_user);
            entry_status_user=(TextView)itemView.findViewById(R.id.entry_status_normal_user);
            expand=(LinearLayout)itemView.findViewById(R.id.expandable_view_normal_user);
            more_less_info_view_user=(TextView)itemView.findViewById(R.id.more_less_info_normal_user);
            join_ended=(TextView)itemView.findViewById(R.id.join_ended_user);
            join_contest=(Button)itemView.findViewById(R.id.join_contest_btn);
            view_vote_entries_user=(Button)itemView.findViewById(R.id.view_vote_entries_normal_user);
            remove_rejected_submission=(TextView)itemView.findViewById(R.id.remove_rejected_submission);
            rank_points=(LinearLayout)itemView.findViewById(R.id.linear_rank_points_normal_user);
            entry_rank=(TextView)itemView.findViewById(R.id.entry_rank_normal_user);
            evaluation_points=(TextView)itemView.findViewById(R.id.evaluation_points_normal_user);

            dialog_appeal_info=new Dialog(itemView.getContext());
        }
    }

}

package com.example.artistify.ContestCreator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.telephony.CellSignalStrength;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.artistify.ContestEntries.ViewEntriesCreatorAdmin;
import com.example.artistify.ModelClasses.Categories;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.ContestReports;
import com.example.artistify.ModelClasses.ContestResults;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.EvaluationRequest;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class myContestCreatorAdapter extends FirebaseRecyclerAdapter<Contests,myContestCreatorAdapter.myViewHolder> {

    private DatabaseReference contest_info;
    private DatabaseReference reference_contest_selected;
    private DatabaseReference array_adapter;
    private DatabaseReference ev_name_selected=FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference contestEntries_reference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference contestReports_reference=FirebaseDatabase.getInstance().getReference().child("Contest_reports");
    private DatabaseReference contestResults_reference=FirebaseDatabase.getInstance().getReference().child("Contest_results");
    private DatabaseReference contestInvites_reference=FirebaseDatabase.getInstance().getReference().child("Evaluation_requests");
    private DatabaseReference image_Reference=FirebaseDatabase.getInstance().getReference().child("Image");
    private DatabaseReference contestReference=FirebaseDatabase.getInstance().getReference().child("Contests");
    private String userID;


    Dialog duration_picker_dialog;
    ArrayAdapter<String> myAdapter;
    private String category_data="";


    int minute_to_milliseconds=60000;
    int hour_to_milliseconds=3600000;
    int day_to_milliseconds=86400000;



    private final static String MESSAGE_KEY="com.example.message_key";


    private String category_selected="";
    private String image_selected_id="";

   // private int nr_entries=0;
    private int update_button_click_counter=0;

    private int flag_correct_evaluation_timing=0;
    private int flag_correct_appeals_timing=0;

    //private Context context;



    public myContestCreatorAdapter(@NonNull FirebaseRecyclerOptions<Contests> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Contests model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.status.setText(model.getStatus());
       // String ev_name_id=model.getEvaluator_id();

        final int[] nr_entries = {0};
        final int[] nr_reports = {0};
        final long[] timestamp_start_contest = {model.getTimestamp_start()};
        final long[] timestamp_end_contest = {model.getTimestamp_end()};
        long timestamp_evaluation_duration=0;
        long timestamp_appeals_duration=0;

        contestEntries_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ContestEntry contestEntry=dataSnapshot.getValue(ContestEntry.class);
                    if(contestEntry!=null){
                        String contest_id=contestEntry.getContestID();
                        int offTopic_count=contestEntry.getOffTopic_count();
                        if(holder.getAdapterPosition()!=-1){
                        if(contest_id.equals(getRef(holder.getAdapterPosition()).getKey())&&offTopic_count==3){ //position replaced with holder.getAdapterPosition
                            nr_reports[0]++;
                        }
                        else{

                        }
                        }
                    }
                }
                holder.reports_number.setText(String.valueOf(nr_reports[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        contestEntries_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ContestEntry contestEntry=dataSnapshot.getValue(ContestEntry.class);
                    if(contestEntry!=null){
                        String contest_id=contestEntry.getContestID();
                        int off_topic_count=contestEntry.getOffTopic_count();
                        String entry_status=contestEntry.getEntry_status();
                        if(holder.getAdapterPosition()!=-1){
                        if(contest_id.equals(getRef(holder.getAdapterPosition()).getKey())&&off_topic_count<3&&entry_status.equals("Approved")){//position replaced with holder.getAdapterPosition
                            nr_entries[0]++;
                        }
                        else{

                        }
                        }
                    }
                }
                holder.entries_number.setText(String.valueOf(nr_entries[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        if(model.getStatus().equals("Open")||model.getStatus().equals("Evaluation")||model.getStatus().equals("Appeals")){
            holder.select_new.setVisibility(View.GONE);
            holder.info_evaluator_selection.setVisibility(View.GONE);
            holder.text_view_evaluator.setVisibility(View.GONE);
            holder.entries.setVisibility(View.VISIBLE);
            holder.update_contest.setText("Contest running");
        }
        if(model.getStatus().equals("Finished")){
            holder.select_new.setVisibility(View.GONE);
            holder.info_evaluator_selection.setVisibility(View.GONE);
            holder.text_view_evaluator.setVisibility(View.GONE);
            holder.entries.setVisibility(View.VISIBLE);
            holder.update_contest.setText("Contest finished");
            holder.update_contest.setEnabled(false);
        }


        if(model.getStatus().equals("Draft")||model.getStatus().equals("Pending")||model.getStatus().equals("Rejected")){
            holder.pick_contest_end.setVisibility(View.VISIBLE);
            holder.pick_contest_start.setVisibility(View.VISIBLE);
            holder.pick_evaluation_duration.setVisibility(View.VISIBLE);
            holder.pick_appeals_duration.setVisibility(View.VISIBLE);
            holder.linear_edit.setVisibility(View.VISIBLE);
            holder.linear_delete.setVisibility(View.VISIBLE);
        }else{
            holder.linear_edit.setVisibility(View.GONE);
            holder.linear_delete.setVisibility(View.GONE);
            holder.pick_contest_end.setVisibility(View.GONE);
            holder.pick_contest_start.setVisibility(View.GONE);
            holder.pick_evaluation_duration.setVisibility(View.GONE);
            holder.pick_appeals_duration.setVisibility(View.GONE);
        }

        if(!model.getEvaluator1_id().equals("Not assigned")) {
            ev_name_selected.child(model.getEvaluator1_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        holder.accepted_evaluator.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ev_name_selected.keepSynced(true);
        }
        else {
            holder.accepted_evaluator.setText(model.getEvaluator1_id());
        }

        if(!model.getEvaluator2_id().equals("Not assigned")) {
            ev_name_selected.child(model.getEvaluator2_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        holder.accepted_evaluator2.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ev_name_selected.keepSynced(true);
        }
        else {
            holder.accepted_evaluator2.setText(model.getEvaluator2_id());
        }

        if(!model.getEvaluator3_id().equals("Not assigned")) {
            ev_name_selected.child(model.getEvaluator3_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        holder.accepted_evaluator3.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ev_name_selected.keepSynced(true);
        }
        else {
            holder.accepted_evaluator3.setText(model.getEvaluator3_id());
        }

        if(!model.getAppeal_evaluator1_id().equals("Not assigned")) {
            ev_name_selected.child(model.getAppeal_evaluator1_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        holder.accepted_appeals_evaluator1.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ev_name_selected.keepSynced(true);
        }
        else {
            holder.accepted_appeals_evaluator1.setText(model.getAppeal_evaluator1_id());
        }

        if(!model.getAppeal_evaluator2_id().equals("Not assigned")) {
            ev_name_selected.child(model.getAppeal_evaluator2_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        holder.accepted_appeals_evaluator2.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ev_name_selected.keepSynced(true);
        }
        else {
            holder.accepted_appeals_evaluator2.setText(model.getAppeal_evaluator2_id());
        }

        if(!model.getAppeal_evaluator3_id().equals("Not assigned")) {
            ev_name_selected.child(model.getAppeal_evaluator3_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);

                    if (user != null) {
                        holder.accepted_appeals_evaluator3.setText(user.getFullName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ev_name_selected.keepSynced(true);
        }
        else {
            holder.accepted_appeals_evaluator3.setText(model.getAppeal_evaluator3_id());
        }

        String status_contest=model.getStatus();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Categories");

        contest_info=FirebaseDatabase.getInstance().getReference().child("Contests").child(Objects.requireNonNull(getRef(position).getKey()));
        contest_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Contests contests =snapshot.getValue(Contests.class);

                    if(contests!=null) {
                        category_data = contests.getCategory();


                        if (!category_data.equals("Not assigned")) {
                            holder.category_txtView.setText(model.getCategory());
                            // holder.category_spinner.setSelection(myAdapter.getPosition(category_data));
                            //Toast.makeText(holder.contest_img.getContext(), category_data, Toast.LENGTH_SHORT).show();
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Categories categories = postSnapshot.getValue(Categories.class);
                    String category_s = categories.getCategory_name();
                    if (category_s.equals(model.getCategory())) {
                        //Toast.makeText(holder.contest_img.getContext(), category_s, Toast.LENGTH_SHORT).show();
                    String image_selected_id = categories.getCover_image_url();
//                    Glide.with(holder.contest_img.getContext()).load(image_selected_id)
//                            .placeholder(R.drawable.progress_animation).centerCrop().into(holder.contest_img);

                        Glide.with(holder.contest_img.getContext()).load(image_selected_id)
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
                                .into(holder.contest_img);
                    //Toast.makeText(holder.contest_img.getContext(),category_selected, Toast.LENGTH_SHORT).show();
               }
            }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.select_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(model.getStatus().equals("Open")||model.getStatus().equals("Evaluation")||model.getStatus().equals("Finished")){
                    Toast.makeText(holder.select_new.getContext(), "You can't select/view another evaluator(s) if contest status is different than Pending or Rejected", Toast.LENGTH_LONG).show();
                }
                else{
                    holder.expand.setVisibility(View.GONE);
                    holder.more_info.setText("More info");
                    Intent intent=new Intent(holder.more_info.getContext(), InviteEvaluator.class);
                    intent.putExtra(MESSAGE_KEY,getRef(position).getKey());
                    v.getContext().startActivity(intent);
                }
            }
        });

        holder.more_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expand.getVisibility()==View.GONE){
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.more_info.setText("Less info");
                }
                else
                {
                    holder.expand.setVisibility(View.GONE);
                    holder.more_info.setText("More info");
                }
            }
        });

        holder.delete_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteContestDialog(getRef(position).getKey());
            }

            private void showDeleteContestDialog(String key) {
                AlertDialog.Builder builder= new AlertDialog.Builder(holder.delete_contest.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Delete contest?");
                builder.setMessage("You are about to delete this contest, do you really want to perform this action?" +
                        "\nPlease note that after you delete the contest all invitations will be deleted as well.");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        contestReference.child(key).removeValue();
                        contestInvites_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                        String invite_id=dataSnapshot.getKey();
                                        EvaluationRequest evaluationRequest=dataSnapshot.getValue(EvaluationRequest.class);
                                        if(evaluationRequest!=null){
                                            String contests_inv_id=evaluationRequest.getContest_id();
                                            if(contests_inv_id.equals(key)){
                                                contestInvites_reference.child(invite_id).removeValue();
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(holder.update_contest.getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        holder.edit_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status_contest.equals("Draft")||status_contest.equals("Pending")||status_contest.equals("Rejected")){
                    if (update_button_click_counter == 0) {
                        Toast.makeText(holder.edit_contest.getContext(), "Edit mode ON", Toast.LENGTH_SHORT).show();
                        holder.title.setEnabled(true);
                        holder.description.setEnabled(true);
                        holder.category_txtView.setVisibility(View.GONE);
                        holder.category_spinner.setVisibility(View.VISIBLE);
                        holder.category_spinner.setEnabled(true);
                        holder.update_contest.setEnabled(true);
//                        holder.pick_contest_start.setVisibility(View.VISIBLE);
//                        if(model.getTimestamp_start()!=0){
//                            holder.pick_contest_end.setVisibility(View.VISIBLE);
//                        }
//
//                        holder.pick_evaluation_duration.setVisibility(View.VISIBLE);
//                        holder.pick_appeals_duration.setVisibility(View.VISIBLE);
                        holder.more_info.setText("Less info");
                        holder.expand.setVisibility(View.VISIBLE);

                        update_button_click_counter=1;
                        holder.edit_contest.setImageResource(R.drawable.ic_baseline_clear_24);


                        holder.category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        category_selected = "Graphite drawings";
                                        break;
                                    case 1:
                                        category_selected = "Colored pencil drawings";
                                        break;
                                    case 2:
                                        category_selected = "Oil paintings";
                                        break;
                                    case 3:
                                        category_selected = "Acrylic paintings";
                                        break;
                                    case 4:
                                        category_selected = "Watercolor paintings";
                                        break;
                                    case 5:
                                        category_selected = "Sculpture";
                                        break;

                                }




                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot postSnapshot: snapshot.getChildren()){
                                            Categories categories=postSnapshot.getValue(Categories.class);
                                            String category_s=categories.getCategory_name();
                                            if(category_s.equals(category_selected)) {
                                                String image_selected_id = categories.getCover_image_url();
//                                                Glide.with(holder.contest_img.getContext()).load(image_selected_id)
//                                                        .placeholder(R.drawable.progress_animation).centerCrop().into(holder.contest_img);


                                                Glide.with(holder.contest_img.getContext()).load(image_selected_id)
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
                                                        .into(holder.contest_img);
                                                //Toast.makeText(holder.contest_img.getContext(),category_selected, Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }


                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                    else if(update_button_click_counter==1){
                        Toast.makeText(holder.edit_contest.getContext(), "Edit mode OFF", Toast.LENGTH_SHORT).show();
                        holder.title.setEnabled(false);
                        holder.description.setEnabled(false);
                        holder.category_spinner.setEnabled(false);
                        holder.category_spinner.setVisibility(View.GONE);
                        holder.category_txtView.setVisibility(View.VISIBLE);
                        holder.category_txtView.setText(model.getCategory());
                        holder.update_contest.setEnabled(false);
                        holder.edit_contest.setImageResource(R.drawable.edit_button);
//                        holder.pick_contest_start.setVisibility(View.GONE);
//                        holder.pick_contest_end.setVisibility(View.GONE);
//                        holder.pick_evaluation_duration.setVisibility(View.GONE);
//                        holder.pick_appeals_duration.setVisibility(View.GONE);
                        holder.expand.setVisibility(View.GONE);
                        holder.more_info.setText("More info");
                        update_button_click_counter=0;
                    }
                }
//                else{
//                    holder.title.setEnabled(false);
//                    holder.description.setEnabled(false);
//                    holder.category_spinner.setVisibility(View.GONE);
//                    holder.category_txtView.setVisibility(View.VISIBLE);
//                    holder.category_txtView.setText(model.getCategory());
//                    holder.category_spinner.setEnabled(false);
//                    holder.update_contest.setEnabled(false);
//                    holder.edit_contest.setImageResource(R.drawable.edit_button);
//                    Toast.makeText(holder.edit_contest.getContext(), "Contest can't be edited", Toast.LENGTH_SHORT).show();
//                }


            }
        });

        if(model.getTimestamp_start()!=0){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy, HH:mm");

            holder.contest_start_time.setText(simpleDateFormat.format(model.getTimestamp_start()));
        }
        else{
            holder.contest_start_time.setText("Not assigned");
        }
        if(model.getTimestamp_end()!=0){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy, HH:mm");

            holder.contest_end_time.setText(simpleDateFormat.format(model.getTimestamp_end()));
        }
        else{
            holder.contest_end_time.setText("Not assigned");
        }
        if(model.getEvaluation_duration()!=0){
            int seconds = (int) model.getEvaluation_duration() / 1000;


            int days = seconds/(24*3600);

            seconds = seconds % (24*3600);
            int hours = seconds / 3600;

            int minutes = (seconds % 3600) / 60;

            String time_formatted="";
            if(days>0){
                time_formatted = String.format(Locale.getDefault(), "%02dd %02dh %02dm",days, hours, minutes);
            }
            else{
                time_formatted = String.format(Locale.getDefault(), "%02dh %02dm",hours, minutes);
            }
            //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd d - HH h - mm m");

            holder.contest_evaluation_duration.setText(time_formatted);
        }
        else{
            holder.contest_evaluation_duration.setText("Not assigned");
        }
        if(model.getAppeal_duration()!=0){
            int seconds = (int) model.getAppeal_duration() / 1000;


            int days = seconds/(24*3600);

            seconds = seconds % (24*3600);
            int hours = seconds / 3600;

            int minutes = (seconds % 3600) / 60;

            String time_formatted="";
            if(days>0){
                time_formatted = String.format(Locale.getDefault(), "%02dd %02dh %02dm",days, hours, minutes);
            }
            else{
                time_formatted = String.format(Locale.getDefault(), "%02dh %02dm",hours, minutes);
            }
            //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd days - HH h - mm m");

            holder.contest_appeals_duration.setText(time_formatted);
        }
        else{
            holder.contest_appeals_duration.setText("Not assigned");
        }


        holder.pick_contest_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference_contest_selected=FirebaseDatabase.getInstance().getReference().child("Contests").child(getRef(position).getKey());

                showDateTimeStartDialog(holder.contest_start_time);
            }

            private void showDateTimeStartDialog(TextView contest_start_time) {
                final Calendar calendar=Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                calendar.set(Calendar.MINUTE,minute);

                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy, HH:mm");

                                timestamp_start_contest[0] =calendar.getTimeInMillis();
                                timestamp_end_contest[0] =model.getTimestamp_end();
                                if(timestamp_end_contest[0] ==0|| timestamp_start_contest[0] < timestamp_end_contest[0]){
                                    contest_start_time.setText(simpleDateFormat.format(calendar.getTime()));
                                    reference_contest_selected.child("timestamp_start").setValue(timestamp_start_contest[0]);
                                }
                                else if(timestamp_end_contest[0] == timestamp_start_contest[0]){
                                    Toast.makeText(holder.pick_contest_end.getContext(), "Contest start time and end time can't be the same, please pick again", Toast.LENGTH_SHORT).show();
                                }
                                else if(timestamp_end_contest[0] < timestamp_start_contest[0]){
                                    Toast.makeText(holder.pick_contest_end.getContext(), "Contest start time can't be after end time, please pick again", Toast.LENGTH_SHORT).show();
                                }
                                //contest_start_time.setText(simpleDateFormat.format(calendar.getTime()));
//                                if(calendar.getTimeInMillis()<Calendar.getInstance().getTimeInMillis()){
//                                    Toast.makeText(holder.pick_contest_start.getContext(), "You picked past time! Please pick again.", Toast.LENGTH_SHORT).show();
//                                }

                            }
                        };
                        TimePickerDialog timePickerDialog=new TimePickerDialog(holder.pick_contest_start.getContext(),R.style.datepicker,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                        timePickerDialog.show();

                        //new TimePickerDialog(holder.pick_contest_start.getContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(holder.pick_contest_start.getContext(),R.style.datepicker,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);
                datePickerDialog.show();
            }
        });


        if(update_button_click_counter==1){
            holder.pick_contest_end.setVisibility(View.VISIBLE);
        }


        holder.pick_contest_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference_contest_selected=FirebaseDatabase.getInstance().getReference().child("Contests").child(getRef(position).getKey());

                showDateTimeStartDialog(holder.contest_end_time);
            }

            private void showDateTimeStartDialog(TextView contest_end_time) {
                final Calendar calendar=Calendar.getInstance();
                DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                calendar.set(Calendar.MINUTE,minute);

                                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy, HH:mm");

                                timestamp_end_contest[0] =calendar.getTimeInMillis();
                                timestamp_start_contest[0] =model.getTimestamp_start();

                                if(timestamp_end_contest[0] > timestamp_start_contest[0]){
                                    contest_end_time.setText(simpleDateFormat.format(calendar.getTime()));
                                    reference_contest_selected.child("timestamp_end").setValue(timestamp_end_contest[0]);
                                }
                                else if(timestamp_end_contest[0] == timestamp_start_contest[0]){
                                    Toast.makeText(holder.pick_contest_end.getContext(), "Contest start time and end time can't be the same, please pick again", Toast.LENGTH_SHORT).show();
                                }
                                else if(timestamp_end_contest[0] < timestamp_start_contest[0]){
                                    Toast.makeText(holder.pick_contest_end.getContext(), "Contest end time can't be before start time, please pick again", Toast.LENGTH_SHORT).show();
                                }

                            }
                        };

                        TimePickerDialog timePickerDialog=new TimePickerDialog(holder.pick_contest_start.getContext(),R.style.datepicker,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                        timePickerDialog.show();
                    }
                };
                DatePickerDialog datePickerDialog=new DatePickerDialog(holder.pick_contest_start.getContext(),R.style.datepicker,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);
                datePickerDialog.show();
            }
        });



        holder.update_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference_contest_selected=FirebaseDatabase.getInstance().getReference().child("Contests").child(getRef(position).getKey());


                if(holder.update_contest.getText().equals("Enter Evaluation")){
                    //Toast.makeText(holder.update_contest.getContext(), "Not implemented yet - Evaluation", Toast.LENGTH_SHORT).show();

                    reference_contest_selected.child("status").setValue("Evaluation");

                    String contest_result_id = contestResults_reference.push().getKey();
                    ContestResults contestResult = new ContestResults(getRef(position).getKey(), "", "", "","invisible");
                    contestResults_reference.child(contest_result_id).setValue(contestResult);

                    TimeZone tz = TimeZone.getTimeZone("GMT+03");
                    Calendar c = Calendar.getInstance(tz);
                    c.setTimeInMillis(System.currentTimeMillis() + model.getEvaluation_duration());

                    reference_contest_selected.child("timestamp_end").setValue(c.getTimeInMillis());

                    Toast.makeText(holder.update_contest.getContext(),"Contest is now in evaluation status", Toast.LENGTH_SHORT).show();
                }
                else if(holder.update_contest.getText().equals("Enter Appeals")){
                    reference_contest_selected.child("status").setValue("Appeals");

                    TimeZone tz = TimeZone.getTimeZone("GMT+03");
                    Calendar c = Calendar.getInstance(tz);
                    c.setTimeInMillis(System.currentTimeMillis() + model.getAppeal_duration());

                    reference_contest_selected.child("timestamp_end").setValue(c.getTimeInMillis());

                    Toast.makeText(holder.update_contest.getContext(),"Contest is now in appeals status", Toast.LENGTH_SHORT).show();
                }
                else if(holder.update_contest.getText().equals("Finish Contest")){

                    //contest results update

                    contestResults_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String contestResultID=dataSnapshot.getKey();
                                ContestResults contestResults=dataSnapshot.getValue(ContestResults.class);
                                if(contestResults!=null){
                                    String contest_id=contestResults.getContestID();
                                    if(contest_id.equals(getRef(position).getKey())){
                                        contestEntries_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                                                    String entry_id=dataSnapshot1.getKey();
                                                    ContestEntry contestEntry1=dataSnapshot1.getValue(ContestEntry.class);
                                                    String contestID=contestEntry1.getContestID();
                                                    String image_id=contestEntry1.getImageID();
                                                    if(contestID.equals(getRef(position).getKey())){
                                                        int rank=contestEntry1.getRank();
                                                        if(rank==1){
                                                            contestResults_reference.child(contestResultID).child("firstPlace").setValue(entry_id);
                                                            image_Reference.child(image_id).child("winner_photo").setValue(1);
                                                        }
                                                        else if(rank==2){
                                                            contestResults_reference.child(contestResultID).child("secondPlace").setValue(entry_id);
                                                            image_Reference.child(image_id).child("winner_photo").setValue(1);
                                                        }
                                                        int vote_rank=contestEntry1.getVote_rank();
                                                        if(vote_rank==1){
                                                            contestResults_reference.child(contestResultID).child("specialPlace").setValue(entry_id);
                                                            image_Reference.child(image_id).child("winner_photo").setValue(1);
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                        contestResults_reference.child(contestResultID).child("visibility").setValue("visible");
                                        contestReference.child(getRef(position).getKey()).child("status").setValue("Finished");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(holder.update_contest.getContext(), "Contest is now Finished, visit Exhibitions to view the winners", Toast.LENGTH_LONG).show();
                }
                else {
                    showConfirmDialog();
                }

            }

            private void showConfirmDialog() {
                AlertDialog.Builder builder= new AlertDialog.Builder(holder.update_contest.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Update contest");
                builder.setMessage("You are about to update this contest information, this means the status will be changed to Pending and admin can review it." +
                        "\nIf the information you changed is not suitable for the category you selected the status will be set to Rejected");
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String cont_title=holder.title.getText().toString();
                        String cont_description=holder.description.getText().toString();
                       reference_contest_selected.child("category").setValue(category_selected);
                       reference_contest_selected.child("title").setValue(cont_title);
                       reference_contest_selected.child("description").setValue(cont_description);
                       reference_contest_selected.child("status").setValue("Pending");
//                       reference_contest_selected.child("timestamp_start").setValue(timestamp_start_contest[0]);
//                       reference_contest_selected.child("timestamp_end").setValue(timestamp_end_contest[0]);
                        holder.edit_contest.setImageResource(R.drawable.edit_button);
                        Toast.makeText(holder.update_contest.getContext(), "Successfully updated", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
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


        holder.view_entries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.more_info.getContext(), ViewEntriesCreatorAdmin.class);
                intent.putExtra(MESSAGE_KEY,getRef(position).getKey());
                v.getContext().startActivity(intent);
            }
        });

        holder.view_reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.more_info.getContext(), ContestReportsCreator.class);
                intent.putExtra(MESSAGE_KEY,getRef(position).getKey());
                v.getContext().startActivity(intent);
            }
        });
        //view_vote_rank
        holder.view_vote_rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.more_info.getContext(), UserVoteRank.class);
                intent.putExtra(MESSAGE_KEY,getRef(position).getKey());
                v.getContext().startActivity(intent);
            }
        });

        //long timestamp_time_left=model.getTimestamp_end();
        if(model.getTimestamp_start()>System.currentTimeMillis()&&model.getStatus().equals("Open")){
            TimeZone tz = TimeZone.getTimeZone("GMT+03");
            Calendar c = Calendar.getInstance(tz);

            holder.status.setText("Starting soon");

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

                    holder.time_left.setText(time_formatted);
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
                    holder.time_left.setText(time_formatted);
                }

                @Override
                public void onFinish() {
                    holder.time_left.setText("time over");
                    if(model.getStatus().equals("Open")) {
                        holder.update_contest.setText("Enter Evaluation");
                        holder.update_contest.setEnabled(true);
                    }
                    else if(model.getStatus().equals("Evaluation")){
                        holder.update_contest.setText("Enter Appeals");
                        holder.update_contest.setEnabled(true);

                    }
                    else if(model.getStatus().equals("Appeals")){
                        holder.update_contest.setText("Finish Contest");
                        holder.update_contest.setEnabled(true);
                    }
                    else if(model.getStatus().equals("Finished")){
                        holder.update_contest.setText("Finished Contest");
                        //holder.update_contest.setEnabled(true);
                    }
                }
            }.start();

        }
        else{
            holder.time_left.setText("N/A");
        }
//        else if(model.getTimestamp_end()==0&&(model.getStatus().equals("Pending")||model.getStatus().equals("Rejected"))){
//            holder.time_left.setText("Not set yet");
//        }

        holder.pick_evaluation_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDurationPickerDialog();
            }

            private void openDurationPickerDialog() {
                duration_picker_dialog.setContentView(R.layout.duration_picker_dialog);
                duration_picker_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView cancel_button,update_button;
                NumberPicker days_picker,hours_picker,minute_picker;

                days_picker=duration_picker_dialog.findViewById(R.id.days_duration_picker);
                hours_picker=duration_picker_dialog.findViewById(R.id.hours_duration_picker);
                minute_picker=duration_picker_dialog.findViewById(R.id.minutes_duration_picker);

                cancel_button=duration_picker_dialog.findViewById(R.id.cancel_duration_dialog);
                update_button=duration_picker_dialog.findViewById(R.id.update_duration_dialog);

                final int[] days = {0};
                final int[] hours = {0};
                final int[] minutes = {0};

                days_picker.setMinValue(0);
                days_picker.setMaxValue(20);
                //days_picker.setValue(0);

                hours_picker.setMinValue(0);
                hours_picker.setMaxValue(23);
                //hours_picker.setValue(0);

                minute_picker.setMinValue(0);
                minute_picker.setMaxValue(59);
                //minute_picker.setValue(0);

                days_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        days[0] =newVal;
                        if(newVal==20){
                            hours_picker.setEnabled(false);
                            minute_picker.setEnabled(false);
                            hours_picker.setValue(0);
                            minute_picker.setValue(0);
                        }
                        else{
                            hours_picker.setEnabled(true);
                            minute_picker.setEnabled(true);
                        }
                    }
                });

                hours_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        hours[0] =newVal;
                    }
                });

                minute_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        minutes[0] =newVal;
                    }
                });

                update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long evaluation_d=0;
                        String duration_selected = "";
                        if(days[0]==20){
                            evaluation_d=day_to_milliseconds*days[0];
                            duration_selected = days[0]+" days";
                        }
                        else if(days[0]>0&&days[0]<20){
                            evaluation_d=day_to_milliseconds*days[0]+hour_to_milliseconds*hours[0]+minute_to_milliseconds*minutes[0];
                            duration_selected = days[0]+" days, "+hours[0]+" hours, "+minutes[0]+" minutes";
                            flag_correct_evaluation_timing=1;
                        }
                        else if(days[0]==0){
                            if(hours[0]!=0){
                                evaluation_d=hour_to_milliseconds*hours[0]+minute_to_milliseconds*minutes[0];
                                duration_selected=hours[0]+" hours, "+minutes[0]+" minutes";
                                flag_correct_evaluation_timing=1;
                            }
                            else{
                                Toast.makeText(holder.pick_evaluation_duration.getContext(), "Contest evaluation duration can be set to minimum 1 hour", Toast.LENGTH_SHORT).show();
                                flag_correct_evaluation_timing=0;
                            }
                        }
                        if(flag_correct_evaluation_timing==1){
                            contest_info=FirebaseDatabase.getInstance().getReference().child("Contests").child(Objects.requireNonNull(getRef(position).getKey()));
                            contest_info.child("evaluation_duration").setValue(evaluation_d);
                            Toast.makeText(holder.pick_evaluation_duration.getContext(), "Contest evaluation duration set to: "+duration_selected, Toast.LENGTH_SHORT).show();
                        }
                        duration_picker_dialog.dismiss();
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        duration_picker_dialog.dismiss();
                    }
                });
                duration_picker_dialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(duration_picker_dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                duration_picker_dialog.getWindow().setAttributes(layoutParams);
            }
        });

        holder.pick_appeals_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDurationPickerDialog();
            }

            private void openDurationPickerDialog() {
                duration_picker_dialog.setContentView(R.layout.duration_picker_dialog);
                duration_picker_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView cancel_button,update_button;
                NumberPicker days_picker,hours_picker,minute_picker;

                days_picker=duration_picker_dialog.findViewById(R.id.days_duration_picker);
                hours_picker=duration_picker_dialog.findViewById(R.id.hours_duration_picker);
                minute_picker=duration_picker_dialog.findViewById(R.id.minutes_duration_picker);

                cancel_button=duration_picker_dialog.findViewById(R.id.cancel_duration_dialog);
                update_button=duration_picker_dialog.findViewById(R.id.update_duration_dialog);

                final int[] days = {0};
                final int[] hours = {0};
                final int[] minutes = {0};

                days_picker.setMinValue(0);
                days_picker.setMaxValue(20);
                //days_picker.setValue(0);

                hours_picker.setMinValue(0);
                hours_picker.setMaxValue(23);
                //hours_picker.setValue(0);

                minute_picker.setMinValue(0);
                minute_picker.setMaxValue(59);
                //minute_picker.setValue(0);

                days_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        days[0] =newVal;
                        if(newVal==20){
                            hours_picker.setEnabled(false);
                            minute_picker.setEnabled(false);
                            hours_picker.setValue(0);
                            minute_picker.setValue(0);
                        }
                        else{
                            hours_picker.setEnabled(true);
                            minute_picker.setEnabled(true);
                        }
                    }
                });

                hours_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        hours[0] =newVal;
                    }
                });

                minute_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                        minutes[0] =newVal;
                    }
                });

                update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long evaluation_d=0;
                        String duration_selected = "";
                        if(days[0]==20){
                            evaluation_d=day_to_milliseconds*days[0];
                            duration_selected = days[0]+" days";
                        }
                        else if(days[0]>0&&days[0]<20){
                            evaluation_d=day_to_milliseconds*days[0]+hour_to_milliseconds*hours[0]+minute_to_milliseconds*minutes[0];
                            duration_selected = days[0]+" days, "+hours[0]+" hours, "+minutes[0]+" minutes";
                            flag_correct_evaluation_timing=1;
                        }
                        else if(days[0]==0){
                            if(hours[0]!=0){
                                evaluation_d=hour_to_milliseconds*hours[0]+minute_to_milliseconds*minutes[0];
                                duration_selected=hours[0]+" hours, "+minutes[0]+" minutes";
                                flag_correct_appeals_timing=1;
                            }
                            else{
                                Toast.makeText(holder.pick_evaluation_duration.getContext(), "Contest appeals duration can be set to minimum 1 hour", Toast.LENGTH_SHORT).show();
                                flag_correct_appeals_timing=0;
                            }
                        }
                        if(flag_correct_appeals_timing==1){
                            contest_info=FirebaseDatabase.getInstance().getReference().child("Contests").child(Objects.requireNonNull(getRef(position).getKey()));
                            contest_info.child("appeal_duration").setValue(evaluation_d);
                            Toast.makeText(holder.pick_evaluation_duration.getContext(), "Contest appeals duration set to: "+duration_selected, Toast.LENGTH_SHORT).show();
                        }
                        duration_picker_dialog.dismiss();
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        duration_picker_dialog.dismiss();
                    }
                });
                duration_picker_dialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(duration_picker_dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                duration_picker_dialog.getWindow().setAttributes(layoutParams);
            }
        });
    }



    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_view_contest_creator,parent,false);
        return new myViewHolder(view);
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        private Button more_info,update_contest,select_new,view_entries,view_reports,view_vote_rank;
        private ImageButton edit_contest,delete_contest;
        private LinearLayout expand,entries,linear_edit,linear_delete;
        private CardView cardView;
        ImageView contest_img,pick_contest_start,pick_contest_end,pick_evaluation_duration,pick_appeals_duration;
        ProgressBar myProgressbar;

        private EditText title,description;
        private Spinner category_spinner;

        private TextView status,accepted_evaluator,accepted_evaluator2,accepted_evaluator3,
                accepted_appeals_evaluator1,accepted_appeals_evaluator2,accepted_appeals_evaluator3,
                category_txtView,time_left,text_view_evaluator,info_evaluator_selection,entries_number,reports_number,
                contest_start_time,contest_end_time,contest_evaluation_duration,contest_appeals_duration;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);


            contest_img=itemView.findViewById(R.id.contest_image);
            myProgressbar=itemView.findViewById(R.id.progress_image_loader);
            title=itemView.findViewById(R.id.editTextTitle);
            description=itemView.findViewById(R.id.editTextDescription);
            category_spinner=itemView.findViewById(R.id.category_selected);
            status=itemView.findViewById(R.id.contest_status);
            update_contest=itemView.findViewById(R.id.update_contest_created);
            expand=itemView.findViewById(R.id.expandable_view);
            more_info=itemView.findViewById(R.id.more_info_contest_created);
            cardView=itemView.findViewById(R.id.card_view_contests_created);
            select_new=itemView.findViewById(R.id.select_new_evaluator);
            edit_contest=itemView.findViewById(R.id.edit_contest_created);
            accepted_evaluator=itemView.findViewById(R.id.contest_accepted_evaluator);
            accepted_evaluator2=itemView.findViewById(R.id.contest_accepted_evaluator2);
            accepted_evaluator3=itemView.findViewById(R.id.contest_accepted_evaluator3);
            accepted_appeals_evaluator1=itemView.findViewById(R.id.contest_accepted_appeals_evaluator1);
            accepted_appeals_evaluator2=itemView.findViewById(R.id.contest_accepted_appeals_evaluator2);
            accepted_appeals_evaluator3=itemView.findViewById(R.id.contest_accepted_appeals_evaluator3);
            category_txtView=itemView.findViewById(R.id.category_view);
            time_left=itemView.findViewById(R.id.time_left_contest);
            text_view_evaluator=itemView.findViewById(R.id.text_view_evaluator);
            entries=itemView.findViewById(R.id.linear_entries_contest_creator);
            info_evaluator_selection=itemView.findViewById(R.id.info_evaluator_selection);
            entries_number=itemView.findViewById(R.id.entries_number_contest_creator);
            reports_number=itemView.findViewById(R.id.reports_number_contest_creator);
            view_entries=itemView.findViewById(R.id.view_entries_contest_creator);
            view_reports=itemView.findViewById(R.id.view_reports_contest_creator);
            view_vote_rank=itemView.findViewById(R.id.view_users_vote_rank);

            linear_edit=itemView.findViewById(R.id.linear_edit_contest);
            linear_delete=itemView.findViewById(R.id.linear_delete_contest);
            delete_contest=itemView.findViewById(R.id.delete_contest_created);



            contest_start_time=itemView.findViewById(R.id.contest_start_time_cc);
            contest_end_time=itemView.findViewById(R.id.contest_end_time_cc);
            contest_evaluation_duration=itemView.findViewById(R.id.contest_evaluation_duration_cc);
            contest_appeals_duration=itemView.findViewById(R.id.contest_appeals_duration_cc);

            pick_contest_start=itemView.findViewById(R.id.pick_contest_start);
            pick_contest_end=itemView.findViewById(R.id.pick_contest_end);
            pick_evaluation_duration=itemView.findViewById(R.id.pick_evaluation_duration);
            pick_appeals_duration=itemView.findViewById(R.id.pick_appeals_duration);


            duration_picker_dialog=new Dialog(itemView.getContext());

            String [] categories_items= itemView.getResources().getStringArray(R.array.categories);


            ArrayList<String> arrayList = new ArrayList<>();

            array_adapter=FirebaseDatabase.getInstance().getReference().child("Categories");

            array_adapter.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                        Categories categories = postSnapshot.getValue(Categories.class);
                        String item_s = categories.getCategory_name();
                        arrayList.add(item_s);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



            myAdapter = new ArrayAdapter<String>(itemView.getContext(),
                    R.layout.contest_spinner_layout,categories_items);

            myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            category_spinner.setAdapter(myAdapter);


            title.setEnabled(false);
            description.setEnabled(false);
            category_spinner.setEnabled(false);
            update_contest.setEnabled(false);
            category_spinner.setVisibility(View.GONE);

        }


    }



}

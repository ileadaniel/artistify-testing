package com.example.artistify.Admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artistify.ContestEntries.ViewEntriesCreatorAdmin;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class myContestAdminAdapter extends FirebaseRecyclerAdapter<Contests,myContestAdminAdapter.myViewHolder> {

    private DatabaseReference creator_n= FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference evaluator_n=FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference reference_contest_status;
    private String status_contest_selected="";
    private int status_selected;

    private String contest_id="";

    private final static String MESSAGE_KEY="com.example.message_key";

    public myContestAdminAdapter(@NonNull FirebaseRecyclerOptions<Contests> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Contests model) {
        holder.cont_title.setText(model.getTitle());
        holder.cont_description.setText(model.getDescription());
        holder.cont_category.setText(model.getCategory());
        String creator_id=model.getContest_creator_id();
        //holder.cont_time.setText(String.valueOf(model.getTimestamp_end()));

        creator_n.child(creator_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user_c=snapshot.getValue(User.class);

                if(user_c!=null){
                    String c_c_name=user_c.fullName;
                    holder.cont_creator.setText(c_c_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String evaluator_id=model.getEvaluator1_id();
        if(!evaluator_id.equals("Not assigned")){
            evaluator_n.child(evaluator_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_e=snapshot.getValue(User.class);

                    if(user_e!=null){
                        String e_name=user_e.fullName;
                        holder.cont_evaluator.setText(e_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.cont_evaluator.setText(evaluator_id);
        }

        String evaluator2_id=model.getEvaluator2_id();
        if(!evaluator2_id.equals("Not assigned")){
            evaluator_n.child(evaluator2_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_e=snapshot.getValue(User.class);

                    if(user_e!=null){
                        String e_name=user_e.fullName;
                        holder.cont_evaluator2.setText(e_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.cont_evaluator2.setText(evaluator2_id);
        }

        String evaluator3_id=model.getEvaluator3_id();

        if(!evaluator3_id.equals("Not assigned")){
            evaluator_n.child(model.getEvaluator3_id()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_e=snapshot.getValue(User.class);

                    if(user_e!=null){
                        String e_name=user_e.fullName;
                        holder.cont_evaluator3.setText(e_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.cont_evaluator3.setText(evaluator3_id);
        }

        String appeals_evaluator1_id=model.getAppeal_evaluator1_id();

        if(!appeals_evaluator1_id.equals("Not assigned")){
            evaluator_n.child(appeals_evaluator1_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_e=snapshot.getValue(User.class);

                    if(user_e!=null){
                        String e_name=user_e.fullName;
                        holder.cont_appeals_evaluator1.setText(e_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.cont_appeals_evaluator1.setText(appeals_evaluator1_id);
        }

        String appeals_evaluator2_id=model.getAppeal_evaluator2_id();

        if(!appeals_evaluator2_id.equals("Not assigned")){
            evaluator_n.child(appeals_evaluator2_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_e=snapshot.getValue(User.class);

                    if(user_e!=null){
                        String e_name=user_e.fullName;
                        holder.cont_appeals_evaluator2.setText(e_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.cont_appeals_evaluator2.setText(appeals_evaluator2_id);
        }


        String appeals_evaluator3_id=model.getAppeal_evaluator3_id();

        if(!appeals_evaluator3_id.equals("Not assigned")){
            evaluator_n.child(appeals_evaluator3_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user_e=snapshot.getValue(User.class);

                    if(user_e!=null){
                        String e_name=user_e.fullName;
                        holder.cont_appeals_evaluator3.setText(e_name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            holder.cont_appeals_evaluator3.setText(appeals_evaluator3_id);
        }





        holder.cont_status.setText(model.getStatus());


        if(model.getTimestamp_start()>System.currentTimeMillis()&&model.getStatus().equals("Open")){
            TimeZone tz = TimeZone.getTimeZone("GMT+03");
            Calendar c = Calendar.getInstance(tz);

            holder.cont_status.setText("Starting soon");

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
                    holder.cont_time.setText(time_formatted);
                }

                @Override
                public void onFinish() {

                }
            }.start();

        }
        else if(model.getTimestamp_start()<System.currentTimeMillis()&&model.getStatus().equals("Open")){

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
                    holder.cont_time.setText(time_formatted);
                }

                @Override
                public void onFinish() {
                    holder.cont_time.setText("time over");

                }
            }.start();

        }
        else{
            holder.cont_time.setText("N/A");
        }


        holder.more_info_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expand.getVisibility()==View.GONE){
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.more_info_contest.setText("Less info");
                    holder.more_info_contest.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.collapse_card,0);
                }
                else{
                    holder.expand.setVisibility(View.GONE);
                    holder.more_info_contest.setText("More info");
                    holder.more_info_contest.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expand_card,0);

                }
            }
        });


        if(model.getTimestamp_start()!=0){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy, HH:mm");
            holder.cont_start_time.setText(simpleDateFormat.format(model.getTimestamp_start()));
        }
        else {
            holder.cont_start_time.setText("Not assigned");

        }
        if(model.getTimestamp_end()!=0){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMMM yyyy, HH:mm");
            holder.cont_end_time.setText(simpleDateFormat.format(model.getTimestamp_end()));
        }
        else {
            holder.cont_end_time.setText("Not assigned");
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
            //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd day(s) - HH hour(s) - mm minute(s)");

            holder.cont_evaluation_duration.setText(time_formatted);
        }
        else{
            holder.cont_evaluation_duration.setText("Not assigned");
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
            //SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd day(s) - HH hour(s) - mm minute(s)");

            holder.cont_appeals_duration.setText(time_formatted);
        }
        else{
            holder.cont_appeals_duration.setText("Not assigned");
        }

        holder.manage_contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference_contest_status = FirebaseDatabase.getInstance().getReference().child("Contests").child(getRef(position).getKey());
                contest_id=getRef(position).getKey();
                showOptionDialog();
            }

            private void showOptionDialog() {
                int position = -1;
                String[] options = {"Set status: Open", "Set status: Rejected", "Delete contest","View entries"};
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.manage_contest.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Choose option");
                builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        status_selected = which;

                        if (status_selected == 0) {
                            status_contest_selected = "Open";
                        }
                        if (status_selected == 1) {
                            status_contest_selected = "Rejected";

                        }
                        if(status_selected==3){
                            dialog.dismiss();
                            Intent intent=new Intent(holder.manage_contest.getContext(), ViewEntriesCreatorAdmin.class);
                            intent.putExtra(MESSAGE_KEY,contest_id);
                            holder.manage_contest.getContext().startActivity(intent);
                        }

                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if(status_selected==2){
                            if (model.getStatus().equals("Pending")||model.getStatus().equals("Draft")||model.getStatus().equals("Rejected")){
                                showDialog();
                            }
                            else{
                                Toast.makeText(holder.manage_contest.getContext(), "You can't delete a contest if current status is Open, Evaluation, Appeals or Finished", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(status_selected==0){
                            if(model.getStatus().equals("Pending")){
                                reference_contest_status.child("visibility").setValue("visible");
                                TimeZone tz = TimeZone.getTimeZone("GMT+03");
                                Calendar c = Calendar.getInstance(tz);
                                long current_time=c.getTimeInMillis();
                                if(current_time<=model.getTimestamp_start()){
                                    reference_contest_status.child("status").setValue(status_contest_selected);
                                }
                                else{
                                    long diff_between_start_end=model.getTimestamp_end()-model.getTimestamp_start();
                                    reference_contest_status.child("timestamp_start").setValue(current_time);
                                    reference_contest_status.child("timestamp_end").setValue(current_time+diff_between_start_end);
                                    reference_contest_status.child("status").setValue(status_contest_selected);
                                }
                            }
                            else{
                                Toast.makeText(holder.manage_contest.getContext(), "You can only set a contest to Open if current status is Pending", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            if(model.getStatus().equals("Pending")||model.getStatus().equals("Draft")){
                                reference_contest_status.child("visibility").setValue("invisible");
                                reference_contest_status.child("status").setValue(status_contest_selected);
                            }
                            else if(model.getTimestamp_start()>System.currentTimeMillis()){
                                reference_contest_status.child("visibility").setValue("invisible");
                                reference_contest_status.child("status").setValue(status_contest_selected);
                            }
                            else {
                                Toast.makeText(holder.manage_contest.getContext(),"You can't Reject contests that are already started or finished", Toast.LENGTH_SHORT).show();
                            }
                        }

                        dialog.dismiss();
                    }

                    private void showDialog() {
                        AlertDialog.Builder builder=new AlertDialog.Builder(holder.manage_contest.getContext(),R.style.MyDialogTheme);
                        builder.setTitle("Important!");
                        builder.setMessage("You are about to delete this contest, that means the contest will no longer be accessible and it can be recovered");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference_contest_status.removeValue();
                                Toast.makeText(holder.manage_contest.getContext(), "Contest deleted", Toast.LENGTH_SHORT).show();
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
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });


    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_view_admin,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        private Button manage_contest;

        private TextView cont_title,cont_description,cont_category,cont_creator,
                cont_evaluator,cont_evaluator2,cont_evaluator3,
                cont_appeals_evaluator1,cont_appeals_evaluator2,cont_appeals_evaluator3,
                cont_start_time,cont_end_time,cont_evaluation_duration,cont_appeals_duration,
                cont_status,cont_time,more_info_contest;
        private LinearLayout expand;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            cont_title=itemView.findViewById(R.id.contest_title);
            cont_description=itemView.findViewById(R.id.contest_description);
            cont_category=itemView.findViewById(R.id.contest_category);
            cont_creator=itemView.findViewById(R.id.contest_creator_name);
            cont_evaluator=itemView.findViewById(R.id.evaluator_name);
            cont_evaluator2=itemView.findViewById(R.id.evaluator2_name);
            cont_evaluator3=itemView.findViewById(R.id.evaluator3_name);
            cont_appeals_evaluator1=itemView.findViewById(R.id.appeals_evaluator1_name);
            cont_appeals_evaluator2=itemView.findViewById(R.id.appeals_evaluator2_name);
            cont_appeals_evaluator3=itemView.findViewById(R.id.appeals_evaluator3_name);
            cont_status=itemView.findViewById(R.id.contest_status);
            cont_time=itemView.findViewById(R.id.time_left);
            manage_contest=itemView.findViewById(R.id.manage_contest);
            expand=itemView.findViewById(R.id.layout_evaluators_and_timing);
            more_info_contest=itemView.findViewById(R.id.expand_contest_view_admin);
            cont_start_time=itemView.findViewById(R.id.contest_start_time_admin);
            cont_end_time=itemView.findViewById(R.id.contest_end_time_admin);
            cont_evaluation_duration=itemView.findViewById(R.id.contest_evaluation_duration_admin);
            cont_appeals_duration=itemView.findViewById(R.id.contest_appeals_duration_admin);

        }
    }
}

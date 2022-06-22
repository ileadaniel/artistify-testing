package com.example.artistify.Evaluator;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.artistify.ContestEntries.ViewEntriesEvaluatorUser;
import com.example.artistify.ModelClasses.AppealRequest;
import com.example.artistify.ModelClasses.Categories;
import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class myContestEvaluatorAdapter extends FirebaseRecyclerAdapter<Contests,myContestEvaluatorAdapter.myViewHolder> {

    private DatabaseReference category_id_selected;

    public final static String MESSAGE_KEY ="com.example.message_key";


    private FirebaseUser user;
    private String userID;

    private DatabaseReference contestEntries_reference=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference appeals_reference=FirebaseDatabase.getInstance().getReference().child("Appeal_requests");


    public myContestEvaluatorAdapter(@NonNull FirebaseRecyclerOptions<Contests> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull Contests model) {
        holder.contest_title_evaluator.setText(model.getTitle());
        holder.contest_description_evaluator.setText(model.getDescription());
        String category=model.getCategory();
        holder.contest_category_evaluator.setText(model.getCategory());
        holder.contest_status_evaluator.setText(model.getStatus());

        final int[] nr_entries = {0};

        contestEntries_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ContestEntry contestEntry=dataSnapshot.getValue(ContestEntry.class);
                    if(contestEntry!=null){
                        String contest_id=contestEntry.getContestID();
                        int off_topic_count=contestEntry.getOffTopic_count();
                        String entry_status=contestEntry.getEntry_status();
                        if(contest_id.equals(getRef(position).getKey())&&off_topic_count<3&&entry_status.equals("Approved")){
                            nr_entries[0] +=1;
                        }
                        else{

                        }
                    }
                }
                holder.entries_number.setText(String.valueOf(nr_entries[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(model.getTimestamp_start()>System.currentTimeMillis()&&model.getStatus().equals("Open")){
            TimeZone tz = TimeZone.getTimeZone("GMT+03");
            Calendar c = Calendar.getInstance(tz);



            Timestamp timestamp1=new Timestamp(model.getTimestamp_start());
            Timestamp timestamp2=new Timestamp(c.getTime().getTime());

            long milliseconds=timestamp1.getTime()-timestamp2.getTime();

            holder.contest_status_evaluator.setText("Starting soon");
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
                    holder.contest_time_left_evaluator.setText(time_formatted);
                }

                @Override
                public void onFinish() {
                    holder.contest_time_left_evaluator.setText("Time over");
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
                    holder.contest_time_left_evaluator.setText(time_formatted);
                }

                @Override
                public void onFinish() {
                    holder.contest_time_left_evaluator.setText("time over");

                }
            }.start();

        }
        else{
            holder.contest_time_left_evaluator.setText("N/A");
        }


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
//                            Glide.with(holder.contest_cover_pic_evaluator.getContext()).load(category_image_url)
//                                    .placeholder(R.drawable.progress_animation).centerCrop().into(holder.contest_cover_pic_evaluator);

                            Glide.with(holder.contest_cover_pic_evaluator.getContext()).load(category_image_url)
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
                                    .into(holder.contest_cover_pic_evaluator);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        if(model.getEvaluator1_id().equals(userID)||model.getEvaluator2_id().equals(userID)||model.getEvaluator3_id().equals(userID)){
            holder.evaluator_role.setText("Contest evaluator");
        }
        else if(model.getAppeal_evaluator1_id().equals(userID)||model.getAppeal_evaluator2_id().equals(userID)||model.getAppeal_evaluator3_id().equals(userID)){
            holder.evaluator_role.setText("Appeals evaluator");
            if(model.getStatus().equals("Appeals")){
                holder.contest_appeals_layout.setVisibility(View.VISIBLE);

                final int[] reviewed_appeals = {0};
                final int[] pending_appeals = {0};
                if(model.getAppeal_evaluator1_id().equals(userID)){
                    appeals_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    AppealRequest appealRequest=dataSnapshot.getValue(AppealRequest.class);
                                    if(appealRequest!=null){

                                        if(appealRequest.getContestID().equals(getRef(position).getKey())){
                                            if(appealRequest.getEvaluation1_status().equals("Reviewed")){
                                                reviewed_appeals[0] = reviewed_appeals[0] +1;


                                            }
                                            else if(appealRequest.getEvaluation1_status().equals("Pending")){
                                                pending_appeals[0] = pending_appeals[0] +1;

                                            }
                                        }
                                    }
                                }
                                holder.pending_appeals_number_evaluator.setText(String.valueOf(pending_appeals[0]));
                                holder.appeals_answered_number_evaluator.setText(String.valueOf(reviewed_appeals[0]));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(model.getAppeal_evaluator2_id().equals(userID)){
                    appeals_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    AppealRequest appealRequest=dataSnapshot.getValue(AppealRequest.class);
                                    if(appealRequest!=null){
                                        if(appealRequest.getContestID().equals(getRef(position).getKey())){
                                            if(appealRequest.getEvaluation2_status().equals("Reviewed")){
                                                reviewed_appeals[0] = reviewed_appeals[0] +1;

                                            }
                                            else if(appealRequest.getEvaluation2_status().equals("Pending")){
                                                pending_appeals[0] = pending_appeals[0] +1;

                                            }
                                        }
                                    }
                                }
                                holder.pending_appeals_number_evaluator.setText(String.valueOf(pending_appeals[0]));
                                holder.appeals_answered_number_evaluator.setText(String.valueOf(reviewed_appeals[0]));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                if(model.getAppeal_evaluator3_id().equals(userID)){
                    appeals_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    AppealRequest appealRequest=dataSnapshot.getValue(AppealRequest.class);
                                    if(appealRequest!=null){
                                        if(appealRequest.getContestID().equals(getRef(position).getKey())){
                                            if(appealRequest.getEvaluation3_status().equals("Reviewed")){
                                                reviewed_appeals[0] = reviewed_appeals[0] +1;

                                            }
                                            else if(appealRequest.getEvaluation3_status().equals("Pending")){
                                                pending_appeals[0] = pending_appeals[0] +1;

                                            }
                                        }
                                    }
                                }
                                holder.pending_appeals_number_evaluator.setText(String.valueOf(pending_appeals[0]));
                                holder.appeals_answered_number_evaluator.setText(String.valueOf(reviewed_appeals[0]));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }



            }
        }
        else{
            holder.evaluator_role.setText("Viewer");
            holder.view_vote_entries.setText("View entries");
        }

        //pending_appeals_number_evaluator=(TextView)itemView.findViewById(R.id.pending_appeals_number_evaluator);
        //appeals_answered_number_evaluator



        holder.view_vote_appeals_evaluator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.view_vote_entries.getContext(), ViewAppealEvaluator.class);
                String message_to_send=getRef(position).getKey()+","+"appeals";
                intent.putExtra(MESSAGE_KEY,message_to_send);
                v.getContext().startActivity(intent);
            }
        });


        holder.more_less_info_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.expand.getVisibility()==View.GONE){
                    holder.expand.setVisibility(View.VISIBLE);
                    holder.more_less_info_view.setText("Less info");
                    holder.more_less_info_view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.collapse_card,0);
                }
                else{
                    holder.expand.setVisibility(View.GONE);
                    holder.more_less_info_view.setText("More info");
                    holder.more_less_info_view.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.expand_card,0);

                }

            }
        });

        holder.view_vote_entries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.view_vote_entries.getContext(), ViewEntriesEvaluatorUser.class);
                String message_to_send=getRef(position).getKey()+","+"evaluation";
                intent.putExtra(MESSAGE_KEY,message_to_send);
                v.getContext().startActivity(intent);
            }
        });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.contest_view_evaluator,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout expand,contest_appeals_layout;
        ImageView contest_cover_pic_evaluator;
        TextView contest_title_evaluator,contest_description_evaluator,contest_category_evaluator,contest_status_evaluator,contest_time_left_evaluator,entries_number,evaluator_role,
                pending_appeals_number_evaluator,appeals_answered_number_evaluator;
        TextView more_less_info_view;
        Button view_vote_entries,view_vote_appeals_evaluator;
        ProgressBar myProgressbar;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            contest_cover_pic_evaluator=(ImageView)itemView.findViewById(R.id.contest_image_evaluator);
            myProgressbar=(ProgressBar)itemView.findViewById(R.id.progress_image_loader);
            contest_title_evaluator=(TextView)itemView.findViewById(R.id.contest_title_evaluator);
            contest_description_evaluator=(TextView)itemView.findViewById(R.id.contest_description_evaluator);
            contest_category_evaluator=(TextView)itemView.findViewById(R.id.contest_category_evaluator);
            contest_status_evaluator=(TextView)itemView.findViewById(R.id.contest_status_evaluator);
            more_less_info_view=(TextView)itemView.findViewById(R.id.more_less_info_evaluator);
            contest_time_left_evaluator=(TextView)itemView.findViewById(R.id.contest_time_left_evaluator);
            expand=(LinearLayout)itemView.findViewById(R.id.expandable_view_evaluator);
            view_vote_entries=(Button)itemView.findViewById(R.id.view_vote_entries_evaluator);
            entries_number=(TextView)itemView.findViewById(R.id.entries_number_evaluator);
            evaluator_role=(TextView)itemView.findViewById(R.id.evaluator_role);

            contest_appeals_layout=(LinearLayout)itemView.findViewById(R.id.contest_appeals_layout);
            pending_appeals_number_evaluator=(TextView)itemView.findViewById(R.id.pending_appeals_number_evaluator);
            appeals_answered_number_evaluator=(TextView)itemView.findViewById(R.id.appeals_answered_number_evaluator);
            view_vote_appeals_evaluator=(Button)itemView.findViewById(R.id.view_vote_appeals_evaluator);






        }
    }
}

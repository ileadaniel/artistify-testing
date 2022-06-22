package com.example.artistify.ContestCreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class myEvaluatorInviteAdapter extends FirebaseRecyclerAdapter<User,myEvaluatorInviteAdapter.myViewHolder> {

    public final static String MESSAGE_KEY ="com.example.message_key";

    private DatabaseReference evaluation_reference = FirebaseDatabase.getInstance().getReference().child("Evaluation_requests");
    private DatabaseReference contest_selected=FirebaseDatabase.getInstance().getReference().child("Contests");
    private DatabaseReference ev_ref;
    private String message="";
    private int flag_no_contest_invites=0;
    private int flag_request=0;
    private int counter_contests_different=0;




    public myEvaluatorInviteAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull User model) {
        String status=model.getStatus();
        int level=model.getAuthentication_level();

        Intent intent =((Activity)holder.invite.getContext()).getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);

                holder.evaluator_fullName.setText(model.getFullName());
                holder.evaluator_email.setText(model.getEmail());
                holder.evaluator_level.setText("Evaluator");
              //  Glide.with(holder.img.getContext()).load(model.getUser_pic_url()).into(holder.img);

        Glide.with(holder.img.getContext()).load(model.getUser_pic_url())
                .diskCacheStrategy(DiskCacheStrategy.NONE).dontAnimate()
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
                .into(holder.img);

         if(!status.equals("Approved")){
             holder.invite.setVisibility(View.INVISIBLE);
         }

         String id_of_evaluator=getRef(position).getKey();


        ev_ref=FirebaseDatabase.getInstance().getReference().child("Evaluation_requests");
        ev_ref.keepSynced(true);
        ev_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                    EvaluationRequest request = dataSnapshot.getValue(EvaluationRequest.class);
                    if (request != null) {
                        String id_ev = request.getEvaluator_id();
                        String contest_id=request.getContest_id();
                        if(contest_id.equals(message)) {
                            if(id_ev.equals(getRef(position).getKey())) {
                                //Toast.makeText(holder.invite.getContext(), id_ev, Toast.LENGTH_SHORT).show();
                                holder.invite.setVisibility(View.INVISIBLE);
                                if(!status.equals("Approved")){
                                    holder.invite.setVisibility(View.INVISIBLE);
                                    ev_ref.keepSynced(true);
                                }
                                ev_ref.keepSynced(true);

                            }
                        }


                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


         holder.invite.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent =((Activity)holder.invite.getContext()).getIntent();

                 message = intent.getStringExtra(MESSAGE_KEY);
                 ev_ref.keepSynced(true);
                 contest_selected.child(message).addListenerForSingleValueEvent(new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         Contests contests=snapshot.getValue(Contests.class);
                         if(contests!=null) {
                             //String status = contests.getStatus();
                             String ev_id=contests.getEvaluator1_id();
                             String ev2_id=contests.getEvaluator2_id();
                             String ev3_id=contests.getEvaluator3_id();
                             String a_ev1_id=contests.getAppeal_evaluator1_id();
                             String a_ev2_id=contests.getAppeal_evaluator2_id();
                             String a_ev3_id=contests.getAppeal_evaluator3_id();
                             //if(status.equals("Open")||status.equals("Finished")){
//                             if(status.equals("Open")){
//                                 showContestStatusIssueDialog();
//                             }
                              if(ev_id.equals("Not assigned")||ev2_id.equals("Not assigned")||ev3_id.equals("Not assigned")||
                             a_ev1_id.equals("Not assigned")||a_ev2_id.equals("Not assigned")||a_ev3_id.equals("Not assigned")){
                                 inviteEvaluator();
                             }
//                             else if(ev2_id.equals("Not assigned")){
//                                 inviteEvaluator();
//
//                             }
//                             else if(ev3_id.equals("Not assigned")){
//                                 inviteEvaluator();
//
//                             }
//                             else if(a_ev1_id.equals("Not assigned")){
//                                 inviteEvaluator();
//
//                             }
//                             else if(a_ev2_id.equals("Not assigned")){
//                                 inviteEvaluator();
//
//                             }
//                             else if(a_ev3_id.equals("Not assigned")){
//                                 inviteEvaluator();
//
//                             }
                             else{
                                 showContestEvaluatorAcceptedDialog();
                             }


                         }
                     }

                     private void inviteEvaluator() {
                         EvaluationRequest evaluationRequest = new EvaluationRequest(message,getRef(position).getKey(),"Pending");
                         String evaluationID = evaluation_reference.push().getKey();
                         evaluation_reference.child(evaluationID).setValue(evaluationRequest);
                         Toast.makeText(holder.invite.getContext(),"Invite sent",Toast.LENGTH_SHORT).show();
                         ((Activity)holder.invite.getContext()).finish();
                         Intent intent=new Intent(holder.invite.getContext(), InviteEvaluator.class);
                         intent.putExtra(MESSAGE_KEY,message);
                         holder.invite.getContext().startActivity(intent);
                     }

                     private void showContestEvaluatorAcceptedDialog() {
                         AlertDialog.Builder builder=new AlertDialog.Builder(holder.invite.getContext(),R.style.MyDialogTheme);
                         builder.setTitle("Action denied!");
                         builder.setMessage("Your contest have all evaluators and appeal evaluators accepted!");
                         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.dismiss();
                             }
                         });
                         builder.show();
                     }

//                     private void showContestStatusIssueDialog() {
//                         AlertDialog.Builder builder=new AlertDialog.Builder(holder.invite.getContext(),R.style.MyDialogTheme);
//                         builder.setTitle("Action denied!");
//                         builder.setMessage("You can't invite any evaluator if the contest status is Open or Finished");
//                         builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                             @Override
//                             public void onClick(DialogInterface dialog, int which) {
//                                 dialog.dismiss();
//                             }
//                         });
//                         builder.show();
//                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 });

                 ev_ref.keepSynced(true);

             }
         });

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluator_invite_view,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img;
        ProgressBar myProgressbar;
        CardView cardView;
        private Button invite;
        private TextView evaluator_fullName,evaluator_email,evaluator_level;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img=(CircleImageView)itemView.findViewById(R.id.evaluator_profile_image);
            myProgressbar=(ProgressBar)itemView.findViewById(R.id.progress_image_loader);
            evaluator_fullName=(TextView)itemView.findViewById(R.id.evaluator_fullname);
            evaluator_email=(TextView)itemView.findViewById(R.id.evaluator_email);
            evaluator_level=(TextView)itemView.findViewById(R.id.evaluator_level);
            invite=(Button)itemView.findViewById(R.id.invite_evaluator);
            cardView=(CardView)itemView.findViewById(R.id.evaluator_card);


            evaluation_reference.keepSynced(true);


            //Toast.makeText(itemView.getContext(), message, Toast.LENGTH_SHORT).show();

        }
    }
}

package com.example.artistify.ContestCreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

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

public class myInvitedEvaluatorAdapter extends FirebaseRecyclerAdapter<EvaluationRequest,myInvitedEvaluatorAdapter.myViewHolder> {

    private DatabaseReference evaluator_ref=FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference contest_id;
    private DatabaseReference request_selected;
    private String ev_n="";

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    private String selected_contests_id;
    private String status_invite_selected="";

    private int flag_delete_request=0;
    private String evaluator="";

    private Context context;


    public myInvitedEvaluatorAdapter(@NonNull FirebaseRecyclerOptions<EvaluationRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull EvaluationRequest model) {
        String evaluator_ID=model.getEvaluator_id();

        Intent intent =((Activity)holder.edit.getContext()).getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);

        evaluator_ref.child(evaluator_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User ev=snapshot.getValue(User.class);

                ev_n=ev.fullName;
                holder.ev_name.setText(ev_n);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.ev_invite_status.setText(model.getInvite_status());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(holder.edit.getContext(), "button pressed", Toast.LENGTH_SHORT).show();

                request_selected=FirebaseDatabase.getInstance().getReference().child("Evaluation_requests").child(Objects.requireNonNull(getRef(position).getKey()));

                showOptionDialog();
            }

            private void showOptionDialog() {
                int position = -1;
                String[] options = {"Resend request", "Delete request"};
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.edit.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Choose option");
                builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (which == 0) {
                            status_invite_selected = "Pending";

                        }
                        if (which == 1) {
                            flag_delete_request=1;
                        }

                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(flag_delete_request==0) {
                            request_selected.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    EvaluationRequest req=snapshot.getValue(EvaluationRequest.class);
                                    assert req != null;
                                    String status_i=req.getInvite_status();
                                    //evaluator=req.getEvaluator_id();
                                    //selected_contests_id=req.getContest_id();
                                    if(status_i.equals("Declined")){
                                        request_selected.child("invite_status").setValue(status_invite_selected);
                                    }
                                    else
                                    {
                                        Toast.makeText(holder.edit.getContext(), "Request already pending or accepted!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                        else if(flag_delete_request==1){
                            request_selected.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    EvaluationRequest req=snapshot.getValue(EvaluationRequest.class);
                                    assert req != null;
                                    selected_contests_id=req.getContest_id();
                                    evaluator=req.getEvaluator_id();
                                    contest_id=FirebaseDatabase.getInstance().getReference().child("Contests").child(selected_contests_id);

                                    contest_id.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Contests contests=snapshot.getValue(Contests.class);
                                            String evaluators_id=contests.getEvaluator1_id();
                                            String evaluators2_id=contests.getEvaluator2_id();
                                            String evaluators3_id=contests.getEvaluator3_id();
                                            String appeals_evaluators1_id=contests.getAppeal_evaluator1_id();
                                            String appeals_evaluators2_id=contests.getAppeal_evaluator2_id();
                                            String appeals_evaluators3_id=contests.getAppeal_evaluator3_id();


                                            String contest_status=contests.getStatus();
                                            //Toast.makeText(holder.edit.getContext(), evaluator, Toast.LENGTH_SHORT).show();
                                            if(evaluators_id.equals(evaluator)){
                                                if(contest_status.equals("Open")||contest_status.equals("Evaluation")||contest_status.equals("Finished")) {
                                                    showRemoveEvaluationIssueDialog();
                                                }
                                                else{
                                                    showRemoveEvaluationDialog("evaluator1_id");

                                                }
                                            }
                                            else if(evaluators2_id.equals(evaluator)){
                                                if(contest_status.equals("Open")||contest_status.equals("Evaluation")||contest_status.equals("Finished")) {
                                                    showRemoveEvaluationIssueDialog();
                                                }
                                                else{
                                                    showRemoveEvaluationDialog("evaluator2_id");

                                                }
                                            }
                                            else if(evaluators3_id.equals(evaluator)){
                                                if(contest_status.equals("Open")||contest_status.equals("Evaluation")||contest_status.equals("Finished")) {
                                                    showRemoveEvaluationIssueDialog();
                                                }
                                                else{
                                                    showRemoveEvaluationDialog("evaluator3_id");

                                                }
                                            }
                                            else if(appeals_evaluators1_id.equals(evaluator)){
                                                if(contest_status.equals("Open")||contest_status.equals("Evaluation")||contest_status.equals("Finished")) {
                                                    showRemoveEvaluationIssueDialog();
                                                }
                                                else{
                                                    showRemoveEvaluationDialog("appeal_evaluator1_id");

                                                }
                                            }
                                            else if(appeals_evaluators2_id.equals(evaluator)){
                                                if(contest_status.equals("Open")||contest_status.equals("Evaluation")||contest_status.equals("Finished")) {
                                                    showRemoveEvaluationIssueDialog();
                                                }
                                                else{
                                                    showRemoveEvaluationDialog("appeal_evaluator2_id");

                                                }
                                            }
                                            else if(appeals_evaluators3_id.equals(evaluator)){
                                                if(contest_status.equals("Open")||contest_status.equals("Evaluation")||contest_status.equals("Finished")) {
                                                    showRemoveEvaluationIssueDialog();
                                                }
                                                else{
                                                    showRemoveEvaluationDialog("appeal_evaluator3_id");

                                                }
                                            }
                                            else{

                                                showRemoveRequestDialog();
                                            }
                                        }

                                        private void showRemoveEvaluationIssueDialog() {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.edit.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Action denied!");
                                            builder.setMessage("You can't remove evaluator from a contest that is Open or Finished.");
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }

                                        private void showRemoveRequestDialog() {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.edit.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Important!");
                                            builder.setMessage("You are about to remove this request.\nPlease keep in mind that you can still resend a new evaluation request to it.");
                                            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    request_selected.removeValue();
                                                    Toast.makeText(holder.edit.getContext(), "Request removed", Toast.LENGTH_SHORT).show();
                                                    ((Activity)holder.edit.getContext()).finish();
                                                    Intent intent=new Intent(holder.edit.getContext(), InviteEvaluator.class);
                                                    intent.putExtra(MESSAGE_KEY,message);
                                                    holder.edit.getContext().startActivity(intent);
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

                                        private void showRemoveEvaluationDialog(String evaluator_number) {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.edit.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Important!");
                                            builder.setMessage("You are about to remove this evaluator from the contest evaluation.\nPlease keep in mind that you can still resend you a new evaluation request to it.");
                                            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                   request_selected.removeValue();
                                                   contest_id.child(evaluator_number).setValue("Not assigned");
                                                    ((Activity)holder.edit.getContext()).finish();
                                                    Intent intent=new Intent(holder.edit.getContext(), InviteEvaluator.class);
                                                    intent.putExtra(MESSAGE_KEY,message);
                                                    holder.edit.getContext().startActivity(intent);
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

                                    //showRemoveEvaluationDialog(); de adaugat unde trebuie

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
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
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluator_selected,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{

        private ImageButton edit;
        private TextView ev_name,ev_invite_status;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            ev_name=(TextView)itemView.findViewById(R.id.evaluator_selected_name);
            ev_invite_status=(TextView)itemView.findViewById(R.id.evaluator_selected_invite_status);
            edit=(ImageButton)itemView.findViewById(R.id.edit_selected_evaluator);


        }
    }

}

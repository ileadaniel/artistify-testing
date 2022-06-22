package com.example.artistify.Evaluator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.EvaluationRequest;
import com.example.artistify.ModelClasses.User;
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

import java.util.Objects;

public class myEvaluationRequestsAdapter extends FirebaseRecyclerAdapter<EvaluationRequest,myEvaluationRequestsAdapter.myViewHolder> {

    private DatabaseReference contest_ref= FirebaseDatabase.getInstance().getReference().child("Contests");
    private DatabaseReference contests_creator_id=FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference contest_selected_request;

    private String evaluator_ID;
    private String status_invite_selected="";
    private String contests_ID;
    private String contest_id_selected;

    public myEvaluationRequestsAdapter(@NonNull FirebaseRecyclerOptions<EvaluationRequest> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull EvaluationRequest model) {

            contests_ID=model.getContest_id();

            contest_ref.child(contests_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Contests contests = snapshot.getValue(Contests.class);

                        if (snapshot.exists()) {
                            String contests_title = contests.getTitle();
                            String contests_creator = contests.getContest_creator_id();
                            String contests_description = contests.getDescription();

                            holder.cont_title.setText(contests_title);
                            holder.cont_description.setText(contests_description);
                            holder.status_invite.setText(model.getInvite_status());
                            contests_creator_id.child(contests_creator).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = snapshot.getValue(User.class);

                                    String cont_creator_name = user.getFullName();
                                    holder.cont_creator.setText(cont_creator_name);
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

            holder.manage_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contest_selected_request=FirebaseDatabase.getInstance().getReference().child("Evaluation_requests").child(Objects.requireNonNull(getRef(position).getKey()));

                    contest_selected_request.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            EvaluationRequest ev_req=snapshot.getValue(EvaluationRequest.class);

                            if(ev_req!=null) {
                                contest_id_selected = ev_req.getContest_id();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    showOptionDialog();

                }

                private void showOptionDialog() {
                    int position = -1;
                    String[] options = {"Accept", "Decline"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.manage_request.getContext(), R.style.MyDialogTheme);
                    builder.setTitle("Choose option");
                    builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            if (which == 0) {
                                status_invite_selected = "Accepted";

                            }
                            if (which == 1) {
                                status_invite_selected = "Declined";
                            }

                        }
                    });
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(user!=null) {
                                evaluator_ID = user.getUid();
                            }

                            if(status_invite_selected.equals("Accepted")){


                                    //contest_ref.child(contest_id_selected).child("evaluator_id").setValue(evaluator_ID);
                                    contest_ref.child(contest_id_selected).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Contests contests=snapshot.getValue(Contests.class);
                                            if(contests!=null){
                                                String ev_id_sel=contests.getEvaluator1_id();
                                                String ev2_id_sel=contests.getEvaluator2_id();
                                                String ev3_id_sel=contests.getEvaluator3_id();
                                                String a_ev1_id_sel=contests.getAppeal_evaluator1_id();
                                                String a_ev2_id_sel=contests.getAppeal_evaluator2_id();
                                                String a_ev3_id_sel=contests.getAppeal_evaluator3_id();
                                                if(ev_id_sel.equals(evaluator_ID)){
                                                    showDialogExisting();
                                                }
                                                else if(ev2_id_sel.equals(evaluator_ID)){
                                                    showDialogExisting();
                                                }
                                                else if(ev3_id_sel.equals(evaluator_ID)){
                                                    showDialogExisting();
                                                }
                                                else if(a_ev1_id_sel.equals(evaluator_ID)){
                                                    showDialogExisting();
                                                }
                                                else if(a_ev2_id_sel.equals(evaluator_ID)){
                                                    showDialogExisting();
                                                }
                                                else if(a_ev3_id_sel.equals(evaluator_ID)){
                                                    showDialogExisting();
                                                }
                                                else if(!ev_id_sel.equals("Not assigned")&&!ev2_id_sel.equals("Not assigned")&&!ev3_id_sel.equals("Not assigned")
                                                        &&!a_ev1_id_sel.equals("Not assigned")&&!a_ev2_id_sel.equals("Not assigned")&&!a_ev3_id_sel.equals("Not assigned")){
                                                    showDialog();

                                                }
                                                else{
                                                    if(ev_id_sel.equals("Not assigned")){
                                                        contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                        contest_ref.child(contest_id_selected).child("evaluator1_id").setValue(evaluator_ID);
                                                        Toast.makeText(holder.manage_request.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(ev2_id_sel.equals("Not assigned")){
                                                        contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                        contest_ref.child(contest_id_selected).child("evaluator2_id").setValue(evaluator_ID);
                                                        Toast.makeText(holder.manage_request.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(ev3_id_sel.equals("Not assigned")){
                                                        contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                        contest_ref.child(contest_id_selected).child("evaluator3_id").setValue(evaluator_ID);
                                                        Toast.makeText(holder.manage_request.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(a_ev1_id_sel.equals("Not assigned")){
                                                        contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                        contest_ref.child(contest_id_selected).child("appeal_evaluator1_id").setValue(evaluator_ID);
                                                        Toast.makeText(holder.manage_request.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(a_ev2_id_sel.equals("Not assigned")){
                                                        contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                        contest_ref.child(contest_id_selected).child("appeal_evaluator2_id").setValue(evaluator_ID);
                                                        Toast.makeText(holder.manage_request.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(a_ev3_id_sel.equals("Not assigned")){
                                                        contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                        contest_ref.child(contest_id_selected).child("appeal_evaluator3_id").setValue(evaluator_ID);
                                                        Toast.makeText(holder.manage_request.getContext(), "Request accepted", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            }
                                        }

                                        private void showDialogExisting() {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.manage_request.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Wrong action!");
                                            builder.setMessage("You already accepted the request, please wait.");
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }

                                        private void showDialog() {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.manage_request.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Important!");
                                            builder.setMessage("Contest already have all accepted evaluators and appeals evaluators, this may change, so please verify the requests list more times until contest start");
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
                            else if(status_invite_selected.equals("Declined")){
                                    contest_ref.child(contest_id_selected).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Contests contests=snapshot.getValue(Contests.class);
                                            if(contests!=null){
                                                String ev_id_selected=contests.getEvaluator1_id();
                                                String ev2_id_selected=contests.getEvaluator2_id();
                                                String ev3_id_selected=contests.getEvaluator3_id();
                                                String a_ev1_id_selected=contests.getAppeal_evaluator1_id();
                                                String a_ev2_id_selected=contests.getAppeal_evaluator2_id();
                                                String a_ev3_id_selected=contests.getAppeal_evaluator3_id();

                                                String contest_status=contests.getStatus();
                                                if(ev_id_selected.equals(evaluator_ID)) {
                                                    if (contest_status.equals("Open") || contest_status.equals("Evaluation")|| contest_status.equals("Finished")) {
                                                        showRemoveEvaluationIssueDialog();
                                                    } else {
                                                        showRemoveEvaluationDialog("evaluator1_id");

                                                    }
                                                }
                                                else if(ev2_id_selected.equals(evaluator_ID)) {
                                                    if (contest_status.equals("Open") || contest_status.equals("Evaluation")|| contest_status.equals("Finished")) {
                                                        showRemoveEvaluationIssueDialog();
                                                    } else {
                                                        showRemoveEvaluationDialog("evaluator2_id");

                                                    }
                                                }
                                                else if(ev3_id_selected.equals(evaluator_ID)) {
                                                    if (contest_status.equals("Open") || contest_status.equals("Evaluation")|| contest_status.equals("Finished")) {
                                                        showRemoveEvaluationIssueDialog();
                                                    } else {
                                                        showRemoveEvaluationDialog("evaluator3_id");

                                                    }
                                                }
                                                else if(a_ev1_id_selected.equals(evaluator_ID)) {
                                                    if (contest_status.equals("Open") || contest_status.equals("Evaluation")|| contest_status.equals("Finished")) {
                                                        showRemoveEvaluationIssueDialog();
                                                    } else {
                                                        showRemoveEvaluationDialog("appeal_evaluator1_id");

                                                    }
                                                }
                                                else if(a_ev2_id_selected.equals(evaluator_ID)) {
                                                    if (contest_status.equals("Open") || contest_status.equals("Evaluation")|| contest_status.equals("Finished")) {
                                                        showRemoveEvaluationIssueDialog();
                                                    } else {
                                                        showRemoveEvaluationDialog("appeal_evaluator2_id");

                                                    }
                                                }
                                                else if(a_ev3_id_selected.equals(evaluator_ID)) {
                                                    if (contest_status.equals("Open") || contest_status.equals("Evaluation") || contest_status.equals("Finished")) {
                                                        showRemoveEvaluationIssueDialog();
                                                    } else {
                                                        showRemoveEvaluationDialog("appeal_evaluator3_id");

                                                    }
                                                }
                                                else{
                                                    contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                }
                                            }
                                        }

                                        private void showRemoveEvaluationIssueDialog() {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.manage_request.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Action denied!");
                                            builder.setMessage("You can't remove your evaluator role from a contest that is Open or Finished.");
                                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            builder.show();
                                        }

                                        private void showRemoveEvaluationDialog(String evaluator_number) {
                                            AlertDialog.Builder builder=new AlertDialog.Builder(holder.manage_request.getContext(),R.style.MyDialogTheme);
                                            builder.setTitle("Important!");
                                            builder.setMessage("You are about to remove yourself from the contest evaluation.\nPlease keep in mind that contest creator can resend you a new evaluation request.\n" +
                                                    "You can still change your mind and update the request status, this can be done until contest starts or another evaluator accept the request!");
                                            builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    contest_selected_request.child("invite_status").setValue(status_invite_selected);
                                                    contest_ref.child(contest_id_selected).child(evaluator_number).setValue("Not assigned");
                                                    Toast.makeText(holder.manage_request.getContext(), "Successfully removed your role of evaluator for this contest", Toast.LENGTH_SHORT).show();

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_requests_evaluator_view,parent,false);
        return new myViewHolder(view);
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        private TextView cont_title,cont_description,cont_creator,status_invite;
        private Button manage_request;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            cont_title=(TextView)itemView.findViewById(R.id.contest_title);
            cont_description=(TextView)itemView.findViewById(R.id.description_contest);
            cont_creator=(TextView)itemView.findViewById(R.id.creator_name);
            status_invite=(TextView)itemView.findViewById(R.id.status_of_invite);
            manage_request=(Button)itemView.findViewById(R.id.manage_request);
        }
    }
}

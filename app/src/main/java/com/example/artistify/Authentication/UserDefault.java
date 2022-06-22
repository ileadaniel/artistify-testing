package com.example.artistify.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class UserDefault extends AppCompatActivity {
    public Button logout_user,need_help;
    private DatabaseReference reference_user;

    private DatabaseReference review_reference=FirebaseDatabase.getInstance().getReference().child("Review_request");
    private FirebaseUser user;
    private String userID;
    Dialog dialog_review;

    private long backPressedTime;
    private Toast backToast;

    @Override
    public void onBackPressed() {

        if(backPressedTime+2000>System.currentTimeMillis()){
            backToast.cancel();

            moveTaskToBack(true);

            finish();
            System.exit(1);
        }
        else{
            backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_default);



        final TextView fullNameTextView = (TextView)findViewById(R.id.fullName_user);
        final TextView statusTextView = (TextView)findViewById(R.id.status_user);
        final Button requestReview = (Button)findViewById(R.id.request_review_button);

        dialog_review=new Dialog(this);

        review_reference.keepSynced(true);
        review_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        ReviewRequest reviewRequest1=dataSnapshot.getValue(ReviewRequest.class);
                        if(reviewRequest1!=null){
                            String users_id=reviewRequest1.getUserID();
                            if(users_id.equals(userID)){
                                requestReview.setEnabled(false);
                                requestReview.setText("Request sent");
                            }
                            else{
                                requestReview.setEnabled(true);
                                requestReview.setText("Request account review");
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_review.setContentView(R.layout.review_request_dialog);
                dialog_review.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView cancel_request=dialog_review.findViewById(R.id.review_request_cancel_button);
                TextView send_request=dialog_review.findViewById(R.id.review_request_send_button);
                EditText message_request=dialog_review.findViewById(R.id.review_request_message);




                send_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String message=message_request.getText().toString();
                        ReviewRequest reviewRequest=new ReviewRequest(userID,message);
                        String review_request_id=review_reference.push().getKey();
                        review_reference.child(review_request_id).setValue(reviewRequest);
                        Toast.makeText(UserDefault.this, "Request sent successfully!", Toast.LENGTH_LONG).show();
                        dialog_review.dismiss();
                        requestReview.setEnabled(false);
                        requestReview.setText("Request sent");
                    }
                });


                cancel_request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_review.dismiss();
                    }
                });

                dialog_review.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog_review.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog_review.getWindow().setAttributes(layoutParams);
            }
        });


        logout_user=(Button)findViewById(R.id.logout_user_default);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference_user = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference_user.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
                    String fullName=userProfile.fullName;
                    String status=userProfile.status;
                    statusTextView.setText(status);
                    fullNameTextView.setText(fullName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserDefault.this,"Something wrong happened!",Toast.LENGTH_LONG).show();
            }
        });

        logout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserDefault.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if(UserDefault.this!=null) {
                    finish();
                }
            }
        });


        need_help=findViewById(R.id.need_help_user_default);

        need_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHelpDialog();
            }

            private void showHelpDialog() {
                AlertDialog.Builder builder=new AlertDialog.Builder(need_help.getContext(),R.style.MyDialogTheme);
                builder.setTitle("Important information!");
                builder.setMessage("Welcome to Artistify, the art contest platform.\n\n" +
                        "This is the default section where you can see your current status, and here are some important information that you need to read:\n\n" +
                        "1. You've been redirected here if you registered as a new user with one of these roles: Contest creator, Evaluator.\n\n" +
                        "2. If your status is pending no worries, admin will review your account soon and your current status will be updated.\n" +
                        "If the admin considered that you are not eligible to receive the Approved status, your status will be changed to Rejected.\n\n" +
                        "3. When you log in and you are redirected here and current status is Rejected or Banned, here are some reasons why it happened:\n\n" +
                        "a) You tried to enter a contest after end time by changing device time, you have been warned after that with a popup message.\n\n" +
                        "   In this case you can do the followings:\n" +
                        "- Submit a new account review by clicking the [Request account review] button and wait for the admin's decision.\n" +
                        "- Send an email to: artistify.official@gmail.com to receive more information.\n\n" +
                        "b) Admin restricted the access to your account because you either spammed contests with off-topic content or you didn't respect other participants.\n\n" +
                        "c) If you are a Contest creator, your account can be restricted if you launch contests with incomplete information, time not being set correctly, the title and description do not fit the chosen category.\n\n" +
                        "d) If you are an Evaluator, your account may be restricted if you don't do your job correctly, do not evaluate or simply give totally irresponsible points.");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //builder.show();


                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                alertDialog.getWindow().setAttributes(layoutParams);
            }
        });
    }
}
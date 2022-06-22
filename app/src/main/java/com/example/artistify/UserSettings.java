package com.example.artistify;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artistify.Authentication.Login;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.ModelClasses.LevelChangeRequest;
import com.example.artistify.ModelClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class UserSettings extends AppCompatActivity {

    private Button logout_normal_user,change_profile_pic,change_cover_pic,update_profile,delete_account,request_auth_change;
    private EditText name,email;
    private String userID;
    private DatabaseReference reference;

    private DatabaseReference level_request_reference=FirebaseDatabase.getInstance().getReference().child("Level_change_requests");

    Dialog delete_dialog;
    private ProgressBar progressBar;
    private int auth_level;
    private int auth_level_requested;

    private final static String MESSAGE_KEY="com.example.message_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        logout_normal_user=(Button) findViewById(R.id.logout_normal_user);
        change_profile_pic=(Button)findViewById(R.id.change_profile_picture);
        change_cover_pic=(Button)findViewById(R.id.change_cover_picture);
        update_profile=(Button)findViewById(R.id.update_profile);
        delete_account=(Button)findViewById(R.id.delete_account);
        request_auth_change=(Button)findViewById(R.id.request_auth_level_change);
        progressBar=(ProgressBar)findViewById(R.id.progressBar3);

        name=(EditText)findViewById(R.id.editTextTextPersonName);
        email=(EditText)findViewById(R.id.editTextTextEmail);
        email.setEnabled(false);

        delete_dialog =new Dialog(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID=firebaseUser.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
                    String fullName=userProfile.getFullName();

                    String email_user = userProfile.getEmail();

                    name.setText(fullName);
                    email.setText(email_user);
                    auth_level=userProfile.authentication_level;
                    if(userProfile.authentication_level==3){
                        delete_account.setVisibility(View.GONE);
                        request_auth_change.setVisibility(View.GONE);
                    }
                    else{
                        delete_account.setVisibility(View.VISIBLE);
                        request_auth_change.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserSettings.this,"Something wrong happened!",Toast.LENGTH_LONG).show();
            }
        });

        level_request_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        LevelChangeRequest levelChangeRequest=dataSnapshot.getValue(LevelChangeRequest.class);
                        if(levelChangeRequest!=null){
                            String users_id=levelChangeRequest.getUserID();
                            if(users_id.equals(userID)){
                                request_auth_change.setText("Change request sent");
                                request_auth_change.setEnabled(false);
                                //request_auth_change.setBackgroundColor(getResources().getColor(R.color.darkblue_1));
                                //request_auth_change.setTextColor(getResources().getColor(R.color.lightblue_white));
                                request_auth_change.setAlpha((float) 0.4);
                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        request_auth_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(auth_level);
            }

            private void showOptionDialog(int auth_level) {
                String[] options = {};
                if(auth_level==0){
                    options= new String[]{"Contest creator","Evaluator"};
                }
                if(auth_level==1){
                    options= new String[]{"Normal user","Evaluator"};
                }
                if(auth_level==2){
                    options= new String[]{"Normal user","Contest creator"};
                }

                int position = -1;

                AlertDialog.Builder builder = new AlertDialog.Builder(request_auth_change.getContext(), R.style.MyDialogTheme);
                builder.setTitle("Choose option");
                String[] finalOptions = options;
                builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String auth_level_selected= finalOptions[which];
                        if(auth_level_selected.equals("Normal user")){
                            auth_level_requested=0;
                        }
                        if(auth_level_selected.equals("Contest creator")){
                            auth_level_requested=1;
                        }
                        if(auth_level_selected.equals("Evaluator")){
                            auth_level_requested=2;
                        }

                    }
                });
                builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        LevelChangeRequest levelChangeRequest=new LevelChangeRequest(userID,auth_level_requested);
                        String level_requested_id=level_request_reference.push().getKey();
                        level_request_reference.child(level_requested_id).setValue(levelChangeRequest);
                        //reference_user_status.child("status").setValue(status_user_selected);
                        Toast.makeText(UserSettings.this, "Request sent successfully!", Toast.LENGTH_SHORT).show();
                        request_auth_change.setText("Change request sent");
                        request_auth_change.setEnabled(false);
                        //request_auth_change.setBackgroundColor(getResources().getColor(R.color.darkblue_1));
                        //request_auth_change.setTextColor(getResources().getColor(R.color.lightblue_white));
                        request_auth_change.setAlpha((float) 0.4);
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

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showDeleteDialog();
            }

            private void showDeleteDialog() {
                delete_dialog.setContentView(R.layout.delete_confirmation_dialog);
                delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText password_field=delete_dialog.findViewById(R.id.password_field_delete);
                TextView cancel_button=delete_dialog.findViewById(R.id.cancel_account_delete);
                TextView confirm_button=delete_dialog.findViewById(R.id.confirm_account_delete);

                confirm_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password_user=password_field.getText().toString().trim();
                        if(password_user.isEmpty()){
                            password_field.setError("Password is required!");
                            password_field.requestFocus();
                            return;
                        }

                        if(password_user.length()<6){
                            password_field.setError("Min password length should be 6 characters!");
                            password_field.requestFocus();
                            return;
                        }
                        verifyPassword(password_user);

                    }

                    private void verifyPassword(String password_user) {
                        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),password_user);
                        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(userID).child("status").setValue("Deleted");
                                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            finish();
                                            Toast.makeText(UserSettings.this, "Your account is deleted, redirecting to Login screen", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(UserSettings.this,Login.class);
                                            startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(UserSettings.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            reference.child(userID).child("status").setValue("Approved");
                                        }
                                    }
                                });
                                delete_dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                password_field.setError("Wrong password, please try again!");
                                password_field.requestFocus();
                                //Toast.makeText(UserSettings.this, "", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                cancel_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delete_dialog.dismiss();
                    }
                });
                delete_dialog.show();
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(delete_dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                delete_dialog.getWindow().setAttributes(layoutParams);
            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String new_name=name.getText().toString();
                reference.child(userID).child("fullName").setValue(new_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UserSettings.this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else{
                            Toast.makeText(UserSettings.this, "Update failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

        change_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this,UploadPicture.class);
                intent.putExtra(MESSAGE_KEY,"profile");
                startActivity(intent);

            }
        });
        change_cover_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserSettings.this,UploadPicture.class);
                intent.putExtra(MESSAGE_KEY,"cover");
                startActivity(intent);
            }
        });



        logout_normal_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                if(UserSettings.this!=null){
                    finishAffinity();
                }
            }
        });

    }
}
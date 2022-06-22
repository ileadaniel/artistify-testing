package com.example.artistify;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.artistify.ModelClasses.ContestEntry;
import com.example.artistify.ModelClasses.Contests;
import com.example.artistify.ModelClasses.Image;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.NormalUser.NormalUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPicture extends AppCompatActivity {

    private Button upload;
    private ImageView imageView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private DatabaseReference reference;
    private DatabaseReference reference_user_pic_update;
    private DatabaseReference reference_picture=FirebaseDatabase.getInstance().getReference("Image");
    private DatabaseReference reference_contest_entry=FirebaseDatabase.getInstance().getReference().child("Contest_entries");
    private DatabaseReference reference_contest=FirebaseDatabase.getInstance().getReference().child("Contests");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    private String profile_image_update;
    private ProgressBar progressBar;


    private long timestamp_submited;

    public final static String MESSAGE_KEY ="com.example.message_key";
    private String message="";

    private String category_selected="";

    private int flag_upload_type=0;

    private CheckBox image_type;

    private EditText img_title;

    private Spinner spinner;

    private String userID;

//    @Override
//    public void onBackPressed() {
//        finish();
//       Intent intent=new Intent(UploadPicture.this, NormalUser.class);
//       startActivity(intent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_picture);

        image_type=(CheckBox)findViewById(R.id.checkBox_image_type);

        img_title=(EditText)findViewById(R.id.editTextTextImageTitle);

        spinner=(Spinner)findViewById(R.id.category_selected_upload);

        String [] categories_items= getResources().getStringArray(R.array.categories);


        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(UploadPicture.this,
                R.layout.color_spinner_layout,categories_items);

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                category_selected= spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Intent intent = getIntent();

        message = intent.getStringExtra(MESSAGE_KEY);

        if(message.equals("profile")||message.equals("cover")){
            image_type.setVisibility(View.INVISIBLE);
        }
        else {
            image_type.setVisibility(View.VISIBLE);
        }

        upload =(Button)findViewById(R.id.upload_btn);

        progressBar=(ProgressBar)findViewById(R.id.progressBar2);

        imageView=(ImageView)findViewById(R.id.profile_pic_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);
            }
        });

        image_type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                  spinner.setVisibility(View.VISIBLE);
                  flag_upload_type=1;
                }
                else {
                    spinner.setVisibility(View.INVISIBLE);
                    flag_upload_type=0;
                }
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null){
                    if(!message.equals("image")&&!message.equals("profile")&&!message.equals("cover")) {
                        reference_contest.child(message).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Contests contests = snapshot.getValue(Contests.class);
                                if (contests != null) {
                                    if (contests.getCategory().equals(category_selected)) {
                                        uploadToFirebase(imageUri);
                                    } else {
                                        Toast.makeText(UploadPicture.this, "Image category is different than the contest category.\nPlease change image category or select another one", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else{
                        uploadToFirebase(imageUri);
                    }
                }
                else{
                    Toast.makeText(UploadPicture.this,"Please select image",Toast.LENGTH_SHORT).show();
                }
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();



        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile!=null){
//                    String fullName=userProfile.fullName;
//                    String email=userProfile.email;
//                    int user_level = userProfile.authentication_level;
//                    String profile_img;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UploadPicture.this,"Something wrong happened!",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            imageUri=data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadToFirebase(Uri uri){
        long startTime = System.nanoTime();
        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if(flag_upload_type==0) {
                            String title=img_title.getText().toString();
                            Image image = new Image(uri.toString(), userID,"profile",title, 0,0);
                            String imageId = reference_picture.push().getKey();
                            reference_picture.child(imageId).setValue(image);
                            reference_picture.child(imageId).child("timestamp_upload").setValue(ServerValue.TIMESTAMP);
                            reference_user_pic_update = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            if (message.equals("profile")) {
                                reference_user_pic_update.child("user_pic_url").setValue(uri.toString());
                            } else if (message.equals("cover")) {
                                reference_user_pic_update.child("cover_pic_url").setValue(uri.toString());
                            } else {
                                //do nothing to Users database
                            }
                            Toast.makeText(UploadPicture.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            long elapsedTime = (System.nanoTime() -startTime);
                            //Toast.makeText(UploadPicture.this, ""+elapsedTime, Toast.LENGTH_SHORT).show();
                            TestInfo.showTestUserInfoLog("Test image uploading time", elapsedTime/1000000);
                            imageView.setImageResource(R.drawable.upload_image_icon);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                        else if(flag_upload_type==1){
                            String title=img_title.getText().toString();
                            Image image = new Image(uri.toString(), userID,category_selected,title, 0,0);
                            String imageId = reference_picture.push().getKey();
                            reference_picture.child(imageId).setValue(image);
                            reference_picture.child(imageId).child("timestamp_upload").setValue(ServerValue.TIMESTAMP);

                            if(!message.equals("image")) {

                                ContestEntry contestEntry = new ContestEntry(message, userID, imageId, category_selected, 0, "Approved", 0, 0,0,0,1000000000,1000000000,1000000000);
                                String contest_entry_id = reference_contest_entry.push().getKey();
                                reference_contest_entry.child(contest_entry_id).setValue(contestEntry);
                                reference_contest_entry.child(contest_entry_id).child("date_submit").setValue(ServerValue.TIMESTAMP);
                                Toast.makeText(UploadPicture.this, "Uploaded and joined successfully", Toast.LENGTH_SHORT).show();
                                long elapsedTime = (System.nanoTime() -startTime);
                                //Toast.makeText(UploadPicture.this, ""+elapsedTime, Toast.LENGTH_SHORT).show();
                                TestInfo.showTestUserInfoLog("Test image uploading time", elapsedTime/1000000);
                            }
                            else{
                                Toast.makeText(UploadPicture.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                                long elapsedTime = (System.nanoTime() -startTime);
                                //Toast.makeText(UploadPicture.this, ""+elapsedTime, Toast.LENGTH_SHORT).show();
                                TestInfo.showTestUserInfoLog("Test image uploading time", elapsedTime/1000000);
                            }


                            progressBar.setVisibility(View.INVISIBLE);
                            finish();
                            Intent intent=new Intent(UploadPicture.this, NormalUser.class);
                            startActivity(intent);
                        }

                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadPicture.this,"Uploading failed!",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }
    private String getFileExtension(Uri mUri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }


}
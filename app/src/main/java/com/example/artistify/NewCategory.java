package com.example.artistify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.artistify.ModelClasses.Categories;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class NewCategory extends AppCompatActivity {

    private static final String TAG = "NewCategory";
    private Button upload;
    private ImageView imageView;

    private DatabaseReference reference_picture= FirebaseDatabase.getInstance().getReference("Categories");
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;
    private String profile_image_update;
    private ProgressBar progressBar;
    private Spinner category_spinner;
    private String category_selected="";
    private DatabaseReference array_adapter=FirebaseDatabase.getInstance().getReference("Categories");
    private TextView existing;
    private int flag_approve=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_category);

        upload =(Button)findViewById(R.id.upload_category_btn);

        category_spinner=(Spinner)findViewById(R.id.category_selected);

        existing=(TextView)findViewById(R.id.existing_categories);

        String [] categories_items= getResources().getStringArray(R.array.categories);


        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(NewCategory.this,
                R.layout.color_spinner_layout,categories_items);

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(myAdapter);
        ArrayList<String> arrayList = new ArrayList<>();

        array_adapter.keepSynced(true);

        array_adapter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {

                    Categories categories = postSnapshot.getValue(Categories.class);
                    String item_s = categories.getCategory_name();
                    arrayList.add(item_s);
                    //Toast.makeText(NewCategory.this, item_s, Toast.LENGTH_SHORT).show();
                }
                String listString = "";

                for (String s : arrayList)
                {
                    listString += s + " -- ";
                }

                    existing.setText(listString);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        category_selected= category_spinner.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        progressBar=(ProgressBar)findViewById(R.id.progressBarCategory);

        imageView=(ImageView)findViewById(R.id.category_image);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,1);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri!=null){
                    reference_picture.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot:snapshot.getChildren()) {
                               Categories categories=dataSnapshot.getValue(Categories.class);
                               String category_s=categories.getCategory_name();
                               if(category_s.equals(category_selected)){
                                   Toast.makeText(NewCategory.this, "This category already exists!", Toast.LENGTH_SHORT).show();
                               }
                               else{
                                   flag_approve=1;
                               }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if(flag_approve==1){
                        uploadToFirebase(imageUri);
                        flag_approve=0;
                    }
                }
                else{
                    Toast.makeText(NewCategory.this,"Please select image",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode== Activity.RESULT_OK && data!=null){
            imageUri=data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    private void uploadToFirebase(Uri uri){
        StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Categories category = new Categories(category_selected,uri.toString());
                        //profile_image_update=uri.toString();
                        String categoryId = reference_picture.push().getKey();
                        reference_picture.child(categoryId).setValue(category);

                        Toast.makeText(NewCategory.this,"Uploaded successfully",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                        //user.child(userID).child("user_pic_url").setValue(image);
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
                Toast.makeText(NewCategory.this,"Uploading failed!",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }
    private String getFileExtension(Uri mUri){
        ContentResolver cr = NewCategory.this.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}
package com.example.artistify.Admin;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.artistify.LogUtils;
import com.example.artistify.ModelClasses.LevelChangeRequest;
import com.example.artistify.ModelClasses.ReviewRequest;
import com.example.artistify.ModelClasses.User;
import com.example.artistify.R;
import com.example.artistify.TestInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class myUsersAdapter extends FirebaseRecyclerAdapter<User,myUsersAdapter.myViewHolder>
{
    private DatabaseReference user_id;
    private DatabaseReference reference;
    private DatabaseReference reference_user_status;
    private DatabaseReference reference_users_photos;
    private DatabaseReference review_reference=FirebaseDatabase.getInstance().getReference().child("Review_request");
    private DatabaseReference level_change_review_reference=FirebaseDatabase.getInstance().getReference().child("Level_change_requests");
    private DatabaseReference testReference=FirebaseDatabase.getInstance().getReference().child("Testing");
    private String userID;
    public String status_user_selected="Approved";
    private int flag_delete_account=-1;



    private int status_selected;


    public myUsersAdapter(@NonNull FirebaseRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull User model) {

        holder.fullName_user.setText(model.getFullName());
        holder.email_user.setText(model.getEmail());
        holder.status_user.setText(model.getStatus());
        if(model.getAuthentication_level()==0){
            holder.level_user.setText("Normal user");
            holder.button.setVisibility(View.VISIBLE);
        }
        else if(model.getAuthentication_level()==1){
            holder.level_user.setText("Contest creator");
            holder.button.setVisibility(View.VISIBLE);
        }
        else if(model.getAuthentication_level()==2){
            holder.level_user.setText("Evaluator");
            holder.button.setVisibility(View.VISIBLE);
        }
        else if(model.getAuthentication_level()==3){
            holder.level_user.setText("Administrator");
            holder.button.setVisibility(View.INVISIBLE);
        }

        if(model.getStatus().equals("Deleted")){
            holder.button.setVisibility(View.INVISIBLE);
        }
        //long startTime = System.nanoTime();




        Glide.with(holder.img.getContext()).load(model.getUser_pic_url()).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop()
                .dontAnimate()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.myProgressbar.setVisibility(View.INVISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.myProgressbar.setVisibility(View.INVISIBLE);
                        //long elapsedTime = (System.nanoTime() -startTime);
                        return false;
                    }
                })
                .into(holder.img);

        try {
            String result=LogUtils.readLogs();
            if(!result.equals("")){
                String[] tokens = result.split(" ");
                String last = tokens[tokens.length-1];

                String changeCommaToDot=last.replaceAll("[,]",".");
                float responseTime=Float.parseFloat(changeCommaToDot.replaceAll("[ms)\n]", ""));

                //Toast.makeText(holder.button.getContext(), ""+ responseTime +" and rounded "+ Math.round(responseTime), Toast.LENGTH_SHORT).show();

                TestInfo.showTestUserInfoLog("Test user profile image retrieve time from database",Math.round(responseTime));

            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        review_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        ReviewRequest reviewRequest1=dataSnapshot.getValue(ReviewRequest.class);
                        if(reviewRequest1!=null){
                            String users_id=reviewRequest1.getUserID();
                            if(users_id.equals(getRef(position).getKey())){
                                holder.review_request_view.setVisibility(View.VISIBLE);
                            }
                            else{
                                holder.review_request_view.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        level_change_review_reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        LevelChangeRequest levelChangeRequest=dataSnapshot.getValue(LevelChangeRequest.class);
                        if(levelChangeRequest!=null){
                            String users_id=levelChangeRequest.getUserID();
                            if(users_id.equals(getRef(position).getKey())){
                                holder.level_change_request_view.setVisibility(View.VISIBLE);
                            }
                            else{
                                holder.level_change_request_view.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.review_request_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                review_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String review_id=dataSnapshot.getKey();
                                ReviewRequest reviewRequest1=dataSnapshot.getValue(ReviewRequest.class);
                                if(reviewRequest1!=null){
                                    String users_id=reviewRequest1.getUserID();
                                    String message=reviewRequest1.getMessage();
                                    if(users_id.equals(getRef(position).getKey())){
                                        showReviewDialog(review_id,message);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            private void showReviewDialog(String review_ID,String message_sent) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.review_request_view.getContext(),R.style.MyDialogTheme);
                builder.setTitle("Account review request");
                builder.setMessage("This user sent an account review request.\nMessage from user:\n\n"+message_sent+"\n");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Remove request", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        review_reference.child(review_ID).removeValue();
                        holder.review_request_view.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        holder.level_change_request_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                level_change_review_reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                String review_id=dataSnapshot.getKey();
                                LevelChangeRequest levelChangeRequest=dataSnapshot.getValue(LevelChangeRequest.class);
                                if(levelChangeRequest!=null){
                                    String users_id=levelChangeRequest.getUserID();
                                    int level_requested=levelChangeRequest.getLevel_requested();
                                    if(users_id.equals(getRef(position).getKey())){
                                        showReviewDialog(review_id,level_requested);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            private void showReviewDialog(String review_ID,int level_request) {
                AlertDialog.Builder builder=new AlertDialog.Builder(holder.level_change_request_view.getContext(),R.style.MyDialogTheme);
                String level_requested_by_user="";
                if(level_request==0){
                    level_requested_by_user="Normal user";
                }
                if(level_request==1){
                    level_requested_by_user="Contest creator";
                }
                if(level_request==2){
                    level_requested_by_user="Evaluator";
                }
                builder.setTitle("Account review request");
                builder.setMessage("This user sent an level change request.\n\nLevel requested:\n"+level_requested_by_user+"\n");
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        level_change_review_reference.child(review_ID).removeValue();
                        holder.level_change_request_view.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        level_change_review_reference.child(review_ID).removeValue();
                        reference_user_status = FirebaseDatabase.getInstance().getReference().child("Users").child(getRef(position).getKey());
                        reference_user_status.child("authentication_level").setValue(level_request);
                        holder.level_change_request_view.setVisibility(View.GONE);
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });


        holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reference_user_status = FirebaseDatabase.getInstance().getReference().child("Users").child(getRef(position).getKey());
                    showOptionDialog();
                }

                private void showOptionDialog() {
                    int position = -1;
                    String[] options = {"Set status: Approved", "Set status: Rejected", "Set status: Banned"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(holder.button.getContext(), R.style.MyDialogTheme);
                    builder.setTitle("Choose option");
                    builder.setSingleChoiceItems(options, position, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            status_selected = which;

                            if (status_selected == 0) {
                                status_user_selected = "Approved";

                            }
                            if (status_selected == 1) {
                                status_user_selected = "Rejected";


                            }
                            if (status_selected == 2) {
                                status_user_selected = "Banned";
                            }

                        }
                    });
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(status_selected==0||status_selected==1||status_selected==2){
                                reference_user_status.child("status").setValue(status_user_selected);
                                status_selected=-1;
                            }
                            else{
                                Toast.makeText(holder.button.getContext(), "Please choose an option before updating!", Toast.LENGTH_SHORT).show();
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

//    private void checkImageLoadingTime(String user_pic_url) throws Exception{
//        String data = null;
//        InputStream in;
////
////        HttpMetric metric =
////                FirebasePerformance.getInstance().newHttpMetric("https://www.google.com",
////                        FirebasePerformance.HttpMethod.GET);
////        final URL url = new URL("https://www.google.com");
////        metric.start();
////        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
////        conn.setDoOutput(true);
////        conn.setRequestProperty("Content-Type", "application/json");
////        try {
////            DataOutputStream outputStream = new DataOutputStream(conn.getOutputStream());
////            outputStream.write(data);
////        } catch (IOException ignored) {
////        }
////        metric.setRequestPayloadSize(data.length);
////        metric.setHttpResponseCode(conn.getResponseCode());
////        //printStreamContent(conn.getInputStream());
////
////        long timetaken=conn.getConnectTimeout();
////        conn.disconnect();
////        metric.stop();
////
////        //long time=data.length;
////        long time=Long.parseLong(metric.getAttribute("responseTime"));
////        Log.i("Important!!", " timpul: "+timetaken);
////        //Toast.makeText(, "wow", Toast.LENGTH_SHORT).show();
//        HttpURLConnection httpURLConnection = null;
//        try{
//            String info;
//            httpURLConnection = (HttpURLConnection) new URL(user_pic_url).openConnection();
//            in = new BufferedInputStream(httpURLConnection.getInputStream());
//
//            //info = readStream(in);
//
//            Scanner s = new Scanner(in).useDelimiter("\\A");
//            String result = s.hasNext() ? s.next() : "";
//
//            Log.i("Important!!", ""+result);
//
//
//        } catch (MalformedURLException exception){
//
//        } catch (IOException exception){
//
//        } finally {
//            if(null!=httpURLConnection){
//                //long responseTime = httpURLConnection.getResponseMessage();
//                //Log.i("Important!!", ""+data);
//                httpURLConnection.disconnect();
//            }
//        }
//    }

//    private String readStream(InputStream in) {
//        BufferedReader reader = null;
//        StringBuffer data = new StringBuffer("");
//        try{
//            reader = new BufferedReader(new InputStreamReader(in));
//            String line = "";
//            while ((line=reader.readLine())!=null){
//                data.append(line);
//            }
//
//        } catch (IOException exception) {
//            Log.e("Problema","IOException");
//        }finally {
//            if(reader!=null){
//                try{
//                    reader.close();
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//        }
//        Log.i("wow", data.toString());
//        return data.toString();
//    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        private Button button;
        CircleImageView img;
        private ProgressBar myProgressbar;
        private TextView fullName_user,email_user, level_user,status_user,review_request_view,level_change_request_view;

        public myViewHolder(@NonNull View itemView)
        {
            super(itemView);
            img=(CircleImageView) itemView.findViewById(R.id.profile_image);
            myProgressbar=(ProgressBar)itemView.findViewById(R.id.progress_image_loader);
            fullName_user=(TextView)itemView.findViewById(R.id.user_fullname);
            email_user=(TextView)itemView.findViewById(R.id.user_email);
            level_user=(TextView)itemView.findViewById(R.id.user_level);
            status_user=(TextView)itemView.findViewById(R.id.user_status);
            review_request_view=(TextView)itemView.findViewById(R.id.review_request_view);
            level_change_request_view=(TextView)itemView.findViewById(R.id.level_change_request_view);

            button=(Button)itemView.findViewById(R.id.Manage_user);


        }

}




}

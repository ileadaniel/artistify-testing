package com.example.artistify;

import android.bluetooth.BluetoothAdapter;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.artistify.ModelClasses.Testing;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestInfo {

    private static DatabaseReference testReference= FirebaseDatabase.getInstance().getReference().child("Testing");
    private static String testID="";
    //private int flagExists=0;


    public static void showLog() {
        Log.i("Testing environment", "This is the testing environment for the Artistify application project");
    }

    public static void showTestUserInfoLog(String tag, long time){
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();

        testID = testReference.push().getKey();


        Log.i("Artistify: "+tag + " "+deviceName, time+ " millis");

        Testing testing = new Testing(deviceName,tag,time);


        testReference.addListenerForSingleValueEvent(new ValueEventListener() {

            public int flagExists=0;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        Testing testing1=dataSnapshot.getValue(Testing.class);

                        if(testing1!=null){
                            if(tag.equals(testing1.getOperation())&&deviceName.equals(testing1.getDevice())){
                                flagExists=1;
                                if(testing1.getElapsedTime()<time){
                                    testReference.child(dataSnapshot.getKey()).child("elapsedTime").setValue(time);
                                }
                            }
                        }
                    }
                    if(flagExists==0){
                        testReference.child(testID).setValue(testing);
                        //Log.i("Test if data stored", time+ " millis");
                        flagExists=0;
                    }
                }
                else{
                    testReference.child(testID).setValue(testing);
                    flagExists=0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

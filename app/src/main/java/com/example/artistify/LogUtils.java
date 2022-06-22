package com.example.artistify;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogUtils {
    public static String readLogs() throws IOException {

        int pid = android.os.Process.myPid();
        String command = "logcat --pid="+pid+" -t 1"+" -s FirebasePerformance";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        StringBuilder log=new StringBuilder();
        String line = "";

        while ((line = bufferedReader.readLine()) != null) {
            if(line.contains("FirebasePerformance: Logging network request trace"))
                log.append(line + "\n");
        }

       return  log.toString();
    }
}

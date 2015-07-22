
package com.luntech.launcher;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MacAddressUtils {

    public static String getMacAddress() {
        try {

            String mac = loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0,
                    17);
            return mac.replace(":", "").toLowerCase();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        try {
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
            }
            return fileData.toString();
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                Log.w("Failed to close reader,", e);
            }
        }
    }
}

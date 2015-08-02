/*
 * Copyright 2014 - Jamdeo
 */

package com.luntech.launcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;

public class IOUtils {

    public static void close(InputStream in) {
        if (in == null) {
            return;
        }
        try {
            in.close();
        } catch (IOException e) {
            Logger.w(e.getMessage(), e);
        }
    }

    public static void close(OutputStream out) {
        if (out == null) {
            return;
        }
        try {
            out.close();
        } catch (IOException e) {
            Logger.w(e.getMessage(), e);
        }
    }

    public static InputStream getInputStream(String urlpath, long start) throws IOException {
        URL url = new URL(urlpath);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        Logger.d("conn.setRequestProperty(RANGE,bytes=" + start + "-)");
        conn.setRequestProperty("RANGE", "bytes=" + start + "-");
        conn.setRequestProperty("Accept", "*/*");
        return conn.getInputStream();
    }

    public static FileOutputStream getRandomAccessFile(File tmpFile) throws IOException {
        if (!tmpFile.exists()) {
            tmpFile.createNewFile();
        }
        return new FileOutputStream(tmpFile);
    }
}

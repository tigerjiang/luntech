
package com.luntech.launcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

/**
 * DownloadTask provide the download function.
 */

public final class DownloadTask implements Runnable {

    private static final String FILE_PREFIX = "launcher";
    private String mDownloadTo;

    private String mDownloadUrl;

    private IDownloadListener mListener;

    public DownloadTask(String doanloadTo, String url, IDownloadListener listener) {
        mDownloadTo = doanloadTo;
        mDownloadUrl = url;
        mListener = listener;
    }

    private void doDownload(File tmpFile) throws IOException {
        Logger.d("doDownload " + tmpFile.getAbsolutePath());
        RandomAccessFile out = null;
        InputStream in = null;
        try {
            in = IOUtils.getInputStream(mDownloadUrl, 0);
            out = IOUtils.getRandomAccessFile(tmpFile);
            byte[] buffer = new byte[2048];

            int count = 0;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            mListener.onCompleted(tmpFile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
    }

    /**
     * Download file
     * 
     * @param out
     * @param in
     * @param tmpFileName
     * @throws IOException
     */
    private void download(File tmpFile) throws IOException {
        try {
            doDownload(tmpFile);
        } catch (SocketException e) {
            Logger.w(e.getMessage(), e);
            retryDelayed(e, tmpFile, 10000);
        } catch (IOException e) {
            Logger.w("", e);
            if (e.getMessage() != null && e.getMessage().contains("I/O error")) {
                Logger.w(tmpFile.getAbsolutePath() + " has been damaged!!");

                int counter = 0;
                while (tmpFile.exists()) {
                    synchronized (this) {
                        try {
                            this.wait(1000);
                        } catch (InterruptedException e1) {
                        }
                    }
                    if (tmpFile.delete()) {
                        Logger.d("delete temp file succeed.");
                        break;
                    } else {
                        counter++;
                        Logger.d("delete temp file failed..." + counter);
                        if (counter > 10) {
                            return;
                        }
                    }
                }

                retryDelayed(e, tmpFile, 1000);
            } else {
                retryDelayed(e, tmpFile, 10000);
            }
        }
    }

    private long getContentLength() throws IOException {
        URL url = new URL(mDownloadUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000);
        return conn.getContentLength();
    }

    private void retryDelayed(IOException cause, File tmpFile, long time) throws IOException {
        if (time > 0) {
            synchronized (this) {
                try {
                    this.wait(time);
                } catch (InterruptedException e) {
                }
            }
        }
        download(tmpFile);
    }

    @Override
    public void run() {
        Logger.d("DownloadTask.run...");
        Logger.i("downdload file from " + mDownloadUrl);
        try {
            File tempFile = null;
            tempFile = getTempFile();
            if (tempFile.exists()) {
                if (tempFile.length() == getContentLength()) {
                    mListener.onCompleted(tempFile);
                    return;
                } else {
                    download(tempFile);
                }
            } else {
                download(tempFile);
            }

        } catch (IOException e) {
            Logger.e(e.getMessage(), e);
            return;
        } catch (RuntimeException e) {
            Logger.e(e.getMessage(), e);
            return;
        } catch (Exception e) {
            Logger.e(e.getMessage(), e);
            return;
        }
    }

    private File getTempFile() {
        File tempFile = new File(getTempFileName());
        return tempFile;
    }

    private String getTempFileName() {
        return mDownloadTo +"/"+ FILE_PREFIX + "-" + getUrlFileName(mDownloadUrl);
    }

    private String getUrlFileName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}

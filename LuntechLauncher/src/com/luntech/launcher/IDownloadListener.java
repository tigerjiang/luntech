
package com.luntech.launcher;

import java.io.File;

/**
 * IDownloadListener for listen the download state.
 */

public interface IDownloadListener {

    /**
     * Download complete.
     */
    void onCompleted(File file);

    /**
     * Download occur error.
     */
    void onError(String errorCode);
}


package com.luntech.launcher;

public class OtaInfo {
    public String currentVersion;
    public String newVersion;
    public String remark;
    public String url;
    public String md5;
    public String fileSize;

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "OtaInfo [currentVersion=" + currentVersion + ", newVersion=" + newVersion
                + ", remark=" + remark + ", url=" + url + ", md5=" + md5 + ", fileSize=" + fileSize
                + "]";
    }

}

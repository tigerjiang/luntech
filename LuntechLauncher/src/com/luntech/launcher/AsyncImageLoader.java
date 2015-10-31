package com.luntech.launcher;


import java.lang.ref.SoftReference;
import java.util.HashMap;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncImageLoader {
    //SoftReference是软引用，是为了更好的为了系统回收变量
    private HashMap<String, SoftReference<Bitmap>> imageCache;
    private Context context;

    public AsyncImageLoader(Context context) {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
    }

    public Bitmap loadDrawable(final String imageUrl, final ImageView imageView, final ImageCallback imageCallback) {
        Bitmap bitmap = null;
        if (imageCache.containsKey(imageUrl)) {
            //从缓存中获取
            SoftReference<Bitmap> softReference = imageCache.get(imageUrl);
            bitmap = softReference.get();
            if (bitmap != null) {
                return bitmap;
            }
        } else {
            try {
                bitmap = ImageUtil.geRoundDrawableFromPath(imageUrl, context);//20
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Bitmap) message.obj, imageView, imageUrl);
            }
        };
        return bitmap;
        //建立新一个新的线程下载图片
    }

    //回调接口
    public interface ImageCallback {
        public void imageLoaded(Bitmap bitmap, ImageView imageView, String imageUrl);
    }
}
package com.luntech.launcher;


import java.lang.ref.SoftReference;
import java.util.HashMap;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class AsyncImageLoader {
    //SoftReference是软引用，是为了更好的为了系统回收变量
    private HashMap<String, SoftReference<Drawable>> imageCache;
    private Context context;

    public AsyncImageLoader(Context context) {
        imageCache = new HashMap<String, SoftReference<Drawable>>();
    }

    public Drawable loadDrawable(final String imageUrl, final ImageView imageView, final ImageCallback imageCallback) {
        Drawable drawable = null;
        if (imageCache.containsKey(imageUrl)) {
            //从缓存中获取
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            drawable = softReference.get();
            if (drawable != null) {
                return drawable;
            }
        } else {
            try {
                drawable = ImageUtil.geRoundDrawableFromPath(imageUrl, context);//20
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }
        final Handler handler = new Handler() {
            public void handleMessage(Message message) {
                imageCallback.imageLoaded((Drawable) message.obj, imageView, imageUrl);
            }
        };
        return drawable;
        //建立新一个新的线程下载图片
    }

    //回调接口
    public interface ImageCallback {
        public void imageLoaded(Drawable imageDrawable, ImageView imageView, String imageUrl);
    }
}
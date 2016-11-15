package net.lzzy.memocard.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

/**
 * Created by lzzy_gxy on 2015/9/6.
 * 图片读取工具
 */
public class PictureReader {
    public static BitmapDrawable getScaledDrawable(Context context,float w, float h, String path){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;//为了查询原始图片尺寸，但不返回实际的bitmap（无需分配内存避免OOM错误）
        BitmapFactory.decodeFile(path,options);
        float srcW=options.outWidth;
        float srcH=options.outHeight;//decodeFile后，即可通过options获取图片尺寸

        int inSampleSize=1;
        if(w==0){
            inSampleSize=Math.round(srcH/h);
        }else if(h==0){
            inSampleSize= Math.round(srcW/w);
        }else {
            if(srcH>h|srcW>w){
                if(srcW>srcH)
                    inSampleSize= Math.round(srcH/h);
                else
                    inSampleSize= Math.round(srcW/w);
            }
        }
        options=new BitmapFactory.Options();
        options.inSampleSize=inSampleSize;
        Bitmap bitmap=BitmapFactory.decodeFile(path,options);
        return new BitmapDrawable(context.getResources(),bitmap);
    }

    public static void releaseImageView(ImageView imageView){
        if(!(imageView.getDrawable() instanceof BitmapDrawable))
            return;
        BitmapDrawable drawable= (BitmapDrawable) imageView.getDrawable();
        drawable.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}

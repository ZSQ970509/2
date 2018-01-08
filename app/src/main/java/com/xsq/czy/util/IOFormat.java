package com.xsq.czy.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Bitmap与DrawAble与byte[]与InputStream之间的转换工具类
 * Created by Administrator on 2017/4/24.
 */
public class IOFormat {

    private static IOFormat tools = new IOFormat();

    public static IOFormat getInstance(){
        if (tools == null) {
            tools = new IOFormat();
            return tools;
        }
        return tools;
    }

    // 将byte[]转换成InputStream
    public InputStream byte2InputStream(byte[] b){
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        return bais;
    }

    // 将InputStream转换成byte[]
    public byte[] inputStream2Bytes(InputStream is){
        String str = "";
        byte[] readByte = new byte[1024];
        @SuppressWarnings("unused")
        int readCount = -1;
        try {
            while ((readCount = is.read(readByte, 0, 1024)) != -1) {
                str += new String(readByte).trim();
            }
            return str.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将Bitmap转换成InputStream
    public InputStream bitmap2InputStream(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    // 将Bitmap转换成InputStream
    public InputStream bitmap2InputStream(Bitmap bm, int quality){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    // 将InputStream转换成Bitmap
    public Bitmap inputStream2Bitmap(InputStream is){
        if(is == null){
            return null;
        }
        return BitmapFactory.decodeStream(is);
    }

    // Drawable转换成InputStream
    public InputStream drawable2InputStream(Drawable d){
        if(d == null){
            return null;
        }
        Bitmap bitmap = this.drawable2Bitmap(d);
        return this.bitmap2InputStream(bitmap);
    }

    // InputStream转换成Drawable
    public Drawable inputStream2Drawable(InputStream is){
        if(is == null){
            return null;
        }
        Bitmap bitmap = this.inputStream2Bitmap(is);
        return this.bitmap2Drawable(bitmap);
    }

    // Drawable转换成byte[]
    public byte[] drawable2Bytes(Drawable d){
        if(d == null){
            return null;
        }
        Bitmap bitmap = this.drawable2Bitmap(d);
        return this.bitmap2Bytes(bitmap);
    }

    // byte[]转换成Drawable
    public Drawable bytes2Drawable(byte[] b){
        if(b == null){
            return null;
        }
        Bitmap bitmap = this.bytes2Bitmap(b);
        return this.bitmap2Drawable(bitmap);
    }

    // Bitmap转换成byte[]
    public byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    // byte[]转换成Bitmap
    public Bitmap bytes2Bitmap(byte[] b){
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return null;
    }

    // Drawable转换成Bitmap
    public Bitmap drawable2Bitmap(Drawable drawable){
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // Bitmap转换成Drawable
    public Drawable bitmap2Drawable(Bitmap bitmap){
        @SuppressWarnings("deprecation")
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }

}

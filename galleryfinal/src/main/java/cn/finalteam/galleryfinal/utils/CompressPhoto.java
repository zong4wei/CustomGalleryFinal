package cn.finalteam.galleryfinal.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by ShiWeiZong
 * date 2016/7/611:02
 * email zong4wei@163.com
 */
public class CompressPhoto {

    /**
     * @param path
     * @param imageSize
     * @param targetSize imageSize 压缩后图片的大小，单位为kb targetSize 为目标分辨率的乘积，例如1280*720
     */
    public static boolean compressBitmap(String path, String outPath, int imageSize, int targetSize) {
        boolean compressSuccess = false;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, targetSize);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp;
        try {
            is = new FileInputStream(path);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
            double scale = getScaling(opts.outWidth * opts.outHeight,
                    targetSize);
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
                    (int) (opts.outWidth * scale),
                    (int) (opts.outHeight * scale), true);
            if (bmp != bmp2)
                bmp.recycle();
            boolean result = compressBmpToFile(outPath, bmp2, imageSize);
            compressSuccess = result;
            bmp2.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return compressSuccess;
        } catch (IOException e) {
            e.printStackTrace();
            return compressSuccess;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        return compressSuccess;
    }

    private static double getScaling(int src, int des) {
        /**
         * 48 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比 49
         */
        double scale = Math.sqrt((double) des / (double) src);
        return scale;
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 压缩图片
     *
     * @param outPath
     */
    public static boolean compressBmpToFile(String outPath, Bitmap bitmap,
                                            int imageSize) {
        boolean writeSuccess = false;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int options = 100;// 从80开始,
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            while (baos.toByteArray().length / 1024 > imageSize) {
                baos.reset();
                options -= 10;
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);
            }
            try {
                FileOutputStream fos = new FileOutputStream(outPath);
                fos.write(baos.toByteArray());
                bitmap.recycle();
                fos.flush();
                fos.close();
                writeSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                return writeSuccess;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return writeSuccess;
        }
        return writeSuccess;
    }

    public static void compressBitmap(PhotoInfo info, File toFile) {
        int targetSize = 1280 * 720;//因为图片大多需要大图显示所以maxNumOfPixels设置为1280 * 720，如需自定义请在FunctionConfig中添加参数
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(info.getPhotoPath(), opts);
        opts.inSampleSize = computeSampleSize(opts, -1, targetSize);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp;
        try {
            is = new FileInputStream(info.getPhotoPath());
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
            double scale = getScaling(opts.outWidth * opts.outHeight,
                    targetSize);
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
                    (int) (opts.outWidth * scale),
                    (int) (opts.outHeight * scale), true);
            if (bmp != bmp2)
                bmp.recycle();

            boolean result = compressBmpToFile(toFile.getAbsolutePath(), bmp2, GalleryFinal.getFunctionConfig().isCompressSize());

            //压缩成功后，更新原来PhotoInfo中的路径
            if (result)
                info.setPhotoPath(toFile.getAbsolutePath());

            bmp2.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }

    }
}

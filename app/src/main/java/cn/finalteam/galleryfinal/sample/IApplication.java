/*
 * Copyright (C) 2014 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.finalteam.galleryfinal.sample;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.sample.listener.UILPauseOnScrollListener;
import cn.finalteam.galleryfinal.sample.loader.UILImageLoader;

/**
 * Desction:
 * Author:pengjianbo
 * Date:15/12/18 下午1:45
 */
public class IApplication extends Application {


    private FunctionConfig functionConfig;

    @Override
    public void onCreate() {
        super.onCreate();


        initGalleryFinalConfig();

        initImageLoader(this);
    }

    private void initGalleryFinalConfig() {
        //设置主题
        ThemeConfig theme = ThemeConfig.DARK;

        //配置功能
        functionConfig = new FunctionConfig.Builder()
                .setMutiSelect(false)//配置是否多选
                .setMutiSelectMaxSize(8)//配置多选数量
                .setEnableEdit(true)//开启编辑功能
                .setEnableCrop(false)//开启裁剪功能
                .setEnableRotate(true)//开启旋转功能
                .setEnableCamera(false)//开启相机功能
                .setCropSquare(false)//裁剪正方形
//         .setSelected(List)//添加已选列表,只是在列表中默认呗选中不会过滤图片
//         .setFilter(List list)//添加图片过滤，也就是不在GalleryFinal中显示
//         .takePhotoFolter(File file)//配置拍照保存目录，不做配置的话默认是/sdcard/DCIM/GalleryFinal/
//         .setRotateReplaceSource(boolean)//配置选择图片时是否替换原始图片，默认不替换
//         .setCropReplaceSource(boolean)//配置裁剪图片时是否替换原始图片，默认不替换
                .setForceCrop(false)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
                .setForceCropEdit(false)//在开启强制裁剪功能时是否可以对图片进行编辑（也就是是否显示旋转图标和拍照图标）
                .setEnablePreview(true)//是否开启预览功能
                .setEnablePreviewDelte(true)
                .setEnableCompress(true)
                .build();

        CoreConfig coreConfig = new CoreConfig.Builder(this, new UILImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new UILPauseOnScrollListener(false, true))
                .setNoAnimcation(true)
                .build();

        GalleryFinal.init(coreConfig);
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)// 设置下载的图片是否缓存在内存中
                .cacheOnDisc(false)// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)
                .cacheOnDisk(false)
                .showImageOnFail(context.getResources().getDrawable(R.mipmap.default_load))
                .showImageForEmptyUri(context.getResources().getDrawable(R.mipmap.default_load))
                .showImageOnLoading(context.getResources().getDrawable(R.mipmap.default_load))
                .imageScaleType(ImageScaleType.EXACTLY)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
                .resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
//                .displayer(new RoundedBitmapDisplayer(20))// 是否设置为圆角，弧度为多少
//                .displayer(new FadeInBitmapDisplayer(100))// 是否图片加载好后渐入的动画时间
                .build();// 构建完成

        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.defaultDisplayImageOptions(options);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
        ImageLoader.getInstance().isInited();
    }


}

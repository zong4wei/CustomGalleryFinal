package cn.finalteam.galleryfinal.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.LinkedList;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by ShiWeiZong
 * date 2016/7/415:03
 * email zong4wei@163.com
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private GridView selectPhotoGv;
    private EditAvaterAdapter gvAdapter;
    List<PhotoInfo> avaterList = new LinkedList<PhotoInfo>();
    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private final int REQUEST_CODE_CROP = 1002;
    private final int REQUEST_CODE_EDIT = 1003;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
        findView();
        init();
        loadData();
    }

    private void setView() {
        setContentView(R.layout.act_main);
    }

    private void findView() {
        selectPhotoGv = (GridView) findViewById(R.id.select_photo_gv);
    }

    private void init() {

        gvAdapter = new EditAvaterAdapter(this, 8, this);
        gvAdapter.addItemChangeMine(avaterList);
        selectPhotoGv.setAdapter(gvAdapter);
    }

    private void loadData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_photo_edit_avater_iv:
                int position = (int) v.getTag(R.id.item_photo_edit_avater_iv);
                clickAvater(position);
                break;
        }
    }

    private void clickAvater(int position) {

        if (position != avaterList.size()) {

        } else {
            ActionSheet.createBuilder(MainActivity.this, getSupportFragmentManager())
                    .setCancelButtonTitle("取消")
                    .setOtherButtonTitles("拍照", "相册")
                    .setCancelableOnTouchOutside(true)
                    .setListener(new ActionSheet.ActionSheetListener() {
                        @Override
                        public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

                        }

                        @Override
                        public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                            switch (index) {
                                case 0:
                                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, mOnHanlderResultCallback);
                                    break;
                                case 1:
                                    GalleryFinal.openGalleryMuti(REQUEST_CODE_GALLERY, 8, mOnHanlderResultCallback);
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .show();
        }

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                avaterList.addAll(resultList);
                gvAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

}

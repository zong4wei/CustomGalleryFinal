package cn.finalteam.galleryfinal;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.adapter.PhotoPreviewAdapter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.utils.BaseDialogTitleContent;
import cn.finalteam.galleryfinal.utils.DownloadFile;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.galleryfinal.widget.GFViewPager;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2015/12/29 0029 14:43
 */
public class PhotoPreviewActivity extends PhotoBaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {

    static final String PHOTO_LIST = "photo_list";
    private static final String SELECE_PHOTO_INDEX = "select_photo_index";

    private RelativeLayout mTitleBar;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private TextView mTvIndicator;

    private GFViewPager mVpPager;
    private List<PhotoInfo> mPhotoList;
    private PhotoPreviewAdapter mPhotoPreviewAdapter;

    private ThemeConfig mThemeConfig;
    private ImageView mTvDeletePhoto;
    private ImageButton mTvDownloadPhoto;
    private BaseDialogTitleContent mDialog;
    private PhotoInfo selectPhoto;
    private ArrayList<PhotoInfo> deletePhoto = new ArrayList<PhotoInfo>();
    protected int current;
    private int mSelectPhotoIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThemeConfig = GalleryFinal.getGalleryTheme();

        if (mThemeConfig == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.gf_activity_photo_preview);
            findViews();
            setListener();
            setTheme();

            mPhotoList = (List<PhotoInfo>) getIntent().getSerializableExtra(PHOTO_LIST);
            mSelectPhotoIndex = getIntent().getExtras().getInt(SELECE_PHOTO_INDEX, 0);
            mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, mPhotoList);
            mVpPager.setAdapter(mPhotoPreviewAdapter);

            updatePercent();
            mVpPager.setCurrentItem(current);
        }
    }

    private void findViews() {
        mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvIndicator = (TextView) findViewById(R.id.tv_indicator);
        mTvDeletePhoto = (ImageView) findViewById(R.id.ib_delete_photo);
        mTvDeletePhoto = (ImageButton) findViewById(R.id.ib_delete_photo);
        mTvDownloadPhoto = (ImageButton) findViewById(R.id.ib_download_photo);

        mVpPager = (GFViewPager) findViewById(R.id.vp_pager);
    }

    private void setListener() {
        mVpPager.addOnPageChangeListener(this);
        mIvBack.setOnClickListener(mBackListener);
        mTvDeletePhoto.setOnClickListener(this);
        mTvDownloadPhoto.setOnClickListener(this);
    }

    private void setTheme() {
        mIvBack.setImageResource(mThemeConfig.getIconBack());
        if (mThemeConfig.getIconBack() == R.drawable.ic_gf_back) {
            mIvBack.setColorFilter(mThemeConfig.getTitleBarIconColor());
        }

        mTitleBar.setBackgroundColor(mThemeConfig.getTitleBarBgColor());
        mTvTitle.setTextColor(mThemeConfig.getTitleBarTextColor());
        if (mThemeConfig.getPreviewBg() != null) {
            mVpPager.setBackgroundDrawable(mThemeConfig.getPreviewBg());
        }

        if (GalleryFinal.getFunctionConfig().isEnablePreviewDelte()) {
            mTvDeletePhoto.setVisibility(View.VISIBLE);
        } else {
            mTvDeletePhoto.setVisibility(View.GONE);
        }

    }

    @Override
    protected void takeResult(PhotoInfo info) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        current = position;
        updatePercent();
    }

    protected void updatePercent() {
        if (mPhotoList.size() <= 0) {
            Log.e(this.getClass().getSimpleName(), "mPhotoList is null or mPhotoList's length is 0 !");
            return;
        }
        mTvIndicator.setText((current + 1) + "/" + mPhotoList.size());
        selectPhoto = mPhotoList.get(current);
        if (selectPhoto.getPhotoPath() != null && selectPhoto.getPhotoPath().contains("http")) {
            mTvDownloadPhoto.setVisibility(View.VISIBLE);
        } else {
            mTvDownloadPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private View.OnClickListener mBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ib_delete_photo) {

            if (mPhotoList.size() > 1) {
                deletePhoto.add(selectPhoto);

                mPhotoList.remove(selectPhoto);

                mVpPager.setAdapter(mPhotoPreviewAdapter);
                mVpPager.setCurrentItem(current);

                if (mPhotoList.size() == 1) {
                    current = 0;
                }
                updatePercent();

                Bundle bundle = new Bundle();
                bundle.putSerializable("deletemPhotoList", deletePhoto);
                setResult(Activity.RESULT_OK, getIntent().putExtras(bundle));
            } else if (mPhotoList.size() == 1) {
                deletePhoto.add(selectPhoto);

                mPhotoList.remove(selectPhoto);

                Bundle bundle = new Bundle();
                bundle.putSerializable("deletemPhotoList", deletePhoto);
                setResult(Activity.RESULT_OK, getIntent().putExtras(bundle));
                finish();
//                Toast.makeText(BasePhotoPreviewActivity.this, "最少保留一张照片", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.ib_download_photo) {
            if (Utils.isExitsSdcard()) {
                showDialog();
            }
        }
    }

    public void showDialog() {
        if (mDialog == null) {
            mDialog = new BaseDialogTitleContent(this, new BaseDialogTitleContent.SubmitListener() {
                @Override
                public void submit(String comment_content) {
                    new DownloadFile(PhotoPreviewActivity.this).execute(selectPhoto.getPhotoPath());
                    hintDialog();
                }
            });
            mDialog.setTitle("下载图片");
            mDialog.setContent("您是否要将此张图片下载到本地？");
        }
        synchronized (PhotoPreviewActivity.class) {
            mDialog.show();
        }
    }

    public void hintDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
}

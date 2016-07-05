package cn.finalteam.galleryfinal.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.finalteam.galleryfinal.R;


public class BaseDialogTitleContent implements View.OnClickListener {

    private Dialog mTipDlg = null;
    private LayoutInflater mLayoutInflater = null;
    private Context mContext = null;
    /**
     * 根视图
     */
    private View rootView = null;

    public TextView title;//标题
    public TextView content;//提示内容
    public TextView tv_left;//取消
    public TextView tv_right;//确定
    private SubmitListener mSubmitListener;

    private int mContentWidth;
    private int mContentMarginLeftRightPixels;

    public interface SubmitListener {
        void submit(String comment_content);
    }


    /**
     * 构造函数
     */
    public BaseDialogTitleContent(Context context, SubmitListener submitListener) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        this.mSubmitListener = submitListener;
        initLayout();
        initListener();
    }

    public void setTitle(String strTitle) {
        title.setText(strTitle);
    }

    public void setTitle(int resStrTitle) {
        title.setText(resStrTitle);
    }

    public void setContent(String strContent) {
        content.setText(strContent);
    }

    public void setContent(int resStrContent) {
        content.setText(resStrContent);
    }

    public void setTextLeft(String strLeft) {
        tv_left.setText(strLeft);
    }

    public void setTextLeft(int resStrLeft) {
        tv_left.setText(resStrLeft);
    }

    public void setTextRight(String strRight) {
        tv_right.setText(strRight);
    }

    public void setTextRight(int resStrRight) {
        tv_right.setText(resStrRight);
    }


    /**
     * 初始化视图组件
     */
    private void initLayout() {
        rootView = mLayoutInflater.inflate(R.layout.dialog_base_titlecontent, null);
        title = (TextView) rootView.findViewById(R.id.title);
        content = (TextView) rootView.findViewById(R.id.content);
        tv_left = (TextView) rootView.findViewById(R.id.tv_left);
        tv_right = (TextView) rootView.findViewById(R.id.tv_right);
    }

    private void initListener() {
        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
    }


    /**
     * 创建对话框
     */
    public void createDlg() {
        if (mTipDlg == null) {
            int width = getWindowsWidth((Activity) mContext);
            mTipDlg = new Dialog(mContext, R.style.AlertDialog);
            mTipDlg.setContentView(rootView);
            WindowManager.LayoutParams lp = mTipDlg.getWindow().getAttributes();
            lp.width = (int) (width * 0.75); // 设置宽度
            if (mContentMarginLeftRightPixels != 0) {
                content.getLayoutParams().width = lp.width - mContentMarginLeftRightPixels;
//                content.setMaxWidth(lp.width - mContentMarginLeftRightPixels);
            } else {
                lp.width = width - dip2px(mContext, 30);
            }
            if (mContentWidth != 0) {
                content.setWidth(mContentWidth);
            }
            mTipDlg.getWindow().setAttributes(lp);
            mTipDlg.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
        }

    }

    /**
     * 显示对话框
     */
    public void show() {
        if (mTipDlg != null && !mTipDlg.isShowing()) {
            mTipDlg.show();
        } else {
            createDlg();
            mTipDlg.show();
        }
    }

    /**
     * 取消显示对话框
     */
    public void dismiss() {
        if (mTipDlg != null && mTipDlg.isShowing()) {
            mTipDlg.dismiss();
            mTipDlg = null;
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_left) {
            dismiss();
            //取消
            if ("取消".equals(tv_left.getText().toString()) || "关闭".equals(tv_left.getText().toString())) {
                return;
            }
            mSubmitListener.submit(content.getText().toString());

        } else if (i == R.id.tv_right) {
            dismiss();
            if ("取消".equals(tv_right.getText().toString()) || "关闭".equals(tv_right.getText().toString())) {
                return;
            }
            mSubmitListener.submit(content.getText().toString());

        } else {
        }

    }

    public void setContentMarginLeftRight(int marginLeftRightPixels) {
        mContentMarginLeftRightPixels = marginLeftRightPixels;
    }

    public void setContentWidth(int contentWidth) {
        mContentWidth = contentWidth;
    }

    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
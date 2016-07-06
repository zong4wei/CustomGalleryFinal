package cn.finalteam.galleryfinal.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedList;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

public class EditAvaterAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedList<PhotoInfo> mInfos;
    private ImageLoader imageLoad = ImageLoader.getInstance();
    private int allWith;
    OnClickListener mOnClickListener;
    private int maxPhoto = -1;

    public EditAvaterAdapter(Context context, int maxPhoto, OnClickListener mOnClickListener) {
        this.mContext = context;
        this.mOnClickListener = mOnClickListener;
        this.maxPhoto = maxPhoto;
        allWith = CustomUtills.getScreenWidth(context) - CustomUtills.dp2Px(mContext, 50);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(parent
                    .getContext());
            convertView = layoutInflator.inflate(R.layout.item_edit_photo_avater, null);
            holder = new ViewHolder();
            holder.item_photo_edit_avater_iv = (ImageView) convertView
                    .findViewById(R.id.item_photo_edit_avater_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(allWith / 4, allWith / 4);
        holder.item_photo_edit_avater_iv.setLayoutParams(params);

        String img = mInfos.get(position).getPhotoPath();
        imageLoad.displayImage((img.contains("GalleryFinal") ? "file://" : "") + img, holder.item_photo_edit_avater_iv);

        holder.item_photo_edit_avater_iv.setOnClickListener(mOnClickListener);
        holder.item_photo_edit_avater_iv.setTag(R.id.item_photo_edit_avater_iv, position);

        return convertView;
    }


    class ViewHolder {
        ImageView item_photo_edit_avater_iv;
    }

    @Override
    public int getCount() {
        return mInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mInfos.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public void addItemLast(List<PhotoInfo> datas) {
        mInfos.addAll(datas);
    }

    public void addItemTop(List<PhotoInfo> datas) {
        for (int i = datas.size() - 1; i >= 0; i--) {
            PhotoInfo item = datas.get(i);
            mInfos.addFirst(item);
        }
    }

    public void addItemChangeMine(List<PhotoInfo> datas) {
        mInfos = new LinkedList<PhotoInfo>();
        for (int i = 0; i < (datas.size() >= maxPhoto ? maxPhoto : datas.size()); i++) {
            mInfos.add(datas.get(i));
        }
        if (datas.size() < maxPhoto) {
            PhotoInfo addPhoto = new PhotoInfo();
            addPhoto.setPhotoPath("drawable://" + R.mipmap.photo_upload_add);
            mInfos.addLast(addPhoto);
        }
    }

    public void addItemChange(List<PhotoInfo> datas) {
        mInfos = new LinkedList<PhotoInfo>();
        for (int i = 0; i < (datas.size() >= maxPhoto ? maxPhoto : datas.size()); i++) {
            mInfos.add(datas.get(i));
        }
    }
}

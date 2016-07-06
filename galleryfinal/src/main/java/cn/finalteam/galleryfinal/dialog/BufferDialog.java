package cn.finalteam.galleryfinal.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import cn.finalteam.galleryfinal.R;

public class BufferDialog extends Dialog implements DialogInterface {
    private int theme;
    private Context context;
    private ImageView buffer_iv;
    private AnimationDrawable animationDrawable;

    public BufferDialog(Context context) {
        super(context, R.style.MyDialogStyleTop);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custormdialog_buffer);
        buffer_iv = (ImageView) findViewById(R.id.buffer_iv);
        animationDrawable = (AnimationDrawable) buffer_iv.getDrawable();
        animationDrawable.setOneShot(false);
        animationDrawable.start();

    }
}

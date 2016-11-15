package net.lzzy.memocard.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.lzzy.memocard.R;

import java.util.List;

/**
 * Created by Administrator on 2016/5/18.
 * 显示图片
 */
public class MemoPictureActivity extends Activity {
    private int position;
    private List<String> files;
    private Button btn_up;
    private Button btn_next;
    private LinearLayout layout;
    private static final int TOUCH_MIN_DIST = 100;
    private ImageView iv;
    private ImageView[] img_dots;
    private float tocuh_x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        position = getIntent().getIntExtra(MemoPicturesActivity.MEMO_POSITION, 0);
        files = getIntent().getStringArrayListExtra(MemoPicturesActivity.FILES);
        initViews();
        iv.setImageURI(Uri.parse(files.get(position)));
        img_dots = new ImageView[files.size()];
        for (int i = 0; i < img_dots.length; i++) {
            ImageView dot = new ImageView(this);
            img_dots[i] = dot;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                    (ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            dot.setBackgroundResource(R.drawable.dot_unselected);
            layout.addView(dot, params);
        }

        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                up();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
        setSelectedDot();
        iv.setOnTouchListener(listener);
    }

    private void initViews() {
        iv = (ImageView) findViewById(R.id.activity_image_iv);
        btn_up = (Button) findViewById(R.id.activity_image_btn_up);
        btn_next = (Button) findViewById(R.id.activity_image_btn_next);
        layout = (LinearLayout) findViewById(R.id.activity_image_layout);
    }

    private void next() {
        if (position < files.size() - 1) {
            iv.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_next_out_right));
            iv.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_next_out_left));
            position++;
            iv.postOnAnimationDelayed(new Runnable() {
                @Override
                public void run() {
                    iv.setImageURI(Uri.parse(files.get(position)));
                }
            }, 400);
            setSelectedDot();
        } else {
            Toast.makeText(MemoPictureActivity.this, "已是最后一张",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void up() {
        if (position > 0) {
            //MediaStore.Images
            iv.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_up_out_left));
            iv.startAnimation(AnimationUtils.loadAnimation(this,
                    R.anim.slide_up_out_right));
            position--;
            iv.postOnAnimationDelayed(new Runnable() {
                @Override
                public void run() {
                    iv.setImageURI(Uri.parse(files.get(position)));
                }
            }, 400);
            setSelectedDot();
        } else {
            Toast.makeText(MemoPictureActivity.this, "已是第一张",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setSelectedDot() {
        for (int i = 0; i < img_dots.length; i++) {
            if (position == i)
                img_dots[i].setBackgroundResource(R.drawable.dot_selected);
            else
                img_dots[i].setBackgroundResource(R.drawable.dot_unselected);
        }
    }

    private View.OnTouchListener listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
                tocuh_x1 = event.getX();
            if (event.getAction() == MotionEvent.ACTION_UP) {
                float touch_x2 = event.getX();
                if (touch_x2 - TOUCH_MIN_DIST > tocuh_x1)//往右滑
                    up();
                else if (tocuh_x1 - TOUCH_MIN_DIST > touch_x2)//往左滑
                    next();
            }
            return true;
        }
    };
}

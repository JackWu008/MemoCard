package net.lzzy.memocard.activitys;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.memocard.IMemoService;
import net.lzzy.memocard.R;
import net.lzzy.memocard.models.Memo;
import net.lzzy.memocard.models.MemoFactory;
import net.lzzy.memocard.models.MemoPicture;
import net.lzzy.memocard.models.PictureFactory;
import net.lzzy.memocard.service.MusicService;
import net.lzzy.memocard.service.PictureService;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/4/27.
 * 备忘录的添加、删除、修改、显示
 */
public class MemoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MEMO_ID = "memoId";
    public static final String SAVE_OR_READ_CONTENT = "saveOrReadContent";
    public static final String SHARED_PREFERENCES = "SharedPreferences";
    private Memo memo;
    private boolean add = false;
    private String picName;
    private String picPath;
    private PictureFactory pictureFactory;
    private static final int REQUEST_PICTURE = 1;
    private EditText et_content;
    private FloatingActionButton but_photo;
    private FloatingActionButton but_camera;
    private SharedPreferences preferences;
    private String stringContent;
    private BroadcastReceiver receiver;
    private ServiceConnection conn;
    private TextView tv_hint;
    private ServiceConnection pconn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMemoService memoService = IMemoService.Stub.asInterface(service);
            try {
                int pCount = memoService.getPictureSize(memo.getId().toString());
                tv_hint.append("共有" + pCount + "张图片");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_memo_container);
        preferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        stringContent = preferences.getString(SAVE_OR_READ_CONTENT, "");
        pictureFactory = PictureFactory.getInstance(this);
        but_camera = (FloatingActionButton) findViewById(R.id.activity_memo_but_camera);
        but_photo = (FloatingActionButton) findViewById(R.id.activity_memo_but_showImage);
        et_content = (EditText) findViewById(R.id.activity_note_ed);
        Button but_save = (Button) findViewById(R.id.activity_note_but);
        tv_hint = (TextView) findViewById(R.id.activity_memo_tv);

        if (getIntent() != null) {
            add = getIntent().getBooleanExtra(MemosActivity.ADD, false);
            if (add) {
                tv_hint.setVisibility(View.GONE);
                memo = new Memo();
                et_content.setText(stringContent);
                et_content.requestFocus();
                et_content.setSelection(et_content.getText().length());//让光标后置
                memo.setContent(stringContent);
            } else {
                but_camera.setVisibility(View.GONE);
                UUID uuid = UUID.fromString(getIntent().getStringExtra(MemosActivity.UUID));
                memo = MemoFactory.getInstance(this).getById(uuid);
                et_content.setText(memo.getContent());
                et_content.setSelection(et_content.getText().length());
            }
        }

        picPath = Environment.getExternalStorageDirectory().getPath().concat("/pics/memo/");
        File dir = new File(picPath);
        dir.mkdirs();
        et_content.addTextChangedListener(new TextWatcher() {//文本变化Listener
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                memo.setContent(s.toString());
                memo.setUpdateTime(new Date());
                stringContent = et_content.getText().toString();
                if (add)
                    preferences.edit().putString(SAVE_OR_READ_CONTENT, s.toString()).apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        tv_hint.setOnClickListener(this);
        but_save.setOnClickListener(this);
        but_camera.setOnClickListener(this);
        but_photo.setOnClickListener(this);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(PictureFactory.NET_LZZY_ACTION_PICTURE_STATE)) {
                    if (intent.getIntExtra(PictureFactory.PICTURE_COUNT, 0) > 0) {
                        Toast.makeText(getApplicationContext(), "已添加" + intent.getIntExtra(PictureFactory.PICTURE_COUNT, 0) + "张图片", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(PictureFactory.NET_LZZY_ACTION_PICTURE_STATE));
        Intent service = new Intent(this, MusicService.class);
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                binder.playMusic();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(service, conn, BIND_AUTO_CREATE);
        Intent intent = new Intent(this, PictureService.class);
        bindService(intent, pconn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE) {
                String file = picPath.concat(picName);
                MemoPicture picture = new MemoPicture();
                picture.setFile(file);
                picture.setMemoId(memo.getId());
                pictureFactory.createPictureOfMemo(picture);
                Toast.makeText(this, "图片已添加！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_note_but:
                stringContent = et_content.getText().toString();
                if (et_content.getText().toString().trim().length() > 0) {
                    if (add) {
                        MemoFactory.getInstance(MemoActivity.this).createMemo(memo);
                        preferences.edit().clear().apply();
                    } else {
                        MemoFactory.getInstance(MemoActivity.this).update(memo);
                    }
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "未输入内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.activity_memo_but_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                picName = UUID.randomUUID().toString().concat(".png");
                Uri picUri = Uri.fromFile(new File(picPath.concat(picName)));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                startActivityForResult(intent, REQUEST_PICTURE);
                break;
            case R.id.activity_memo_but_showImage:
                Intent intent_view = new Intent(MemoActivity.this, MemoPicturesActivity.class);
                intent_view.putExtra(MEMO_ID, memo.getId().toString());
                startActivity(intent_view);
                break;
            case R.id.activity_memo_tv:
                tv_hint.setVisibility(View.GONE);
                but_camera.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<MemoPicture> pictures = pictureFactory.getPicturesOfMemo(memo.getId().toString());
        if (pictures.size() == 0) {
            but_photo.setVisibility(View.INVISIBLE);
        } else {
            but_photo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (et_content.getText().toString().trim().length() > 0) {
            if (add) {
                MemoFactory.getInstance(MemoActivity.this).createMemo(memo);
                preferences.edit().clear().apply();
            } else {
                MemoFactory.getInstance(MemoActivity.this).update(memo);
            }
            finish();
            super.onBackPressed();
        } else {
            if (add)
                super.onBackPressed();
            else
                Toast.makeText(getApplicationContext(), "内容为空不能保存", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(conn);
        unbindService(pconn);
    }
}

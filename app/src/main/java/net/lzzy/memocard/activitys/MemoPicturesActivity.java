package net.lzzy.memocard.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import net.lzzy.memocard.R;
import net.lzzy.memocard.models.MemoPicture;
import net.lzzy.memocard.models.PictureFactory;
import net.lzzy.memocard.util.GenericAdapter;
import net.lzzy.memocard.util.PictureReader;
import net.lzzy.memocard.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16.
 * 照片查看
 */
public class MemoPicturesActivity extends Activity {
    public static final String MEMO_POSITION = "MemoPosition";
    public static final String FILES = "files";

    private List<MemoPicture> pictures;
    private GridView gv;
    private GenericAdapter<MemoPicture> adapter;
    private boolean allSelected;
    private List<String> files;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        PictureFactory pictureFactory = PictureFactory.getInstance(this);
        String memoId = getIntent().getStringExtra(MemoActivity.MEMO_ID);
        pictures = pictureFactory.getPicturesOfMemo(memoId);
        Button but = (Button) findViewById(R.id.activity_memo_picture_but);
        files = pictureFactory.getPicturesFiles(memoId);
        gv = (GridView) findViewById(R.id.activity_memo_picture_gv);
        adapter = new GenericAdapter<MemoPicture>(this, R.layout.picture_item, pictures) {
            @Override
            public void populate(ViewHolder holder, MemoPicture picture) {

                Drawable drawable = PictureReader.getScaledDrawable(MemoPicturesActivity.this, getWidth() / 2, 0, picture.getFile());
                holder.setImagView(R.id.picture_item_iv, drawable);
            }
        };
        gv.setAdapter(adapter);

        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        gv.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle("已选中" + gv.getCheckedItemCount() + "项");
                if (gv.getCheckedItemCount() == pictures.size()) {
                    mode.getMenu().getItem(0).setTitle("取消");
                    allSelected = true;
                } else {
                    mode.getMenu().getItem(0).setTitle("全选");
                    allSelected = false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_picture, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_picture_delete:
                        if (gv.getCheckedItemCount() > 0) {
                            new AlertDialog.Builder(MemoPicturesActivity.this)
                                    .setTitle("提示")
                                    .setMessage("确定删除？")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            PictureFactory factory = PictureFactory.getInstance(MemoPicturesActivity.this);
                                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                                if (gv.isItemChecked(i)) {
                                                    factory.removePictureOfMemo(adapter.getItem(i));
                                                    pictures.remove(adapter.getItem(i));
                                                }
                                            }
                                            mode.finish();
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", null).show();
                        } else {
                            Toast.makeText(MemoPicturesActivity.this, "未选中！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.menu_picture_choose:
                        if (allSelected) {
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                gv.setItemChecked(i, false);
                            }
                        } else {
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                gv.setItemChecked(i, true);
                            }
                        }
                        break;
                    case R.id.menu_picture_invertSelection:
                        for (int i = 0; i < adapter.getCount(); i++)
                            gv.setItemChecked(i, !gv.isItemChecked(i));
                        break;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MemoPicturesActivity.this, MemoPictureActivity.class);
                intent.putExtra(MEMO_POSITION, position);
                intent.putStringArrayListExtra(FILES, (ArrayList<String>) files);
                startActivity(intent);
            }
        });
    }

    private int getWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x;
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}

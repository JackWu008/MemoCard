package net.lzzy.memocard.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import net.lzzy.memocard.R;
import net.lzzy.memocard.models.CreateMemoEvent;
import net.lzzy.memocard.models.Memo;
import net.lzzy.memocard.models.MemoFactory;
import net.lzzy.memocard.models.PictureFactory;
import net.lzzy.memocard.receivers.BatteryReceiver;
import net.lzzy.memocard.util.DateTimeUtil;
import net.lzzy.memocard.util.GenericAdapter;
import net.lzzy.memocard.util.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 * 备忘录列表
 */
public class MemosActivity extends AppCompatActivity {
    public static final String UUID = "uuid";
    public static final String ADD = "add";
    private List<Memo> memos;
    private GenericAdapter<Memo> adapter;
    private java.util.UUID uuid;
    private GridView gv;
    private SearchView search;
    private boolean allSelected;
    private long exitTime = 0;
    private BatteryReceiver batteryReceiver;//广播接收器

    private BroadcastReceiver sortReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MemoFactory.NET_LZZY_MEMO_UPDATE)) {
                MemoFactory.getInstance(MemosActivity.this).sort();
                adapter.notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memos);
        EventBus.getDefault().register(this);
        memos = MemoFactory.getInstance(this).getMemos();
        MemoFactory.getInstance(MemosActivity.this).sort();
        SearchView sv = (SearchView) findViewById(R.id.activity_memos_sv);
        ImageButton ib_add = (ImageButton) findViewById(R.id.activity_memos_ib);
        gv = (GridView) findViewById(R.id.activity_memos_gv);
        adapter = new GenericAdapter<Memo>(this, net.lzzy.memocard.R.layout.memo_item, memos) {
            @Override
            public void populate(ViewHolder holder, final Memo memo) {
                TextView tv_title = (TextView) holder.getView(R.id.activity_view_tv_title);
                TextView tv_time = (TextView) holder.getView(R.id.activity_view_tv_time);
                final CheckBox cb_done = (CheckBox) holder.getView(R.id.activity_view_cb);
                tv_time.setText(DateTimeUtil.DEFAULT_DATE_FORMAT.format(memo.getUpdateTime()));
                tv_title.setText(memo.getTitle());
                cb_done.setChecked(memo.isDone());
                cb_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        memo.setDone(cb_done.isChecked());
                        MemoFactory.getInstance(MemosActivity.this).update(memo);
                    }
                });
            }
        };
        gv.setAdapter(adapter);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                uuid = adapter.getItem(position).getId();
                Intent intent = new Intent(MemosActivity.this, MemoActivity.class);
                intent.putExtra(UUID, uuid.toString());
                startActivity(intent);
            }
        });

        ib_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemosActivity.this, MemoActivity.class);
                intent.putExtra(ADD, true);
                startActivity(intent);
            }
        });


        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                memos.clear();
                memos.addAll(MemoFactory.getInstance(MemosActivity.this).getMemos(newText));
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        gv.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        gv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                mode.setTitle("已选中" + gv.getCheckedItemCount() + "项");
                if (gv.getCheckedItemCount() == memos.size()) {
                    mode.getMenu().getItem(0).setTitle("取消");
                    allSelected = true;
                } else {
                    mode.getMenu().getItem(0).setTitle("全选");
                    allSelected = false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_memo, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_memo_content_delete:
                        if (gv.getCheckedItemCount() > 0) {
                            new AlertDialog.Builder(MemosActivity.this)
                                    .setTitle("提示")
                                    .setMessage("确定删除？")
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            MemoFactory factory = MemoFactory.getInstance(MemosActivity.this);
                                            PictureFactory factory1 = PictureFactory.getInstance(MemosActivity.this);
                                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                                if (gv.isItemChecked(i)) {
                                                    factory1.deletePictures(adapter.getItem(i).getId().toString());
                                                    factory.deleteMemo(adapter.getItem(i));
                                                }
                                            }
                                            mode.finish();
                                            adapter.notifyDataSetChanged();
                                        }
                                    })
                                    .setNegativeButton("取消", null).show();
                        } else {
                            Toast.makeText(MemosActivity.this, "未选中！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.menu_memo_share:
                        if (gv.getCheckedItemCount() > 0) {
                            String s = "";
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (gv.isItemChecked(i)) {
                                    s = s + adapter.getItem(i).getContent() + "\n";
                                }
                            }
                            MemoFactory.getInstance(MemosActivity.this).shareText(MemosActivity.this, s.substring(0, s.length() - 1));
                        } else {
                            Toast.makeText(MemosActivity.this, "未选中！", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.menu_memo_choose:
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
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });


        batteryReceiver = new BatteryReceiver();//广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(batteryReceiver, filter);//广播接收器
        registerReceiver(sortReceiver, new IntentFilter(MemoFactory.NET_LZZY_MEMO_UPDATE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            super.onBackPressed();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      //  search=new SearchView(this);
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_exit:
//                finish();
//                System.exit(0);
//                break;
//            case R.id.menu_about:
//                new AlertDialog.Builder(MemosActivity.this)
//                        .setTitle("卡片备忘录")
//                        .setMessage("作者很懒什么也没留下！")
//                        .setPositiveButton("ok", null)
//                        .show();
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);//注销广播接收器
        unregisterReceiver(sortReceiver);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(CreateMemoEvent event) {
        String uuid = event.getMid();
        adapter.notifyDataSetChanged();
    }
}

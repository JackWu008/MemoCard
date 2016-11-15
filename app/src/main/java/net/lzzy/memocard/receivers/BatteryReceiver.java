package net.lzzy.memocard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import net.lzzy.memocard.models.Memo;
import net.lzzy.memocard.models.MemoFactory;
import net.lzzy.memocard.util.DateTimeUtil;

import java.util.Date;

/**
 * Created by Administrator on 2016/5/31.
 * 广播接收器电量
 */
public class BatteryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String content = "";
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        switch (intent.getAction()) {
            case Intent.ACTION_BATTERY_LOW:
                content = "电量低 当前电量为" + (level / 100.0);
                break;
            case Intent.ACTION_BATTERY_OKAY:
                content = "电量满了";
                break;
            case Intent.ACTION_POWER_CONNECTED:
                content = "充电中";
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                content = "电源已断开";
                break;
            case Intent.ACTION_SCREEN_OFF:
                content = "屏慕已关闭";
                break;
            case Intent.ACTION_SCREEN_ON:
                content = "屏慕已开启";
                break;
        }
        content += "\n" + DateTimeUtil.DEFAULT_DATE_TIME_FORMAT.format(new Date());
        Memo memo = new Memo();
        memo.setContent(content);
        MemoFactory.getInstance(context).createMemo(memo);
    }
}

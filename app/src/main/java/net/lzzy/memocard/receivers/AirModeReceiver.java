package net.lzzy.memocard.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.lzzy.memocard.models.Memo;
import net.lzzy.memocard.models.MemoFactory;
import net.lzzy.memocard.util.DateTimeUtil;

import java.util.Date;

/**
 * Created by Administrator on 2016/5/30.
 * 继承BroadcastReceiver 广播接收器飞行模式
 */
public class AirModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_AIRPLANE_MODE_CHANGED)) {
            String content;
            if (intent.getBooleanExtra("state", false))
                content = "已开启飞行模式";
            else
                content = "已关闭飞行模式";
            content += "\n" + DateTimeUtil.DEFAULT_DATE_TIME_FORMAT.format(new Date());
           Memo memo = new Memo();
            memo.setContent(content);
          MemoFactory.getInstance(context).createMemo(memo);
        }
    }
}

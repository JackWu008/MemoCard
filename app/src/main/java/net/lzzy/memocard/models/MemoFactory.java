package net.lzzy.memocard.models;

import android.content.Context;
import android.content.Intent;

import net.lzzy.memocard.constants.DbConstants;
import net.lzzy.memocard.dataPersist.Repository;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/4/27.
 * Memo工厂 生产Memo 也就是数据
 */
public class MemoFactory {
    public static final String NET_LZZY_AIRPLANE_MODE_NOTICE = "netLzzyAirplaneModeNotice";
    public static final String NET_LZZY_MEMO_UPDATE = "netLzzyMemoUpdate";
    List<Memo> memos;
    private static MemoFactory instance;
    private Repository<Memo> repository;
    private Context context;

    private MemoFactory(Context context) {
        this.context = context;
        repository = new Repository<>(context, Memo.class);
        try {
            memos = repository.getByKeyWord(null, new String[]{}, false);
        } catch (Exception e) {
            memos = new ArrayList<>();
        }
    }

    public static MemoFactory getInstance(Context context) {
        if (instance == null)
            instance = new MemoFactory(context);
        return instance;
    }

    public List<Memo> getMemos() {
        return memos;
    }

    public Memo getById(UUID uuid) {
        for (Memo m : memos) {
            if (m.getId().equals(uuid))
                return m;
        }
        return null;
    }

    public List<Memo> getMemos(String kw) {
        try {
            return repository.getByKeyWord(kw, new String[]{DbConstants.MEMO_CONTENT}, false);
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    public void createMemo(Memo memo) {
        memos.add(0, memo);
        repository.insert(memo);
        context.sendBroadcast(new Intent(NET_LZZY_AIRPLANE_MODE_NOTICE));
        EventBus.getDefault().post(new CreateMemoEvent(memo.getId().toString()));
    }

    public void update(Memo memo) {
        repository.update(memo);
        context.sendBroadcast(new Intent(NET_LZZY_MEMO_UPDATE));
    }

    public void deleteMemo(Memo memo) {
        try {
            repository.delete(memo.getId());
            memos.remove(memo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareText(Context context, String text) {
        if (context == null || text == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "分享备忘录内容"));
    }

    public void sort() {
        Collections.sort(memos, new Comparator<Memo>() {
            @Override
            public int compare(Memo lhs, Memo rhs) {
                int l = lhs.isDone() ? 1 : 0;
                int r = rhs.isDone() ? 1 : 0;
                if (l > r)
                    return 1;
                if (r > l)
                    return -1;
                if (r == l) {
                    long lTime = lhs.getUpdateTime().getTime();
                    long rTime = rhs.getUpdateTime().getTime();
                    if (lTime > rTime)
                        return -1;
                    if (rTime > lTime)
                        return 1;
                }
                return 0;
            }
        });
    }
}

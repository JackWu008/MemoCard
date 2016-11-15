package net.lzzy.memocard.models;

import android.content.Context;
import android.content.Intent;

import net.lzzy.memocard.dataPersist.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/11.
 * 图片data
 */
public class PictureFactory {
    public static final String NET_LZZY_ACTION_PICTURE_STATE = "netLzzyActionPictureState";
    public static final String PICTURE_MEMO_ID = "pictureMemoId";
    public static final String PICTURE_COUNT = "PICTURE_COUNT";
    List<MemoPicture> pictures;
    private static PictureFactory ourInstance;
    private Repository<MemoPicture> repository;
    private List<String> files;
    private Context context;

    private PictureFactory(Context context) {
        this.context = context;
        repository = new Repository<>(context, MemoPicture.class);
        try {
            pictures = repository.getByKeyWord(null, new String[]{}, false);
        } catch (Exception e) {
            pictures = new ArrayList<>();
        }
    }

    public static PictureFactory getInstance(Context context) {//锁定多线程
        if (ourInstance == null) {
            synchronized (PictureFactory.class) {
                if (ourInstance == null)
                    ourInstance = new PictureFactory(context);
            }
        }
        return ourInstance;
    }

    public List<MemoPicture> getPicturesOfMemo(String mid) {
        List<MemoPicture> picturesOfMemo = new ArrayList<>();
        for (MemoPicture picture : pictures) {
            if (picture.getMemoId().toString().equals(mid))
                picturesOfMemo.add(picture);
        }
        return picturesOfMemo;
    }

    public void createPictureOfMemo(MemoPicture picture) {
        repository.insert(picture);
        pictures.add(picture);
        int count = 0;
        for (MemoPicture p : pictures) {
            if (p.getMemoId().equals(picture.getMemoId()))
                count++;
        }
        Intent intent = new Intent(NET_LZZY_ACTION_PICTURE_STATE);
        intent.putExtra(PICTURE_MEMO_ID, picture.getMemoId());
        intent.putExtra(PICTURE_COUNT, count);
        context.sendBroadcast(intent);
    }

    public void removePictureOfMemo(MemoPicture picture) {
        File file = new File(picture.getFile());
        try {
            pictures.remove(picture);
            files.remove(picture.getFile());
            repository.delete(picture.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        file.delete();
    }

    public List<String> getPicturesFiles(String MemoId) {
        files = new ArrayList<>();
        for (MemoPicture picture : pictures) {
            if (picture.getMemoId().toString().equals(MemoId))
                files.add(picture.getFile());
        }
        return files;
    }

    public List<MemoPicture> getPictures() {
        return pictures;
    }

    public void deletePictures(String memoId) {
        if (pictures.size() > 0) {
            for (int i = pictures.size() - 1; i >= 0; i--) {
                if (pictures.get(i).getMemoId().toString().equals(memoId)) {
                    removePictureOfMemo(pictures.get(i));
                }
            }
        }
    }
}

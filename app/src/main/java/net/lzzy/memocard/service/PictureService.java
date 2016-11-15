package net.lzzy.memocard.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import net.lzzy.memocard.IMemoService;
import net.lzzy.memocard.models.PictureFactory;

/**
 * Created by Administrator on 2016/6/2.
 * aidl 进程访问
 */
public class PictureService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    IMemoService.Stub binder=new IMemoService.Stub() {
        @Override
        public int getPictureSize(String id) throws RemoteException {
            PictureFactory factory=PictureFactory.getInstance(getApplicationContext());
            if (id.length()==0)
                return factory.getPictures().size();
            return factory.getPicturesOfMemo(id).size();
        }
    };
}

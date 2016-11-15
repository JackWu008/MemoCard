package net.lzzy.memocard.models;

import android.database.Cursor;

import net.lzzy.memocard.constants.DbConstants;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Administrator on 2016/5/11.
 * 图片
 */
public class MemoPicture implements SQLiteTable {
    private UUID id;
    private String file;
    private UUID memoId;


    public MemoPicture() {
        id = UUID.randomUUID();
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public UUID getId() {
        return id;
    }

    public UUID getMemoId() {
        return memoId;
    }

    public void setMemoId(UUID memoId) {
        this.memoId = memoId;
    }

    @Override
    public String getTableName() {
        return DbConstants.PICTURE_TABLE_NAME;

    }

    @Override
    public HashMap<String, Object> getInsertCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.PICTURE_ID, id.toString());
        map.put(DbConstants.PICTURE_FILE, file);
        map.put(DbConstants.PICTURE_MEMO_ID, memoId.toString());
        return map;
    }

    @Override
    public HashMap<String, Object> getUpdateCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.PICTURE_FILE, file);
        map.put(DbConstants.PICTURE_MEMO_ID, memoId.toString());
        return map;

    }

    @Override
    public String getPKName() {
        return DbConstants.PICTURE_ID;

    }

    @Override
    public UUID getUUID() {
        return id;
    }

    @Override
    public void fromCursor(Cursor cursor) {
        id = UUID.fromString(cursor.getString(cursor.getColumnIndex(DbConstants.PICTURE_ID)));
        file = cursor.getString(cursor.getColumnIndex(DbConstants.PICTURE_FILE));
        memoId = UUID.fromString(cursor.getString(cursor.getColumnIndex(DbConstants.PICTURE_MEMO_ID)));
    }
}

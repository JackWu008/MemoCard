package net.lzzy.memocard.models;

import android.database.Cursor;

import net.lzzy.memocard.constants.DbConstants;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Administrator on 2016/4/27.
 * memo对象
 */
public class Memo implements SQLiteTable {
    private UUID id;
    private String title;//显示部分内容
    private String content;//内容
    private Date updateTime;//日期
    private boolean done;

    public Memo(UUID id) {
        this.id = id;
        updateTime = new Date();
    }

    public Memo() {
        id = java.util.UUID.randomUUID();
        updateTime = new Date();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        if (content.length() > 44) {
            title = content.substring(0, 44).concat("...");
        } else {
            title = content;
        }
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String getTableName() {
        return DbConstants.MEMO_TABLE_NAME;
    }

    @Override
    public HashMap<String, Object> getInsertCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.MEMO_ID, id.toString());
        map.put(DbConstants.MEMO_CONTENT, content);
        map.put(DbConstants.MEMO_UPDATE_TIME, updateTime.getTime());
        map.put(DbConstants.MEMO_DONE, done ? 1 : 0);
        return map;
    }

    @Override
    public HashMap<String, Object> getUpdateCols() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(DbConstants.MEMO_CONTENT, content);
        map.put(DbConstants.MEMO_UPDATE_TIME, updateTime.getTime());
        map.put(DbConstants.MEMO_DONE, done ? 1 : 0);
        return map;
    }

    @Override
    public String getPKName() {
        return DbConstants.MEMO_ID;
    }

    @Override
    public UUID getUUID() {
        return id;
    }

    @Override
    public void fromCursor(Cursor cursor) {
        id = UUID.fromString(cursor.getString(cursor.getColumnIndex(DbConstants.MEMO_ID)));
        setContent(cursor.getString(cursor.getColumnIndex(DbConstants.MEMO_CONTENT)));
        setDone(cursor.getInt(cursor.getColumnIndex(DbConstants.MEMO_DONE)) > 0);
        updateTime = new Date(cursor.getLong(cursor.getColumnIndex(DbConstants.MEMO_UPDATE_TIME)));
    }
}

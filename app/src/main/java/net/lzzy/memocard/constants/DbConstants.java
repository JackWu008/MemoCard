package net.lzzy.memocard.constants;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/4.
 * 静态常量键
 */
public final class DbConstants {
    private DbConstants() {
    }//不让实例化 不给NEW和单例模式不一样

    public static final String DB_NAME = "easy.db";
    /**
     * 数据库版本
     */
    public static final int DB_VERSION = 1;
    /**
     * 创建表头语句
     **/
    public static final String CREATE_TABLE_HEAD = "create table ";
    /**
     * 数据库字段类型
     */
    private static final String TYPE_INT = " integer";
    private static final String TYPE_STRING = " text";
    //    private static final String TYPE_DOUBLE = " real";
    private static final String TYPE_REAL = " real";

    private static final String RESTRAIN_AUTOINCREMENT = " primary key autoincrement";

    /**
     * table memo
     **/
    public static final String MEMO_TABLE_NAME = "memo";
    public static final String MEMO_ID = "id";
    public static final String MEMO_CONTENT = "content";
    public static final String MEMO_UPDATE_TIME = "updateTime";
    public static final String MEMO_DONE = "done";
    public static final StringBuilder MEMO_TABLE_SQL = new StringBuilder();

    /**
     * table picture
     **/
    public static final String PICTURE_TABLE_NAME = "picture";
    public static final String PICTURE_ID = "id";
    public static final String PICTURE_FILE = "file";
    public static final String PICTURE_MEMO_ID = "memoId";
    public static final StringBuilder PICTURE_TABLE_SQL = new StringBuilder();

    public static final List<String> CREATE_TABLE_SQLS = new ArrayList<>();
    public static final List<String> UPDATE_TABLE_SQLS = new ArrayList<>();

    static {
        MEMO_TABLE_SQL.append(CREATE_TABLE_HEAD)
                .append(MEMO_TABLE_NAME).append("(")
                .append(MEMO_ID).append(TYPE_STRING).append(",")
                .append(MEMO_CONTENT).append(TYPE_STRING).append(",")
                .append(MEMO_UPDATE_TIME).append(TYPE_REAL).append(",")
                .append(MEMO_DONE).append(TYPE_INT).append(")");
        PICTURE_TABLE_SQL.append(CREATE_TABLE_HEAD)
                .append(PICTURE_TABLE_NAME).append("(")
                .append(PICTURE_ID).append(TYPE_STRING).append(",")
                .append(PICTURE_FILE).append(TYPE_STRING).append(",")
                .append(PICTURE_MEMO_ID).append(TYPE_STRING).append(")");
        CREATE_TABLE_SQLS.add(MEMO_TABLE_SQL.toString());
        CREATE_TABLE_SQLS.add(PICTURE_TABLE_SQL.toString());
       // UPDATE_TABLE_SQLS.add(PHONE_STATE_TABLE_SQL.toString());
    }
}

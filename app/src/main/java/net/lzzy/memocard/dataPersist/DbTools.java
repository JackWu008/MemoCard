package net.lzzy.memocard.dataPersist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.lzzy.memocard.util.DatasUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 * sql工具
 */
public class DbTools extends SQLiteOpenHelper {
    private List<String> createTableSqls;
    private List<String> updateTableSqls;

    public DbTools(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbTools(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DbTools(Context context, String name) {
        this(context, name, 1);
    }

    public void setUpdateTableSqls(List<String> updateTableSqls) {
        this.updateTableSqls = updateTableSqls;
    }

    public void setCreateTableSqls(List<String> createTableSqls) {
        this.createTableSqls = createTableSqls;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//数据库创建时 第1次
        if (createTableSqls.size() > 0) {
            for (String s : createTableSqls) {
                db.execSQL(s);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//数据更新时
        if (updateTableSqls.size() > 0) {
            for (String s : updateTableSqls) {
                db.execSQL(s);
            }
        }
    }


    public void insertRows(String table, List<String> cols, List<Object> values) {
        String sql = DatasUtil.getInsert(table, cols, values);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    /**
     * 更新数据库
     * table 表名
     * cols 要更新的列
     * values 要更新列的值，与cols必须一一对应
     * whereCols 作为条件的列名
     * whereVals 条件列参数，与whereCols必须一一对应
     */
    public void updateRows(String table, List<String> cols, List<Object> vals,
                           List<String> whereCols, List<Object> whereVals) {
        String sql = DatasUtil.getUpdateRows(table, cols, vals, whereCols, whereVals);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    /**
     * 删除数据行
     * table 表名
     * whereCols 作为条件的列名
     * whereVals 条件列参数，与whereCols必须一一对应
     */
    public void deleteRows(String table, List<String> whereCols, List<Object> whereVals) {
        String sql = DatasUtil.getDeleteRow(table, whereCols, whereVals);
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
    }

    /**
     * cursor
     * <p/>
     * orderBy 拍序列，null表示使用默认排序
     * limit 等同于top
     * return 结果集的Cursor对象，记住读取完后关闭cursor
     */
    public Cursor getCursor(String table, String[] columns, String selection, String[] selectionArgs,
                            String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * 对多个数据库操作执行事务处理
     * sqls 多个操作的sql
     */
    public void execTrans(List<String> sqls) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (String s : sqls) {
                db.execSQL(s);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
}

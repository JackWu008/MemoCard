package net.lzzy.memocard.dataPersist;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import net.lzzy.memocard.constants.DbConstants;
import net.lzzy.memocard.models.SQLiteTable;
import net.lzzy.memocard.util.DatasUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/5/4.
 * sql的CRUD方法
 */
public class Repository<T extends SQLiteTable> {
    private DbTools dbTools;
    private Class<T> classType;

    public Repository(Context context, Class<T> classType) {
        dbTools = new DbTools(context, DbConstants.DB_NAME, DbConstants.DB_VERSION);
        dbTools.setCreateTableSqls(DbConstants.CREATE_TABLE_SQLS);
        dbTools.setUpdateTableSqls(DbConstants.UPDATE_TABLE_SQLS);
        this.classType = classType;
    }


    public void insert(T t) {
        List<String> cols = new ArrayList<>();
        List<Object> vals = new ArrayList<>();
        for (Map.Entry<String, Object> entry : t.getInsertCols().entrySet()) {
            cols.add(entry.getKey());
            vals.add(entry.getValue());
        }
        dbTools.insertRows(t.getTableName(), cols, vals);
    }

    public void update(T t) {
        List<String> cols = new ArrayList<>();
        List<Object> vals = new ArrayList<>();
        for (Map.Entry<String, Object> entry : t.getUpdateCols().entrySet()) {
            cols.add(entry.getKey());
            vals.add(entry.getValue());
        }

        List<String> whereCols = new ArrayList<>();
        List<Object> whereVals = new ArrayList<>();
        whereCols.add(t.getPKName());
        whereVals.add(t.getUUID().toString());
        dbTools.updateRows(t.getTableName(), cols, vals, whereCols, whereVals);
    }

    public void delete(UUID id) throws IllegalAccessException, InstantiationException {
        List<String> whereCols = new ArrayList<>();
        List<Object> whereVals = new ArrayList<>();
        T t = classType.newInstance();
        whereCols.add(t.getPKName());
        whereVals.add(id.toString());
        dbTools.deleteRows(t.getTableName(), whereCols, whereVals);
    }

    public List<T> getByKeyWord(String kw, String[] cols, boolean explicit) throws IllegalAccessException, InstantiationException {
        List<T> list = new ArrayList<>();
        String selection;
        String[] selectionArgs;
        SparseArray<String> array = explicit ? DatasUtil.getSelectionAndArgsExplicit(kw, cols) :
                DatasUtil.getSelectionAndArgs(kw, cols);
        selection = array.get(DatasUtil.KEY_SELECTION);
        String allArgs = array.get(DatasUtil.KEY_ARGUMENTS);
        selectionArgs = allArgs == null ? null : allArgs.split(",");
       // String orderBy = "updateTime desc";
        Cursor cursor = dbTools.getCursor(classType.newInstance().getTableName(), null, selection, selectionArgs,
                null, null, null, null);
        while (cursor.moveToNext()) {
            T t = classType.newInstance();
            t.fromCursor(cursor);
            list.add(0,t);
        }
        cursor.close();
        return list;
    }
}

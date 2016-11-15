package net.lzzy.memocard.util;

import android.util.SparseArray;

import java.util.List;
import java.util.Objects;

/**
 * Created by Administrator on 2016/4/28.
 * 生成sql
 */
public class DatasUtil {
    public static final int KEY_SELECTION = 0;
    public static final int KEY_ARGUMENTS = 1;

    public static String getInsert(String tableName, List<String> cols, List<Object> values) {
        String sql = "INSERT INTO ".concat(tableName).concat("(");
        String colsString = "";
        for (String c : cols) {
            colsString += c + ",";
        }
        colsString = colsString.substring(0, colsString.length() - 1).concat(")");
        sql = sql.concat(colsString).concat("values(");
        for (Object val : values) {
            if (val instanceof String)
                sql = sql.concat("'").concat(val.toString()).concat("'");
            else
                sql = sql.concat(val.toString());
            sql = sql.concat(",");
        }
        sql = sql.substring(0, sql.length() - 1).concat(")");
        return sql;
    }

    public static String getDeleteRow(String table, List<String> whereCols, List<Object> whereVals) {
        String sql = "DELETE FROM ".concat(table).concat(" where ");
        for (int i = 0; i < whereCols.size(); i++) {
            if (whereVals.get(i) instanceof String)
                sql = sql.concat(whereCols.get(i) + "='" + whereVals.get(i) + "' and ");
            else
                sql = sql.concat(whereCols.get(i) + "=" + whereVals.get(i) + " and ");
        }
        sql = sql.substring(0, sql.length() - 5);
        return sql;

//        String sql = "delete from " + table;
//        if (whereCols != null && whereCols.size() > 0) {
//            sql += " where ";
//            int a = 0;
//            for (String col : whereCols) {
//                Object val = whereVals.get(a++);
//                if (val instanceof String)
//                    sql += col + "='" + val + "' and ";
//                else
//                    sql += col + "=" + val + " and ";
//            }
//        }
//        sql = sql.substring(0, sql.length() - 5);
//        return sql;
    }

    public static String getUpdateRows(String table, List<String> cols, List<Object> vals,
                                       List<String> whereCols, List<Object> whereVals) {
        String sql = "UPDATE ".concat(table).concat(" SET ");
        for (int i = 0; i < cols.size(); i++) {
            if (vals.get(i) instanceof String)
                sql = sql.concat(cols.get(i) + "='" + vals.get(i) + "',");
            else
                sql = sql.concat(cols.get(i) + "=" + vals.get(i) + ",");
        }
        sql = sql.substring(0, sql.length() - 1).concat(" where ");
        for (int i = 0; i < whereCols.size(); i++) {
            if (whereVals.get(i) instanceof String)
                sql = sql.concat(whereCols.get(i) + "='" + whereVals.get(i) + "' and ");
            else
                sql = sql.concat(whereCols.get(i) + "=" + whereVals.get(i) + " and ");
        }
        sql = sql.substring(0, sql.length() - 5);
        return sql;
//        StringBuilder builder = new StringBuilder();
//        builder.append("update ").append(table).append(" set ");
//        int a = 0;
//        for (String col : cols) {
//            Object val = vals.get(a++);
//            if (val instanceof String)
//                builder.append(col).append("='").append(val).append("',");
//            else
//                builder.append(col).append("=").append(val).append(",");
//        }
//        builder.deleteCharAt(builder.length() - 1);
//        a = 0;
//        if (whereCols != null && whereCols.size() > 0) {
//            builder.append(" where ");
//            for (String col : whereCols) {
//                Object val = whereVals.get(a++);
//                if (val instanceof String)
//                    builder.append(col).append("='").append(val).append("' and ");
//                else
//                    builder.append(col).append("=").append(val).append(" and ");
//            }
//            builder.delete(builder.length()-4,builder.length()-1);
//        }
//        return builder.toString();
    }

    public static SparseArray<String> getSelectionAndArgs(String kw, String[] cols) {
        SparseArray<String> array = new SparseArray<>();
        String selection = "";
        String keywordArgs = "";
        if (kw == null || kw.length() == 0) {
            array.put(KEY_SELECTION, null);
            array.put(KEY_ARGUMENTS, null);
            return array;
        }
        for (String col : cols) {
            selection += col + " like ? or ";
            keywordArgs += "%" + kw + "%,";
        }
        selection = selection.substring(0, selection.length() - 4);
        keywordArgs = keywordArgs.substring(0, keywordArgs.length() - 1);
        array.put(KEY_SELECTION, selection);
        array.put(KEY_ARGUMENTS, keywordArgs);
        return array;
    }

    public static SparseArray<String> getSelectionAndArgsExplicit(String kw, String[] cols) {
        SparseArray<String> array = new SparseArray<>();
        String selection = "";
        String keywordArgs = "";
        if (kw == null || kw.length() == 0) {
            array.put(KEY_SELECTION, null);
            array.put(KEY_ARGUMENTS, null);
            return array;
        }
        for (String col : cols) {
            selection += col + " = ? or ";
            keywordArgs += ",";
        }
        selection = selection.substring(0, selection.length() - 4);
        keywordArgs = keywordArgs.substring(0, keywordArgs.length() - 1);
        array.put(KEY_SELECTION, selection);
        array.put(KEY_ARGUMENTS, keywordArgs);
        return array;
    }
}

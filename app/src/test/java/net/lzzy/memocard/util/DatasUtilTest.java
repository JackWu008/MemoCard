package net.lzzy.memocard.util;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class DatasUtilTest {
    @Test
    public void testGetInsert() throws Exception {
        String table = "company";
        List<String> cols = new ArrayList<>();
        cols.add("id");
        cols.add("name");
        cols.add("age");
        cols.add("address");
        cols.add("salary");
        List<Object> values = new ArrayList<>();
        values.add(1);
        values.add("Paul");
        values.add(32);
        values.add("California");
        values.add(20000.0);
        String actual = DatasUtil.getInsert(table, cols, values);
        String expected = "INSERT INTO COMPANY(ID,NAME,AGE,ADDRESS,SALARY)VALUES(1,'Paul',32,'California',20000.0)";
        assertEquals(actual.toUpperCase(), expected.toUpperCase());
    }

    @Test
    public void testGetUpdateRows() throws Exception {
        String table = "company";
        List<String> cols = new ArrayList<>();
        cols.add("address");
        cols.add("name");
        List<Object> vals = new ArrayList<>();
        vals.add("texas");
        vals.add("abc");
        List<String> wCols = new ArrayList<>();
        wCols.add("id");
        wCols.add("name");
        List<Object> wVals = new ArrayList<>();
        wVals.add(6);
        wVals.add("kim");
        String actual = DatasUtil.getUpdateRows(table, cols, vals, wCols, wVals);
        String expected = "UPDATE COMPANY SET ADDRESS='Texas',NAME='ABC' " +
                "WHERE ID=6 AND NAME='KIM'";
//        assertEquals(sql.trim().toUpperCase(), result.toUpperCase());
        assertEquals(expected.toUpperCase(), actual.toUpperCase());
    }

    @Test
    public void testGetDeleteRow() throws Exception {
        String table = "company";
        List<String> wCols = new ArrayList<>();
        wCols.add("id");
        List<Object> wVals = new ArrayList<>();
        wVals.add(7);
        String actual = DatasUtil.getDeleteRow(table, wCols, wVals);
        String expected = "DELETE FROM COMPANY WHERE ID=7";
        assertEquals(expected.toUpperCase(), actual.toUpperCase());
    }


    @Test
    public void testGetSelectionAndArgs() throws Exception {
        String kw = "1";
        String[] cols = {"id","name"};
        String actual =DatasUtil.getSelectionAndArgs(kw, cols).get(DatasUtil.KEY_SELECTION);

        String expected = "id like ? or name like ?";
        assertEquals(expected.toUpperCase(), actual.toUpperCase());
    }
}
package sql_classes;

import java.io.Serializable;
import java.util.ArrayList;

public class ResultColumn implements Serializable,Cloneable {
    String ColumnID;
    ArrayList<String> values ;

    public ResultColumn() {
       values = new ArrayList<>();
    }

    public String getColumnID() {
        return ColumnID;
    }

    public ResultColumn setColumnID(String columnID) {
        ColumnID = columnID;
        return this;
    }

    public ArrayList<String> getValues() {
        return values;
    }



    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public void addValueToArray(String value)
    {
        values.add(value);
    }

    public void update(int position,String value)
    {
        values.set(position,value);
    }

    public String[] convertValueListToArray()
    {
        String[] arr = new String[values.size()];
        values.toArray(arr);
        return arr;
    }
    public static class Array implements Cloneable{
        ArrayList<String> values;

    }







}

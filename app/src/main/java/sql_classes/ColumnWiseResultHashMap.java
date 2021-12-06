package sql_classes;

import android.util.Log;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;

public class ColumnWiseResultHashMap implements Serializable, Cloneable {
    LinkedHashMap<String, ResultColumn> QueryResult = new LinkedHashMap<>();
    int rowCount=0;
    ResultColumn resultColumn;

    public ColumnWiseResultHashMap() {
        //rowCount = in.readInt();
    }



    public void addResultColumn(ResultColumn resultColumn)
    {

        //Log.e("ColumnWiseHashMap",resultColumn.getColumnID()+"-"+resultColumn.getValues().get(0));
        QueryResult.put(resultColumn.getColumnID().toLowerCase(),resultColumn);
        this.resultColumn=resultColumn;
    }
    public ColumnWiseResultHashMap getBlankCopy()
    {
        ColumnWiseResultHashMap resultHashMap = new ColumnWiseResultHashMap();
        for (ResultColumn column:QueryResult.values()
             ) {
            ResultColumn resultColumn = new ResultColumn();
            resultColumn.setColumnID(column.getColumnID());
            resultHashMap.addResultColumn(resultColumn);
        }
        return resultHashMap;
    }
    public int size()
    {
        return QueryResult.size();
    }
    //public Linked

    public int getRowCount()
    {
        return resultColumn.getValues().size() ;
    }

    public ResultColumn getColumn(String label)
    {
        return QueryResult.get(label.toLowerCase());
    }

    public String getColumnValue(String columnname,int index)
    {
        if(QueryResult.get(columnname.toLowerCase())==null)
        {
            Log.e("SqlError",columnname+" not found!");
            return "-1";
        }
        else
        {
//            Log.w("SqlError",columnname+" found!");
//            if(QueryResult.get("macname")!=null)
//            {
//                Log.w("dhruvcheck",QueryResult.get("macname").getValues().get(0));
//            }
            return QueryResult.get(columnname.toLowerCase()).getValues().get(index);
        }
    }

    public Collection<ResultColumn> getValues()
    {
        return QueryResult.values();
    }

    public String getMsg(String defaultMsg)
    {
        ResultColumn resultColumn = QueryResult.get("msg");
        if(resultColumn!=null)
        {
            return resultColumn.getValues().get(0);
        }
        else
        {
            return defaultMsg;
        }
    }
    public String getMsg(String defaultMsg,int position)
    {
        ResultColumn resultColumn = QueryResult.get("msg");
        if(resultColumn!=null)
        {
            return resultColumn.getValues().get(position);
        }
        else
        {
            return defaultMsg;
        }
    }


    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public int getColumnCount()
    {
        return QueryResult.size();
    }

    public boolean getCommand()
    {
        ResultColumn resultColumn= QueryResult.get("command");
        if(resultColumn==null)
        {
            return true;
        }
        else
        {
            if(resultColumn.getValues().get(0).equals("1"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }


    public boolean getCommand(int row)
    {
        ResultColumn resultColumn= QueryResult.get("command");
        if(resultColumn==null)
        {
            return true;
        }
        else
        {
            if(resultColumn.getValues().get(row).equals("1"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }


}

package com.example.hawkeyeconfigure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import sql_classes.ColumnWiseResultHashMap;
import sql_classes.ResultColumn;

public class HashGridList extends AppCompatActivity implements Serializable {


    LinearLayout horizontal;
    EditText search ;
    String label;
    ColumnWiseResultHashMap columns;
    ArrayList<ResultColumn> tempcolumns;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_hash_grid_list);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


//        getWindow()
//                .setLayout(
//                        ViewGroup.LayoutParams.FILL_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT
//                );
        horizontal=findViewById(R.id.horizontal);
        columns =(ColumnWiseResultHashMap)getIntent().getSerializableExtra("data");
        label=getIntent().getStringExtra("label");
        showData(columns);
        search = findViewById(R.id.searchtxt);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ColumnWiseResultHashMap resultcolumns=new ColumnWiseResultHashMap();
                for (ResultColumn column:columns.getValues()
                     ) {
                    ResultColumn resultColumn = new ResultColumn();
                    resultColumn.setColumnID(column.getColumnID());
                    resultcolumns.addResultColumn(resultColumn);
                }
//                for(int c=0;c<columns.size();c++)
//                {
//                    ResultColumn resultColumn = new ResultColumn();
//                    resultColumn.setColumnID(columns.get(c).getColumnID());
//                    resultcolumns.add(resultColumn);
//                }
                
                for(int row=0;row<columns.getRowCount();row++)
                {
                    int found =0;

                    for (ResultColumn resultColumn:columns.getValues()
                         ) {
                        if(resultColumn.getValues().get(row)!=null&&resultColumn.getValues().get(row).toLowerCase().contains(charSequence.toString().toLowerCase()))
                        {
                            found=1;
                            break;
                        }
                    }
//                    for(int column=0;column<columns.size();column++)
//                    {
//                        if(columns.get(column).getValues().get(row)!=null&&columns.get(column).getValues().get(row).toLowerCase().contains(charSequence.toString().toLowerCase()))
//                        {
//                            found=1;
//                            break;
//                        }
//                    }
                    if(found==1)
                    {

                        //ResultColumn columnWiseResult = new ResultColumn();
                        for (ResultColumn resultcolumn:columns.getValues()
                             ) {
                            resultcolumns.getColumn(resultcolumn.getColumnID()).addValueToArray(resultcolumn.getValues().get(row));
                        }
                        
//                        for (int c = 0; c < columns.size(); c++) {
//                            resultcolumns.get(c).addValueToArray(columns.get(c).getValues().get(row));
//                        }


                        //resultcolumns.add(columns.get(column));
                    }
                }
                showData(resultcolumns);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }






    public void showData(final ColumnWiseResultHashMap columns)
    {

        horizontal.removeAllViews();
        if(columns.getValues().size()>0) {
            final View[][] views = new View[columns.getRowCount()][columns.getValues().size()];
            int columnsize=0;
            for (ResultColumn resultColumn:columns.getValues()
                 ) {
                LinearLayout vertical = new LinearLayout(HashGridList.this);
                vertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                vertical.setOrientation(LinearLayout.VERTICAL);
                for (int row = 0; row < resultColumn.getValues().size() + 1; row++) {

                    if (row == 0) {
                        vertical.addView(getCell(resultColumn.getColumnID(), 0));
                    } else {
                        View view = null;
                        if (row % 2 == 0) {
                            view = getCell(resultColumn.getValues().get(row - 1), 2);
                            vertical.addView(view);

                        } else {
                            view = getCell(resultColumn.getValues().get(row - 1), 1);
                            vertical.addView(view);
                        }
                        view.setTag(row - 1);
                        views[row - 1][columnsize] = view;
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final int pos = (int) view.getTag();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        for (int i = 0; i < columns.getValues().size(); i++) {
                                            final int finalI = i;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    views[pos][finalI].setBackgroundResource(R.drawable.grid_click_bg);
                                                }
                                            });

                                        }

                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }


                                        for (int i = 0; i < columns.getValues().size(); i++) {
                                            final int finalI = i;
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (pos % 2 == 0) {
                                                        views[pos][finalI].setBackgroundResource(R.drawable.grid_cell_bg_1);
                                                    } else {
                                                        views[pos][finalI].setBackgroundResource(R.drawable.grid_cell_bg_2);
                                                    }

                                                    Intent resultIntent = new Intent();
                                                    ColumnWiseResultHashMap results = new ColumnWiseResultHashMap();
                                                    for (ResultColumn c:columns.getValues()
                                                         ) {
                                                        ResultColumn resultColumn = new ResultColumn();
                                                        resultColumn.setColumnID(c.getColumnID());
                                                        resultColumn.addValueToArray(c.getValues().get(pos));
                                                        results.addResultColumn(resultColumn);
                                                    }
//                                                    for (int c = 0; c < columns.size(); c++) {
//                                                        ResultColumn resultColumn = new ResultColumn();
//                                                        resultColumn.setColumnID(columns.get(c).getColumnID());
//                                                        resultColumn.addValueToArray(columns.get(c).getValues().get(pos));
//                                                        results.add(resultColumn);
//                                                    }
                                                    resultIntent.putExtra("data", (Serializable) results);
                                                    resultIntent.putExtra("type", "list2");
                                                    resultIntent.putExtra("label", label);
                                                    setResult(Activity.RESULT_OK, resultIntent);
                                                    supportFinishAfterTransition();
                                                    //finish();

                                                }
                                            });

                                        }


                                    }
                                }).start();


                            }
                        });

                    }

                }


                horizontal.addView(vertical);
                columnsize++;
            }
//            for (int column = 0; column < columns.size(); column++) {
//                LinearLayout vertical = new LinearLayout(HashGridList.this);
//                vertical.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//                vertical.setOrientation(LinearLayout.VERTICAL);
//                for (int row = 0; row < columns.get(0).getValues().size() + 1; row++) {
//
//                    if (row == 0) {
//                        vertical.addView(getCell(columns.get(column).getColumnID(), 0));
//                    } else {
//                        View view = null;
//                        if (row % 2 == 0) {
//                            view = getCell(columns.get(column).getValues().get(row - 1), 2);
//                            vertical.addView(view);
//
//                        } else {
//                            view = getCell(columns.get(column).getValues().get(row - 1), 1);
//                            vertical.addView(view);
//                        }
//                        view.setTag(row - 1);
//                        views[row - 1][column] = view;
//                        view.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                final int pos = (int) view.getTag();
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        for (int i = 0; i < columns.size(); i++) {
//                                            final int finalI = i;
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    views[pos][finalI].setBackgroundResource(R.drawable.grid_click_bg);
//                                                }
//                                            });
//
//                                        }
//
//                                        try {
//                                            Thread.sleep(100);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//
//
//                                        for (int i = 0; i < columns.size(); i++) {
//                                            final int finalI = i;
//                                            runOnUiThread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    if (pos % 2 == 0) {
//                                                        views[pos][finalI].setBackgroundResource(R.drawable.grid_cell_bg_1);
//                                                    } else {
//                                                        views[pos][finalI].setBackgroundResource(R.drawable.grid_cell_bg_2);
//                                                    }
//
//                                                    Intent resultIntent = new Intent();
//                                                    ArrayList<ResultColumn> results = new ArrayList<>();
//                                                    for (int c = 0; c < columns.size(); c++) {
//                                                        ResultColumn resultColumn = new ResultColumn();
//                                                        resultColumn.setColumnID(columns.get(c).getColumnID());
//                                                        resultColumn.addValueToArray(columns.get(c).getValues().get(pos));
//                                                        results.add(resultColumn);
//                                                    }
//                                                    resultIntent.putExtra("data", results);
//                                                    resultIntent.putExtra("type", "list2");
//                                                    resultIntent.putExtra("label", label);
//                                                    setResult(Activity.RESULT_OK, resultIntent);
//                                                    supportFinishAfterTransition();
//                                                    //finish();
//
//                                                }
//                                            });
//
//                                        }
//
//
//                                    }
//                                }).start();
//
//
//                            }
//                        });
//
//                    }
//
//                }
//
//
//                horizontal.addView(vertical);
//
//            }
        }
    }

    public View getCell(String text,int type)
    {
        View mCustomView = LayoutInflater.from(HashGridList.this).inflate(R.layout.hash_grid_list_cell, null);
        TextView textView = mCustomView.findViewById(R.id.text);
        textView.setText(text);
        if(type==0)
        {
            mCustomView.setBackgroundResource(R.drawable.grid_head_bg);
        }
        else if(type==1)
        {
            mCustomView.setBackgroundResource(R.drawable.grid_cell_bg_1);
        }
        else if(type==2)
        {
            mCustomView.setBackgroundResource(R.drawable.grid_cell_bg_2);
        }
        return mCustomView;
    }

}






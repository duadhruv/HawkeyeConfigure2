package sql_classes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hawkeyeconfigure.DBSettings;
import com.example.hawkeyeconfigure.R;


/**
 * Created by Sonaal on 24-07-2016.
 */

public class DBSettingsDialog extends Activity {

    Context context;
    private SharedPreferences sharedPref;


    @Override
    protected void onDestroy() {
        Intent service = new Intent(getApplication(), SQLService.class);
        service.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startService(service);
        super.onDestroy();
    }

    Dialog d;
    EditText ip1,ip2,ip3,ip4,etdb,etonline,etdbuser,etdbpass;
    Button ipok;
    String ipadd= "192.168.1.25";
    String dbname = "NotificationDB";
    String dbuser= "hex";
    String dbpass = "123hex";
    String hexphno = "";
    int ipmode=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        initdbconstring();


        d = new Dialog(this,android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.db_settings_dialog);
        d.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        final Dialog dpass = new Dialog(this,android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
        dpass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dpass.setContentView(R.layout.dialog_password);

        String token = getFCMToken();

        final TextView tvFCMToken = (TextView) d.findViewById(R.id.tvFCMToken);
        tvFCMToken.setText(token);

        final EditText etpass = (EditText)dpass.findViewById(R.id.etpass);
        Button bok = (Button)dpass.findViewById(R.id.buttonok);


        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                finish();
            }
        });

        bok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etpass.getText().toString().trim().equals("123@hex"))
                {
                    dpass.dismiss();
                    d.show();
                }
                else
                {
                    etpass.setText("");
                    etpass.setHint("Incorrect Password");
                }
            }
        });
//        Bundle extras = getIntent().getExtras();
//        int access_type = extras.getInt("AccessType",0);
//        if(access_type==0)
//        {
//            d.show();
//            //dpass.show();
//        }
//        else if(access_type==1)
//        {
//            d.show();
//        }

        d.show();


        initdbconstring();



        View.OnKeyListener ipKeypadListner = new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                //if(keyCode == 66)
                {
                    Log.e("KeyPress", "" + keyCode);
                    //Toast.makeText(context, "Key:"+keyCode, Toast.LENGTH_LONG).show();
                    //tNPCode.setFocusable(true);
                }
                return false;
            }
        };

        ip1 = (EditText)d.findViewById(R.id.ip1);
        ip2 = (EditText)d.findViewById(R.id.ip2);
        ip3 = (EditText)d.findViewById(R.id.ip3);
        ip4 = (EditText)d.findViewById(R.id.ip4);


        ip1.addTextChangedListener(new IpTextWatcher(ip1));
        ip2.addTextChangedListener(new IpTextWatcher(ip2));
        ip3.addTextChangedListener(new IpTextWatcher(ip3));
        ip4.addTextChangedListener(new IpTextWatcher(ip4));

        ip1.setTag(ip2);
        ip2.setTag(ip3);
        ip3.setTag(ip4);
        ip4.setTag(null);
        etonline = (EditText)d.findViewById(R.id.etonlineserver);
        etdb = (EditText)d.findViewById(R.id.etdbname);
        etdbuser = (EditText)d.findViewById(R.id.etdbuser);
        etdbpass = (EditText)d.findViewById(R.id.etdbpass);
        //ethexphno = (EditText)d.findViewById(R.id.ethexphno);
        final EditText number = findViewById(R.id.ethexphno);
        ipok = (Button)d.findViewById(R.id.buttonok);
        ip1.setText("");ip2.setText("");ip3.setText("");ip4.setText("");

        etonline.setVisibility(View.VISIBLE);
        if(ipmode==0) {
            ip1.setText(Integer.toString(ip[0]));
            ip2.setText(Integer.toString(ip[1]));
            ip3.setText(Integer.toString(ip[2]));
            ip4.setText(Integer.toString(ip[3]));
        }
        else if(ipmode==1)
        {
            etonline.setText(ipadd);
        }

        //ethexphno.setText(hexphno);
        //etdbuser.setText(dbuser);
        etdb.setText(dbname);

        ip1.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//        InputMethodManager inputMethodManager =
//                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInputFromWindow(
//                ip1.getApplicationWindowToken(),
//                InputMethodManager.SHOW_FORCED, 0);
        etdb.setText(dbname);
        d.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        dpass.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(!d.isShowing())
                    finish();
            }
        });
        ipok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //ipadd = ip1.getText().toString() + "." + ip2.getText().toString() + "." + ip3.getText().toString() + "." + ip4.getText().toString();

                //connect = CONN();
                if(ipmode==0) {
//                    if (ethexphno.getText().toString().trim().equals("") || ethexphno.getText().toString().trim().length()<10) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Incorrect Hex Phn No.");
//                        builder.setMessage("Please enter valid phone number");
//                        AlertDialog popup = null;
//                        builder.setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(final DialogInterface dialog, int id) {
//
//                                    }
//                                });
//                        popup = builder.create();
//                        popup.show();
//                    }
                    if (ip1.getText().toString().trim().equals("") || ip2.getText().toString().trim().equals("")
                            || ip3.getText().toString().trim().equals("") || ip4.getText().toString().trim().equals("")
                            || etdb.getText().toString().trim().equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Incorrect IP or Server");
                        builder.setMessage("Please enter valid IP address of Server");
                        AlertDialog popup = null;
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {

                                    }
                                });
                        popup = builder.create();
                        popup.show();
                    }
                    else if(etdbuser.getText().toString().trim().equals("") && etdbpass.getText().toString().trim().equals(""))
                    {
                        {
                            //hexphno = ethexphno.getText().toString().trim();
                            ip[0] = Integer.parseInt(ip1.getText().toString());
                            ip[1] = Integer.parseInt(ip2.getText().toString());
                            ip[2] = Integer.parseInt(ip3.getText().toString());
                            ip[3] = Integer.parseInt(ip4.getText().toString());
                            dbname = etdb.getText().toString();
                            //dbuser = "sa";

                            //dbuser = etdbuser.getText().toString();
                            //dbpass = etdbpass.getText().toString();

                            SQLService.dbname = dbname;
                            SQLService.user = dbuser;
                            SQLService.pass = dbpass;

                            SharedPreferences.Editor editor = sharedPref.edit();
                            String ipstr = "";


                            for (int i = 0; i < 4; i++) {
                                SQLService.ip[i] = ip[i];
                                Log.e("IP PUT:", "ip" + i + " : " + ip[i]);
                                editor.putInt("ip" + i, ip[i]);
                                ipstr += ip[i];
                                if (i < 3) {
                                    ipstr += ".";
                                }
                            }
                            ipadd = ipstr;

                            DBSettings.setOrgDB(getApplicationContext(),dbuser,dbpass,dbname,ipstr);

                            d.dismiss();

                        }
                    }
                    else if(etdbuser.getText().toString().trim().equals(""))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Incorrect Username");
                        builder.setMessage("Please enter valid Database Username");
                        AlertDialog popup = null;
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {

                                    }
                                });
                        popup = builder.create();
                        popup.show();
                    }
                    else if(etdbpass.getText().toString().trim().equals(""))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Incorrect Password");
                        builder.setMessage("Please enter valid Database Password");
                        AlertDialog popup = null;
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {

                                    }
                                });
                        popup = builder.create();
                        popup.show();
                    }
                    else {

                        //hexphno = ethexphno.getText().toString().trim();
                        ip[0] = Integer.parseInt(ip1.getText().toString());
                        ip[1] = Integer.parseInt(ip2.getText().toString());
                        ip[2] = Integer.parseInt(ip3.getText().toString());
                        ip[3] = Integer.parseInt(ip4.getText().toString());
                        dbname = etdb.getText().toString();
                        dbuser = etdbuser.getText().toString();
                        dbpass = etdbpass.getText().toString();

                        SQLService.dbname = dbname;
                        SQLService.user = dbuser;
                        SQLService.pass = dbpass;

                        SharedPreferences.Editor editor = sharedPref.edit();
                        String ipstr = "";


                        for (int i = 0; i < 4; i++) {
                            SQLService.ip[i] = ip[i];
                            Log.e("IP PUT:", "ip" + i + " : " + ip[i]);
                            editor.putInt("ip" + i, ip[i]);
                            ipstr += ip[i];
                            if (i < 3) {
                                ipstr += ".";
                            }
                        }
                        ipadd = ipstr;

                        DBSettings.setOrgDB(getApplicationContext(),dbuser,dbpass,dbname,ipstr);

                        d.dismiss();

                    }
                }
                else
                {
//                    if (ethexphno.getText().toString().trim().equals("") || ethexphno.getText().toString().trim().length()<10) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Incorrect Hex Phn No.");
//                        builder.setMessage("Please enter valid phone number");
//                        AlertDialog popup = null;
//                        builder.setCancelable(false)
//                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    public void onClick(final DialogInterface dialog, int id) {
//
//                                    }
//                                });
//                        popup = builder.create();
//                        popup.show();
//                    }
                    if(etdbuser.getText().toString().trim().equals(""))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Incorrect Username");
                        builder.setMessage("Please enter valid Database Username");
                        AlertDialog popup = null;
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {

                                    }
                                });
                        popup = builder.create();
                        popup.show();
                    }
                    else if(etdbpass.getText().toString().trim().equals(""))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Incorrect Password");
                        builder.setMessage("Please enter valid Database Password");
                        AlertDialog popup = null;
                        builder.setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int id) {

                                    }
                                });
                        popup = builder.create();
                        popup.show();
                    }
                    else
                    {
                        //hexphno = ethexphno.getText().toString().trim();
                        ip[0] = Integer.parseInt(ip1.getText().toString());
                        ip[1] = Integer.parseInt(ip2.getText().toString());
                        ip[2] = Integer.parseInt(ip3.getText().toString());
                        ip[3] = Integer.parseInt(ip4.getText().toString());

                        dbname = etdb.getText().toString();
                        dbuser = etdbuser.getText().toString();
                        dbpass = etdbpass.getText().toString();

                        SQLService.dbname = dbname;
                        SQLService.user = dbuser;
                        SQLService.pass = dbpass;

                        SharedPreferences.Editor editor = sharedPref.edit();



                        String ipstr = etonline.getText().toString();
                        if(!ipstr.isEmpty())
                        {
                            editor.putString("onlineip",ipstr);
                        }
                        else {
                            for (int i = 0; i < 4; i++) {
                                SQLService.ip[i] = ip[i];
                                Log.e("IP PUT:", "ip" + i + " : " + ip[i]);
                                editor.putInt("ip" + i, ip[i]);
                                ipstr += ip[i];
                                if (i < 3) {
                                    ipstr += ".";
                                }
                            }
                            editor.putString("onlineip","");
                        }
                        ipadd = ipstr;
                        //SQLService.ipadd = ipadd;


                        DBSettings.setOrgDB(getApplicationContext(),dbuser,dbpass,dbname,ipstr);


//                        editor.putString("ipstr", ipstr);
//                        editor.putString("dbname", dbname);
//                        editor.putString("dbuser", dbuser);
//                        editor.putString("dbpass", dbpass);
//                        editor.putString("hexphno", hexphno);
//                        editor.putString("dbip",ipadd);
//                        editor.apply();
//                        MainActivity.serv_ipadd = ipadd;
//                        MainActivity.dbname = dbname;
//                        MainActivity.dbuser = dbuser;
//                        MainActivity.dbpass = dbpass;
                       // SQLService.dbname = dbname;
                        //SQLService.ipadd = ipadd;

                        //fire_start_proc();

                        //init = true;
                        //SQLService.connbusy = false;
                        d.dismiss();
                    }


                }
            }
        });

        ipok.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if(etonline.getVisibility() == View.GONE)
                {
                    etonline.setVisibility(View.VISIBLE);
                    //return true;
                }
                else
                {
                    etonline.setVisibility(View.GONE);
                }


                return false;
            }
        });

    }

    private String getFCMToken() {

        SharedPreferences sharedPrefCache = getSharedPreferences(getResources().getString(R.string.cache), MODE_MULTI_PROCESS);
        return sharedPrefCache.getString("token","");
    }

    int[] ip = new int[4];
    private void initdbconstring() {
        sharedPref = getSharedPreferences(getResources().getString(R.string.dbcache), MODE_PRIVATE);
        String ipstr = "";
        String iponline = sharedPref.getString("onlineip","");
        if(iponline.equals("")) {
            ipmode=0;
            for (int i = 0; i < 4; i++) {
                ip[i] = sharedPref.getInt("ip" + i, 0);
                Log.e("IP:", "ip" + i + " : " + ip[i]);
                ipstr += ip[i];
                if (i < 3) {
                    ipstr += ".";
                }
            }
            ipadd = ipstr;
            dbname = sharedPref.getString("dbname", "HawkEye");
            dbuser = sharedPref.getString("dbuser", "sa");
            dbpass = sharedPref.getString("dbpass", "123hex");
            hexphno = sharedPref.getString("hexphno", "");
            SQLService.dbname = dbname;
            SQLService.user = dbuser;
            SQLService.pass = dbpass;

            Log.d("cache-dbsettingsdialog", ipadd + "," + dbname);
        }
        else
        {
            ipmode=1;
            ipadd = iponline;
            dbname = sharedPref.getString("dbname", "HawkEye");
            dbuser = sharedPref.getString("dbuser", "sa");
            dbpass = sharedPref.getString("dbpass", "123hex");
            hexphno = sharedPref.getString("hexphno", "");
            Log.d("cache-dbsettingsdialog", ipadd + "," + dbname);

        }
    }

    public static class IpTextWatcher implements TextWatcher {

        private EditText mEditText;

        public IpTextWatcher(EditText editText) {
            mEditText = editText;
        }
        String prev;
        boolean revert = false;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            prev = charSequence.toString();
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(i<charSequence.length()) {
                Log.e("tw", charSequence + "," + i + "," + i1 + "," + i2 + ":" + mEditText.getTag());

                if (charSequence.charAt(i) == '.') {
                    revert = true;
                    //charSequence = charSequence.
                } else {
                    revert = false;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

            if(revert)
            {
                mEditText.setText(prev);
                EditText next = (EditText) mEditText.getTag();
                next.requestFocus();
            }
        }
    }
}

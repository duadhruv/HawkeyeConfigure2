package sql_classes;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;


import com.example.hawkeyeconfigure.DBSettings;
import com.example.hawkeyeconfigure.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

//import me.leolin.shortcutbadger.ShortcutBadger;


public class SQLService extends Service {


    private static Notification mNotification;
    static NotificationManager notificationManager;

    public static String ipadd= "192.168.1.125";
    //String ipadd= "10.0.2.2";
    public static String dbname = "BHATIA1920XDat01042019_1202";//"HawkEye_microns_13_12";
    public static String user = "sa";//"hex";//"sa";
    public static String pass = "123@hex";
    public static int[] ip = new int[4];


    static Context context;
    private static SharedPreferences sharedPref;
    private static String macAddress;

    Listener listener;
    Listener Notificationlistener;
    Activity activity;

    private final IBinder mBinder = new SqlServiceBinder();

    public class SqlServiceBinder extends Binder {
        public SQLService getService() {
            return SQLService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        context = this;

        Log.d("cache-service",ipadd+","+dbname);
        return mBinder;

    }



    public void setNotificationlistener(SQLService.Listener listener)
    {
        this.Notificationlistener = listener;
    }
    public void setListener(Activity activity, SQLService.Listener listener)
    {
        this.listener=listener;
        this.activity=activity;

    }
    public void setListener(SQLService.Listener listener)
    {
        this.listener=listener;

    }

    public interface Listener{
        void OnDBResult(ColumnWiseResultHashMap columnWiseResultHashMap, int requestcode, SQLQueryResult sqlQueryResult, String labelname, String type);
        void DBCheck(boolean connected);
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Log.e("ServiceTask","Removed");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

    }

    @Override
    public boolean stopService(Intent name) {

//        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
//        restartServiceIntent.setPackage(getPackageName());
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(
//                AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 1000,
//                restartServicePendingIntent);
//        Log.d("Service","Stop");
        return super.stopService(name);
    }

    @Override
    public void onCreate() {
        //startForeground(1,new Notification());
        context = this;
        //DBCheck();
        //showrunningnotification("HawkEye is running.",R.drawable.hawkeye_icon_new);
        super.onCreate();
    }

    static WifiManager wifiManager;
    static ConnectivityManager connManager;
    @SuppressLint("WifiManagerLeak")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        initdbconstring();
//        Bundle extras = intent.getExtras();
//        if(extras == null)
//            Log.d("Service","null");
//        else
//        {
//            String query = (String)extras.get("query");
//            //String qr2 = (String)extras.get("Qr2");
//            execSql(query);
//            //execSql();
//        }
        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        context = this;
        Log.d("cache-service",ipadd+","+dbname);
        return START_STICKY;
    }


    public SQLQueryResult execCallableSql(final String query, String[] args, int requestcode, String LabelName, String type) {


        initdbconstring();
        try {
            int col = 1;
            //String query;
            boolean cleared;
            ResultSet rs;

            if(macAddress==null)
            {
                if (mWifi==null)
                {
                    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                }
                if (mWifi!=null && mWifi.isConnected()) {
                    wifi_connected = true;
                    //WifiInfo wInfo = wifiManager.getConnectionInfo();
                    @SuppressLint("WifiManagerLeak")
                    final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wInfo = wifiManager.getConnectionInfo();
                    macAddress = wInfo.getMacAddress();
                    //macAddress = //LiveProduction_Main.macAddress;//wInfo.getMacAddress();
                }
            }
            Log.e("Sending:", query);
            sql_error = false;
            AtomicInteger errorcode = new AtomicInteger();
            rs = CallableQuerySQL(query,args,errorcode);
            SQLQueryResult sqlQueryResult= new SQLQueryResult(rs,errorcode);
            //listener.OnDBResult(sqlQueryResult,requestcode);
            sortData(sqlQueryResult,requestcode,LabelName,type);
            return sqlQueryResult;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }



    public ResultSet CallableQuerySQL(final String query,String[] args,AtomicInteger errorcode)
    {
        boolean storedprocnotcreated = true;
        Log.e("SQL","executing check for" + query);
        while (executing_query) //todo do something when infinite hang
        {
            if (queryretry > 200) {
                queryretry = 0;
                break;
            }

            queryretry++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Log.e("SQL","executing check ok");
        int retry = 0;
        ResultSet rsq = null;

        while (retry < 5) {
            if (!executing_query) {

                executing_query = true;

                if (connect != null) {

                    try {
                        connect.close();
                    } catch (SQLException e) {
                        server_connected = false;
                        connect = null;
                        e.printStackTrace();
//                        if (sqlerrorcount > 5)
//                            showerrornotification("Information", "SQL Error", R.drawable.error_icon);
                    }
                }
                connect = CONN();
                Log.e("Conn Query","Got con");

                if (connect != null && server_connected)// && !executing)
                {
                    Log.e("SQL", query);
                    statement = null;
                    try {
                        int connretry=0;
//                        while (connect.isClosed() && connretry<10) {
//                            connect = CONN();
//                            connretry++;
//                        }
                        try {
                            PreparedStatement prep = connect.prepareStatement(query);
                            prep.setQueryTimeout(120);
                            if(args!= null) {
                                for (int i = 0; i < args.length; i++) {
//                                    if (args[i].getClass() == Integer.class) {
//                                        prep.setInt(i+1, Integer.parseInt(String.valueOf(args[i])));
//                                    } else if (args[i].getClass() == String.class) {
//                                        p
//                                    }
                                    prep.setString(i+1, String.valueOf(args[i]));
//                                    else {
//                                        Log.w(TAG, "Data type not registered");
//                                        return;
//                                    }
                                }
                            }
                            rsq = prep.executeQuery();
//                            if(query.toLowerCase().contains("Create Procedure".toLowerCase()))
//                            {
//                                statement.executeUpdate(query);
//                            }
//                            else {
//                                rsq = statement.executeQuery(query);
//                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Log.e("Conn Query","statement catch");
                            server_connected = false;
                            e.printStackTrace();
                            if(e.getMessage().toLowerCase().contains("Could not find stored procedure".toLowerCase()))
                            {
                                storedprocnotcreated = false;
                                errorcode.set(3);
                            }
                            Log.w("catch sql err",e.getMessage());
                            connect = null;
                        }

                    } catch (Exception e) {
                        //sql_error=true;

                        server_connected = false;
                        e.printStackTrace();
                        Log.w("catch sql err",e.getMessage());
                        if(e.getMessage().toLowerCase().contains("Could not find stored procedure".toLowerCase()))
                        {
                            storedprocnotcreated = false;
                            errorcode.set(3);
                        }
                        connect = null;
                    }

                    //executing = true;
                }
                if (!server_connected) {

                    if (connect != null) {
                        try {
                            connect.close();
                        } catch (SQLException e) {
                            server_connected = false;
                            connect = null;
                            e.printStackTrace();
                            //Log.w("catch sql err",e.getMessage());
                        }
                        connect = null;
                        server_connected = false;
                        errorcode.set(1);
                        //update_text();
                    }
                    if(!wifi_connected)
                        errorcode.set(2);
                    //connect = CONN();
                }
//                if (connect == null) {
//                    server_connected = false;
//
//                }


                executing_query = false;

                if (rsq != null||query.toLowerCase().contains("Create Procedure".toLowerCase())) {
                    errorcode.set(0);
                    break;
                }
                else if(!storedprocnotcreated)
                {
                    errorcode.set(3);
                    break;
                }
                else
                {
                    Log.e("Query","Rs null");

                    if(!wifi_connected)
                        errorcode.set(2);
                    else
                        errorcode.set(1);
                }
            } else {
                Log.e("update err", "Executing");

            }
            retry++;
            Log.e("Query","Retry:"+ String.valueOf(retry)+" - "+ String.valueOf(executing_query));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Log.e("Query","errorcode:"+ String.valueOf(errorcode.get()));

        return rsq;
    }



    public SQLQueryResult execSql(final String sql, int requestcode, String LabelName, String type) {


        initdbconstring();
        try {
            int col = 1;
            String query;
            boolean cleared;
            ResultSet rs;

            if(macAddress==null)
            {
                if (mWifi==null)
                {
                    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                }
                if (mWifi!=null && mWifi.isConnected()) {
                    wifi_connected = true;
                    //WifiInfo wInfo = wifiManager.getConnectionInfo();
                    @SuppressLint("WifiManagerLeak")
                    final WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wInfo = wifiManager.getConnectionInfo();
                    macAddress = wInfo.getMacAddress();
                    //macAddress = //LiveProduction_Main.macAddress;//wInfo.getMacAddress();
                }
            }
            //ShortcutBadger.with(getApplicationContext()).count(badgeCount); //for 1.1.3
            query = sql;//"HawkEye_Proc_TabApp_Pending @DeviceId='"+macAddress+"'";
            Log.e("Sending:", query);
            sql_error = false;
            AtomicInteger errorcode = new AtomicInteger();
            rs = qQuerySQL(query,errorcode);
            SQLQueryResult sqlQueryResult= new SQLQueryResult(rs,errorcode);
            //listener.OnDBResult(sqlQueryResult,requestcode);
            sortData(sqlQueryResult,requestcode,LabelName,type);
            return sqlQueryResult;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    int ipmode=0;
    private void initdbconstring() {
//        sharedPref = getSharedPreferences(getResources().getString(R.string.dbcache), MODE_PRIVATE);
//        String ipstr = "";
//        String iponline = sharedPref.getString("onlineip","");
//        if(iponline.equals("")) {
//            ipmode=0;
//            for (int i = 0; i < 4; i++) {
//                ip[i] = sharedPref.getInt("ip" + i, 0);
//                Log.e("IP:", "ip" + i + " : " + ip[i]);
//                ipstr += ip[i];
//                if (i < 3) {
//                    ipstr += ".";
//                }
//            }
//            //ipadd= (String) sharedPref.getString("ipstr","0.0.0.0");
//            //ipadd = ipstr;//////////////////////////////////////////////////////////this removed by me/////////
//            dbname = sharedPref.getString("dbname", dbname);
//            user = sharedPref.getString("dbuser", user);
//            pass = sharedPref.getString("dbpass", pass);
////            switchpass = sharedPref.getString("switchpass", "123hex");
//
//
//            Log.d("cache-dbsettingsdialog", ipadd + "," + dbname+", pass = "+pass );
//        }
//        else
//        {
//            ipmode=1;
//            ipadd = iponline;
//            dbname = sharedPref.getString("dbname", "HawkEye");
//            user = sharedPref.getString("dbuser", "sa");
//            pass = sharedPref.getString("dbpass", "123hex");
//            Log.d("cache-dbsettingsdialog", ipadd + "," + dbname+", pass = "+pass);
//
//        }
        user= DBSettings.getOrgDBUser(getApplicationContext());
        dbname=DBSettings.getOrgDB(getApplicationContext());
        pass=DBSettings.getOrgDBPass(getApplicationContext());
        ipadd=DBSettings.getOrgServer(getApplicationContext());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service","Destroy");

        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        Intent intent = new Intent("com.android.serviceresume");
        //intent.putExtra("yourvalue", "torestore");
    }

    static public boolean connbusy = false;

    private static class Task implements java.util.concurrent.Callable<Connection>{

        @Override
        public Connection call() throws Exception {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Connection conn = null;
            String ConnURL = null;
            String errmsg = null;
            try
            {
                connbusy = true;

                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                if(ipadd.contains(":"))
                    ConnURL = "jdbc:jtds:sqlserver://" + ipadd + "/"+dbname;
                else
                    ConnURL = "jdbc:jtds:sqlserver://" + ipadd + ":1433/"+dbname;

                DriverManager.setLoginTimeout(2);
                Log.e("conn", "start url:"+ConnURL);
                conn = DriverManager.getConnection(ConnURL, user, pass);
                //if(condialog.isShowing())
                {
                    //   condialog.dismiss();
                }

                connbusy = false;
                Log.e("Success", "Done");
                //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                server_connected = true;
                //update_text();
                //SharedPreferences.Editor editor = sharedPref.edit();
                //editor.putInt("err", 0);
                //editor.commit();
                return conn;
            }

            catch (final SQLException se)
            {
                errmsg = se.getMessage() + "" + se.getErrorCode() + " bitti";
                connbusy = false;
                server_connected = false;
                //update_text();

                //Toast.makeText(getApplicationContext(), "S_Error", Toast.LENGTH_LONG).show();
//                new AlertDialog.Builder(context)
//                        .setTitle("Server Not Found")
//                        .setMessage("Server not found on IP: "+ ipadd +" .\n")
//
//                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // android.os.Process.killProcess(android.os.Process.myPid());
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert)
//                        .show();
                Log.e("ERRO",errmsg);
                executing_query = false;
                //SharedPreferences.Editor editor = sharedPref.edit();
                //editor.putInt("err", 1);
                //editor.commit();
                if(sqlerrorcount>5)
                {
                    if(se.getMessage().contains("Network error")) {
                        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                        if (mWifi.isConnected()) {
//                            showerrornotification("Information", "Server Not Found", R.drawable.error_icon);
                        } else {
//                            showerrornotification("Information", "APN Not Found", R.drawable.error_icon);
                        }
                    }
                    else
//                        showerrornotification("Information","SQL Error",R.drawable.error_icon);
                        sqlerrorcount=0;
                }
                else
                {
                    sqlerrorcount++;
                }
                if(connect!=null)
                {
                    connect.close();

                    Log.e("Conn","connect close");
                }

                Log.e("SqlError", String.valueOf(sqlerrorcount));
                //showerrornotification("Information","SQL Error",R.drawable.error_icon);
                return null;
                //s_error=true;


            }

            catch (final Exception e)
            {
                errmsg = e.getMessage();
                Log.e("ERRO",errmsg);
                connbusy = false;
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

//                if (mWifi.isConnected()) {
////                    showerrornotification("Information","Server Not Found",R.drawable.error_icon);
//                }
//                else
////                    showerrornotification("Information","APN Not Found",R.drawable.error_icon);

                return null;
            }

            finally
            {
                //connect = conn;
            }

            //return conn;


        }

    }

    static boolean wifi_connected =false;
    static boolean sql_error = false;

    static boolean server_connected=false;
    static Connection con = null;
    static Future<Connection> future=null;
    static ExecutorService executor = Executors.newSingleThreadExecutor();
    static NetworkInfo mWifi;
    static Statement statement=null;
    boolean dbcheckruning = false;
    //


    public void startDBCHeck()
    {
        if(!dbcheckruning)
        {
            DBCheck();
        }
    }

    public void stopDBCheck()
    {
        dbcheckruning=false;
    }
    private void DBCheck()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbcheckruning = true;
                while (true) {
                    Connection connection = CONN();
                    listener.DBCheck(server_connected);
//                    if(Notificationlistener!=n)
//                    Notificationlistener.DBCheck(server_connected);
                    Log.w("dbcheck","dbcheck runing" + String.valueOf(server_connected));
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!dbcheckruning)
                    {
                        break;
                    }
                }
            }
        }).start();


    }
    private static Connection CONN()
    {
        //initdbconstring();
//          if(future!=null)
//            future.cancel(true);

        try {
            future = executor.submit(new Task());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try {
            mWifi=null;
            if(connManager!=null)
            {
                mWifi= connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            }

            if (mWifi!=null && mWifi.isConnected()) {
                wifi_connected = true;
                //WifiInfo wInfo = wifiManager.getConnectionInfo();
                // macAddress = LiveProduction_Main.macAddress;//wInfo.getMacAddress();

            }
//            else
//            {
////                new AlertDialog.Builder(context)
////                        .setTitle("Network Error")
////                        .setMessage("There is a network error.Please check the network and try again.")
////                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                            public void onClick(DialogInterface dialog, int which) {
////                                //offline_login=true;
////                            }
////                        })
////                        .setIcon(android.R.drawable.ic_dialog_alert)
////                        .show();
////                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
////                NetworkInfo Wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
////
////                if (Wifi.isConnected()) {
////                    showerrornotification("Information","Server Not Found",R.drawable.error_icon);
////                }
////                showerrornotification("Information","APN Not Found",R.drawable.error_icon);
//                Log.d("Service Error","Network Error");
//                wifi_connected = false;
//                return null;
//            }

//            new Thread()
//            {
//                @Override
//                public void run()
//                {
////                    Looper.prepare();

            try {
                con = future.get(15, TimeUnit.SECONDS);

            } catch (InterruptedException e) {
                e.printStackTrace();
                con = null;
            } catch (ExecutionException e) {
                e.printStackTrace();
                con = null;
            } catch (TimeoutException e) {
                e.printStackTrace();
                con = null;
                server_connected = false;
                executing_query=false;
//                        executor.shutdownNow();
//                        try {
//                            if(connect!=null)
//                            {
//                                connect.close();
//
//                                Log.e("Conn","connect close");
//                            }
//                            else
//                            {
//                                Log.e("Conn","connect already closed");
//
//                            }
//                            if(statement!=null) {
//                                statement.cancel();
//                                Log.e("Conn","statement cancel");
//                            }
//
//                        } catch (Exception e1) {
//                            e1.printStackTrace();
//                        }
                //return null;
            }

//                }
//            }.start();

            if(con!=null)
            {
                server_connected = true;
                //LiveProduction_Main.server_connected = true;
				/*if(db.checkbacklog()>0 && backlog_files<=0)
				{
					max_backlog_files = db.checkbacklog();
					backlog_files = max_backlog_files;
				}*/
                Log.e("Conn","return con");
                return con;
            }
            else
            {
                Log.e("Conn","return con null");
                server_connected = false;
//                LiveProduction_Main.server_connected = false;
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
//            connect = null;
            server_connected = false;

        }
        return null;
    }

    static boolean executing_query = false;
    static int queryretry=0;
    static Connection connect;
    private static int sqlerrorcount=0;











    public static ResultSet qQuerySQL(String COMMANDSQL, AtomicInteger errorcode) //1 -- !Server 2-- !APN 3--!storedproc
    {

        boolean storedprocnotcreated = true;
        Log.e("SQL","executing check for" + COMMANDSQL);
        while (executing_query) //todo do something when infinite hang
        {
            if (queryretry > 200) {
                queryretry = 0;
                break;
            }

            queryretry++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //Log.e("SQL","executing check ok");
        int retry = 0;
        ResultSet rsq = null;

        while (retry < 5) {
            if (!executing_query) {

                executing_query = true;

                if (connect != null) {

                    try {
                        connect.close();
                    } catch (SQLException e) {
                        server_connected = false;
                        connect = null;
                        e.printStackTrace();
//                        if (sqlerrorcount > 5)
//                            showerrornotification("Information", "SQL Error", R.drawable.error_icon);
                    }
                }
                connect = CONN();
                Log.e("Conn Query","Got con");

                if (connect != null && server_connected)// && !executing)
                {
                    Log.e("SQL", COMMANDSQL);
                    statement = null;
                    try {
                        int connretry=0;
//                        while (connect.isClosed() && connretry<10) {
//                            connect = CONN();
//                            connretry++;
//                        }
                        try {
                            statement = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                            statement.setQueryTimeout(120);
                            statement.setFetchSize(1000);

                            if(COMMANDSQL.toLowerCase().contains("Create Procedure".toLowerCase()))
                            {
                                statement.executeUpdate(COMMANDSQL);
                            }
                            else {
                                rsq = statement.executeQuery(COMMANDSQL);
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                            Log.e("Conn Query","statement catch");
                            server_connected = false;
                            e.printStackTrace();
                            if(e.getMessage().toLowerCase().contains("Could not find stored procedure".toLowerCase()))
                            {
                                storedprocnotcreated = false;
                                errorcode.set(3);
                            }
                            Log.w("catch sql err",e.getMessage());
                            connect = null;
                        }

                    } catch (Exception e) {
                        //sql_error=true;

                        server_connected = false;
                        e.printStackTrace();
                        Log.w("catch sql err",e.getMessage());
                        if(e.getMessage().toLowerCase().contains("Could not find stored procedure".toLowerCase()))
                        {
                            storedprocnotcreated = false;
                            errorcode.set(3);
                        }
                        connect = null;
                    }

                    //executing = true;
                }
                if (!server_connected) {

                    if (connect != null) {
                        try {
                            connect.close();
                        } catch (SQLException e) {
                            server_connected = false;
                            connect = null;
                            e.printStackTrace();
                            //Log.w("catch sql err",e.getMessage());
                        }
                        connect = null;
                        server_connected = false;
                        errorcode.set(1);
                        //update_text();
                    }
                    if(!wifi_connected)
                        errorcode.set(2);
                    //connect = CONN();
                }
//                if (connect == null) {
//                    server_connected = false;
//
//                }


                executing_query = false;

                if (rsq != null||COMMANDSQL.toLowerCase().contains("Create Procedure".toLowerCase())) {
                    errorcode.set(0);
                    break;
                }
                else if(!storedprocnotcreated)
                {
                    errorcode.set(3);
                    break;
                }
                else
                {
                    Log.e("Query","Rs null");

                    if(!wifi_connected)
                        errorcode.set(2);
                    else
                        errorcode.set(1);
                }
            } else {
                Log.e("update err", "Executing");

            }
            retry++;
            Log.e("Query","Retry:"+ String.valueOf(retry)+" - "+ String.valueOf(executing_query));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Log.e("Query","errorcode:"+ String.valueOf(errorcode.get()));

        return rsq;

    }


    void sortData(final SQLQueryResult sqlQueryResult, final int requestcode, final String labelName, final String type)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (sqlQueryResult.getErrorCode().get() != 3) {
                    ResultSet rs = sqlQueryResult.getRs();
                    try {


                        if (rs != null) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int columnCount = rsmd.getColumnCount();

                            ColumnWiseResultHashMap columnWiseResultHashMap = new ColumnWiseResultHashMap();
                            for (int i = 1; i <= columnCount; i++) {
                                String name = rsmd.getColumnName(i);
                                ResultColumn resultColumn = new ResultColumn();
                                resultColumn.setColumnID(name);
                                columnWiseResultHashMap.addResultColumn(resultColumn);
                            }
                            Log.w("sqlservice","result assembly started");
                            while (rs.next()) {
                                for (ResultColumn resultColumn:
                                        columnWiseResultHashMap.getValues()) {
                                    Log.w("sqlservice","result next fired");
                                    String val = rs.getString(resultColumn.getColumnID());
                                    if(val!=null)
                                    {
                                        resultColumn.addValueToArray(val.trim());
                                    }
                                    else
                                    {
                                        resultColumn.addValueToArray(val);
                                    }

                                }
                            }
                            Log.w("sqlservice","result assembled");
                            if(listener!=null)
                            {
                                listener.OnDBResult(columnWiseResultHashMap, requestcode, sqlQueryResult,labelName,type);
                            }

                            if(labelName.equalsIgnoreCase("Notification"))
                            {
                                Notificationlistener.OnDBResult(columnWiseResultHashMap, requestcode, sqlQueryResult,labelName,type);
                            }

                        }
                        else
                        {
                            listener.OnDBResult(null, requestcode, sqlQueryResult,labelName,type);
                            if(labelName.equalsIgnoreCase("Notification"))
                            {
                                Notificationlistener.OnDBResult(null, requestcode, sqlQueryResult,labelName,type);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    listener.OnDBResult(null,requestcode,sqlQueryResult,labelName,type);
                    if(labelName.equalsIgnoreCase("Notification"))
                    {
                        Notificationlistener.OnDBResult(null, requestcode, sqlQueryResult,labelName,type);
                    }
                }

            }

        }).start();
    }



}
package com.example.hawkeyeconfigure;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import sql_classes.SQLService;

import static android.content.Context.MODE_PRIVATE;

public class DBSettings {
    public static void setOrgDB(Context context, String DBUser, String DBPass, String DBName , String Server)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.dbcache), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("OrgDBUser",DBUser);
        editor.putString("OrgDBPass",DBPass);
        editor.putString("OrgDB",DBName);
        editor.putString("OrgServer",Server);
        Log.w("UserInformation","set Server settings -" + Server+" "+DBName+" "+DBUser+" "+DBPass);
        editor.commit();
        SQLService.user=DBUser;
        SQLService.pass=DBPass;
        SQLService.dbname=DBName;
        SQLService.ipadd =Server;
    }

    public static String getOrgDBUser(Context context)
    {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.dbcache), MODE_PRIVATE);

        return sharedPref.getString("OrgDBUser","sa");
    }

    public static String getOrgDBPass(Context context)
    {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.dbcache), MODE_PRIVATE);
        return sharedPref.getString("OrgDBPass","123@hex");
    }

    public static String getOrgDB(Context context)
    {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.dbcache), MODE_PRIVATE);
        return sharedPref.getString("OrgDB","HRApp_OrgDb_Hex");
    }

    public static String getOrgServer(Context context)
    {
        SharedPreferences sharedPref;
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.dbcache), MODE_PRIVATE);
        return sharedPref.getString("OrgServer","0.0.0.0");
    }
}

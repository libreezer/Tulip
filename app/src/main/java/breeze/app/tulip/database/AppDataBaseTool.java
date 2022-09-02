package breeze.app.tulip.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import breeze.app.tulip.APP;
import breeze.app.tulip.model.AppPathBundle;

public class AppDataBaseTool {

    private static AppDataBaseHelper appDataBaseHelper;
    private static SQLiteDatabase sqLiteDatabase;
    private static final String table_name = "pathconfig";
    private static final String packageName = "PackageName";
    private static final String content = "Content";

    public static void init(Context context){
        appDataBaseHelper = new AppDataBaseHelper(context,"PathConfig.db",null,1);
        sqLiteDatabase = appDataBaseHelper.getWritableDatabase();
    }

    public static List<AppPathBundle> getAllPath(){
        List<AppPathBundle> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = sqLiteDatabase.query(table_name,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Gson gson = new Gson();
                AppPathBundle appPathBundle = new AppPathBundle();
                String packageName = cursor.getString(cursor.getColumnIndex("PackageName"));
                String json = cursor.getString(cursor.getColumnIndex("Content"));
                appPathBundle.setPackageName(packageName);
                List<AppPathBundle.MethodPath> methodPaths = gson.fromJson(json,new TypeToken<List<AppPathBundle.MethodPath>>(){}.getType());
                appPathBundle.setMethodPaths(methodPaths);
                list.add(appPathBundle);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public static boolean isExistApp(String package_name){
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(table_name, new String[]{packageName},"PackageName = ?", new String[]{package_name},null,null,null);
        return cursor.moveToFirst();
    }

    public static AppPathBundle getAppPathBundle(String package_Name){
        @SuppressLint("Recycle")
        Cursor cursor = sqLiteDatabase.query(table_name, null,"PackageName = ?", new String[]{package_Name},null,null,null);
        if (cursor.moveToFirst()){
            AppPathBundle appPathBundle = new AppPathBundle();
            String pkgname = cursor.getString(cursor.getColumnIndex(packageName));
            String json = cursor.getString(cursor.getColumnIndex(content));
            Gson gson = new Gson();
            List<AppPathBundle.MethodPath> methodPaths = gson.fromJson(json,new TypeToken<List<AppPathBundle.MethodPath>>(){}.getType());
            appPathBundle.setPackageName(pkgname);
            appPathBundle.setMethodPaths(methodPaths);
            return appPathBundle;
        }
        return null;
    }

    public static void insertData(AppPathBundle appPathBundle){
        if (!isExistApp(appPathBundle.getPackageName())){
            ContentValues contentValues = new ContentValues();
            String json = new Gson().toJson(appPathBundle.getMethodPaths());
            contentValues.put("PackageName",appPathBundle.getPackageName());
            contentValues.put("Content",json);
            sqLiteDatabase.insert(table_name,null,contentValues);
            contentValues.clear();
        }else {
            updateData(appPathBundle);
        }
    }

    public static void updateData(AppPathBundle appPathBundle){
        ContentValues contentValues = new ContentValues();
        String json = new Gson().toJson(appPathBundle.getMethodPaths());
        contentValues.put("PackageName",appPathBundle.getPackageName());
        contentValues.put("Content",json);
        sqLiteDatabase.update(table_name,contentValues,"PackageName = ?", new String[]{appPathBundle.getPackageName()});
        contentValues.clear();
    }


    public static void deleteData(String packageName) {
        if (isExistApp(packageName)){
            sqLiteDatabase.delete(table_name,"PackageName = ?", new String[]{packageName});
        }
    }
}

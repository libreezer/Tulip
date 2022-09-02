package breeze.app.tulip.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import breeze.app.tulip.R;
import breeze.app.tulip.database.AppDataBaseTool;
import breeze.app.tulip.model.AppPathBundle;
import breeze.app.tulip.widget.AppEditLayout;
import brz.breeze.app_utils.BAppCompatActivity;

public class AppStorageConfigActivity extends BAppCompatActivity {

    static {
        System.loadLibrary("app");
    }

    public native void initActivity();

    private TextView appName;
    private Button look_log;
    private AppEditLayout mAppConfigExternalStorageDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_storage_config);
        init();
        initActivity();
    }

    private PackageInfo packageInfo;

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        appName = find(R.id.app_config_appname);
        look_log = find(R.id.app_config_log);
        mAppConfigExternalStorageDirectory = findViewById(R.id.app_config_ExternalStorageDirectory);
    }

    @Override
    public void initData() {
        String packageName = getIntent().getStringExtra("packageName");
        try {
            if (packageName != null) {
                packageInfo = getPackageManager().getPackageInfo(packageName, 0);
                CharSequence applicationLabel = getPackageManager().getApplicationLabel(packageInfo.applicationInfo);
                appName.setText(applicationLabel);
                AppPathBundle appPathBundle = AppDataBaseTool.getAppPathBundle(packageName);
                if (appPathBundle != null) {
                    List<AppPathBundle.MethodPath> methodPaths = appPathBundle.getMethodPaths();
                    LinearLayout all_config = find(R.id.app_config_all_config);
                    for (int i = 0; i < all_config.getChildCount(); i++) {
                        AppPathBundle.MethodPath methodPath = methodPaths.get(i);
                        View view1 = all_config.getChildAt(i);
                        if (view1.getClass().toString().contains("breeze.app.tulip.widget.AppEditLayout")) {
                            AppEditLayout appEditLayout = (AppEditLayout) view1;
                            if (appEditLayout.getMethodName().equals(methodPath.getMethodName())){
                                appEditLayout.setEditText(methodPath.getValue());
                                appEditLayout.setParentClass(methodPath.getParentClass());
                            }
                        }
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void save(View view) {
        //给每个View添加个新属性:methodName然后设置父布局的id，循环获取子布局来添加数据
        try {
            List<AppPathBundle.MethodPath> arrayList = new ArrayList<>();
            LinearLayout all_config = find(R.id.app_config_all_config);
            for (int i = 0; i < all_config.getChildCount(); i++) {
                View view1 = all_config.getChildAt(i);
                if (view1.getClass().toString().contains("breeze.app.tulip.widget.AppEditLayout")) {
                    AppEditLayout appEditLayout = (AppEditLayout) view1;
                    AppPathBundle.MethodPath methodPath = new AppPathBundle.MethodPath();
                    methodPath.setMethodName(appEditLayout.getMethodName());
                    methodPath.setValue(appEditLayout.getText());
                    methodPath.setParentClass(appEditLayout.getParentClass());
                    arrayList.add(methodPath);
                }
            }
            AppPathBundle appPathBundle = new AppPathBundle();
            appPathBundle.setPackageName(packageInfo.packageName);
            appPathBundle.setMethodPaths(arrayList);
            AppDataBaseTool.insertData(appPathBundle);
            this.setResult(RESULT_OK);
            toast("保存成功");
        }catch (Exception e){
            e.printStackTrace();
            toast("保存失败");
        }
    }

    public void delete(View view) {
        AppDataBaseTool.deleteData(packageInfo.packageName);
        this.setResult(RESULT_OK);
        finish();
    }

    public void log(View view) {
        Intent intent = new Intent(this, LookLogActivity.class);
        intent.putExtra("pkgName",packageInfo.packageName);
        startActivity(intent);
    }
}
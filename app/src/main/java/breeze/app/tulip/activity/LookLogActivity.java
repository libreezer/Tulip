package breeze.app.tulip.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import breeze.app.tulip.R;
import breeze.app.tulip.utils.AppFileLog;
import breeze.app.tulip.utils.AppUtils;
import brz.breeze.app_utils.BAppCompatActivity;
import brz.breeze.file_utils.BFileUtils;

public class LookLogActivity extends BAppCompatActivity {

    private LinearLayout baseview;
    private TextView log_txv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_log);
        init();
        initData();
    }

    private String pkgName;
    @Override
    public void init() {
        baseview = find(R.id.log_baseView);
        log_txv = find(R.id.log_textview);
        pkgName = getIntent().getStringExtra("pkgName");
        Toolbar toolbar = find(R.id.log_toolbar);
        toolbar.setTitle("日志查看");
        setSupportActionBar(toolbar);
        File log = AppFileLog.getLog(pkgName);
        if (!log.exists()){
            toolbar.setSubtitle("当前应用没有日志");
        }else {
            try {
                FileInputStream fileInputStream = new FileInputStream(log);
                InputStreamReader bufferedInputStream = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(bufferedInputStream);
                String line = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((line = bufferedReader.readLine())!=null){
                    stringBuilder.append(AppUtils.depswd(line,3872)).append("\n");
                }
                log_txv.setText(stringBuilder);
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                toast("获取日志失败="+e);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem delete = menu.add(1, 1, 1, "删除");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            delete.setIcon(getDrawable(R.drawable.ic_baseline_delete_24));
        }
        delete.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==1){
            File log = AppFileLog.getLog(pkgName);
            if (log.exists()){
                boolean delete = log.delete();
                if (delete){
                    toast("删除成功");
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initData() {

    }
}
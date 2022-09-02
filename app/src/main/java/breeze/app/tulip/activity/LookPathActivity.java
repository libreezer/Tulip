package breeze.app.tulip.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import breeze.app.tulip.R;
import brz.breeze.app_utils.BAppCompatActivity;

public class LookPathActivity extends BAppCompatActivity {

    private LinearLayout look_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_path);
        init();
        initData();
    }

    @Override
    public void init() {
        look_layout = find(R.id.look_path_linearlayout);
    }

    @Override
    public void initData() {
        addPath("****文件类别****","***********data目录**********");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addPath("私有目录路径",getDataDir().getAbsolutePath());
        }
        addPath("私有缓存路径",getCacheDir().getAbsolutePath());
        addPath("私有文件路径",getFilesDir().getAbsolutePath());
        addPath("安装程序路径(不作更改)",getPackageCodePath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addPath("代码缓存路径",getCodeCacheDir().getAbsolutePath());
        }
        addPath("****文件类别****","***********沙盒目录**********");
        addPath("obb目录路径",getObbDir().getAbsolutePath());//外部
        addPath("沙盒缓存路径",getExternalCacheDir().getAbsolutePath());
        addPath("****文件类别****","***********外部目录**********");
        addPath("下载缓存路径",Environment.getDownloadCacheDirectory().getAbsolutePath());
        addPath("公共目录路径",Environment.getExternalStorageDirectory().getAbsolutePath());
        addPath("系统目录路径",Environment.getRootDirectory().getAbsolutePath());
        addPath("数据目录路径",Environment.getDataDirectory().getAbsolutePath());
    }

    private void addPath(String name,String path){
        TextView textview = new TextView(this);
        textview.setTextIsSelectable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2,-2);
        params.setMargins(0,10,0,0);
        textview.setLayoutParams(params);
        StringBuilder sb = new StringBuilder(name).append("=").append(path);
        SpannableString ss = new SpannableString(sb.toString());
        int color = Color.parseColor("#009688");
        if(name.startsWith("****")){
            color = Color.parseColor("#F44336");
        }
        ss.setSpan(new ForegroundColorSpan(color),0,sb.toString().indexOf("="),SpannableString.SPAN_INCLUSIVE_INCLUSIVE);
        textview.setText(ss);
        look_layout.addView(textview);
    }
}
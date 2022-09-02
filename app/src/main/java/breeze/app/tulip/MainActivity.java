package breeze.app.tulip;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import breeze.app.tulip.utils.AppFileLog;
import breeze.app.tulip.utils.AppUtils;
import brz.breeze.app_utils.BAppCompatActivity;

public class MainActivity extends BAppCompatActivity {
    static {
        System.loadLibrary("app");
    }

    private LinearLayout state_background;
    private TextView state_textview, state_k_text;
    private ImageView state_icon;

    public native void initApp();

    public native boolean isPro();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApp();
    }

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        state_textview = find(R.id.main_state_text);
        state_background = find(R.id.main_state_background);
        state_icon = find(R.id.main_state_icon);
        state_k_text = find(R.id.main_state_k_text);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initData() {
        initState();
    }

    private boolean isHooked() {
        return false;
    }

    private void initState() {
        if (isHooked()) {
            int background = R.drawable.style_main_state_background_checked, img = R.drawable.ic_baseline_check_circle_24;
            if (isPro()) {
                background = R.drawable.style_main_state_background_checked_k;
                img = R.mipmap.icon_crown_black;
                state_k_text.setVisibility(View.VISIBLE);
            }
            state_background.setBackgroundResource(background);
            state_textview.setText("模块已启动");
            state_icon.setImageResource(img);
        } else {
            state_background.setBackgroundResource(R.drawable.style_main_state_background_error);
            state_textview.setText("模块未启动");
            state_icon.setImageResource(R.drawable.ic_baseline_error_24);
        }
    }
}
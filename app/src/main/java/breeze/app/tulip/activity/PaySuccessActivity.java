package breeze.app.tulip.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import breeze.app.tulip.R;
import breeze.app.tulip.database.AppMySQLTool;
import breeze.app.tulip.utils.AppUtils;
import brz.breeze.app_utils.BAppCompatActivity;
import brz.breeze.app_utils.BAppUtils;

public class PaySuccessActivity extends BAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_success);
        init();
        initData();
    }

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private static String token;

    @Override
    public void initData() {
        Uri data = getIntent().getData();
        token = data.getQueryParameter("token");
    }

    public void Btn_Back(View view) {
        if (!token.isEmpty()){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("提示");
            progressDialog.setMessage("正在验证中...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            BAppUtils.execute(() -> {
                preference.edit().putString("Token",token).apply();
                AppMySQLTool appMySQLTool = new AppMySQLTool();
                appMySQLTool.initData();
                long l = appMySQLTool.setData(token);
                AppUtils.setToken(token);
                AppUtils.setLong(token,l);
                this.setResult(RESULT_OK);
                progressDialog.cancel();
                finish();
            });
        }
    }
}
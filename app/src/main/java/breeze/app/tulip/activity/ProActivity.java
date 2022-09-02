package breeze.app.tulip.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import breeze.app.tulip.R;
import breeze.app.tulip.database.AppMySQLTool;
import breeze.app.tulip.utils.AppUtils;
import brz.breeze.app_utils.BAppCompatActivity;
import brz.breeze.app_utils.BAppUtils;
import brz.breeze.payment.BPayTools;

public class ProActivity extends BAppCompatActivity {

    static {
        System.loadLibrary("app");
    }

    public native void initView();
    public native void submit(String token,long data);
    public native BPayTools pay(BPayTools tools);

    private LinearLayout mProStateBackground;
    private TextView mProStateText;
    private LinearLayout mProTextBackground;
    private TextView mProTextTime;
    private TextView mProTextToken;
    private Button mProWechatPay;
    private EditText mProEditCode;
    private Button mProSubmitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro);
        init();
        initView();
    }

    @Override
    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        mProStateBackground = findViewById(R.id.pro_state_background);
        mProStateText = findViewById(R.id.pro_state_text);
        mProTextBackground = findViewById(R.id.pro_text_background);
        mProTextTime = findViewById(R.id.pro_text_time);
        mProTextToken = findViewById(R.id.pro_text_token);
        mProWechatPay = findViewById(R.id.pro_wechat_pay);
        mProEditCode = findViewById(R.id.pro_edit_code);
        mProSubmitBtn = findViewById(R.id.pro_submit_btn);
    }

    @Override
    public void initData() {

    }

    @SuppressLint("SetTextI18n")
    public void showDetail(String token, long t){
        mProTextToken.setText("Token:"+token);
        mProTextTime.setText("到期时间:"+ AppUtils.getTime(t));
    }

    public void isPro(){
        mProWechatPay.setEnabled(false);
        mProStateText.setText("已激活高级版");
        mProTextBackground.setVisibility(View.VISIBLE);
        mProStateBackground.setBackgroundResource(R.drawable.style_main_state_background_checked_k);
    }

    public void isntPro(){
        mProWechatPay.setEnabled(true);
        mProStateText.setText("未激活高级版");
        mProTextBackground.setVisibility(View.GONE);
        mProStateBackground.setBackgroundResource(R.drawable.style_main_state_background_checked);
    }

    public void code_ok(View view) {
        final String token = mProEditCode.getText().toString();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("正在验证中...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        BAppUtils.execute(() -> {
            AppMySQLTool appMySQLTool = new AppMySQLTool();
            appMySQLTool.initData();
            long data = appMySQLTool.getData(token);
            runOnUiThread(() -> {
                progressDialog.setCancelable(true);
                progressDialog.cancel();
                if (data!=0){
                    toast("激活成功,请重启应用");
                    submit(token,data);
                }else {
                    toast("激活失败");
                }
            });
        });
    }

    public void pay(View view) {
        BAppUtils.execute(() -> {
            String token = AppUtils.createToken();
            if (token.isEmpty()){
                toast("创建Token失败");
            }else {
                preference.edit().putString("Token",token).apply();
                String orderID = String.valueOf(System.currentTimeMillis());
                BPayTools bPayTools = new BPayTools();
                bPayTools.setReturnType(BPayTools.returnMethod.JSON);
                bPayTools.setOrderNo(orderID);
                bPayTools.setPayDuration("5");
                BPayTools pay = pay(bPayTools);
                if (pay!=null){
                    pay.setNotifyUrl("http://www.libreeze.top/tulip/Notice.php?token="+token);
                    pay.setReturnUrl("http://www.libreeze.top/tulip/success.php?token="+token);
                    pay.pay(new BPayTools.onPayResultListner() {
                        @Override
                        public void createOrderFail(String s) {
                            toast("创建订单失败");
                        }

                        @Override
                        public void createOrderSuccess(String s) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                            startActivityForResult(intent,200);
                        }
                    });
                }else {
                    toast("调用失败");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK){
            initView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
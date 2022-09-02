package breeze.app.tulip.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;

import breeze.app.tulip.R;
import brz.breeze.app_utils.BAppUpdate;
import brz.breeze.app_utils.BToast;

public class MainToolFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    static {
        System.loadLibrary("app");
    }

    public native String updateUri();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_tool_sharedpreference);
        Preference prf_main_check_update = findPreference("prf_main_check_update");
        prf_main_check_update.setOnPreferenceClickListener(this);
        Preference joinQQ = findPreference("joinQQ");
        joinQQ.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("prf_main_check_update")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                BAppUpdate.setOnUpdateListener(getContext(), updateUri(), new BAppUpdate.BAppUpdateListener() {
                    @Override
                    public void haveNewVersion(BAppUpdate.BUpdateMode bUpdateMode) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("发现新版本");
                        builder.setMessage(bUpdateMode.getUpdateContent());
                        builder.setPositiveButton("立即更新", (dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+bUpdateMode.getDownLink()));
                            getActivity().startActivity(intent);
                        });
                        builder.setNegativeButton("取消",null);
                        builder.show();
                    }

                    @Override
                    public void noNewVersion() {
                        toast("当前已是最新版");
                    }

                    @Override
                    public void onError(Exception e) {
                        toast("获取更新信息失败="+e);
                    }
                });
            }
        }else if (preference.getKey().equals("joinQQ")){
            if (!joinQQGroup("lepIXINtLxsva0DmS0JXS7ogRmEaNc0E")) {
                toast("未安装手Q或安装的版本不支持");
            }
        }
        return false;
    }

    private void toast(String content){
        getActivity().runOnUiThread(() -> BToast.toast(getActivity(),content,BToast.LENGTH_LONG).show());
    }

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D"+key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}

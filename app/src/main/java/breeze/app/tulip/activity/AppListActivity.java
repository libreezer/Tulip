package breeze.app.tulip.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import breeze.app.tulip.R;
import breeze.app.tulip.adapter.AppListAdapter;
import breeze.app.tulip.widget.AppEditLayout;
import brz.breeze.app_utils.BAppCompatActivity;
import brz.breeze.app_utils.BAppUtils;

public class AppListActivity extends BAppCompatActivity {
    private RecyclerView recyclerView;
    private List<PackageInfo> installedPackages = new ArrayList<>();
    private final List<PackageInfo> allPackages = new ArrayList<>();
    private AppListAdapter appListAdapter;
    private static boolean hasAppChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        init();
        initData();
    }

    @Override
    public void init() {
        recyclerView = find(R.id.appl_recyclerview);
        Toolbar toolbar = find(R.id.appl_toolbar);
        toolbar.setTitle("应用列表");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    public void initData() {
        BAppUtils.execute(() -> {
            allPackages.addAll(getPackageManager().getInstalledPackages(0));
            runOnUiThread(() -> {
                appListAdapter = new AppListAdapter(AppListActivity.this, installedPackages);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(AppListActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(appListAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE){
                            appListAdapter.setScroll(false);
                            appListAdapter.notifyDataSetChanged();
                        }else {
                            appListAdapter.setScroll(true);
                        }
                        super.onScrollStateChanged(recyclerView, newState);
                    }
                });
            });
            showApps(preference.getBoolean("isShowSysApps", false));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_list, menu);
        MenuItem item = menu.findItem(R.id.menu_app_list_search);
        MenuItem show_app = menu.findItem(R.id.menu_app_list_show_sys);
        show_app.setChecked(preference.getBoolean("isShowSysApps", false));
        SearchView actionView = (SearchView) item.getActionView();
        actionView.setQueryHint("请输入应用名称");
        actionView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    BAppUtils.execute(() -> {
                        List<PackageInfo> objects = new ArrayList<>();
                        for (PackageInfo info : allPackages) {
                            CharSequence applicationLabel = getPackageManager().getApplicationLabel(info.applicationInfo);
                            if (applicationLabel.toString().contains(newText)) {
                                objects.add(info);
                            }
                        }
                        runOnUiThread(() -> {
                            installedPackages.clear();
                            installedPackages.addAll(objects);
                            appListAdapter.update(installedPackages);
                        });
                    });
                } else {
                    installedPackages.clear();
                    installedPackages.addAll(allPackages);
                    appListAdapter.update(installedPackages);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public boolean isSystemApp(PackageInfo pInfo) {
        return (pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    private void showApps(boolean isShowSystemApps) {
        installedPackages.clear();
        for (PackageInfo info : allPackages) {
            if (isShowSystemApps) {
                installedPackages.add(info);
            } else {
                if (!isSystemApp(info)){
                    installedPackages.add(info);
                }
            }
        }
        appListAdapter.update(installedPackages);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.setResult(BAppCompatActivity.RESULT_CANCELED);
            finish();
        } else if (item.getItemId() == R.id.menu_app_list_show_sys) {
            preference.edit().putBoolean("isShowSysApps", !item.isChecked()).apply();
            showApps(!item.isChecked());
            item.setChecked(!item.isChecked());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200 && resultCode == BAppCompatActivity.RESULT_OK) {
            hasAppChanged = true;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (hasAppChanged) {
                this.setResult(RESULT_OK);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
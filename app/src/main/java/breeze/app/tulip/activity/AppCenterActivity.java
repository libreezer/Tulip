package breeze.app.tulip.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import breeze.app.tulip.R;
import breeze.app.tulip.adapter.AppCenterAdapter;
import breeze.app.tulip.database.AppDataBaseTool;
import breeze.app.tulip.model.AppPathBundle;
import brz.breeze.app_utils.BAppCompatActivity;

public class AppCenterActivity extends BAppCompatActivity {

    private final List<AppPathBundle> appPathBundles = new ArrayList<>();

    private RecyclerView recyclerView;
    private AppCenterAdapter appCenterAdapter;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_center);
        init();
        initData();
    }

    @Override
    public void init() {
        recyclerView = find(R.id.appc_recyclerview);
        floatingActionButton = find(R.id.appc_float_button);
        Toolbar toolbar = find(R.id.appc_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar.setTitle("应用中心");
        setSupportActionBar(toolbar);
    }

    @Override
    public void initData() {
        appCenterAdapter = new AppCenterAdapter(this,appPathBundles);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(appCenterAdapter);
        recyclerView.setLayoutManager(layoutManager);
        initRecyclerView();
        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(AppCenterActivity.this, AppListActivity.class);
            startActivityForResult(intent,100);
        });
    }

    private void initRecyclerView() {
        appPathBundles.clear();
        appPathBundles.addAll(AppDataBaseTool.getAllPath());
        if (!appPathBundles.isEmpty()){
            appCenterAdapter.update(appPathBundles);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK){
            initRecyclerView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
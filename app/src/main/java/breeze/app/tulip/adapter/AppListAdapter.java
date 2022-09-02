package breeze.app.tulip.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import breeze.app.tulip.R;
import breeze.app.tulip.activity.AppListActivity;
import breeze.app.tulip.activity.AppStorageConfigActivity;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {

    private AppListActivity activity;
    private List<PackageInfo> packageInfos;
    private boolean isScrolling = false;

    public AppListAdapter(AppListActivity appListActivity, List<PackageInfo> list){
        this.activity = appListActivity;
        this.packageInfos = list;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppViewHolder(LayoutInflater.from(activity).inflate(R.layout.view_app_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        PackageInfo info = packageInfos.get(position);
        holder.name.setText(activity.getPackageManager().getApplicationLabel(info.applicationInfo));
        holder.packagename.setText(info.packageName);
        if (!isScrolling){
            Drawable drawable = info.applicationInfo.loadIcon(activity.getPackageManager());
            holder.icon.setImageDrawable(drawable);
        }else {
            holder.icon.setImageDrawable(null);
        }
        holder.baseView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AppStorageConfigActivity.class);
            intent.putExtra("packageName",info.packageName);
            activity.startActivityForResult(intent,200);
        });
    }

    @Override
    public int getItemCount() {
        return packageInfos.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name,packagename;
        LinearLayout baseView;
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.app_item_icon);
            packagename = itemView.findViewById(R.id.app_item_packagename);
            name = itemView.findViewById(R.id.app_item_appname);
            baseView = itemView.findViewById(R.id.app_item_background);
        }
    }

    public void update(List<PackageInfo> list){
        packageInfos = list;
        this.notifyDataSetChanged();
    }

    public void setScroll(boolean i){
        this.isScrolling = i;
    }
}

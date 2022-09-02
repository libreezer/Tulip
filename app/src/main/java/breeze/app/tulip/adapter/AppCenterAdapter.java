package breeze.app.tulip.adapter;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import breeze.app.tulip.activity.AppCenterActivity;
import breeze.app.tulip.activity.AppListActivity;
import breeze.app.tulip.activity.AppStorageConfigActivity;
import breeze.app.tulip.model.AppPathBundle;

public class AppCenterAdapter extends RecyclerView.Adapter<AppCenterAdapter.AppViewHolder> {

    private final AppCenterActivity activity;
    private List<AppPathBundle> appPathBundles;

    public AppCenterAdapter(AppCenterActivity appListActivity, List<AppPathBundle> list) {
        this.activity = appListActivity;
        this.appPathBundles = list;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AppCenterAdapter.AppViewHolder(LayoutInflater.from(activity).inflate(R.layout.view_app_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppPathBundle appPathBundle = appPathBundles.get(position);
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(appPathBundle.getPackageName(), 0);
            holder.name.setText(activity.getPackageManager().getApplicationLabel(info.applicationInfo));
            holder.packagename.setText(info.packageName);
            Drawable drawable = info.applicationInfo.loadIcon(activity.getPackageManager());
            holder.icon.setImageDrawable(drawable);
            holder.baseView.setOnClickListener(v -> {
                Intent intent = new Intent(activity, AppStorageConfigActivity.class);
                intent.putExtra("packageName", info.packageName);
                activity.startActivityForResult(intent, 100);
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return appPathBundles.size();
    }

    public static class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, packagename;
        LinearLayout baseView;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.app_item_icon);
            packagename = itemView.findViewById(R.id.app_item_packagename);
            name = itemView.findViewById(R.id.app_item_appname);
            baseView = itemView.findViewById(R.id.app_item_background);
        }
    }

    public void update(List<AppPathBundle> list) {
        appPathBundles = list;
        notifyDataSetChanged();
    }
}

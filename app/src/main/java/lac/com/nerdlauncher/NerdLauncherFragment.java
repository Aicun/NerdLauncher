package lac.com.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aicun on 9/5/2017.
 */

public class NerdLauncherFragment extends Fragment{

    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment getInstance(){
        return new NerdLauncherFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerd_launcher,container,false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setAdapter();
        return view;
    }

    private void setAdapter() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(packageManager).toString(),b.loadLabel(packageManager).toString());
            }
        });
        ActivityAdapter activityAdapter = new ActivityAdapter(activities);
        mRecyclerView.setAdapter(activityAdapter);
    }

    public class ActivityHolder extends RecyclerView.ViewHolder {

        private ResolveInfo resolveInfo;
        private ImageView activityIcon;
        private TextView activityNameTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            activityIcon = (ImageView) itemView.findViewById(R.id.app_icon);

            activityNameTextView = (TextView) itemView.findViewById(R.id.app_name);
            activityNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityInfo activityInfo = resolveInfo.activityInfo;
                    Intent intent = new Intent(Intent.ACTION_MAIN)
                            .setClassName(activityInfo.packageName,activityInfo.name)//the activity to start
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//start the activity in new task
                    startActivity(intent);
                }
            });
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            this.resolveInfo = resolveInfo;
            PackageManager packageManager = getActivity().getPackageManager();
            String appName = resolveInfo.loadLabel(packageManager).toString();
            activityNameTextView.setText(appName);

            Drawable drawable = resolveInfo.loadIcon(packageManager);
            activityIcon.setImageDrawable(drawable);
        }

    }

    public class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private List<ResolveInfo> activities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            this.activities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.list_item_apps,parent,false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = activities.get(position);
            holder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            return activities.size();
        }
    }
}

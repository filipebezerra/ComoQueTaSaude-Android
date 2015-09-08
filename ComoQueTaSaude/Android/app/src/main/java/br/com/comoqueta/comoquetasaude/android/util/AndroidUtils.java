package br.com.comoqueta.comoquetasaude.android.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import br.com.comoqueta.comoquetasaude.android.R;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version 0.0.1, 07/08/2015
 * @since 0.0.1
 */
public final class AndroidUtils {
    private static final String PREF_USER_LEARNED_DRAWER = "pref-user-learned-drawer";

    public static void showThisAppOnPlayStore(@NonNull Context context) {
        showAppOnPlayStore(context, context.getPackageName());
    }

    public static void showAppOnPlayStore(@NonNull Context context, @NonNull String appId) {
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                "market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);

        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name);
                rateIntent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;

            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=" + context.getPackageName()));
            context.startActivity(webIntent);
        }
    }

    public static String getAppVersion(@NonNull Context context) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).versionName;
        } catch (NameNotFoundException e) {
            version = "Unknown Version";
        }
        return version;
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo != null && wifiNetworkInfo.isConnected();
    }

    public static boolean hasNetworkConnectivity(@NonNull Context context) {
        if (isWifiConnected(context) || isNetworkConnected(context)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static Intent createShareIntent(@NonNull Context context) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text));

        if (!resolveActivity(shareIntent, context)) {
            return null;
        }

        return Intent.createChooser(shareIntent, "Compartilhe com seus amigos");
    }

    public static boolean resolveActivity(@NonNull Intent intent, @NonNull Context context) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    public static void savePrefUserLearnedDrawer(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_USER_LEARNED_DRAWER, true);
        editor.apply();
    }

    public static boolean isUserLearnedDrawerPref(@NonNull Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return preferences.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }
}

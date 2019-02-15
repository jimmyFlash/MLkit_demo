package com.jimmy.ml_firebase.permissions;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import com.jimmy.ml_firebase.R;

import java.util.ArrayList;

public class PermissionManager {

    public static final int FRAGMENT_DELEGATE_PERMISSIONS = 3;

    String yes_ = "OK";


    private  ArrayList<String> permissionsList;

    private  ArrayList<String> deniedPermissionsList;
    private  int permCat;
    private Activity ctx;

    private PermissionManager(PermissionBuilder builder) {
        permissionsList = builder.permissionsList;
        permCat = builder.permCat;
        ctx = builder.ctx;
        deniedPermissionsList = new ArrayList<>();
        askForPermissionSet();
    }

    public ArrayList<String> getPermissionsList() {
        return permissionsList;
    }

    public int getPermCat() {
        return permCat;
    }

    public ArrayList<String> getDeniedPermissionsList() {
        return deniedPermissionsList;
    }


    // builder creation pattern impl.
    public static class PermissionBuilder{

        private ArrayList<String> permissionsList;
        private Activity ctx;
        private int permCat;

        // constructor to be use with incremental addition of requests
        public PermissionBuilder(Activity ctx, int permCat) {
            this.ctx = ctx;
            this.permCat = permCat;
            permissionsList = new ArrayList<>();

        }

        // used with predefined array of permissions
        public PermissionBuilder(Activity ctx, int permCat, ArrayList<String> permissionsList) {
            this.ctx = ctx;
            this.permCat = permCat;
            this.permissionsList = permissionsList;
        }

        public PermissionBuilder setCtx(Activity ctx) {
            this.ctx = ctx;
            return this;
        }

        public PermissionBuilder setPermCat(int permCat) {
            this.permCat = permCat;
            return this;
        }

        public PermissionBuilder setPermissionsList(ArrayList<String> permissionsList) {
            this.permissionsList = permissionsList;
            return this;
        }


        public PermissionBuilder addPermission(String permissionName){
            permissionsList.add(permissionName);
            return this;
        }

        public PermissionManager build(){
            return new PermissionManager(this);
        }
    }


    private void askForPermissionSet() {

        boolean permissionsNotGranted = false;

        /**
         * check if permission is permitted, otherwise add to list of requested permissions permission
         */
        for(String perm : permissionsList){
            if (!validatePermission(perm)) {
                permissionsNotGranted = true;
                break;
            }
        }

        if (permissionsNotGranted) {
            // 1st time asking permission
            ActivityCompat.requestPermissions(ctx,
                    permissionsList.toArray(new String[permissionsList.size()]), permCat);
        }
    }

    /**
     * check for permission logic if granted or not, add non-granted permission to list
     *
     * @return
     */
    private boolean validatePermission(String permission) {
        // Check for Rationale Option
        return ActivityCompat.shouldShowRequestPermissionRationale(ctx, permission);
    }


    /**
     * to be called in onRequestPermission method of activity
     * @param permissions
     * @param grantResults
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public boolean multiplePermissionProcessor(@NonNull String[] permissions, @NonNull int[] grantResults){
        int approvedPerm = 0;
        int deniedRequests = 0;
        // for each permission check if the user granted/denied them
        // you may want to group the rationale in a single dialog,
        // this is just an example
        for (int i = 0, len = permissions.length; i < len; i++) {
            String permission = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedRequests++;
                // user rejected the permission, should we show rational?
                boolean showRationale = ctx.shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {
                   /*
                     user denied before AND CHECKED "never ask again"  you can either enable some fall back,
                     disable features of your app or open another dialog explaining again the permission and directing to
                     the app setting
                   */

                    deniedPermissionsList.add(permission);

                    // display alert informing the user
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                    alertDialogBuilder.setTitle("Permission(s) denied");
                    alertDialogBuilder.setMessage(permission + " is denied, application may not function properly");
                    alertDialogBuilder.setPositiveButton(yes_, (dialog, id) -> dialog.dismiss());
                    alertDialogBuilder.show();
                } else {
                    // user did NOT check "never ask again"
                    // this is a good place to explain the user
                    // why you need the permission and ask if he wants
                    // to accept it (the rationale)
                    showMessageOKCancel(ctx, ctx.getResources().getString(R.string.permission_rationale_message),
                            (dialog, which) -> ActivityCompat.requestPermissions(
                                    ctx,
                                    permissionsList.toArray(new String[permissionsList.size()]),
                                    permCat));

                }
            } else if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // update the count of granted permissions
                approvedPerm++;
            }
        }
        // if number of approved permission matches the number of permission requests stored in list
        if (approvedPerm == permissions.length) {
            return true;
        }
        return false;
    }



    /**
     * rational alert dialog to inform user of what the permission is needed for
     *
     * @param message    : message to show to user
     * @param okListener : click interface generic implementation to handle user click
     */
    private void showMessageOKCancel(Context ctx, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .create()
                .show();

    }

    /**
     * create intent that launches the application manager -> settings for this application to have user edit permission manually
     *
     * @param context
     */
    public void startAppSettingsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

}

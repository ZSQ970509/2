package com.xsq.czy.util;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.xsq.czy.R;

/**
 * Created by lan on 2016/7/7.
 */
public class UIHelper {
    private static Toast toast;
    private static ProgressDialog progressDialog;
    private static AlertDialog alertDialog;
    private static AlertDialog bleDialog;
    private static AlertDialog pushDialog;

    public static void registBroadCast(Context context, BroadcastReceiver b, String ...action) {
        registBroadCast(context, b, 0, action);
    }

    public static void registBroadCast(Context context, BroadcastReceiver b, int priority, String ...action) {
        if (context != null && b != null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.setPriority(priority);
            for (String string : action) {
                intentFilter.addAction(string);
            }
            context.registerReceiver(b, intentFilter);
        }
    }

    public static void unRegistBroadCast(Context context, BroadcastReceiver b) {
        if (context != null && b != null) {
            context.unregisterReceiver(b);
        }
    }

    private static boolean checkActivityNoValid(Context context) {
        if(context instanceof AppCompatActivity) {
            boolean noValid = ((AppCompatActivity) context).isDestroyed() || ((AppCompatActivity) context).isFinishing();

            return noValid;
        }
        return false;
    }

    public static void showToast(Context context, String str) {
        showToast(context, str, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int stringId) {
        showToast(context, context.getString(stringId), Toast.LENGTH_SHORT);
    }

    private static void showToast(Context context, CharSequence str, int duration) {
        if(toast != null) {
            toast.cancel();
            toast = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        toast = Toast.makeText(context, str, duration);
        toast.show();
    }

    public static void cancelToast() {
        if(toast != null) {
           // toast.cancel();
            toast = null;
        }
    }

    public static void cancelNotification(Context context, int notificationId) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(notificationId);
    }

    public static void showAlertDialog(Context context, int msgId) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, String msg) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, int msgId,
                                       DialogInterface.OnClickListener confirmListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, confirmListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, int msgId,
                                       DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener neutralListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, positiveListener)
                .setNeutralButton(R.string.never, neutralListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, int titleId, int msgId,
                                       DialogInterface.OnClickListener confirmListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleId)
                .setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, confirmListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialog(Context context, String title, String msg,
                                       DialogInterface.OnClickListener confirmListener) {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, confirmListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showBleDialog(Context context, int msgId,
                                       DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener neutralListener) {
        if(bleDialog != null) {
            bleDialog.dismiss();
            bleDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msgId)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, positiveListener)
                .setNeutralButton(R.string.never, neutralListener)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        bleDialog = builder.create();
        bleDialog.show();
    }

    public static void showPushDialog(Context context, String title, String msg,
                                      DialogInterface.OnClickListener confirmListener,
                                      DialogInterface.OnClickListener cancelListener) {
        if(pushDialog != null) {
            pushDialog.dismiss();
            pushDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm, confirmListener)
                .setNegativeButton(R.string.cancel, cancelListener);
        pushDialog = builder.create();
        pushDialog.show();
    }

    public static void showListDialog(Context context, String title, String[] array,
                                      DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setItems(array, confirmListener);
        builder.show();
    }

    public static void cancelAlertDialog() {
        if(alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    public static void cancelBleDialog() {
        if(bleDialog != null) {
            bleDialog.dismiss();
            bleDialog = null;
        }
    }

    public static void cancelPushDialog() {
        if(pushDialog != null) {
            pushDialog.dismiss();
            pushDialog = null;
        }
    }

    public static boolean showProgress(Context context, int strId) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return false;
        }

        String msg = context.getString(strId);
        progressDialog = ProgressDialog.show(context, "", msg, false, false);
        progressDialog.show();

        return true;
    }

    public static void showProgress(Context context, int strId, boolean cancelable) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        String msg = context.getString(strId);
        progressDialog = ProgressDialog.show(context, "", msg, cancelable, cancelable);
        progressDialog.show();
    }

    public static void showProgress(Context context, String text) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        progressDialog = ProgressDialog.show(context, "", text, true, true);
        progressDialog.show();
    }

    public static void showProgressValue(Context context, int strId) {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        if(checkActivityNoValid(context)) {
            return;
        }

        String msg = context.getString(strId);
        progressDialog = ProgressDialog.show(context, "", msg, true, true);
        progressDialog.show();
    }

    public static void setProgress(int progress) {
        if(progressDialog != null) {
            progressDialog.setMessage(progress + "%");
        }
    }

    public static void dismiss() {
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}

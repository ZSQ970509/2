package com.xsq.common.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xsq.common.R;
import com.xsq.common.core.XsqCommon;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class ViewUtil {

    private static Dialog showingDialog = null;
    private static Object maskDiagOpt = new Object();

    public static void showProcessingDialog(Activity activity){
        showLoadingDialog(activity,"处理中",false);
    }

    public static void showLoadingDialog(Activity activity,String tipContent,boolean cancelable) {
        synchronized (maskDiagOpt){
            if(showingDialog == null){
                showingDialog = new Dialog(activity, R.style.component_loading_dialog);// 创建自定义样式dialog

                showingDialog.setContentView(R.layout.loading_process_dialog);

                ((TextView) showingDialog.findViewById(R.id.progress_type))
                        .setText(tipContent);
                
                showingDialog.setCanceledOnTouchOutside(cancelable);
                showingDialog.setCancelable(cancelable);

                showingDialog.show();
            }else{
                if(showingDialog.isShowing()){
                    ((TextView) showingDialog.findViewById(R.id.progress_type))
                            .setText(tipContent);

                    showingDialog.setCancelable(cancelable);
                }
            }
        }
    }

    public static void closeLoadingDialog(){
        synchronized (maskDiagOpt){
            if(showingDialog != null && showingDialog.isShowing()){
                showingDialog.dismiss();
                showingDialog = null;
            }
        }
    }

}

package com.xsq.common.app.customzx;

import android.graphics.Bitmap;

import com.google.zxing.Result;
import com.xsq.common.R;
import com.xsq.common.app.customzx.result.ResultHandler;
import com.xsq.common.util.TextUtil;

/**
 * Created by Administrator on 2016/1/17.
 */
public class CaptureExActivity extends CaptureActivity {

    @Override
    protected void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        getIntent().putExtra("barCodeResult", rawResult.getText());
        sendReplyMessage(R.id.return_scan_result, getIntent(), 0);
    }
}

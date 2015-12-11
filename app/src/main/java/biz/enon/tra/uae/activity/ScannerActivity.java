package biz.enon.tra.uae.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;

import biz.enon.tra.uae.activity.base.BaseActivity;
import biz.enon.tra.uae.global.C;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler;


/**
 * Created by mobimaks on 13.08.2015.
 */
public class ScannerActivity extends BaseActivity implements ResultHandler {

    private ZXingScannerView mScannerView;

    public static Intent getStartIntent(final Context _context) {
        return new Intent(_context, ScannerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result _result) {
        Intent data = new Intent();
        data.putExtra(C.KEY_SCANNER_RESULT_TEXT, _result.getText());
        setResult(RESULT_OK, data);
        finish();
    }
}

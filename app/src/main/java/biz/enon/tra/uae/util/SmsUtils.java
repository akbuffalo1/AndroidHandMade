package biz.enon.tra.uae.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.enon.tra.uae.R;

import biz.enon.tra.uae.global.C;

/**
 * Created by mobimaks on 25.09.2015.
 */
public final class SmsUtils {

    private SmsUtils() {
    }

    public static void sendSms(final Context _context, final Long _smsNumber, final String _smsText) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("smsto:" + _smsNumber));
        intent.putExtra("address", _smsNumber);
        intent.putExtra("sms_body", _smsText);
        if (intent.resolveActivity(_context.getPackageManager()) != null) {
            _context.startActivity(intent);
        } else {
            Toast.makeText(_context, R.string.fragment_report_spam_cant_send_sms, C.TOAST_LENGTH).show();
        }
    }

    public static void sendBlockSms(final Context _context, final String _blockUser) {
        sendSms(_context, 7726L, "b " + _blockUser);
    }

    public static void sendUnblockSms(final Context _context, final String _unblockUser) {
        sendSms(_context, 7726L, "u " + _unblockUser);
    }

}

package liddell.onus.com.fistbump_ble;

import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class BLEMessageHandler extends Handler {

    // defines for identifying shared types between calling functions
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    // FistBump INCOMING MESSAGES
    private final static String AttackCompleteMessage = "ATTACK_COMPLETE";
    private final static String HashesMessage = "HASHES";
    private final static String DriveErrorMessage = "ERROR_NO_DRIVE";

    private final static String TAG = "BLEMessageHandler";
    private MainActivity activity;

    public BLEMessageHandler (MainActivity activity){
        this.activity = activity;
    }


    public void handleMessage(android.os.Message msg) {


        if (msg.what == MESSAGE_READ) {

            String readMessage = null;
            try {
                readMessage = new String((byte[]) msg.obj, "UTF-8").trim();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "Message: "+readMessage);

            if (readMessage.contains(HashesMessage)){
              activity.handlesHashesData(readMessage);

            } else if (readMessage.contains(DriveErrorMessage)) {
                activity.showNoDriveErrorDialog();

            } else if (readMessage.contains(AttackCompleteMessage)) {
                activity.handleAttackComplete();

            }

        }

        if (msg.what == CONNECTING_STATUS) {
            if (msg.arg1 == 1) {
                activity.setViewState(MainActivity.ViewState.ARMED);
            } else {
                activity.setViewState(MainActivity.ViewState.BT_RETRY);
            }
        }
    }
}

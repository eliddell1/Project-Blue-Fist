package liddell.onus.com.fistbump_ble;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;

import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //region Variables
    //-----------------------------------------------

    private static final String TAG = "FBMainActivity";

    // Display / View State stuff
    private Display display;
    private Point screenSize;
    private float sWidth;
    private float sHeight;

    public enum ViewState {
        ARMED,
        BT_RETRY,
        BT_DISCOVERY,
        ATTACKING
    }

    private ViewState currentState = ViewState.BT_DISCOVERY;
    // GUI Components

    private FrameLayout mTopBar;
    private Button mAttackButton;
    private Button mRetryButton;
    private Button mQuitButton;
    private FrameLayout mAttackSheet;
    private LinearLayout mRetrySheet;
    private ProgressDialog mSpinnerProgressDialog;
    private ProgressDialog mHorizontalProgressDialog;
    private ImageView refreshBtn;
    private ImageView closeBtn;
    private TextView mTargetMessageView;
    private View handshake_dash;
    private View pmkid_dash;
    private TextView mPMKIDTotal;
    private TextView mPMKIDTargeted;
    private TextView mPMKIDUntargeted;
    private TextView mHandshakeTotal;
    private TextView mHandshakeTargeted;
    private TextView mHandshakeUntargeted;
    private TextView mHandshakeDashLabel;
    private TextView mPMKIDDashLabel;
    private ProgressBar wifiSpinner;
    private TextView noWifiMessage;

    //Bluetooth
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private BLEMessageHandler mHandler; // Our main handler that will receive callback notifications
    private Handler mTimeOutHandler;
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private static final String DEVICE_NAME = "fistbump";

    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //WIFI
    private WifiManager mWifiManager;
    private ArrayList<ScanResult> mScanResults;
    private NetworkResultsAdapter mListAdapter;
    private ListView mNetworksListView;

    // milliseconds before ble scan times out
    private static final long SCAN_PERIOD = 20000;

    //-----------------------------------------------
    //endregion

    //region LifeCycle
    //-----------------------------------------------

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        display = getWindowManager().getDefaultDisplay();
        screenSize = new Point();
        display.getSize(screenSize);
        sWidth = screenSize.x;
        sHeight = screenSize.y;


        setContentView(R.layout.activity_main);

        mTopBar = findViewById(R.id.TopBar);
        mTopBar.setVisibility(View.GONE);

        mAttackButton = findViewById(R.id.attackbtn);
        mTargetMessageView = findViewById(R.id.targetMessageView);

        mAttackSheet = findViewById(R.id.attack_sheet);
        mAttackSheet.setVisibility(View.GONE);

        mRetrySheet = findViewById(R.id.retrySheet);
        mRetryButton = findViewById(R.id.retryBTN);
        mQuitButton = findViewById(R.id.quitBTN);

        wifiSpinner = findViewById(R.id.wifi_spinner);
        noWifiMessage = findViewById(R.id.no_wifiMessage);

        mNetworksListView = findViewById(R.id.wifi_list);
        mNetworksListView.setVisibility(View.GONE);
        mNetworksListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        handshake_dash = findViewById(R.id.handshake_dash);
        mHandshakeTotal = handshake_dash.findViewById(R.id.total_num);
        mHandshakeTargeted = handshake_dash.findViewById(R.id.targetd_num);
        mHandshakeUntargeted = handshake_dash.findViewById(R.id.untargeted_num);
        mHandshakeDashLabel = handshake_dash.findViewById(R.id.item_label);
        mHandshakeDashLabel.setText(getString(R.string.handshakes));

        pmkid_dash = findViewById(R.id.pmkid_dash);
        mPMKIDTotal = pmkid_dash.findViewById(R.id.total_num);
        mPMKIDTargeted = pmkid_dash.findViewById(R.id.targetd_num);
        mPMKIDUntargeted = pmkid_dash.findViewById(R.id.untargeted_num);
        mPMKIDDashLabel = pmkid_dash.findViewById(R.id.item_label);
        mPMKIDDashLabel.setText(getString(R.string.pmkid));

        refreshBtn = findViewById(R.id.refreshbtn);
        closeBtn = findViewById(R.id.closeBtn);

        mSpinnerProgressDialog = new ProgressDialog(this, R.style.FistBumpAlertDialogStyle);
        mSpinnerProgressDialog.setIndeterminate(false);
        mSpinnerProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mSpinnerProgressDialog.setIcon(R.mipmap.ic_launcher);
        mSpinnerProgressDialog.setTitle(R.string.app_name);
        mSpinnerProgressDialog.setCancelable(false);

        mHorizontalProgressDialog = new ProgressDialog(this,  R.style.FistBumpAlertDialogStyle);
        mHorizontalProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mHorizontalProgressDialog.setIndeterminate(false);
        mHorizontalProgressDialog.setProgressNumberFormat(null);

        mHorizontalProgressDialog.setIcon(R.mipmap.ic_launcher);
        mHorizontalProgressDialog.setCancelable(false);

        int titleDividerId = getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = mSpinnerProgressDialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(getApplicationContext().getColor(R.color.colorAccent));


        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mBTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBTAdapter = bluetoothManager.getAdapter();

        mHandler = new BLEMessageHandler(this);

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiScanReceiver);

    }

    @Override
    protected void onDestroy() {
        stopServicesIfNeeded();
        super.onDestroy();
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                discover();
            } else {
                setViewState(ViewState.BT_RETRY);
            }

        }
    }

    //-----------------------------------------------
    //endregion

    //region Private Methods
    //-----------------------------------------------

    private void initialize(){
        mAttackButton.setOnClickListener(v -> {

            String message;
            if(mListAdapter.getSelectedNetwork() != null) {
                mHorizontalProgressDialog.setTitle("Attacking: "+ mListAdapter.getSelectedNetwork().SSID);
                message = "TARGET:"+mListAdapter.getSelectedNetwork().BSSID.replace(":","")+":"+mListAdapter.getSelectedNetwork().SSID;
            } else {
                mHorizontalProgressDialog.setTitle("Conducting Sweeping Attack");
                message = "attack";
            }

            mHorizontalProgressDialog.setMax(45000);
            mHorizontalProgressDialog.show();
            new CountDownTimer(45000, 250) {

                @Override
                public void onTick(long millisUntilFinished) {
                    if (mHorizontalProgressDialog != null && mHorizontalProgressDialog.isShowing()) {
                        mHorizontalProgressDialog.setProgress((int)(45000-millisUntilFinished));
                    }
                }

                @Override
                public void onFinish() {

                }
            }.start();
            mConnectedThread.write(message);
        });

        refreshBtn.setOnClickListener(v -> {
            wifiSpinner.setVisibility(View.VISIBLE);
            noWifiMessage.setVisibility(View.GONE);
            mListAdapter.clear();
            mAttackButton.setText(R.string.broad_attack);
            mAttackButton.setBackgroundResource(R.drawable.btn_red_bg);
            mTargetMessageView.setText(R.string.no_target_message);
            mListAdapter.notifyDataSetChanged();
            mWifiManager.startScan();
        });

        closeBtn.setOnClickListener(v -> killApp());

        mQuitButton.setOnClickListener(v -> killApp());

        mRetryButton.setOnClickListener(v -> setUpConnection());
        setUpConnection();
    }

    private void killApp() {
        stopServicesIfNeeded();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
        finish();
    }

    private void stopServicesIfNeeded () {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
        }
        if (mBTAdapter != null && mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        try {
            unregisterReceiver(blReceiver);
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage());
        }
    }

    private void setUpConnection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 87);
                killApp();
                return;
            }
        }
        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Your Device Doesn't seem to support Bluetooth. Quiting. Sorry?", Toast.LENGTH_LONG).show();
            killApp();
            return;
        } else {
            turnBluetoothOnIfNeeded();
        }
    }

    private void turnBluetoothOnIfNeeded() {
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            setViewState(ViewState.BT_RETRY);
        } else {
            discover();
        }

    }

    private void discover() {
        setViewState(ViewState.BT_DISCOVERY);

        // Check if the device is already discovering
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
        }

        if (mBTAdapter.isEnabled()) {
            mBTArrayAdapter.clear(); // clear items
            mBTAdapter.startDiscovery();
            mTimeOutHandler = new Handler();
            mTimeOutHandler.postDelayed(this::handleDiscoveryTimeout, SCAN_PERIOD);
            mSpinnerProgressDialog.setMessage("Searching for FistBump Device...");
            registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
        }

    }

   private void cancelTimeOutHandler() {
        if (mTimeOutHandler != null) {
            mTimeOutHandler.removeCallbacks(this::handleDiscoveryTimeout);
            mTimeOutHandler = null;
        }
    }


    private void connectToFistBump(BluetoothDevice device) {
        final String deviceAddress = device.getAddress();
        final String deviceName = device.getName();
        MainActivity activity = this;
        new Thread() {
            public void run() {
                boolean fail = false;

                BluetoothDevice device = mBTAdapter.getRemoteDevice(deviceAddress);
                Log.v(TAG, "attempting to connect to device: " + device.getName() + " " + device.getAddress());
                try {
                    mBTSocket = createBluetoothSocket(device);
                } catch (IOException e) {
                    fail = true;
                    Log.e(TAG, "CREATE SOCKET FAILED IOException: " + e.getMessage());
                    Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                }
                // Establish the Bluetooth socket connection.
                try {
                    mBTSocket.connect();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Connect FAILED IOException: " + e.getMessage());
                    try {
                        fail = true;
                        mBTSocket.close();
                        mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                .sendToTarget();
                    } catch (IOException e2) {
                        //insert code to deal with this
                        Log.e(TAG, "Socket Creation FAILED IOException: " + e.getMessage());
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                }
                if (fail == false) {
                    mConnectedThread = new ConnectedThread(mBTSocket, activity);
                    mConnectedThread.start();
                    mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, deviceName)
                            .sendToTarget();
                }
            }
        }.start();
    }


    private void animateInAttackMode() {

        mTopBar.setVisibility(View.VISIBLE);
        mTopBar.setY(-100);


        mAttackSheet.setAlpha(1f);
        mAttackSheet.setPivotY(100);
        mAttackSheet.setY(sHeight);
        mAttackSheet.setVisibility(View.VISIBLE);

        mNetworksListView.setVisibility(View.GONE);
        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        mTopBar.animate()
                .translationY(0)
                .setDuration(250)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mNetworksListView.setVisibility(View.VISIBLE);
                    }
                });
        mAttackSheet.animate()
                .setStartDelay(250)
                .alpha(1f)
                .translationY(0)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(250)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mNetworksListView.setAdapter(mListAdapter);
                        mListAdapter.notifyDataSetChanged();
                        mConnectedThread.write("refresh");
                    }
                });

    }

    private void dismissProgressDialogIfNeeded(){
        if (mSpinnerProgressDialog != null && mSpinnerProgressDialog.isShowing()) {
            mSpinnerProgressDialog.dismiss();
        }
        if (mHorizontalProgressDialog != null && mHorizontalProgressDialog.isShowing()) {
            mHorizontalProgressDialog.dismiss();
        }
    }

    //-----------------------------------------------
    //endregion

    //region Public Methods
    //-----------------------------------------------

    public void handlesHashesData (String msg) {
       Log.v(TAG,"DATA RECIEVED::"+ msg);
       String pmkidData = msg.split(",")[1];
       String handshakeData = msg.split(",")[2];
       Log.v(TAG, "PMKID::::: "+pmkidData+"  vs handshakeDATA::::: " +handshakeData);

       String pmkidTotal = pmkidData.split(":")[1];
       String pmkidTargeted = pmkidData.split(":")[0];
       mPMKIDTargeted.setText(pmkidTargeted);
       mPMKIDTotal.setText(pmkidTotal);
       mPMKIDUntargeted.setText(String.valueOf(Integer.parseInt(pmkidTotal)-Integer.parseInt(pmkidTargeted)));

        String handshakeTotal = handshakeData.split(":")[1];
        String handshakeTargeted = handshakeData.split(":")[0];
        mHandshakeTargeted.setText(handshakeTargeted);
        mHandshakeTotal.setText(handshakeTotal);
        mHandshakeUntargeted.setText(String.valueOf(Integer.parseInt(handshakeTotal)-Integer.parseInt(handshakeTargeted)));

    }
    public void handleAttackComplete() {
        mConnectedThread.write("refresh");
        dismissProgressDialogIfNeeded();
    }

    public void handleDiscoveryTimeout() {

        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        if (mBTSocket == null || !mBTSocket.isConnected()) {
            Toast.makeText(getApplicationContext(), "FistBump Not Found: Timed Out", Toast.LENGTH_LONG).show();
            setViewState(ViewState.BT_RETRY);
        }
    }

    public void setViewState(ViewState state) {
        switch (state) {
            case ARMED:
                mSpinnerProgressDialog.setMessage("Finding Potential Targets...");
                mWifiManager.startScan();
                break;
            case BT_RETRY:
                mTopBar.setVisibility(View.GONE);
                mAttackSheet.setVisibility(View.GONE);

                dismissProgressDialogIfNeeded();
                mRetrySheet.setVisibility(View.VISIBLE);
                mNetworksListView.setVisibility(View.GONE);
                break;
            case BT_DISCOVERY:
                mAttackSheet.setVisibility(View.GONE);

                mSpinnerProgressDialog.setMessage("Checking BlueTooth...");
                mSpinnerProgressDialog.show();
                mRetrySheet.setVisibility(View.GONE);
                mNetworksListView.setVisibility(View.GONE);
                break;
            default:
                break;

        }
    }

    public BLEMessageHandler getBleMessageHandler (){
        return mHandler;
    }

    public void showNoDriveErrorDialog() {

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.FistBumpAlertDialogStyle))
                .setView(R.layout.fb_dialog_view)
                .setCancelable(false)
                .create();

        alertDialog.show();
        TextView title = alertDialog.findViewById(R.id.title);
        TextView message = alertDialog.findViewById(R.id.message);
        Button okbtn = alertDialog.findViewById(R.id.okbtn);
        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        title.setText("Missing Thumb Drive");
        message.setText("Looks like you forgot to insert a usb thumbdrive.\nPlease insert one to continue.");
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
    }


    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(TAG, "RECIEVED ACTION: " + action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                Log.v(TAG, "found device named: " + device.getName());

                if (device.getName() != null &&
                        device.getName().equals(DEVICE_NAME) &&
                        mBTArrayAdapter.getCount() < 1) {
                    cancelTimeOutHandler();
                    mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBTArrayAdapter.notifyDataSetChanged();
                    mSpinnerProgressDialog.setMessage("Found FistBump Device, Connecting..");
                    connectToFistBump(device);

                }
            }
        }
    };



    //-----------------------------------------------
    //endregion

    //region Private Classes
    //-----------------------------------------------

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            Log.v(TAG, "Recieved :: " + intent.getAction());
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (mScanResults != null) {
                    mScanResults.clear();
                }
                mScanResults = new ArrayList<ScanResult>(mWifiManager.getScanResults());

                Log.v(TAG, "we have a list of " + mScanResults.size() + "networks");
                wifiSpinner.setVisibility(View.GONE);
                if(mScanResults.size()<1){
                    noWifiMessage.setVisibility(View.VISIBLE);
                } else {
                    noWifiMessage.setVisibility(View.GONE);
                }
                mListAdapter = new NetworkResultsAdapter(getApplicationContext(), mScanResults);

                mNetworksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        if (mListAdapter.getItem(position).equals(mListAdapter.getSelectedNetwork())) {
                            mListAdapter.setSelectedNetwork(null);
                            mNetworksListView.setItemChecked(position, false);
                            mAttackButton.setBackgroundResource(R.drawable.btn_red_bg);
                            mAttackButton.setText(R.string.broad_attack);
                            mTargetMessageView.setText(R.string.no_target_message);

                        } else {
                            mListAdapter.setSelectedNetwork(mListAdapter.getItem(position));
                            mNetworksListView.setItemChecked(position, true);
                            mAttackButton.setBackgroundResource(R.drawable.btn_green_bg);
                            mAttackButton.setText(R.string.targeted_attack);
                            String essid = mListAdapter.getSelectedNetwork().SSID;
                            if (essid.isEmpty()) {
                                essid = "Hidden Network";
                            }
                            mTargetMessageView.setText(String.format("%s selected for targeted attack! Unselect the highlighted target above to revert.", essid));

                        }
                    }
                });
                dismissProgressDialogIfNeeded();
                if (currentState == ViewState.ARMED) {
                    mNetworksListView.setAdapter(mListAdapter);
                    mListAdapter.notifyDataSetChanged();
                } else {
                    currentState = ViewState.ARMED;
                    animateInAttackMode();
                }

            }
        }
    };

}


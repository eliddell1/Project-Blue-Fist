package liddell.onus.com.fistbump_ble;
import java.util.ArrayList;

import android.content.Context;

import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class NetworkResultsAdapter extends ArrayAdapter<ScanResult> {

    private ScanResult selectedNetwork = null;
    public NetworkResultsAdapter(Context context, ArrayList<ScanResult> networks) {
        super(context, 0, networks);

    }

    public NetworkResultsAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ScanResult network = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.network_item, parent, false);
        }
        // Lookup view for data population
        LinearLayout selectableView = convertView.findViewById(R.id.list_item_root);
        TextView name = convertView.findViewById(R.id.essid_label);
        TextView address = convertView.findViewById(R.id.bssid_label);
        ImageView wifiIcon = convertView.findViewById(R.id.wifi_bars_icon);

        // Populate the data into the template view using the data object
        if (network.SSID.isEmpty()){
            name.setText("Hidden Network");
            name.setTypeface(name.getTypeface(), Typeface.ITALIC);
        } else {
            name.setText(network.SSID);
            name.setTypeface(name.getTypeface(), Typeface.BOLD);
        }
        address.setText(network.BSSID);

        if (network.level >= -51) {
            // 4bars
            if(network.capabilities.contains("WPA")){
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_4_bar_lock_black_24dp);
            } else {
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_24dp);
            }
        } else if (network.level >= -62) {
            //3 bars
            if(network.capabilities.contains("WPA")){
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_3_bar_lock_black_24dp);
            } else {
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_24dp);
            }
        } else if (network.level >= -68) {
            //2 bars
            if(network.capabilities.contains("WPA")){
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_2_bar_lock_black_24dp);
            } else {
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_24dp);
            }
        } else {
            //1 bars
            if(network.capabilities.contains("WPA")){
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_1_bar_lock_black_24dp);
            } else {
                wifiIcon.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_24dp);
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }

    public void setSelectedNetwork(ScanResult network) {
        selectedNetwork = network;
    }

    public ScanResult getSelectedNetwork() {
        return selectedNetwork;
    }
}
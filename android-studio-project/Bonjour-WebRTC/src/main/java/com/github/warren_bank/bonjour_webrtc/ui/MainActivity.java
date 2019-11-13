package com.github.warren_bank.bonjour_webrtc.ui;

import com.github.warren_bank.bonjour_webrtc.R;
import com.github.warren_bank.bonjour_webrtc.data_model.ServerListItem;
import com.github.warren_bank.bonjour_webrtc.data_model.SharedPrefs;
import com.github.warren_bank.bonjour_webrtc.lock_management.MulticastLockMgr;
import com.github.warren_bank.bonjour_webrtc.security_model.RuntimePermissions;
import com.github.warren_bank.bonjour_webrtc.service.ServerService;
import com.github.warren_bank.bonjour_webrtc.util.OrgAppspotApprtcGlue;
import com.github.warren_bank.bonjour_webrtc.util.Util;

import org.appspot.apprtc.SettingsActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.net.Inet4Address;
import java.util.ArrayList;

import javax.jmdns.impl.util.ByteWrangler;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class MainActivity extends RuntimePermissionsActivity {
    private static final String TAG = "MainActivity";

    private ListView                      listView;
    private ArrayList<ServerListItem>     listItems;
    private ArrayAdapter<ServerListItem>  listAdapter;

    private ServerListItem selectedServerListItem;

    private String BONJOUR_SERVICE_TYPE;
    private BonjourServiceListener bonjourServiceListener;
    private JmDNS bonjour;

    private class BonjourServiceListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            if (!isMatchingBonjourService(event))
                return;

            Log.d(TAG, "Service added: " + event.getName());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            if (!isMatchingBonjourService(event))
                return;

            ServerListItem item = getServerListItem(event);
            if (item == null)
                return;

            listItems.add(item);
            listAdapter.notifyDataSetChanged();

            Log.d(TAG, "Service resolved: " + event.getName());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            if (!isMatchingBonjourService(event))
                return;

            ServerListItem item = getServerListItem(event);
            if (item == null)
                return;

            int position = listItems.indexOf(item);
            if (position >= 0) {
                listItems.remove(position);
                listAdapter.notifyDataSetChanged();

                Log.d(TAG, "Service removed: " + event.getName());
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Bonjour events helper:
    // ---------------------------------------------------------------------------------------------

    private boolean isMatchingBonjourService(ServiceEvent event) {
        ServiceInfo info = event.getInfo();
        if (info == null)
            return false;

        String text = ByteWrangler.readUTF(info.getTextBytes());
        return text.equals(getPackageName());
    }

    private ServerListItem getServerListItem(ServiceEvent event) {
        ServerListItem item = null;

        ServiceInfo info = event.getInfo();
        if (info == null)
            return item;

        String title = info.getName();
        String ip    = null;

        int port = info.getPort();
        Inet4Address[] ipv4Addresses = info.getInet4Addresses();
        for (Inet4Address address : ipv4Addresses) {
            if (!address.isAnyLocalAddress() && !address.isLinkLocalAddress() && !address.isLoopbackAddress()) {
                ip = String.format("%1$s:%2$s", address.getHostAddress(), port);
                break;
            }
        }

        if ((title != null) && (ip != null) && !title.isEmpty()) {
            item = new ServerListItem(title, ip);
        }

        return item;
    }

    // ---------------------------------------------------------------------------------------------
    // Lifecycle Events:
    // ---------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView    = (ListView) findViewById(R.id.listview);
        listItems   = new ArrayList<ServerListItem>();
        listAdapter = new ArrayAdapter<ServerListItem>(MainActivity.this, android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(listAdapter);

        selectedServerListItem = null;
        BONJOUR_SERVICE_TYPE   = getString(R.string.constant_bonjour_service_type);
        bonjourServiceListener = new BonjourServiceListener();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedServerListItem = listItems.get(position);

                if (RuntimePermissions.isEnabled(MainActivity.this)) {
                    onPermissionsGranted();
                }
                return;
            }
        });

        OrgAppspotApprtcGlue.setDefaultPreferenceValues(MainActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (!ServerService.isStarted())
                MulticastLockMgr.acquire(MainActivity.this);

            bonjour = JmDNS.create(Util.getWlanIpAddress_InetAddress(MainActivity.this), getPackageName());

            bonjour.addServiceListener(BONJOUR_SERVICE_TYPE, bonjourServiceListener);
        }
        catch(Exception e) {}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (!ServerService.isStarted())
                MulticastLockMgr.release();

            if (bonjour != null) {
                bonjour.removeServiceListener(BONJOUR_SERVICE_TYPE, bonjourServiceListener);
                bonjour = null;
            }
        }
        catch(Exception e) {}
    }

    // ---------------------------------------------------------------------------------------------
    // ActionBar:
    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayShowHomeEnabled(false);
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_toggle_server: {
                toggleServer();
                return true;
            }
            case R.id.menu_update_server_alias: {
                openUpdateServerAliasDialog();
                return true;
            }
            case R.id.menu_open_webrtc_settings: {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(menuItem);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Runtime Permissions:
    // ---------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        RuntimePermissions.onRequestPermissionsResult(MainActivity.this, requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionsGranted() {
        connectToServer(selectedServerListItem);

        selectedServerListItem = null;
        finish();
    }

    // ---------------------------------------------------------------------------------------------
    // enable/disable local server and its Bonjour registration on LAN:
    // ---------------------------------------------------------------------------------------------

    private void toggleServer() {
        ServerService.doToggle(MainActivity.this);
    }

    // ---------------------------------------------------------------------------------------------
    // connect to remote server on LAN:
    // ---------------------------------------------------------------------------------------------

    private void connectToServer(ServerListItem serverListItem) {
        String serverIpAddress = serverListItem.ip;

        Intent intent = OrgAppspotApprtcGlue.getCallActivityIntent(MainActivity.this, serverIpAddress);
        startActivity(intent);
    }

    // ---------------------------------------------------------------------------------------------
    // open Server Alias dialog:
    // ---------------------------------------------------------------------------------------------

    private void openUpdateServerAliasDialog() {
        String oldServerAlias = SharedPrefs.getServerAlias(MainActivity.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.dialog_update_server_alias_title);

        final EditText input_server_alias = new EditText(MainActivity.this);
        input_server_alias.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        input_server_alias.setText(oldServerAlias, TextView.BufferType.EDITABLE);
        builder.setView(input_server_alias);

        builder.setPositiveButton(R.string.dialog_update_server_alias_positive, new DialogInterface.OnClickListener() { 
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newServerAlias = input_server_alias.getText().toString();
                if (!newServerAlias.isEmpty() && !newServerAlias.equals(oldServerAlias)) {
                    SharedPrefs.putServerAlias(MainActivity.this, newServerAlias);
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.dialog_update_server_alias_negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}

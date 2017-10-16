package com.example.nario.draglayout.Fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.nario.draglayout.Activity.People_info;
import com.example.nario.draglayout.Adapter.PeopleListAdapter;
import com.example.nario.draglayout.Net.SocketClient;
import com.example.nario.draglayout.Net.SocketServer;
import com.example.nario.draglayout.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static android.os.Looper.getMainLooper;

public class PagerFragment3 extends Fragment {
    public Activity mActivity;
    public LayoutInflater inflater;
    private ListView listView;
    private List<Map<String, Object>> listinfo = new ArrayList<Map<String, Object>>();;
    private SocketServer server = new SocketServer(6666);
    private SocketClient client;
    private boolean isClient = false;
    private final IntentFilter intentFilter = new IntentFilter();
    //channel is to keep connect with Wifi P2P communication
    WifiP2pManager.Channel mChannel;
    //Manager is a class to manage all the function of Wifi P2P
    WifiP2pManager mManager;
    Button sendBut, connBut;
    EditText textEditer, ipEditer;
    private final String TAG = "MainActivity";
    private List<WifiP2pDevice> peers = new ArrayList<>();
    private String[] peersName;
    private FloatingActionButton floating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        inflater = (LayoutInflater) mActivity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = this.inflater.inflate(R.layout.pager3, null);
        floating = (FloatingActionButton) view.findViewById(R.id.floating);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().registerReceiver(mReceiver, intentFilter);
                    mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "discoverPeers onSuccess");
                            Toast.makeText(getActivity(),"discoverPeers onSuccess",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int reasonCode) {
                            Log.d(TAG, "discoverPeers onFailure");
                            Toast.makeText(getActivity(),"discoverPeers onFailure",Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (Exception ex){
                    Log.v("page3 ",ex+"");
                }
            }
        });
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        //peers list change
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        //wifi P2P connection change
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        //Wifi P2P device information change
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mManager = (WifiP2pManager) getActivity().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity(), getMainLooper(), null);
        peersName = new String[1];
        peersName[0] = "No Devices";
        listView = (ListView) view.findViewById(R.id.people_list);
        final List<Map<String, Object>> list = getData();
        listView.setAdapter(new PeopleListAdapter(getActivity(), list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), People_info.class);
                intent.putExtra("name", list.get(position).get("name").toString());
                intent.putExtra("info", list.get(position).get("info").toString());
                intent.putExtra("sex", list.get(position).get("sex").toString());
                try {
                    connect(position);
                    //String ip = connect(picker.getValue());

                    Toast.makeText(getActivity(), "Success Connect!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                startActivity(intent);
            }
        });
        return view;
    }

    public List<Map<String, Object>> getData() {
        return listinfo;
    }

    public String connect(final int num) {
        // Picking the first device found on the network.
        final WifiP2pDevice device = peers.get(num);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        config.groupOwnerIntent = 15; // I want this device to become the owner
        Log.i(TAG, "Start Conection--------" + config.groupOwnerIntent);
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Successfully connect " + device.deviceName, Toast.LENGTH_SHORT).show();
//                peerInfo.setText("" + device.toString() + "\n\nDEVICE address" + device.deviceAddress);
                Log.d(TAG, "connect success");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getActivity(), "Fail to connect peer", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "connect fail");
            }
        });
        Log.i(TAG, "END Connection-------");

        return device.deviceAddress;
    }

    private final WifiP2pManager.ConnectionInfoListener c = new WifiP2pManager.ConnectionInfoListener(){
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            // 这里可以查看变化后的网络信息
            // 通过传递进来的WifiP2pInfo参数获取变化后的地址信息
            // InetAddress from WifiP2pInfo struct.
            InetAddress groupOwnerAddress = info.groupOwnerAddress;
//            peerInfo.append("\ngroup Owner ip: " + groupOwnerAddress.getHostAddress());
            // 通过协商，决定一个小组的组长
            Log.d(TAG, "onConnectionInfoAvailable");
            Log.d(TAG, info.toString());
            if (info.groupFormed && info.isGroupOwner) {
                Log.d(TAG, "我是群主");
                // 这里执行P2P小组组长的任务。
                // 通常是创建一个服务线程来监听客户端的请求
                isClient = false;
                if(server==null){
                    server = new SocketServer(6666);
                    server.startListen();
                }
                sendBut.setEnabled(true);

            } else if (info.groupFormed) {
                Log.d(TAG, "我是客户端");
                // 这里执行普通组员的任务
                // 通常是创建一个客户端向组长的服务器发送请求
                client = new SocketClient(getActivity(), groupOwnerAddress.getHostAddress(), 6666);
                client.startClientThread();
                sendBut.setEnabled(true);
                isClient = true;
            }
        }
    };

    ;

    private final WifiP2pManager.PeerListListener a = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            peers.clear();
            peers.addAll(peerList.getDeviceList());
            //set all peer list into comportment TODO: 2017/09/17 0017
            if (peers.size() == 0) {
                Log.d(TAG, "No devices found");
                if (peersName.length > 0) {
                    peersName[0] = "No Devices";
                } else {
                    peersName = new String[1];
                    peersName[0] = "No Devices";
                    Toast.makeText(getActivity(), "No devices", Toast.LENGTH_SHORT).show();
                }
                return;
            } else {
                peersName = new String[peers.size()];
                int i = 0;
                listinfo = new ArrayList<Map<String, Object>>();
                for (WifiP2pDevice device : peers) {
                    peersName[i++] = device.deviceName;
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("image", R.mipmap.ic_launcher);
                    map.put("name", device.deviceName);
                    map.put("sex", "male");
                    map.put("info", "personal information" + i);
                    listinfo.add(map);
                }
                connBut.setEnabled(true);
            }
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:
                    //表明Wi-Fi对等网络（P2P）是否已经启用
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        Log.d(TAG, "WIFI_P2P is able to use");
                    } else {
                        Log.d(TAG, "WIFI_P2P is unable to use");
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:
                    //表明可用的对等点的列表发生了改变
                    Log.d(TAG, "WIFI_P2P_PEERS_CHANGED_ACTION");
                    if (mManager != null) {
                        mManager.requestPeers(mChannel, a);
                    }
                    Log.d(TAG, "P2P peers changed");
                    break;
                case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:
                    //表示Wi-Fi对等网络的连接状态发生了改变
                    //也可能表示连接成功
                    Log.d(TAG, "WIFI_P2P_CONNECTION_CHANGED_ACTION");
                    if (mManager == null) {
                        return;
                    }
                    NetworkInfo networkInfo = intent
                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                    if (networkInfo.isConnected()) {
                        mManager.requestConnectionInfo(mChannel, c);
                        Log.i(TAG, "已经连接");
                    } else {
                        Log.i(TAG, "断开连接");
                    }
                    break;
                case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:
                    //表示该设备的配置信息发生了改变
                    Log.d(TAG, "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
                    break;
                case WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION:

                    int State = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);

                    if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED)
                        Toast.makeText(getActivity(), "搜索开启", Toast.LENGTH_SHORT).show();
                    else if (State == WifiP2pManager.WIFI_P2P_DISCOVERY_STOPPED)
                        Toast.makeText(getActivity(), "搜索已关闭", Toast.LENGTH_SHORT).show();
            }

        }
    };
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }


    public String getLocalIpAddress() {


        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        try {
            return InetAddress.getByName(String.format("%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff))).toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) getActivity().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }
}

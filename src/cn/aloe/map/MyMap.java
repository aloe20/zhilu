package cn.aloe.map;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.aloe.zhilu.Explanatory;
import cn.aloe.zhilu.GetIp;
import cn.aloe.zhilu.MyOnTouch;
import cn.aloe.zhilu.MyPanelSlideListener;
import cn.aloe.zhilu.R;

public class MyMap extends Activity {
	private MapView mMapView = null;
	private BaiduMap mBaiduMap = null;
	private SlidingPaneLayout paneLayout = null;
	private String strIp = "";
	String ip = "";
	private String latiLongi = "";
	private boolean isSend = false;
	private Socket socket = null;
	private DataOutputStream dosClient = null;
	private DataInputStream disClient = null;
	private DataOutputStream dosServer = null;
	DataInputStream disServer = null;
	ServerSocket server = null;
	FileInputStream fis = null;
	BufferedReader br = null;
	File file = null;
	FileOutputStream fos = null;
	double[] loca = new double[2];
	boolean isBug = false;
	LinearLayout contextMessage = null;
	Button sendBtn = null;
	TextView showMessage = null;
	EditText inputMessage = null;
	Button sendMessage = null;
	boolean isLocation = true;
	boolean isClient = false;
	boolean isServer = false;
	String strMessage = "";
	Vibrator vibrator = null;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x01:
				String[] string = (String[]) msg.obj;
				loca[0] = Double.parseDouble(string[0]);
				loca[1] = Double.parseDouble(string[1]);
				if (loca[0] != 0.0) {
					isLocation = false;
				}
				break;
			case 0x02:
				vibrator.vibrate(500);
				showMessage.setText(showMessage.getText().toString() + "\n" + (String) msg.obj);
				break;
			case 0x03:
				showToast("对方不允许帮助，请重新输入");
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		// 注意该方法要再setContentView方法之前实现
		SDKInitializer.initialize(getApplicationContext());
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_map);
		// 获取地图控件引用
		mMapView = (MapView) findViewById(R.id.bmapView);
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
		initMenu();
		buttonAction();
		mBaiduMap = mMapView.getMap();
		location();
	}

	public void buttonAction() {
		Button helpBtn = (Button) findViewById(R.id.help);
		Button showIP = (Button) findViewById(R.id.show_ip);
		Button sendIp = (Button) findViewById(R.id.send_ip);
		Button moreBtn = (Button) findViewById(R.id.open_server);
		sendBtn = (Button) findViewById(R.id.send_btn);
		Button explanatoryBtn = (Button) findViewById(R.id.explanatory_btn);
		sendMessage = (Button) findViewById(R.id.send_message);
		contextMessage = (LinearLayout) findViewById(R.id.context_message);
		showMessage = (TextView) findViewById(R.id.show_message);
		inputMessage = (EditText) findViewById(R.id.input_message);

		helpBtn.setOnClickListener(new MyOnClickListener());
		showIP.setOnClickListener(new MyOnClickListener());
		sendIp.setOnClickListener(new MyOnClickListener());
		moreBtn.setOnClickListener(new MyOnClickListener());
		sendBtn.setOnClickListener(new MyOnClickListener());
		explanatoryBtn.setOnClickListener(new MyOnClickListener());
		showMessage.setOnClickListener(new MyOnClickListener());
		inputMessage.setOnClickListener(new MyOnClickListener());
		sendMessage.setOnClickListener(new MyOnClickListener());
		showMessage.setOnTouchListener(new MyOnTouch());
	}

	private void initMenu() {
		paneLayout = (SlidingPaneLayout) findViewById(R.id.slidepanel);
		paneLayout.setPanelSlideListener(new MyPanelSlideListener());
	}

	public void location() {
		LocationClient mLocationClient = new LocationClient(this);
		MyLocationListener mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(3000);
		option.setNeedDeviceDirect(true);
		option.setLocationMode(LocationMode.Hight_Accuracy);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	public void showLocation(double latitude, double longitude) {
		// 定义Maker坐标点
		LatLng point = new LatLng(latitude, longitude);
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.point);
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
		// 定义地图状态
		MapStatus mMapStatus = new MapStatus.Builder().target(point).zoom(20).build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(final BDLocation location) {
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			latiLongi = location.getLatitude() + "##" + location.getLongitude();
			if (isLocation) {
				showLocation(location.getLatitude(), location.getLongitude());
				Log.i("aloe", location.getLatitude() + "**" + location.getLongitude());
			} else {
				showLocation(loca[0], loca[1]);
				Log.i("aloe", loca[0] + "@@" + loca[1]);
			}
			if (isSend && dosClient != null) {
				try {
					dosClient.writeUTF(latiLongi);
					dosClient.flush();
				} catch (IOException e) {
					try {
						dosClient.close();
					} catch (IOException e1) {

					}
					isSend = false;
				}
			}
		}
	}

	// 输入IP寻求帮助
	@SuppressLint("InflateParams")
	public void inputIpAlertDialog() {
		View view = (LinearLayout) getLayoutInflater().inflate(R.layout.input_ip, null);
		final EditText edit1 = (EditText) view.findViewById(R.id.editText1);
		final EditText edit2 = (EditText) view.findViewById(R.id.editText2);
		final EditText edit3 = (EditText) view.findViewById(R.id.editText3);
		final EditText edit4 = (EditText) view.findViewById(R.id.editText4);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("请输入对方IP");
		builder.setView(view);

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				strIp = edit1.getText().toString() + "." + edit2.getText().toString() + "." + edit3.getText().toString()
						+ "." + edit4.getText().toString();
				String checkIP = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
				if (strIp.matches(checkIP)) {
					if (ip.equals(strIp)) {
						showToast("请勿向自己求助");
					} else {
						new Thread(new Runnable() {
							public void run() {
								try {
									createClient(strIp);
								} catch (UnknownHostException e) {
								} catch (IOException e) {
									isSend = false;
									handler.sendEmptyMessage(0x03);
								}
							}
						}).start();
						showToast("已发送求助");
						isSend = true;
					}
				} else {
					showToast("输入的IP有误");
				}
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	// 手机按键事件
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			exit2Click();
			break;
		default:
			break;
		}
		return false;
	}

	private static long startTime = 0;

	// 双击返回键退出程序
	private void exit2Click() {
		if (startTime == 0) {
			showToast("再按一次退出程序");
			startTime = System.currentTimeMillis();
		} else {
			if (System.currentTimeMillis() - startTime < 1500)
				System.exit(0);
			else {
				showToast("再按一次退出程序");
				startTime = System.currentTimeMillis();
			}
		}
	}

	// 弹出toast提示信息
	public void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	// 关闭数据流
	public void closeIo() throws IOException {
		if (dosClient != null) {
			dosClient.close();
		}
		if (socket != null) {
			socket.close();
		}
		if (disClient != null) {
			disClient.close();
		}
		if (dosServer != null) {
			dosServer.close();
		}
		if (disServer != null) {
			disServer.close();
		}
	}

	// 单击事件处理
	class MyOnClickListener implements OnClickListener {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.show_ip:
				showToast("IP地址：" + ip);
				break;
			case R.id.send_ip:
//				showToast("发送" + ip);
				Intent intent1 = new Intent();
				intent1.putExtra("ipAddress", ip);
				intent1.setClass(MyMap.this, ShowContacts.class);
				startActivity(intent1);
				break;
			case R.id.help:
				inputIpAlertDialog();
				break;
			case R.id.open_server:

				new Thread(new Runnable() {
					public void run() {
						try {
							creatServer();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}).start();
				showToast("允许被求助");
				break;
			case R.id.send_btn:
				if (sendBtn.getText().toString().equals("发送消息")) {
					sendBtn.setText("隐藏消息");
					contextMessage.setVisibility(View.VISIBLE);
				} else if (sendBtn.getText().toString().equals("隐藏消息")) {
					showMessage.setText("");
					inputMessage.setText("");
					strMessage = "";
					sendBtn.setText("发送消息");
					contextMessage.setVisibility(View.GONE);
				}
				break;
			case R.id.explanatory_btn:
				Intent intent = new Intent(MyMap.this, Explanatory.class);
				startActivity(intent);
				break;
			case R.id.show_message:

				break;
			case R.id.input_message:
				break;
			case R.id.send_message:
				showMessage.setText(showMessage.getText().toString() + "\n我:" + inputMessage.getText().toString());
				if (isClient) {
					try {
						dosClient.writeUTF("@" + strMessage + inputMessage.getText().toString());
						dosClient.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (isServer) {
					try {
						dosServer.writeUTF("@" + strMessage + inputMessage.getText().toString());
						dosServer.flush();
						Log.i("aloe2", "BA##:" + inputMessage.getText().toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				inputMessage.setText("");
				break;
			default:
				break;
			}
		}
	}

	// 创建服务端
	public void creatServer() throws IOException {
		server = new ServerSocket(8779);
		Socket socket2 = server.accept();
		String[] string = new String[2];
		dosServer = new DataOutputStream(socket2.getOutputStream());
		disServer = new DataInputStream(socket2.getInputStream());
		isServer = true;
		while (true) {
			Message message = Message.obtain();
			String ss = disServer.readUTF();
			if (ss.charAt(0) == '@') {
				String sMessage = ss.substring(1, ss.length());
				message.obj = sMessage;
				message.what = 0x02;
				Log.i("aloe2", "AC##:" + (String) message.obj);
			} else {
				string = ss.split("##");
				message.obj = string;
				message.what = 0x01;
			}
			handler.sendMessage(message);
		}
	}

	public void createClient(String ipAddress) throws UnknownHostException, IOException {
		socket = new Socket(ipAddress, 8779);
		dosClient = new DataOutputStream(socket.getOutputStream());
		disClient = new DataInputStream(socket.getInputStream());
		isClient = true;
		while (true) {
			String st = disClient.readUTF();
			String stMessage = st.substring(1, st.length());

			new MyTask().execute(stMessage);
		}
	}

	class MyTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return params[0];
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			vibrator.vibrate(500);
			showMessage.setText(showMessage.getText().toString() + "\n" + result);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			closeIo();
			disClient.close();
			disServer.close();
			server.close();
		} catch (IOException e) {
		}
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		ip = new GetIp().getIp();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}

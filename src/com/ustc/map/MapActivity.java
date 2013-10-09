package com.ustc.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ustc.R;
import com.ustc.utils.HttpDownload;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MapActivity extends Activity {

	private static final String TAG = MapActivity.class.getName();

	private static final String KEY = "CB112e11a89e30d71bbdc2dc8872d316";
	
	private static final String SERVER_IP = "192.168.1.131";
	private static final int SERVER_PORT = 8189;
	

	private BMapManager mBMapMan;

	private MapView mMapView;
	private TextView tvInfo;
	private Button btnSearch;

	public static final int MSG_ZOOM = 0;
	public static final int MSG_RESPONSE = 1;
	public static final int MSG_GET_LOCATION_SUCCESS = 2;
	
	private boolean firstTime = true;

	private GeoPoint myPosition;

	private LocationClient mLocationClient = null;
	// private BDLocationListener myListener = new MyLocationListener();

	private boolean zoomStatus = false; // false means already zoom in

	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ZOOM:
				zoom();
				break;
			case MSG_RESPONSE:
				tvInfo.setText(msg.getData().getString("response"));
				break;
			case MSG_GET_LOCATION_SUCCESS:
				BDLocation location = (BDLocation)msg.obj;
				if(firstTime){
					initMyLocationOverlay(location);
					firstTime = false;
				}
				setMyPositon(location);
			default:
				break;

			}
		}
	};
	
	protected void initMyLocationOverlay(BDLocation location) {
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);  
		LocationData locData = new LocationData();  
		locData.latitude = location.getLatitude();  
		locData.longitude = location.getLongitude();  
		locData.direction = location.getDerect();  
		myLocationOverlay.setData(locData);  
		mMapView.getOverlays().add(myLocationOverlay);  
		mMapView.refresh();  
	}

	protected void setMyPositon(BDLocation location) {
		mMapView.getController().animateTo(new GeoPoint((int)(location.getLatitude()*1e6),  
		(int)(location.getLongitude()* 1e6)));

	}



	protected void zoom() {
		MapController mMapController = mMapView.getController();
		// mMapController.setZoom(zoomStatus?12:16);
		mMapController.setZoom(12);
		zoomStatus = zoomStatus ? false : true;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
	}

	private void initView() {
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(KEY, null);
		setContentView(R.layout.activity_map);
		mMapView = (MapView) findViewById(R.id.bmapView);

		tvInfo = (TextView) findViewById(R.id.tv_info);
		btnSearch = (Button) findViewById(R.id.btn_search);
		btnSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getShopInfo();
			}
		});

		mMapView.setBuiltInZoomControls(true);
		addMapListener();
		MapController mMapController = mMapView.getController();
		GeoPoint point = new GeoPoint((int) (31.84596 * 1E6),
				(int) (117.262938 * 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom(12);
		initLocationClient();
	}

	protected void getShopInfo() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				HttpDownload dlTool = new HttpDownload();
				String response = dlTool.download(SERVER_IP, SERVER_PORT,
						"Search");
				Message msg = Message.obtain(uiHandler);
				msg.what = MSG_RESPONSE;
				Bundle data = new Bundle();
				data.putString("response", response);
				msg.setData(data);
				msg.sendToTarget();
			}
		});
		t.start();
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.setAK(KEY);
		mLocationClient
				.registerLocationListener(new LocationListener(uiHandler));
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setAddrType("all");// 返回的定位结果包含地址信息
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(10000);// 设置发起定位请求的间隔时间为5000ms
		option.disableCache(true);// 禁止启用缓存定位
		option.setPoiNumber(5); // 最多返回POI个数
		option.setPoiDistance(1000); // poi查询距离
		option.setPoiExtraInfo(true); // 是否需要POI的电话和地址等详细信息
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	private void getMyPosition() {
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();
		else
			Log.e(TAG, "locClient is null or not started!");

	}

	private void addMapListener() {
		MKMapTouchListener mapTouchListener = new MKMapTouchListener() {
			@Override
			public void onMapClick(GeoPoint point) {
				Log.i(TAG, "onMapClick");
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				Log.i(TAG, "onMapDoubleClick");
				// Message msg = Message.obtain(uihandler);
				// msg.what = MSG_ZOOM;
				// msg.sendToTarget();
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				Log.i(TAG, "onMapLongClick");
			}
		};
		mMapView.regMapTouchListner(mapTouchListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		mMapView.destroy();
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		super.onResume();
	}
}

class LocationListener implements BDLocationListener {
	private static final String TAG = LocationListener.class.getName();
	private Handler uiHandler;

	LocationListener(Handler uiHandler) {
		this.uiHandler = uiHandler;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		if (location == null)
			return;
		StringBuffer sb = new StringBuffer(256);
		sb.append("time : ");
		sb.append(location.getTime());
		sb.append("\nerror code : ");
		sb.append(location.getLocType());
		sb.append("\nlatitude : ");
		sb.append(location.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(location.getLongitude());
		sb.append("\nradius : ");
		sb.append(location.getRadius());
		if (location.getLocType() == BDLocation.TypeGpsLocation) {
			sb.append("\nspeed : ");
			sb.append(location.getSpeed());
			sb.append("\nsatellite : ");
			sb.append(location.getSatelliteNumber());
		} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(location.getAddrStr());
		}
		Log.i(TAG, sb.toString());
		Message msg = Message.obtain(uiHandler);
		msg.what = MapActivity.MSG_GET_LOCATION_SUCCESS;
		msg.obj = location;
		msg.sendToTarget();
	}

	@Override
	public void onReceivePoi(BDLocation poiLocation) {
		if (poiLocation == null) {
			return;
		}
		StringBuffer sb = new StringBuffer(256);
		sb.append("Poi time : ");
		sb.append(poiLocation.getTime());
		sb.append("\nerror code : ");
		sb.append(poiLocation.getLocType());
		sb.append("\nlatitude : ");
		sb.append(poiLocation.getLatitude());
		sb.append("\nlontitude : ");
		sb.append(poiLocation.getLongitude());
		sb.append("\nradius : ");
		sb.append(poiLocation.getRadius());
		if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
			sb.append("\naddr : ");
			sb.append(poiLocation.getAddrStr());
		}
		if (poiLocation.hasPoi()) {
			sb.append("\nPoi:");
			sb.append(poiLocation.getPoi());
		} else {
			sb.append("noPoi information");
		}

	}

}

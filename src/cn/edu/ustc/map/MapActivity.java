package cn.edu.ustc.map;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import cn.edu.ustc.R;
import cn.edu.ustc.command.Command;
import cn.edu.ustc.command.CommandPool;
import cn.edu.ustc.command.CommandSink;
import cn.edu.ustc.command.GetShopCommand;
import cn.edu.ustc.data.ShopDataModel;
import cn.edu.ustc.shopinfo.ShopInfoActivity;
import static cn.edu.ustc.utils.Consts.*;

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
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {

	private static final String TAG = MapActivity.class.getName();

	private static final String KEY = "CB112e11a89e30d71bbdc2dc8872d316";

	private BMapManager mBMapMan;

	private MapView mMapView;

	public static final int MSG_ZOOM = 0;
	public static final int MSG_GET_SHOPINFO_SUCCESS = 1;
	public static final int MSG_GET_SHOPINFO_FAILED = 2;
	public static final int MSG_GET_LOCATION_SUCCESS = 3;

	private boolean firstTime = true;

	private GeoPoint searchPosition;;
	private GeoPoint currentPosition;

	private LocationClient mLocationClient;

	private PopupOverlay pop;

	private ShopPoiOverlay shopPoiOverlay;

	private float zoomLevel = 0F;

	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ZOOM:
				break;
			case MSG_GET_SHOPINFO_SUCCESS:
				// TEST
				showShop();
				break;
			case MSG_GET_SHOPINFO_FAILED:
				Toast.makeText(MapActivity.this, R.string.get_shop_failed,
						Toast.LENGTH_SHORT).show();
				break;
			case MSG_GET_LOCATION_SUCCESS:
				BDLocation location = (BDLocation) msg.obj;
				if (firstTime) {
					initMyLocationOverlay(location);
					firstTime = false;
				}
				// setMyPositon(location);
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

	protected void showShop() {
		mMapView.getOverlays().remove(shopPoiOverlay);
		shopPoiOverlay = new ShopPoiOverlay(this, mMapView);
		ArrayList<MKPoiInfo> shopPoiList = new ArrayList<MKPoiInfo>();
		Collection<ShopData> shopDataSet = ShopDataModel.getInstance()
				.getShopDataList();
		for (ShopData shopData : shopDataSet) {
			MyPoi poi = new MyPoi(shopData);
			poi.ePoiType = 0;
			poi.hasCaterDetails = true;
			shopPoiList.add(poi);
		}
		shopPoiOverlay.setData(shopPoiList);

		mMapView.getOverlays().add(shopPoiOverlay);
		mMapView.refresh();
	}

	protected void setMyPositon(BDLocation location) {
		currentPosition = new GeoPoint((int) (location.getLatitude() * 1e6),
				(int) (location.getLongitude() * 1e6));
		mMapView.getController().animateTo(currentPosition);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			zoomLevel = savedInstanceState.getFloat(ZOOM_LEVEL);
		} else {
			zoomLevel = 12;
		}
		Log.d(TAG, "onCreate!");
		initView();
		showShop();
	}

	private void initView() {
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(KEY, null);
		setContentView(R.layout.activity_map);
		mMapView = (MapView) findViewById(R.id.bmapView);

		mMapView.setBuiltInZoomControls(true);
		addMapListener();
		MapController mMapController = mMapView.getController();
		GeoPoint point = new GeoPoint((int) (31.84596 * 1E6),
				(int) (117.262938 * 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom(zoomLevel);
		mMapController.enableClick(true);
		initLocationClient();
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.setAK(KEY);
		mLocationClient.registerLocationListener(new LocationListener());
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
				if (pop != null) {
					pop.hidePop();
				}
				// checkTapOnShop(point);
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				Log.i(TAG, "onMapDoubleClick");
				if (pop != null) {
					pop.hidePop();
				}
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				Log.i(TAG, "onMapLongClick");
				searchPosition = point;
				mMapView.getController().animateTo(point);
				showBubble(point);

			}
		};
		mMapView.regMapTouchListner(mapTouchListener);

	}

	protected void showBubble(GeoPoint point) {
		pop = new PopupOverlay(mMapView, new PopupClickListener() {
			@Override
			public void onClickedPopup(int index) {
				switch (index) {
				case 0:
					searchShop();
					pop.hidePop();
					break;
				case 1:
					break;
				case 2:
					break;
				default:
					break;
				}
			}
		});
		Bitmap[] bmps = new Bitmap[1];
		try {
			bmps[0] = BitmapFactory
					.decodeStream(getAssets().open("search.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		pop.showPopup(bmps, point, 32);

	}

	protected void searchShop() {
		double locationX = (double) (searchPosition.getLatitudeE6()) / 1e6;
		double locationY = (double) (searchPosition.getLongitudeE6()) / 1e6;
		GetShopCommand cmd = new GetShopCommand(locationX, locationY, 500F,
				new CommandSink() {

					@Override
					public void onCommandExcuted(int result, Command cmd,
							Object[]... args) {
						Message msg;
						switch (result) {
						case GET_FAILED:
							msg = Message.obtain(uiHandler);
							msg.what = MSG_GET_SHOPINFO_FAILED;
							msg.sendToTarget();
							break;
						case GET_SUCCESS:
							msg = Message.obtain(uiHandler);
							msg.what = MSG_GET_SHOPINFO_SUCCESS;
							msg.sendToTarget();
							break;
						}
					}
				});
		CommandPool.getInstance().add(cmd);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart!");
		mLocationClient.start();
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume!");
		mMapView.onResume();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
		super.onResume();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "onRestoreInstanceState!");
		if (savedInstanceState != null) {
			zoomLevel = savedInstanceState.getFloat(ZOOM_LEVEL);
		} else {
			zoomLevel = 12;
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState!");
		outState.putFloat(ZOOM_LEVEL, mMapView.getZoomLevel());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause!");
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop!");
		mLocationClient.stop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy!");
		mMapView.destroy();
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onDestroy();
	}

	class LocationListener implements BDLocationListener {
		private Handler uiHandler;

		LocationListener() {
			this.uiHandler = MapActivity.this.uiHandler;
		}

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
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
		}
	}

	class ShopPoiOverlay extends PoiOverlay {

		public ShopPoiOverlay(Activity activity, MapView mapView) {
			super(activity, mapView);
		}

		@Override
		protected boolean onTap(int i) {
			Log.i(TAG, "On tap on Poi " + i +"!");
			MyPoi poi = (MyPoi) getPoi(i);
			Intent intent = new Intent(MapActivity.this, ShopInfoActivity.class);
			intent.putExtra(SHOP_ID, poi.uid);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			MapActivity.this.startActivity(intent);
			return true;
		}

	}

	public class MyPoi extends MKPoiInfo {
		public MyPoi(ShopData shopData) {
			this.uid = shopData.getShopID();
			this.pt = shopData.getLocation();
			this.address = shopData.getShopAddr();
			this.name = shopData.getShopName();
			this.phoneNum = shopData.getShopTel();
			this.intro = shopData.getShopIntro();
			this.label = shopData.getShopLabel();
		}

		public String intro;
		public String label;

	}
}

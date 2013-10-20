package cn.edu.ustc.map;

import java.io.IOException;
import java.util.ArrayList;

import cn.edu.ustc.R;
import cn.edu.ustc.command.Command;
import cn.edu.ustc.command.CommandPool;
import cn.edu.ustc.command.CommandSink;
import cn.edu.ustc.command.GetShopCommand;
import cn.edu.ustc.shopinfo.ShopInfoActivity;
import static cn.edu.ustc.utils.Consts.*;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity {

	private static final String TAG = MapActivity.class.getName();

	private static final String KEY = "CB112e11a89e30d71bbdc2dc8872d316";

	private BMapManager mBMapMan;

	private MapView mMapView;
	private TextView tvInfo;

	public static final int MSG_ZOOM = 0;
	public static final int MSG_GET_SHOPINFO_SUCCESS = 1;
	public static final int MSG_GET_LOCATION_SUCCESS = 2;

	private boolean firstTime = true;

	private GeoPoint searchPosition;;
	private GeoPoint currentPosition;

	private ArrayList<ShopData> shopList = new ArrayList<ShopData>();

	private LocationClient mLocationClient;
	
	private PopupOverlay pop;

	private ShopPoiOverlay shopPoiOverlay;

	private boolean zoomStatus = false; // false means already zoom in

	private Handler uiHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ZOOM:
				zoom();
				break;
			case MSG_GET_SHOPINFO_SUCCESS:
				//TEST
				showShop();
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
		if(shopPoiOverlay == null)
			shopPoiOverlay = new ShopPoiOverlay(this, mMapView);
		ArrayList<MKPoiInfo> shopPoiList = new ArrayList<MKPoiInfo>();
		for (ShopData shopData : shopList) {
			MKPoiInfo poi = new MKPoiInfo();
			poi.uid = shopData.getShopID();
			poi.pt = shopData.getLocation();
			poi.address = shopData.getShopAddr();
			poi.name = shopData.getShopName();
			poi.phoneNum = shopData.getShopTel();
			poi.ePoiType = 0;
			poi.hasCaterDetails = true;
			shopPoiList.add(poi);
			break;
		}
		shopPoiOverlay.setData(shopPoiList);
		mMapView.getOverlays().clear();
		if(!mMapView.getOverlays().contains(shopPoiOverlay))
			mMapView.getOverlays().add(shopPoiOverlay);
		mMapView.refresh();
		mMapView.getController().animateTo(searchPosition);

	}

	protected void setMyPositon(BDLocation location) {
		currentPosition = new GeoPoint((int) (location.getLatitude() * 1e6),
				(int) (location.getLongitude() * 1e6));
		mMapView.getController().animateTo(currentPosition);

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
		Log.d(TAG, "onCreate!");
		initView();
	}

	private void initView() {
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(KEY, null);
		setContentView(R.layout.activity_map);
		mMapView = (MapView) findViewById(R.id.bmapView);

		tvInfo = (TextView) findViewById(R.id.tv_info);

		mMapView.setBuiltInZoomControls(true);
		addMapListener();
		MapController mMapController = mMapView.getController();
		GeoPoint point = new GeoPoint((int) (31.84596 * 1E6),
				(int) (117.262938 * 1E6));
		mMapController.setCenter(point);
		mMapController.setZoom(12);
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
				if (pop != null) {
					pop.hidePop();
				}
				checkTapOnShop(point);
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
				showBubble(point);
			}
		};
		mMapView.regMapTouchListner(mapTouchListener);

	}
	
	//Temp solution.
	protected void checkTapOnShop(GeoPoint point) {
		for(ShopData shopData:shopList){
			if((shopData.getLocation().getLatitudeE6() - point.getLatitudeE6() <= 100)&&(shopData.getLocation().getLongitudeE6() - point.getLongitudeE6() <= 100)){
				Intent intent = new Intent(MapActivity.this, ShopInfoActivity.class);
				intent.putExtra(SHOP_ID, shopData.getShopID());
				MapActivity.this.startActivity(intent);
				break;
			}
				
		}
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
		GetShopCommand cmd = new GetShopCommand(locationX, locationY,
				new CommandSink() {

					@Override
					public void onCommandExcuted(int result, Command cmd,
							Object[]... args) {
						shopList = ((GetShopCommand) cmd).getShopList();
						Message msg = Message.obtain(uiHandler);
						msg.what = MSG_GET_SHOPINFO_SUCCESS;
						msg.sendToTarget();
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
	protected void onStart(){
		Log.d(TAG, "onStart!");
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
	protected void onPause() {
		Log.d(TAG, "onPause!");
		mMapView.onPause();
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}
	
	@Override
	protected void onStop(){
		Log.d(TAG, "onStop!");
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
			//TODO: It doesn't work. Need check!
			Log.i(TAG, "On tap!!!!!!");
			MKPoiInfo poi = getPoi(i);
			Intent intent = new Intent(MapActivity.this, ShopInfoActivity.class);
			intent.putExtra(SHOP_ID, poi.uid);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			MapActivity.this.startActivity(intent);
			return super.onTap(i);
		}

	}
}

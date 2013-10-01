package com.ustc;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MapActivity extends Activity {
	
	private static final String TAG = MapActivity.class.getName();
	
	private static final String KEY = "CB112e11a89e30d71bbdc2dc8872d316";
    private BMapManager mBMapMan;
	private MapView mMapView;
	
	private TextView tvInfo;
	
	private static final int MSG_ZOOM = 0;
	
	private boolean zoomStatus = false; //false means already zoom in
	
	private Handler uihandler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case MSG_ZOOM:
				zoom();
				break;
			default:
				break;
				
			}
		}
	};


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    protected void zoom() {
        MapController mMapController=mMapView.getController();  
//        mMapController.setZoom(zoomStatus?12:16);
        mMapController.setZoom(12);
        zoomStatus = zoomStatus?false:true;
	}


	private void initView() {
        mBMapMan=new BMapManager(getApplication());  
        mBMapMan.init(KEY, null); 
        setContentView(R.layout.activity_map);
        mMapView=(MapView)findViewById(R.id.bmapView);
        tvInfo=(TextView) findViewById(R.id.tv_info);
        mMapView.setBuiltInZoomControls(true); 
        addMapListener();
        MapController mMapController=mMapView.getController();  
        GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));  
        mMapController.setCenter(point);
        mMapController.setZoom(12);
	}


	private void addMapListener() {
		MKMapTouchListener mapTouchListener = new MKMapTouchListener(){  
	        @Override  
	        public void onMapClick(GeoPoint point) { 
	        	Log.i(TAG, "onMapClick");
	        }  
	  
	        @Override  
	        public void onMapDoubleClick(GeoPoint point) {
	        	Log.i(TAG, "onMapDoubleClick");
//	        	Message msg = Message.obtain(uihandler);
//	        	msg.what = MSG_ZOOM;
//	        	msg.sendToTarget();
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
    protected void onDestroy(){  
            mMapView.destroy();  
            if(mBMapMan!=null){  
                    mBMapMan.destroy();  
                    mBMapMan=null;  
            }  
            super.onDestroy();  
    }  
    @Override  
    protected void onPause(){  
            mMapView.onPause();  
            if(mBMapMan!=null){  
                   mBMapMan.stop();  
            }  
            super.onPause();  
    }  
    @Override  
    protected void onResume(){  
            mMapView.onResume();  
            if(mBMapMan!=null){  
                    mBMapMan.start();  
            }  
           super.onResume();  
    } 
}

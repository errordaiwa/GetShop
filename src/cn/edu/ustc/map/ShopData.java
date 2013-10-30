package cn.edu.ustc.map;

import cn.edu.ustc.map.MapActivity.MyPoi;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class ShopData {
	public ShopData() {

	}

	public ShopData(MyPoi poi) {
		this.shopID = poi.uid;
		this.shopName = poi.name;
		this.location = poi.pt;
		this.shopAddr = poi.address;
		this.shopLabel = poi.label;
		this.shopTel = poi.phoneNum;
		this.shopIntro = poi.intro;
	}

	private String shopID;
	private String shopName;
	private String shopAddr;
	private GeoPoint location;
	private String shopLabel;
	private String shopIntro;
	private String shopTel;

	public String getShopID() {
		return shopID;
	}

	public void setShopID(String shopID) {
		this.shopID = shopID;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopAddr() {
		return shopAddr;
	}

	public void setShopAddr(String shopAddr) {
		this.shopAddr = shopAddr;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public void setLocation(int locationX, int locationY) {
		if (location == null)
			location = new GeoPoint(0, 0);
		location.setLatitudeE6(locationX);
		location.setLongitudeE6(locationY);
	}

	public String getShopLabel() {
		return shopLabel;
	}

	public void setShopLabel(String shopLabel) {
		this.shopLabel = shopLabel;
	}

	public String getShopIntro() {
		return shopIntro;
	}

	public void setShopIntro(String shopIntro) {
		this.shopIntro = shopIntro;
	}

	public String getShopTel() {
		return shopTel;
	}

	public void setShopTel(String shopTel) {
		this.shopTel = shopTel;
	}

}

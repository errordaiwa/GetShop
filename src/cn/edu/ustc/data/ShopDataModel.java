package cn.edu.ustc.data;

import java.util.Collection;
import java.util.HashMap;

import cn.edu.ustc.map.ShopData;

public class ShopDataModel {
	private static final ShopDataModel instance = new ShopDataModel();
	private HashMap<String, ShopData> shopList = new HashMap<String, ShopData>();

	private ShopDataModel() {

	}

	public static ShopDataModel getInstance() {
		return instance;
	}
	
	
	public synchronized Collection<ShopData> getShopDataList(){
		return shopList.values();
	}
	
	public synchronized void setShopDataList(HashMap<String, ShopData> shopList){
		this.shopList = shopList;
	}
	
	public synchronized void addShop(ShopData shopData){
		shopList.put(shopData.getShopID(), shopData);
	}
	
	public synchronized ShopData getShop(String shopID){
		return shopList.get(shopID);
	}
	
	public synchronized void clearShop(){
		shopList.clear();
	}

}

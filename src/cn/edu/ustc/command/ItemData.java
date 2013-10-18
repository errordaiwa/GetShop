package cn.edu.ustc.command;

import android.graphics.Bitmap;

public class ItemData {
	private String itemID = null;
	private Bitmap itemImage = null;

	public String getItemID() {
		return itemID;
	}

	public void setItemID(String itemID) {
		this.itemID = itemID;
	}

	public Bitmap getItemImage() {
		return itemImage;
	}

	public void setItemImage(Bitmap itemImage) {
		this.itemImage = itemImage;
	}
}

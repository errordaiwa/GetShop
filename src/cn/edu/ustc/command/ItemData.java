package cn.edu.ustc.command;

import android.graphics.Bitmap;

public class ItemData {
	private String itemID;
	private Bitmap itemImage;
	private long time;
	private int imageLength;

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

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getImageLength() {
		return imageLength;
	}

	public void setImageLength(int imageLength) {
		this.imageLength = imageLength;
	}
}

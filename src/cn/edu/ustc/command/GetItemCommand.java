package cn.edu.ustc.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import cn.edu.ustc.map.ShopData;
import cn.edu.ustc.utils.HttpDownload;
import cn.edu.ustc.utils.StringUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class GetItemCommand extends Command {
	private static final String TAG = GetItemCommand.class.getName();
	
	private String shopID;
	private long lastModifierTime;
	private int limit;
	
	private CommandSink sink;
	
	private ArrayList<ItemData> itemList = new ArrayList<ItemData>();

	private String xmlRequest;

	private String xmlReturn;



	public GetItemCommand(String shopID, long lastModifierTime, int limit, CommandSink sink) {
		this.shopID = shopID;
		this.lastModifierTime = lastModifierTime;
		this.limit = limit;
		this.sink = sink;
	}

	@Override
	public void onPrepare() {
		XmlSerializer xmlBuilder = Xml.newSerializer();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			xmlBuilder.setOutput(outStream, "UTF-8");
			xmlBuilder.startDocument("UTF-8", true);
			xmlBuilder.startTag(null, "command");
			xmlBuilder.startTag(null, "type");
			xmlBuilder.text("get_item");
			xmlBuilder.endTag(null, "type");
			xmlBuilder.startTag(null, "data");
			xmlBuilder.startTag(null, "shopID");
			xmlBuilder.text(shopID);
			xmlBuilder.endTag(null, "shopID");
			xmlBuilder.startTag(null, "lastModifierTime");
			xmlBuilder.text(Long.toString(lastModifierTime));
			xmlBuilder.endTag(null, "lastModifierTime");
			xmlBuilder.startTag(null, "limit");
			xmlBuilder.text(Integer.toString(limit));
			xmlBuilder.endTag(null, "limit");
			xmlBuilder.endTag(null, "data");
			xmlBuilder.endTag(null, "command");
			xmlBuilder.endDocument();
			outStream.flush();
			byte[] xmlBytes = outStream.toByteArray();
			outStream.close();
			xmlRequest = new String(xmlBytes, "UTF-8");
			Log.i(TAG, "Request xml: " + xmlRequest);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onExcute() {
		xmlReturn = new HttpDownload().download(xmlRequest);

	}

	@Override
	public void onParse() {
		Log.i(TAG, "Return xml: " + xmlReturn);
		try {
			 ByteArrayInputStream inStream = new ByteArrayInputStream(
			 xmlReturn.getBytes("UTF-8"));
//			FileInputStream inStream = new FileInputStream(
//					Environment.getExternalStorageDirectory() + "/piclist.txt");
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			ItemData currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if ("image".equalsIgnoreCase(tagName)) {
						currentItem = new ItemData();
					} else if ("len".equalsIgnoreCase(tagName)) {
						currentItem.setImageLength(Integer.parseInt(parser.nextText().trim()));
					} else if ("time".equalsIgnoreCase(tagName)) {
						currentItem.setTime(Long.parseLong(parser.nextText().trim()));
					} else if ("data".equalsIgnoreCase(tagName)) {
						byte[] imageBytes = StringUtils.hexStringToBytes(parser.nextText().trim());
						Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
						currentItem.setItemImage(bitmap);
					} 
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if ("image".equalsIgnoreCase(tagName)) {
						itemList.add(currentItem);
					} 
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

			sink.onCommandExcuted(1, this);
		} catch (Exception e) {
			Log.e(TAG, "Error: " + e.getMessage());                                                                                                                                                                                                                        
			sink.onCommandExcuted(0, this);
		}

	}
	
	public ArrayList<ItemData> getItemList(){
		return itemList;
	}

}

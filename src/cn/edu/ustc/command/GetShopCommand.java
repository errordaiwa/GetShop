package cn.edu.ustc.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import cn.edu.ustc.map.ShopData;
import cn.edu.ustc.utils.HttpDownload;

import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class GetShopCommand extends Command {
	private static final String TAG = GetShopCommand.class.getName();

	private CommandSink sink = null;
	private String xmlRequest = null;
	private double locationX = 0;
	private double locationY = 0;

	private String xmlReturn = null;

	private String dataReturn = null;

	private ArrayList<ShopData> shopList = new ArrayList<ShopData>();

	public GetShopCommand(double locationX, double locationY, CommandSink sink) {
		this.locationX = locationX;
		this.locationY = locationY;
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
			xmlBuilder.text("get_shop");
			xmlBuilder.endTag(null, "type");
			xmlBuilder.startTag(null, "data");
			xmlBuilder.startTag(null, "location");
			xmlBuilder.startTag(null, "x");
			xmlBuilder.text(Double.toString(locationX));
			xmlBuilder.endTag(null, "x");
			xmlBuilder.startTag(null, "y");
			xmlBuilder.text(Double.toString(locationY));
			xmlBuilder.endTag(null, "y");
			xmlBuilder.endTag(null, "location");
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
			// ByteArrayInputStream inStream = new ByteArrayInputStream(
			// xmlReturn.getBytes("UTF-8"));
			FileInputStream inStream = new FileInputStream(
					Environment.getExternalStorageDirectory() + "/shoplist.txt");
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(inStream, "UTF-8");
			int eventType = parser.getEventType();
			ShopData currentShop = null;
			GeoPoint location = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String tagName = parser.getName();
					if ("shop".equalsIgnoreCase(tagName)) {
						currentShop = new ShopData();
					} else if ("id".equalsIgnoreCase(tagName)) {
						currentShop.setShopID(parser.nextText().trim());
					} else if ("name".equalsIgnoreCase(tagName)) {
						currentShop.setShopName(parser.nextText().trim());
					} else if ("label".equalsIgnoreCase(tagName)) {
						currentShop.setShopLabel(parser.nextText().trim());
					} else if ("location".equalsIgnoreCase(tagName)) {
						location = new GeoPoint(0, 0);
					} else if ("x".equalsIgnoreCase(tagName)) {
						location.setLatitudeE6((int) (Float.parseFloat(parser
								.nextText().trim()) * 1e6));
					} else if ("y".equalsIgnoreCase(tagName)) {
						location.setLongitudeE6((int) (Float.parseFloat(parser
								.nextText().trim()) * 1e6));
					} else if ("intro".equalsIgnoreCase(tagName)) {
						currentShop.setShopIntro(parser.nextText().trim());
					} else if ("tel".equalsIgnoreCase(tagName)) {
						currentShop.setShopTel(parser.nextText().trim());
					} else if ("addr".equalsIgnoreCase(tagName)) {
						currentShop.setShopAddr(parser.nextText().trim());
					}
					break;
				case XmlPullParser.END_TAG:
					tagName = parser.getName();
					if ("shop".equalsIgnoreCase(tagName)) {
						shopList.add(currentShop);
					} else if ("location".equalsIgnoreCase(tagName)) {
						currentShop.setLocation(location);
					}
					break;
				default:
					break;
				}
				eventType = parser.next();
			}

			sink.onCommandExcuted(1, this);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			sink.onCommandExcuted(0, this);
		}

	}

	public String getDataReturn() {
		return dataReturn;
	}

	public ArrayList<ShopData> getShopList() {
		return shopList;
	}

}

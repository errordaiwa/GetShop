package cn.edu.ustc.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.util.Log;

public class HttpDownload {
	private static final String TAG = HttpDownload.class.getName();
	public static final String SERVER_IP = "114.214.167.95";
	public static final int SERVER_PORT = 8888;
	
	private Socket socket;
	public static final int TYPE_GET_SHOP = 0;

	public HttpDownload() {
		socket = new Socket();
	}

	public String download(String ip, int port, String request) {
		try {
			try {
				InetSocketAddress remotAddr = new InetSocketAddress(InetAddress.getByName(ip), port);
				socket.connect(remotAddr, 5000);
				InputStream inStream = socket.getInputStream();
				OutputStream outStream = socket.getOutputStream();
				outStream.write(request.getBytes("UTF-8"), 0,
						request.getBytes("UTF-8").length);
				socket.shutdownOutput();
				ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
//				byte[] bytes = new byte[2048];
				byte[] temp = new byte[2];
				int len = 0;
//				int index = 0;
				while (true) {
					len = inStream.read(temp);
					if (len < 0)
						break;
					byteBuffer.write(temp);
//					System.arraycopy(temp, 0, bytes, index, temp.length);
//					index += 2;
				}
				byteBuffer.flush();
				String response = new String(byteBuffer.toByteArray(), "UTF-8");
				byteBuffer.close();
				return response;
			} finally {
				socket.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;

	}

}
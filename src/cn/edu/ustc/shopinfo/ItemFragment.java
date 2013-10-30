package cn.edu.ustc.shopinfo;

import java.util.ArrayList;

import com.baidu.location.BDLocation;

import cn.edu.ustc.R;
import cn.edu.ustc.command.Command;
import cn.edu.ustc.command.CommandPool;
import cn.edu.ustc.command.CommandSink;
import cn.edu.ustc.command.GetItemCommand;
import cn.edu.ustc.command.ItemData;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ItemFragment extends Fragment {

	private final long aWeek = 7L * 24 * 60 * 60 * 1000;
	private String shopID;

	private ArrayList<ItemData> itemList = new ArrayList<ItemData>();

	private LinearLayout llImage;
	protected static final int SHOW_ITEMS = 0;

	private Handler uiHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_ITEMS:
				showItems();
				break;
			default:
				break;

			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shopID = ((ShopInfoActivity) getActivity()).getShopData().getShopID();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_item, container, false);
		llImage = (LinearLayout) rootView.findViewById(R.id.ll_image);
		getItems();
		return rootView;
	}

	private void getItems() {
		long lastModifierTime = System.currentTimeMillis() - aWeek;
		GetItemCommand cmd = new GetItemCommand(shopID, lastModifierTime, 20,
				new CommandSink() {

					@Override
					public void onCommandExcuted(int result, Command cmd,
							Object[]... args) {
						itemList = ((GetItemCommand) cmd).getItemList();
						Message msg = Message.obtain(uiHandler);
						msg.what = SHOW_ITEMS;
						msg.sendToTarget();
					}
				});
		CommandPool.getInstance().add(cmd);

	}

	protected void showItems() {
		for (ItemData item : itemList) {
			Bitmap image = item.getItemImage();
			ItemView itemView = new ItemView(getActivity());
			itemView.setImage(image);
			llImage.addView(itemView);
		}
	}

}

package cn.edu.ustc.shopinfo;

import java.util.ArrayList;

import cn.edu.ustc.R;
import cn.edu.ustc.command.Command;
import cn.edu.ustc.command.CommandPool;
import cn.edu.ustc.command.CommandSink;
import cn.edu.ustc.command.GetItemCommand;
import cn.edu.ustc.command.ItemData;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class ItemFragment extends Fragment {
	
	private final long aWeek = 7L*24*60*60*1000;
	private String shopID;
	
	private ArrayList<ItemData> itemList = new ArrayList<ItemData>();
	
	private LinearLayout llImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shopID = ((ShopInfoActivity)getActivity()).getShopID();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_item, container, false);
		getItems();
		return rootView;
	}

	private void getItems() {
		long lastModifierTime = System.currentTimeMillis() - aWeek;
		GetItemCommand cmd = new GetItemCommand(shopID, lastModifierTime, new CommandSink() {
			
			@Override
			public void onCommandExcuted(int result, Command cmd, Object[]... args) {
				itemList = ((GetItemCommand)cmd).getItemList();
				showItems();
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

package cn.edu.ustc.command;

import java.util.ArrayList;

public class GetItemCommand extends Command {
	private static final String TAG = GetItemCommand.class.getName();
	
	private String shopID;
	private long lastModifierTime;
	
	private CommandSink sink;
	
	private ArrayList<ItemData> itemList = new ArrayList<ItemData>();

	public GetItemCommand(String shopID, long lastModifierTime, CommandSink sink) {
		this.shopID = shopID;
		this.lastModifierTime = lastModifierTime;
		this.sink = sink;
	}

	@Override
	public void onPrepare() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onExcute() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onParse() {
		// TODO Auto-generated method stub

	}
	
	public ArrayList<ItemData> getItemList(){
		return itemList;
	}

}

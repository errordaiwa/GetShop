package cn.edu.ustc.shopinfo;

import cn.edu.ustc.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ShopInfoFragment extends Fragment {
	
	private String shopID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shopID = ((ShopInfoActivity)getActivity()).getShopID();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.page_shop_info, container, false);
		return rootView;
	}

}

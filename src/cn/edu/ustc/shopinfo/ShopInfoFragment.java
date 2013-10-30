package cn.edu.ustc.shopinfo;

import cn.edu.ustc.R;
import cn.edu.ustc.map.ShopData;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShopInfoFragment extends Fragment {

	private View rootView;
	private ShopData shopData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shopData = ((ShopInfoActivity) getActivity()).getShopData();

	}

	private void initViews() {
		TextView shopName = (TextView) rootView.findViewById(R.id.shop_name);
		shopName.setText(shopData.getShopName());
		TextView shopAddr = (TextView) rootView.findViewById(R.id.shop_addr);
		shopAddr.setText(shopData.getShopAddr());
		TextView shopTel = (TextView) rootView.findViewById(R.id.shop_tel);
		shopTel.setText(shopData.getShopTel());
		TextView shopIntro = (TextView) rootView.findViewById(R.id.shop_intro);
		shopIntro.setText(shopData.getShopIntro());
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.page_shop_info, container, false);
		initViews();
		return rootView;
	}

}

package cn.edu.ustc.shopinfo;

import cn.edu.ustc.R;
import static cn.edu.ustc.utils.Consts.*;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class ShopInfoActivity extends FragmentActivity {
	private static final String TAG = ShopInfoActivity.class.getName();

	private String shopID = null;

	private CollectionPagerAdapter mCollectionPagerAdapter;

	private ViewPager mViewPager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		shopID = getIntent().getStringExtra(SHOP_ID);
		initViews();
	}

	private void initViews() {
		setContentView(R.layout.activity_shop_info);
		mCollectionPagerAdapter = new CollectionPagerAdapter(
				getSupportFragmentManager());

		setupActionBar();

		final ActionBar actionBar = getActionBar();

		mViewPager = (ViewPager) findViewById(R.id.pager_shop_info);
		 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mViewPager.setAdapter(mCollectionPagerAdapter);
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						getActionBar().setSelectedNavigationItem(position);

					}
				});

		ActionBar.TabListener tabListener = new ActionBar.TabListener() {
			public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
				mViewPager.setCurrentItem(tab.getPosition());
			}

			public void onTabUnselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}

			public void onTabReselected(ActionBar.Tab tab,
					FragmentTransaction ft) {
			}
		};

		for (int i = 0; i < mCollectionPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mCollectionPagerAdapter.getPageTitle(i))
					.setTabListener(tabListener));
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getMenuInflater().inflate(R.menu.detail_page, menu);
	// return true;
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public String getShopID() {
		return shopID;
	}

	class CollectionPagerAdapter extends FragmentStatePagerAdapter {

		public CollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				ItemFragment itemFragment = new ItemFragment();
				Bundle args = new Bundle();
				args.putString(SHOP_ID, position + "");
				itemFragment.setArguments(args);
				return itemFragment;
			case 1:
				ShopInfoFragment shopInfoFragmen = new ShopInfoFragment();
				Bundle args1 = new Bundle();
				args1.putString(SHOP_ID, position + "");
				shopInfoFragmen.setArguments(args1);
				return shopInfoFragmen;
			default:
				break;
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "Items";
			case 1:
				return "ShopInfo";
			default:
				break;
			}
			return null;
		}

	}

}
package cn.edu.ustc.shopinfo;

import cn.edu.ustc.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemView extends FrameLayout {
	private View mRootView;
	ImageView ivItem;
	TextView tvItem;

	public ItemView(Context context) {
		super(context);
		mRootView = View.inflate(context, R.layout.item_view, this);
		ivItem = (ImageView) mRootView.findViewById(R.id.iv_item);
		tvItem = (TextView) mRootView.findViewById(R.id.tv_item);
	}

	public void setImage(int id) {
		ivItem.setImageResource(id);
	}
	
	public void setImage(Bitmap image){
		ivItem.setImageBitmap(image);
	}

	public void setText(String string) {
		tvItem.setText(string);
	}

}

package com.dd.meiying.abul.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dd.meiying.R;
import com.dd.meiying.abul.AlbumHelper;
import com.dd.meiying.abul.BitmapCache;
import com.dd.meiying.abul.BitmapCache.ImageCallback;
import com.dd.meiying.abul.ImageItem;
import com.dd.meiying.abul.view.ImageGridAdapter.ItemCallback;
import com.dd.meiying.constent.IntentExtra;
import com.dd.meiying.util.Utils;

public class ImageBucketGridActivity extends Activity {
	final String TAG = getClass().getSimpleName();
	
	public static final String EXTRA_IMAGE_LIST = "imagelist";
	public static final String EXTRA_BUCKET_NAME = "bucket name";
	public static final String EXTRA_IMAGE_CHOOSE_LIST = "choosed image list";
	public static final String ACTION_CHOOSED = "choosed image";
	
	public static final int MAX_MAX_IMG_NUM = 9;
	int MAX_IMG_NUM = 9;
	int imgChoosedNum = 0;
	
	Activity act;
	BitmapCache cache;
	
//	ArrayList<Entity> dataList;//用来装载数据源的列表
	List<ImageItem> dataList;
	String bucketName;
	GridView gridView;
	TextView imgCountTv;
	ImageGridAdapter adapter;//自定义的适配器
	AlbumHelper helper;
	
	//标题栏
	ImageView left;
	Button right;
	TextView titleTv;
	
	//已选中
	View choosedLayout;
	TextView chooseTv;
	HorizontalScrollView choosedScrollview;
	LinearLayout choosedLinear;
	List<String> choosedList = new ArrayList<String>();
	ItemCallback itemCallback = new ItemCallback() {
		@Override
		public void onChoose(int position, boolean choosed) {
//			if(choosed){
				if(choosedList.contains(dataList.get(position).imagePath)){
					Utils.displayToast(act, "已经选过");
				}else{
					doChooseOne(dataList.get(position));
				}
//			}
		}
	};
	ImageCallback imgCallback = new ImageCallback() {
		@Override
		public void imageLoad(ImageView imageView, Bitmap bitmap, Object... params) {
			if (imageView != null && bitmap != null) {
				String url = (String) params[0];
				if (url != null && url.equals((String) imageView.getTag())) {
					((ImageView) imageView).setImageBitmap(bitmap);
				}else{
					Log.e(TAG, "callback, bmp not match");
				}
			}else{
				Log.e(TAG, "callback, bmp null");
			}
		}
	};
	
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_iv:
				setResult(RESULT_CANCELED);
				back();
				break;
			case R.id.right_btn:
				doChooseConfirm();
				break;
			default:
				break;
			}
		}
	};
	
	public static Intent getStartIntent(Activity from){
		Intent intent = new Intent(from,ImageBucketGridActivity.class);
		return intent;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket_grid);
		act = this;
		cache = new BitmapCache();
		
		int imgNum = getIntent().getIntExtra(IntentExtra.EXTRA_IMAGE_MAX_NUM, MAX_MAX_IMG_NUM);
		imgChoosedNum = getIntent().getIntExtra(IntentExtra.EXTRA_IMAGE_CHOOSED_NUM, 0);
		MAX_IMG_NUM = imgNum>MAX_MAX_IMG_NUM?MAX_MAX_IMG_NUM:imgNum;
		
		
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		dataList = (List<ImageItem>) getIntent().getSerializableExtra(EXTRA_IMAGE_LIST);
		dataList = (dataList == null?new ArrayList<ImageItem>():dataList);
		bucketName = getIntent().getStringExtra(EXTRA_BUCKET_NAME);
		initView();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		back();
	}
	public boolean back() {
		finish();
//		overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
		overridePendingTransition(0, R.anim.slide_right_out);
		return true;
	}
	
	void doChooseOne(final ImageItem item){
		if(imgChoosedNum+choosedList.size()>=MAX_IMG_NUM){
			Utils.displayToast(act, "最多"+MAX_IMG_NUM+"张");
			return;
		}
		
		if(!BitmapCache.isBitmapExist(item.imagePath)){
			Utils.displayToast(act, "图片不存在，"+item.imagePath);
			return;
		}
		
		choosedList.add(item.imagePath);
		
		final View child = View.inflate(act, R.layout.item_bucket_item_choosed, null);
		choosedLinear.addView(child);
		ImageView iv = (ImageView) child.findViewById(R.id.image);
		iv.setTag(item.imagePath);
		cache.displayBmp(iv, item.thumbnailPath, item.imagePath, imgCallback);
		chooseTv.setText("已选中"+(imgChoosedNum+choosedList.size())+"张（最多"+MAX_IMG_NUM+"张）");
		child.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				choosedList.remove(item.imagePath);
				choosedLinear.removeView(child);
				chooseTv.setText("已选中"+(imgChoosedNum+choosedList.size())+"张（最多"+MAX_IMG_NUM+"张）");
			}
		});
		
		//滑动到当前插入的图片
		new CountDownTimer(200, 200) {
			@Override
			public void onTick(long millisUntilFinished) {}
			
			@Override
			public void onFinish() {				
				choosedScrollview.scrollBy(choosedLinear.getWidth()-choosedScrollview.getWidth()-choosedScrollview.getScrollX(), 0);
			}
		}.start();
	}
	
	void doChooseConfirm(){
		if(dataList == null || dataList.size()<=0){
			return ;
		}
//		for(int i=0; i<dataList.size(); ++i){
//			if(dataList.get(i).isSelected){
//				choosedList.add(dataList.get(i).imagePath);
//			}
//		}
		if(choosedList.size() >0){
			Intent intent = new Intent(ACTION_CHOOSED);
			intent.putExtra(EXTRA_IMAGE_CHOOSE_LIST, (Serializable)choosedList);
			sendBroadcast(intent);
			
			setResult(RESULT_OK, intent);
			
			back();
		}
	}
	
	/**
	 * 初始化view视图
	 */
	private void initView(){
		//标题栏
		left = (ImageView) findViewById(R.id.left_iv);
		titleTv = (TextView) findViewById(R.id.title_tv);
		right = (Button) findViewById(R.id.right_btn);
		left.setOnClickListener(clickListener);
		right.setOnClickListener(clickListener);
		titleTv.setText(TextUtils.isEmpty(bucketName)?"":bucketName);
		right.setVisibility(View.VISIBLE);
		
		//相册
		gridView = (GridView)findViewById(R.id.gridview);
		adapter = new ImageGridAdapter(ImageBucketGridActivity.this, dataList);
		adapter.setItemCallback(itemCallback);
		gridView.setAdapter(adapter);
		imgCountTv = (TextView) findViewById(R.id.img_count_tv);
		imgCountTv.setText(dataList.size()+"张照片");
		
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				/**
				 * 根据position参数，可以获得跟GridView的子View相绑定的实体类，然后根据它的isSelected状态，来判断是否显示选中效果。
				 * 至于选中效果的规则，下面适配器的代码中会有说明
				 */
//				if(dataList.get(position).isSelected()){
//					dataList.get(position).setSelected(false);
//				}else{
//					dataList.get(position).setSelected(true);
//				}
				/**
				 * 通知适配器，绑定的数据发生了改变，应当刷新视图
				 */
				adapter.notifyDataSetChanged();
			}
			
		});
		
		final ScrollView scrollView = (ScrollView) findViewById(R.id.vscroll);
		new CountDownTimer(200, 200) {
			@Override
			public void onTick(long millisUntilFinished) {}
			
			@Override
			public void onFinish() {
				scrollView.scrollTo(0, 0);
			}
		}.start();
		
		//已选中
		choosedLayout = findViewById(R.id.choosed_layout);
		chooseTv = (TextView) findViewById(R.id.choosed_tv);
		choosedScrollview = (HorizontalScrollView) findViewById(R.id.choosed_list_scroll);
		choosedLinear = (LinearLayout) findViewById(R.id.choosed_list);
		chooseTv.setText("已选中"+(imgChoosedNum+choosedList.size())+"张（最多"+MAX_IMG_NUM+"张）");
	}
	
	
}

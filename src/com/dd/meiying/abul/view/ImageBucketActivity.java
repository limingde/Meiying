package com.dd.meiying.abul.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.dd.meiying.R;
import com.dd.meiying.abul.AlbumHelper;
import com.dd.meiying.abul.ImageBucket;
import com.dd.meiying.constent.IntentExtra;

public class ImageBucketActivity extends Activity {

//	ArrayList<Entity> dataList;//用来装载数据源的列表
	List<ImageBucket> dataList = new ArrayList<ImageBucket>();
	GridView gridView;
	ImageBucketAdapter adapter;//自定义的适配器
	AlbumHelper helper;
	Activity act;
	
	int maxImageNum;
	int imgChoosedNum;
	
	BroadcastReceiver chooseImgReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			List<String> choosedList = (List<String>) intent.getSerializableExtra(ImageBucketGridActivity.EXTRA_IMAGE_CHOOSE_LIST);
			act.setResult(Activity.RESULT_OK, intent);
			back();
		}
	};
	
	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_iv:
				back();
				break;

			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_bucket);
		act = this;
		
		maxImageNum = getIntent().getIntExtra(IntentExtra.EXTRA_IMAGE_MAX_NUM, ImageBucketGridActivity.MAX_MAX_IMG_NUM);
		imgChoosedNum = getIntent().getIntExtra(IntentExtra.EXTRA_IMAGE_CHOOSED_NUM, 0);
		
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		initView();
		
		registerReceiver(chooseImgReceiver, new IntentFilter(ImageBucketGridActivity.ACTION_CHOOSED));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		List<ImageBucket> tmpList = helper.getImagesBucketList(true);
		if(tmpList != null){
			dataList.clear();
			dataList.addAll(tmpList);
			adapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(chooseImgReceiver);
		super.onDestroy();
	}

	/**
	 * 初始化view视图
	 */
	private void initView(){
		//标题栏
		View header = findViewById(R.id.header);
		View left = header.findViewById(R.id.left_iv);
		left.setOnClickListener(clickListener);
		TextView title = (TextView) header.findViewById(R.id.title_tv);
		title.setText("选择相册");
		View right = header.findViewById(R.id.right_btn);
		right.setVisibility(View.GONE);
		
		gridView = (GridView)findViewById(R.id.gridview);
		adapter = new ImageBucketAdapter(ImageBucketActivity.this, dataList);
		gridView.setAdapter(adapter);
		
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
//				adapter.notifyDataSetChanged();
				Intent intent = new Intent(ImageBucketActivity.this, ImageBucketGridActivity.class);
				intent.putExtra(ImageBucketGridActivity.EXTRA_IMAGE_LIST, (Serializable)dataList.get(position).imageList);
				intent.putExtra(ImageBucketGridActivity.EXTRA_BUCKET_NAME, dataList.get(position).bucketName);
				intent.putExtra(IntentExtra.EXTRA_IMAGE_MAX_NUM, maxImageNum);
				intent.putExtra(IntentExtra.EXTRA_IMAGE_CHOOSED_NUM, imgChoosedNum);
				startActivity(intent);
			}
			
		});
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
	
}

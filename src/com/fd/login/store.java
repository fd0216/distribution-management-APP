package com.fd.login;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.fd.login.R;
import com.fd.sql.Util;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class store extends Activity implements OnClickListener {

	Button btn_store_ok, btn_get_photo, btn_store_to_login,
			btn_get_photo_local;
	EditText edit_deliver_man, edit_phone, edit_shop_no, edit_weight,
			edit_section_number, edit_length, edit_width, edit_height,
			edit_volume, edit_numbers, edit_numbers_one, edit_warehouse,
			edit_count, edit_value, edit_store_datetime;
	private Util dbUtil;
	private Spinner spinner, spinner_wareType, spinner_wareHouse;
	private List<String> list_CustormerID, list_WareName, list_shipprice,
			list_wareTypeID, list_wareUnit, list_InsuranceRatio,
			list_Customer_LoginID, list_HouseNum, list_HouseGUID;
	ArrayList<String> brrayList;
	List<HashMap<String, String>> list_wareType =  new ArrayList<HashMap<String, String>>() ;
	private String employeeId = "", image_path = "";
	private ImageView imageView; // 图片显示
	ProgressDialog progressDialog;
	private static final int EVENT_TIME_TO_CHANGE_IMAGE = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.store);
		btn_store_ok = (Button) findViewById(R.id.store_OK);
		btn_store_ok.setOnClickListener(this);
		btn_get_photo = (Button) findViewById(R.id.Photo_Get);
		btn_get_photo.setOnClickListener(this);
		btn_get_photo_local = (Button) findViewById(R.id.Photo_Get_Local);
		btn_get_photo_local.setOnClickListener(this);
		btn_store_to_login = (Button) findViewById(R.id.store_to_login);
		btn_store_to_login.setOnClickListener(this);
		edit_deliver_man = (EditText) findViewById(R.id.Deliver_man_edit);
		edit_phone = (EditText) findViewById(R.id.Deliver_phone_edit);
		edit_shop_no = (EditText) findViewById(R.id.Shop_no_edit);

		edit_section_number = (EditText) findViewById(R.id.product_Section_Number_edit);
		edit_weight = (EditText) findViewById(R.id.prduct_weight_edit);
		edit_length = (EditText) findViewById(R.id.product_length_edit);
		edit_width = (EditText) findViewById(R.id.prduct_Width_edit);
		edit_height = (EditText) findViewById(R.id.prduct_height_edit);
		edit_volume = (EditText) findViewById(R.id.product_volume_edit);
		edit_numbers = (EditText) findViewById(R.id.prduct_nums_edit);

		edit_numbers_one = (EditText) findViewById(R.id.product_weight_one_edit);

		edit_count = (EditText) findViewById(R.id.prduct_nums_count_edit);
		edit_value = (EditText) findViewById(R.id.product_value_edit);

		edit_store_datetime = (EditText) findViewById(R.id.product_store_date_edit);
		edit_store_datetime.setFocusable(false);
		edit_store_datetime.setOnClickListener(this);
		Calendar ca = Calendar.getInstance();
		edit_store_datetime.setText(String.format("%d-%d-%d",ca.get(Calendar.YEAR),
				ca.get(Calendar.MONTH)+1,ca.get(Calendar.DAY_OF_MONTH)));
		spinner = (Spinner) findViewById(R.id.spinner_customer);
		spinner_wareType = (Spinner) findViewById(R.id.spinner_ware_type);
		spinner_wareHouse = (Spinner) findViewById(R.id.spinner_ware_house);
		imageView = (ImageView) findViewById(R.id.ware_image);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		employeeId = bundle.getString("employeeID");
		edit_numbers
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 此处为得到焦点时的处理内容
						} else {
							// 此处为失去焦点时的处理内容
							GetCostToCostEdit();
						}
					}
				});
		edit_numbers_one
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 此处为得到焦点时的处理内容
						} else {
							// 此处为失去焦点时的处理内容
							GetNumsCountToEditView();
						}
					}
				});
		edit_volume
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 此处为得到焦点时的处理内容
							GetVolume();
						} else {
							// 此处为失去焦点时的处理内容
						}
					}
				});
		dbUtil = new Util();
		CustomerSpinner();
		EmployeeSpinner();
	//	WareTypeSpinner();
		WareHouseSpinner();
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EVENT_TIME_TO_CHANGE_IMAGE:
				login.closeProgressDialog();
				break;
			}
		}
	};

	private void initViews() {
		Bitmap bitmap = getLoacalBitmap("/sdcard/mhc.jpg");
		imageView.setImageBitmap(bitmap); // 设置Bitmap
	}

	/**
	 * 根据文件路径获取所需要文件，并将该文件封装成Bitmap对象返回
	 * 
	 * @param fileUrl
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String fileUrl) {
		try {
			FileInputStream fis = new FileInputStream(fileUrl);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void GetCostToCostEdit() { /*
										 * long id =
										 * spinner_wareType.getSelectedItemId();
										 * int nums
										 * =Integer.parseInt(edit_numbers
										 * .getText().toString()); double price
										 * =Double.valueOf(
										 * list_shipprice.get((int)(id)));
										 * double cost = price * nums; //
										 * edit_costs
										 * .setText(Double.toString(cost));
										 */
	}

	private void GetVolume() {
		String strLen = edit_length.getText().toString();
		String strwidth = edit_width.getText().toString();
		String strheight = edit_height.getText().toString();
		if (strLen.length() == 0 || strwidth.length() == 0
				|| strheight.length() == 0) {
			Toast.makeText(store.this, "长度、宽度、高度不能为空！", Toast.LENGTH_LONG)
					.show();
		} else {
			int len = Integer.parseInt(strLen);
			int width = Integer.parseInt(strwidth);
			int height = Integer.parseInt(strheight);
			double volume = len * width * height * 1.0 / 1000000;
			//edit_volume.setText(  Double.toString(volume));
			edit_volume.setText(new DecimalFormat("0.000000").format(volume));			
		}
	}
	private void GetNumsCountToEditView() {
		String strnums = edit_numbers.getText().toString();
		String strnums_one = edit_numbers_one.getText().toString();
		if (strnums.length() == 0 || strnums_one.length() == 0) {
			Toast.makeText(store.this, "件数、每件数量不能为空！", Toast.LENGTH_LONG)
					.show();
		} else {
			int nums = Integer.parseInt(strnums); // 件数
			int nums_one = Integer.parseInt(strnums_one);// 每件数量
			int nums_total = nums * nums_one; // 总数 = 件数 * 每件数量
			edit_count.setText(Integer.toString(nums_total));// 总数
		}
	}

	private void CustomerSpinner() {
		Thread thr_customer = new Thread(getCustomerTask);
		thr_customer.start();
		try {
			thr_customer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int len = list_Customer_LoginID.size();
		final String arr[] = new String[len];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list_Customer_LoginID.get(i);
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr);
		spinner.setAdapter(arrayAdapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {			
				String loginId =  spinner.getSelectedItem().toString();
				String guid="";
				List<String> list_product =  new ArrayList<String>(); 
				for(int i =0;i<list_Customer_LoginID.size();i++)
				{
					if(list_Customer_LoginID.get(i).equals(loginId))
					{
						guid = list_CustormerID.get(i).toString();
						break;
					}
				}
				for(int j =0;j<list_wareType.size();j++)
				{
					if(list_wareType.get(j).get("CustomerGUID").equals(guid))
					{
						list_product.add(list_wareType.get(j).get("Name").toString());				
					}					
				}
               int pro_len = list_product.size();
             
               String[] arrpro=new String[pro_len];
				for (int i = 0; i < list_product.size(); i++) {
					arrpro[i] = list_product.get(i).toString();
				}
				
				ArrayAdapter<String> proAdapter = new ArrayAdapter<String>(store.this,
						android.R.layout.simple_spinner_item, arrpro);
				spinner_wareType.setAdapter(proAdapter);
             
			
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {			
			}
		});
	}
	private void EmployeeSpinner() {
		Thread thr_store = new Thread(getWareTypesTask);
		thr_store.start();
		try {
			thr_store.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void WareTypeSpinner() {
		int len = list_WareName.size();
		final String arr[] = new String[len];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list_WareName.get(i);
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr);
		spinner_wareType.setAdapter(arrayAdapter);
		spinner_wareType
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Spinner spinner = (Spinner) parent;
						((TextView) parent.getChildAt(0))
								.setTextColor(Color.BLACK);
					}
					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
	}

	private void WareHouseSpinner() {
		Thread thr_customer = new Thread(getWareHouseTask);
		thr_customer.start();
		try {
			thr_customer.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int len = list_HouseNum.size();
		final String arr[] = new String[len];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list_HouseNum.get(i);
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr);
		spinner_wareHouse.setAdapter(arrayAdapter);

		spinner_wareHouse
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						Spinner spinner = (Spinner) parent;
						((TextView) parent.getChildAt(0))
								.setTextColor(Color.BLACK);
						// Toast.makeText(getApplicationContext(),
						// "xxxx"+spinner.getItemAtPosition(position),
						// Toast.LENGTH_LONG).show();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// Toast.makeText(getApplicationContext(), "没有改变的处理",
						// Toast.LENGTH_LONG).show();
					}
				});
		Message message = mHandler.obtainMessage(EVENT_TIME_TO_CHANGE_IMAGE);
		mHandler.sendMessage(message);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.store_OK:
			Store();
			break;
		case R.id.product_store_date_edit:
			DateShow();
			break;
		case R.id.Photo_Get:
			image_path = "";
			startActivity(new Intent(this, CameraActivity.class));
			break;
		case R.id.store_to_login:
			Intent intent = new Intent();
			intent.setClass(store.this, GuideActivity.class);
			intent.putExtra("employeeID", employeeId);
			startActivityForResult(intent, 3);
		//	startActivity(new Intent(this, GuideActivity.class));
			break;
		case R.id.Photo_Get_Local:
			Photo_Get_Local();
			break;
		default:
			break;
		}
	}

	private void Photo_Get_Local() {
		Intent intent = new Intent();
		/* 开启Pictures画面Type设定为image */
		intent.setType("image/*");
		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
		/* 取得相片后返回本画面 */
		startActivityForResult(intent, 1);
	}

	// 获取本地图片
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri uri = data.getData();
			String img_url = uri.getPath();// 这是本机的图片路径
			ContentResolver cr = this.getContentResolver();
			try {
				image_path = img_url;
				Bitmap bitmap = BitmapFactory.decodeStream(cr
						.openInputStream(uri));
				/* 将Bitmap设定到ImageView */
				imageView.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				Log.e("Exception", e.getMessage(), e);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void DateShow() {		
		Calendar calendar = Calendar.getInstance();	
		new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
				edit_store_datetime.setText(String.format("%d-%d-%d", i,
						i1 + 1, i2));
			}
		}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar
				.get(Calendar.DAY_OF_MONTH)).show();							
	}

	/*
	private void Store() {
		
		if( spinner_wareType.getChildCount()>0)
		{
		if (edit_section_number.getText().toString().length() > 0
				&& edit_store_datetime.getText().toString().length() > 0
				) // 款号、入库时间必须填写
		{
			brrayList = new ArrayList<String>();
			brrayList.add(UUID.randomUUID().toString());
			brrayList.add(spinner_wareType.getSelectedItem().toString());// 货物名称
			brrayList.add(edit_section_number.getText().toString());// 款号
			brrayList.add(list_wareTypeID.get((int) spinner_wareType
					.getSelectedItemId()));// 货物类型
			brrayList.add(edit_value.getText().toString()); // 货值
			brrayList.add(edit_weight.getText().toString());// 重量
			brrayList.add(edit_length.getText().toString());
			brrayList.add(edit_height.getText().toString());
			brrayList.add(edit_width.getText().toString());
			// 长、宽、高
			brrayList.add(edit_volume.getText().toString());// 体积
			brrayList.add(edit_numbers.getText().toString());// 件数,piece
			brrayList.add(edit_count.getText().toString());// 总数,totalnum
			brrayList.add(edit_numbers_one.getText().toString());// 每件数量
			double shipPrice = 0;// 运费			
			if(list_wareUnit.get((int) spinner_wareType.getSelectedItemId()).equals("kg") )
				shipPrice = Double.parseDouble(edit_numbers.getText().toString()) * 
				           Double.parseDouble(list_shipprice.get((int) spinner_wareType
						  .getSelectedItemId())) *Double.parseDouble(edit_weight.getText().toString());
			if(list_wareUnit.get((int) spinner_wareType.getSelectedItemId()).equals("cbm"))	
				shipPrice = Double.parseDouble(edit_numbers.getText().toString()) * 
		           Double.parseDouble(list_shipprice.get((int) spinner_wareType
				  .getSelectedItemId())) *Double.parseDouble(edit_volume.getText().toString());
			brrayList.add(""+shipPrice);// 货运单价
			brrayList.add(list_wareUnit.get((int) spinner_wareType
					.getSelectedItemId()));// 单位
			// brrayList.add(edit_photo.getText().toString());
			brrayList.add(list_InsuranceRatio.get((int) spinner_wareType
					.getSelectedItemId()));// 保费比例
			brrayList.add(list_Customer_LoginID.get((int)spinner
					.getSelectedItemId()));// 用户登录ID
			brrayList.add(list_HouseGUID.get((int) spinner_wareHouse
					.getSelectedItemId()));// 所在仓库号
			if (employeeId.length() > 0)
				brrayList.add(employeeId);// 登记员工号
			else
				brrayList.add("000");// 错误的 登记员工号
			brrayList.add("入库");// 状态
			String t1= edit_count.getText().toString();
			String t2= edit_value.getText().toString();
			String t3= list_InsuranceRatio.get((int) spinner_wareType
					.getSelectedItemId());
			double InsurancePrice = Double.parseDouble(list_InsuranceRatio.get((int) spinner_wareType
					.getSelectedItemId())) * Double.parseDouble(edit_value.getText().toString()) *
					Double.parseDouble(edit_count.getText().toString());//保额
			brrayList.add(""+InsurancePrice);	
			double totalPrice = InsurancePrice+shipPrice;//总额 = 保额 + 运费
			brrayList.add(""+totalPrice);
			// 送货人、店铺号、送货人电话
			brrayList.add(edit_deliver_man.getText().toString());
			brrayList.add(edit_shop_no.getText().toString());
			brrayList.add(edit_phone.getText().toString());
			brrayList.add(edit_store_datetime.getText().toString()); // 入库时间

			Thread thr_store = new Thread(storeTask);
			thr_store.start();
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("数据上传");
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		} 
		else {
			Toast.makeText(store.this, "款号、入库时间必须填写!", Toast.LENGTH_LONG)
					.show();
		   }
		}
		else
		{	      	   
         	Toast.makeText(store.this, "没有找到该用户的相关货物信息!", Toast.LENGTH_LONG).show(); 
		}
	}
	*/
private void Store() {
		
	if( spinner_wareType.getChildCount()>0)
	{
		if (edit_section_number.getText().toString().length() > 0
				&& edit_store_datetime.getText().toString().length() > 0
				) // 款号、入库时间必须填写
	     	{
			brrayList = new ArrayList<String>();	
			brrayList.add(UUID.randomUUID().toString());//第一个UUID是本次插入信息的GUID
			
			brrayList.add(spinner_wareType.getSelectedItem().toString());// 货物名称
			brrayList.add(edit_section_number.getText().toString());// 款号
			brrayList.add(list_wareTypeID.get((int) spinner_wareType
					.getSelectedItemId()));// 货物类型
			brrayList.add(edit_value.getText().toString()); // 货值
			brrayList.add(edit_weight.getText().toString());// 重量
			brrayList.add(edit_length.getText().toString());
			brrayList.add(edit_height.getText().toString());
			brrayList.add(edit_width.getText().toString());
			// 长、宽、高
			brrayList.add(edit_volume.getText().toString());// 体积
			brrayList.add(edit_numbers.getText().toString());// 件数,piece
			brrayList.add(edit_count.getText().toString());// 总数,totalnum
			brrayList.add(edit_numbers_one.getText().toString());// 每件数量
			double shipPrice = 0;// 运费			
			if(list_wareUnit.get((int) spinner_wareType.getSelectedItemId()).equals("kg") )
				shipPrice = Double.parseDouble(edit_numbers.getText().toString()) * 
				           Double.parseDouble(list_shipprice.get((int) spinner_wareType
						  .getSelectedItemId())) *Double.parseDouble(edit_weight.getText().toString());
			if(list_wareUnit.get((int) spinner_wareType.getSelectedItemId()).equals("cbm"))	
				shipPrice = Double.parseDouble(edit_numbers.getText().toString()) * 
		           Double.parseDouble(list_shipprice.get((int) spinner_wareType
				  .getSelectedItemId())) *Double.parseDouble(edit_volume.getText().toString());
			brrayList.add(""+shipPrice);// 货运单价
			brrayList.add(list_wareUnit.get((int) spinner_wareType
					.getSelectedItemId()));// 单位
			// brrayList.add(edit_photo.getText().toString());
			brrayList.add(list_InsuranceRatio.get((int) spinner_wareType
					.getSelectedItemId()));// 保费比例
			brrayList.add(list_Customer_LoginID.get((int)spinner
					.getSelectedItemId()));// 用户登录ID
			brrayList.add(list_HouseGUID.get((int) spinner_wareHouse
					.getSelectedItemId()));// 所在仓库号
			if (employeeId.length() > 0)
				brrayList.add(employeeId);// 登记员工号
			else
				brrayList.add("000");// 错误的 登记员工号
			brrayList.add("入库");// 状态
			String t1= edit_count.getText().toString();
			String t2= edit_value.getText().toString();
			String t3= list_InsuranceRatio.get((int) spinner_wareType
					.getSelectedItemId());
			double InsurancePrice = Double.parseDouble(list_InsuranceRatio.get((int) spinner_wareType
					.getSelectedItemId())) * Double.parseDouble(edit_value.getText().toString()) *
					Double.parseDouble(edit_count.getText().toString());//保额
			brrayList.add(""+InsurancePrice);	
			double totalPrice = InsurancePrice+shipPrice;//总额 = 保额 + 运费
			brrayList.add(""+totalPrice);
			// 送货人、店铺号、送货人电话
			brrayList.add(edit_deliver_man.getText().toString());
			brrayList.add(edit_shop_no.getText().toString());
			brrayList.add(edit_phone.getText().toString());
			brrayList.add(UUID.randomUUID().toString());//第二个UUID是图片的GUID	
			brrayList.add(edit_store_datetime.getText().toString()); // 入库时间

			Thread thr_store = new Thread(storeTask);
			thr_store.start();
			progressDialog = new ProgressDialog(this);
			progressDialog.setTitle("数据上传");
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.show();
		} 
		else {
			Toast.makeText(store.this, "款号、入库时间必须填写!", Toast.LENGTH_LONG)
					.show();
		   }
		}
		else
		{	      	   
         	Toast.makeText(store.this, "没有找到该用户的相关货物信息!", Toast.LENGTH_LONG).show(); 
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			int tta = msg.what;
			String val = data.getString("wareInsert");
			if (val != null && val.endsWith("true")) {
				Toast.makeText(store.this, "信息添加成功！", Toast.LENGTH_LONG).show();
			} else if (val != null && val.endsWith("false")) {
				Toast.makeText(store.this, "信息添加失败！", Toast.LENGTH_LONG).show();
			}
			if (progressDialog != null && progressDialog.isShowing())
				progressDialog.dismiss();
			// TODO
			// UI界面的更新等相关操作
		}
	};

	Runnable getWareTypesTask = new Runnable() {
		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			list = dbUtil.getWareTypes();
			list_wareType = list;
			//list_CustormerID = new ArrayList<String>();
			list_WareName = new ArrayList<String>();
			list_shipprice = new ArrayList<String>();
			list_wareTypeID = new ArrayList<String>();
			list_wareUnit = new ArrayList<String>();
			list_InsuranceRatio = new ArrayList<String>();
			for (int i = 1; i < list.size(); i++) {
				HashMap hashMapTemp = list.get(i);
				//list_CustormerID.add(hashMapTemp.get("CustomerGUID").toString());
				list_WareName.add(hashMapTemp.get("Name").toString());
				list_shipprice.add(hashMapTemp.get("ShipPrice").toString());
				list_wareTypeID.add(hashMapTemp.get("GUID").toString());
				list_wareUnit.add(hashMapTemp.get("Unit").toString());
				list_InsuranceRatio.add(hashMapTemp.get("InsuranceRatio").toString());
			}
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("value", "请求结果");
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};
	Runnable getWareHouseTask = new Runnable() {
		@Override
		public void run() {
			// TODO
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			list = dbUtil.getWareHouse();
			list_HouseNum = new ArrayList<String>();
			list_HouseGUID = new ArrayList<String>();
			for (int i = 1; i < list.size(); i++) {
				HashMap hashMapTemp = list.get(i);
				list_HouseGUID.add(hashMapTemp.get("GUID").toString());
				list_HouseNum.add(hashMapTemp.get("Name").toString());
			}
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("value", "请求结果");

			msg.setData(data);
			handler.sendMessage(msg);
		}
	};
	Runnable getCustomerTask = new Runnable() {
		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			list = dbUtil.getCustomerInfo();
			list_Customer_LoginID = new ArrayList<String>();
			list_CustormerID = new ArrayList<String>();
			for (int i = 1; i < list.size(); i++) {
				HashMap hashMapTemp = list.get(i);
				list_Customer_LoginID.add(hashMapTemp.get("LoginID").toString());
				list_CustormerID.add(hashMapTemp.get("GUID").toString());
			}
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("value", "请求结果");
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};
	Runnable storeTask = new Runnable() {
		@Override
		public void run() {
			String resStr = dbUtil.insertCargoInfo(brrayList, image_path);
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("wareInsert", resStr);
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};
}

package com.fd.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.fd.sql.Util;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

public class laod extends Activity implements OnClickListener {
	Button btn_load, btn_load_to_login;
	ListView ware_listView;
	private List<HashMap<String, Object>> list_ware;
	List<HashMap<String, String>> list_TruckInfo, list_WareInfo;
	List<String> list_wareGuid, list_customerId, list_WareName, list_WareNum,
			list_TruckId;
	ArrayList<String> listStr = null, list_WareGUID_ToUpdate;
	private MyAdapter adapter;
	private Util dbUtil;
	private Spinner spinner_Truck;
	private String employeeId = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wareload);
		btn_load = (Button) findViewById(R.id.load_OK);
		btn_load.setOnClickListener(this);
		btn_load_to_login = (Button) findViewById(R.id.load_to_login);
		btn_load_to_login.setOnClickListener(this);
		ware_listView = (ListView) findViewById(R.id.ware_listView);
		spinner_Truck = (Spinner) findViewById(R.id.spinner_truck);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		employeeId = bundle.getString("employeeID");
		dbUtil = new Util();
		TruckInfoGet();
		WareInfoGet();	
	}
	// 显示带有checkbox的listview
	public void showCheckBoxListView() {
		Log.i("debug001", "showCheckBoxListView");
		list_WareGUID_ToUpdate = new ArrayList<String>();
		list_ware = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < list_wareGuid.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("item_loginId", list_customerId.get(i));
			map.put("item_wareName", list_WareName.get(i));
			map.put("item_wareNum", list_WareNum.get(i));
			map.put("item_cb", false);
			list_ware.add(map);
			adapter = new MyAdapter(this, list_ware, R.layout.listview,
					new String[] { "item_loginId", "item_wareName",
							"item_wareNum", "item_cb" }, new int[] {
							R.id.customer_loginId, R.id.ware_name,
							R.id.ware_nums, R.id.cb_selected });
			ware_listView.setAdapter(adapter);
			listStr = new ArrayList<String>();
			ware_listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long arg3) {
					com.fd.login.MyAdapter.ViewHolder holder = (com.fd.login.MyAdapter.ViewHolder) view
							.getTag();
					holder.cb.toggle();// 在每次获取点击的item时改变checkbox的状态
					MyAdapter.isSelected.put(position, holder.cb.isChecked()); // 同时修改map的值保存状态
					if (holder.cb.isChecked() == true) {
						list_WareGUID_ToUpdate.add(list_wareGuid.get(position));
					} else {
						list_WareGUID_ToUpdate.remove(list_wareGuid
								.get(position));
					}
				}
			});
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.load_OK:
			Load();
			break;
		case R.id.ware_listView:
			ListView_Load();
			break;
		case R.id.load_to_login:
			//startActivity(new Intent(this, GuideActivity.class));
			
			Intent intent = new Intent();
			intent.setClass(laod.this, GuideActivity.class);
			intent.putExtra("employeeID", employeeId);
			startActivityForResult(intent, 3);
			break;
		default:
			break;
		}
	}

	private void ListView_Load() {

	}

	private void Load() {
		// 装车
		Thread thr_load = new Thread(thrLoadTask);
		thr_load.start();
		try {
			thr_load.join(2000);
			Toast.makeText(this, "装车成功！", Toast.LENGTH_LONG).show();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, "装车失败！", Toast.LENGTH_LONG).show();
		}

	}

	private void TruckInfoGet() {
		list_TruckInfo = new ArrayList<HashMap<String, String>>();
		Thread thrTruck = new Thread(truckInfoGetTask);
		thrTruck.start();
		
		try {
			thrTruck.join(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TruckSpinner();
	}

	private void WareInfoGet() {
		list_WareInfo = new ArrayList<HashMap<String, String>>();
		Thread thrTruck = new Thread(wareInfoGetTask);
		thrTruck.start();
		try {
			thrTruck.join();		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showCheckBoxListView();
	}

	private void TruckSpinner() {
		int len = list_TruckId.size();
		final String arr[] = new String[len];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list_TruckId.get(i).toString();
		}
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, arr);
		spinner_Truck.setAdapter(arrayAdapter);
	}

	Runnable thrLoadTask = new Runnable() {
		@Override
		public void run() {
			for (int i = 0; i < list_WareGUID_ToUpdate.size(); i++) {
				dbUtil.UpdateWareStatue(list_WareGUID_ToUpdate.get(i));
			}
		}
	};
	Runnable truckInfoGetTask = new Runnable() {
		@Override
		public void run() {
			list_TruckId = new ArrayList<String>();
			list_TruckInfo = dbUtil.getTruckInfo();
			for (int i = 1; i < list_TruckInfo.size(); i+=2) {
				HashMap hashMapTemp = list_TruckInfo.get(i);
				list_TruckId.add(hashMapTemp.get("License").toString());
			}
		}
	};
	Runnable wareInfoGetTask = new Runnable() {
		@Override
		public void run() {
			list_wareGuid = new ArrayList<String>();
			list_customerId = new ArrayList<String>();
			list_WareName = new ArrayList<String>();
			list_WareNum = new ArrayList<String>();
			int count = 0;
			list_WareInfo = dbUtil.getWareInfo();
			int recLen = list_WareInfo.size();
			/*
			while (count < 10 || recLen == 1) 
			{
				list_WareInfo = dbUtil.getWareInfo();
			   recLen = list_WareInfo.size();
			   count++;
				
			}
				*/
				for (int i = 1; i < list_WareInfo.size(); i++) {
					HashMap hashMapTemp = list_WareInfo.get(i);
					list_wareGuid.add(hashMapTemp.get("GUID").toString());
					list_customerId.add(hashMapTemp.get("CustomerID")
							.toString());
					list_WareName.add(hashMapTemp.get("Name").toString());
					list_WareNum.add(hashMapTemp.get("Num").toString());
				}
				
				Log.i("debug001", "wareinfo_complete"+String.valueOf(recLen));
		}
	};
}

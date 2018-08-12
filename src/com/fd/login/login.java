package com.fd.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.fd.sql.*;

public class login extends Activity implements OnClickListener {
	Button btn_load, btn_store, btn_login, btn_quit;
	EditText et_user, et_pwd;
	TextView tv_title;
	boolean Is_login = false;
	private Util dbUtil;
	ArrayList<String> list_test;
	List<HashMap<String, String>> list_employee;
	private String employeeID = "";
	private static ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	//	btn_load = (Button) findViewById(R.id.load);
	//	btn_store = (Button) findViewById(R.id.storage);
		btn_login = (Button) findViewById(R.id.login);
		btn_quit = (Button) findViewById(R.id.quit);
		et_user = (EditText) findViewById(R.id.accountEdittext);
		et_pwd = (EditText) findViewById(R.id.pwdEdittext);
		tv_title = (TextView)findViewById(R.id.tv_friend_title);
	//	btn_load.setOnClickListener(this);
	//	btn_store.setOnClickListener(this);
		btn_login.setOnClickListener(this);
		btn_quit.setOnClickListener(this);
		dbUtil = new Util();

	}

	public static void closeProgressDialog() {
		dialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		/*
		case R.id.load:
			LoginToLoad();
			break;
		case R.id.storage:
			LoginToStore();
			break;
			*/
		case R.id.login:
			try {
				Login();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.quit:
			Quit();
			break;
		default:
			break;
		}
	}

	private void LoginToLoad() {
		if(Is_login)
		{
		  Intent intent_load = new Intent(this, laod.class);
		  startActivity(intent_load);
		}
		else
		{
			Toast.makeText(this, "未登录！", Toast.LENGTH_LONG).show();
		}
	}

	private void LoginToStore() {
		if(Is_login)
		{
		Handler handler = new Handler();
		// 在run里面写了跳转activity
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(login.this, store.class);
				intent.putExtra("employeeID", employeeID);
				startActivityForResult(intent, 3);
			}
		};
		handler.post(runnable);
		dialog = ProgressDialog.show(this, "请稍等", "正在跳转", true, true);
		// Intent intent_load = new Intent(this,store.class);
		// intent_load.putExtra("employeeID", employeeID);
		// startActivity(intent_load);
		}
		else
		{
			Toast.makeText(this, "未登录！", Toast.LENGTH_LONG).show();
		}
	}
	private void LoginToGuide() {
		if(Is_login)
		{
		Handler handler = new Handler();
		// 在run里面写了跳转activity
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(login.this, GuideActivity.class);
				intent.putExtra("employeeID", employeeID);
				startActivityForResult(intent, 3);
			}
		};
		handler.post(runnable);
		dialog = ProgressDialog.show(this, "请稍等", "正在跳转", true, true);
		// Intent intent_load = new Intent(this,store.class);
		// intent_load.putExtra("employeeID", employeeID);
		// startActivity(intent_load);
		}
		else
		{
			Toast.makeText(this, "未登录！", Toast.LENGTH_LONG).show();
		}
	}
	private void Quit() {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
			Intent startMain = new Intent(Intent.ACTION_MAIN);
			startMain.addCategory(Intent.CATEGORY_HOME);
			startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(startMain);
			
			System.exit(0);
		} else {// android2.1
			ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
			am.restartPackage(getPackageName());
		}
	}
	private void Test() {
		list_test = new ArrayList<String>();
		for (int i = 0; i < 19; i++) {
			list_test.add(String.valueOf(i + 10));
		}
	}

	private void Login() throws InterruptedException {
		String strUser = et_user.getText().toString();
		String strPwd = et_pwd.getText().toString();
		if ((!"".equals(strUser)) && (!"".equals(strPwd))) {
			Thread thrLogin = new Thread(networkTask);
			thrLogin.start();
			thrLogin.join();
			int len = list_employee.size();
			for (int i = 0; i < len; i++) {
				HashMap hashMapTemp = list_employee.get(i);
				if (hashMapTemp.containsValue(strPwd)
						&& hashMapTemp.containsValue(strPwd)) {
					Is_login = true;
					employeeID = hashMapTemp.get("EmployeeID").toString();							
					tv_title.setText("已登录");
					LoginToGuide();
					return;
				}
			}
			Is_login = false;
			Toast.makeText(this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
		} else {
			Is_login = false;
			Toast.makeText(this, "请填写用户名和密码！", Toast.LENGTH_LONG).show();
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("value");
			// TODO
			// UI界面的更新等相关操作
		}
	};
	Runnable networkTask = new Runnable() {
		@Override
		public void run() {
			// TODO
			// 在这里进行 http request.网络请求相关操作
			list_employee = new ArrayList<HashMap<String, String>>();
			list_employee = dbUtil.getEmployeeInfo();
			Message msg = new Message();
			Bundle data = new Bundle();
			data.putString("value", "请求结果");
			msg.setData(data);
			handler.sendMessage(msg);
		}
	};

}

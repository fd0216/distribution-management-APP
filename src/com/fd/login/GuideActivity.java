package com.fd.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GuideActivity extends Activity implements OnClickListener  {

	Button btn_load, btn_store,  btn_quit;
	private static ProgressDialog dialog;
	private String employeeId = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		btn_load = (Button) findViewById(R.id.guide_load);
		btn_store = (Button) findViewById(R.id.guide_store);
		btn_quit = (Button) findViewById(R.id.guide_quit);
		btn_load.setOnClickListener(this);
		btn_store.setOnClickListener(this);	
		btn_quit.setOnClickListener(this);
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		employeeId = bundle.getString("employeeID");
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch (id) {
		case R.id.guide_load:
			GuideToLoad();
			break;
		case R.id.guide_store:
			GuideToStore();
			break;	
		case R.id.guide_quit:
			GuideQuit();
			break;
		default:
			break;
		}
	}
	private void GuideToStore()
	{
		Handler handler = new Handler();
		// 在run里面写了跳转activity
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, store.class);
				intent.putExtra("employeeID", employeeId);
				startActivityForResult(intent, 3);
			}
		};
		handler.post(runnable);
		dialog = ProgressDialog.show(this, "请稍等", "正在跳转", true, true);	
	}
	private void GuideToLoad()
	{
		// Intent intent_load = new Intent(this, laod.class);
		// startActivity(intent_load);
		
		Handler handler = new Handler();
		// 在run里面写了跳转activity
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(GuideActivity.this, laod.class);
				intent.putExtra("employeeID", employeeId);
				startActivityForResult(intent, 3);
			}
		};
		handler.post(runnable);
		dialog = ProgressDialog.show(this, "请稍等", "正在跳转", true, true);	
	}
	private void GuideQuit()
	{
		startActivity(new Intent(this, login.class));
	}
}

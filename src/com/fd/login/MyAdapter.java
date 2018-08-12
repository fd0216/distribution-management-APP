package com.fd.login;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {
	public static HashMap<Integer, Boolean> isSelected;
	private Context context = null;
	private LayoutInflater inflater = null;
	private List<HashMap<String, Object>> list = null;
	private String keyString[] = null;
	private String itemString = null; // 记录每个item中textview的值
	private int idValue[] = null;// id值

	public MyAdapter(Context context, List<HashMap<String, Object>> list,
			int resource, String[] from, int[] to) {
		this.context = context;
		this.list = list;
		keyString = new String[from.length];
		idValue = new int[to.length];
		System.arraycopy(from, 0, keyString, 0, from.length);
		System.arraycopy(to, 0, idValue, 0, to.length);
		inflater = LayoutInflater.from(context);
		init();
	}

	// 初始化 设置所有checkbox都为未选择
	public void init() {
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < list.size(); i++) {
			isSelected.put(i, false);
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (holder == null) {
			holder = new ViewHolder();
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.listview, null);
			}
			holder.tv_customerLoginId = (TextView) convertView
					.findViewById(R.id.customer_loginId);
			holder.tv_wareName = (TextView) convertView
					.findViewById(R.id.ware_name);
			holder.tv_wareNums = (TextView) convertView
					.findViewById(R.id.ware_nums);
			holder.cb = (CheckBox) convertView.findViewById(R.id.cb_selected);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, Object> map = list.get(position);
		if (map != null) {
			holder.tv_customerLoginId.setText((String) map.get(keyString[0]));
			holder.tv_wareName.setText((String) map.get(keyString[1]));
			holder.tv_wareNums.setText((String) map.get(keyString[2]));
		}
		holder.cb.setChecked(isSelected.get(position));
		return convertView;
	}

	public class ViewHolder {
		public TextView tv_customerLoginId = null;
		public TextView tv_wareName = null;
		public TextView tv_wareNums = null;
		public CheckBox cb = null;

	}
}

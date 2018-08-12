package com.fd.sql;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Util {
	private ArrayList<String> arrayList = new ArrayList<String>();
	private ArrayList<String> brrayList = new ArrayList<String>();
	private ArrayList<String> crrayList = new ArrayList<String>();
	private HttpConnSoap Soap = new HttpConnSoap();

	public static Connection getConnection() {
		Connection con = null;
		try {
			// Class.forName("org.gjt.mm.mysql.Driver");
			// con=DriverManager.getConnection("jdbc:mysql://192.168.0.106:3306/test?useUnicode=true&characterEncoding=UTF-8","root","initial");
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return con;
	}

	/**
	 * 获取所有货物种类信息
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getWareTypes() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		arrayList.clear();
		brrayList.clear();

		crrayList = Soap.GetWebServre("selectAllCargoInfor", arrayList,
				brrayList);
		if (crrayList != null) {
			HashMap<String, String> tempHash = new HashMap<String, String>();
			tempHash.put("GUID", "GUID");
			tempHash.put("Name", "Name");
			tempHash.put("ShipPrice", "ShipPrice");
			tempHash.put("Unit", "Unit");
			tempHash.put("InsuranceRatio", "InsuranceRatio");
			tempHash.put("CustomerGUID", "CustomerGUID");
			list.add(tempHash);
			for (int j = 0; j < crrayList.size(); j += 6) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("GUID", crrayList.get(j));
				hashMap.put("Name", crrayList.get(j + 1));
				hashMap.put("ShipPrice", crrayList.get(j + 2));
				hashMap.put("Unit", crrayList.get(j + 3));
				hashMap.put("InsuranceRatio", crrayList.get(j + 4));
				hashMap.put("CustomerGUID", crrayList.get(j + 5));
				list.add(hashMap);
			}
		}
		return list;
	}

	/**
	 * 获取所有员工的登陆ID和Password信息
	 * 
	 * @return
	 */
	public List<HashMap<String, String>> getEmployeeInfo() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList = Soap.GetWebServre("selectAllEmployeeInfo", arrayList,
				brrayList);
		if (crrayList != null) {
			HashMap<String, String> tempHash = new HashMap<String, String>();
			tempHash.put("EmployeeID", "EmployeeID");
			tempHash.put("PassWord", "PassWord");
			list.add(tempHash);

			for (int j = 0; j < crrayList.size(); j += 2) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("EmployeeID", crrayList.get(j));
				hashMap.put("PassWord", crrayList.get(j + 1));
				list.add(hashMap);
			}
		}
		return list;

	}

	public List<HashMap<String, String>> getCustomerInfo() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList = Soap.GetWebServre("selectAllCustomerInfo", arrayList,
				brrayList);
		if (crrayList != null) {
			HashMap<String, String> tempHash = new HashMap<String, String>();
			tempHash.put("LoginID", "LoginID");
			tempHash.put("PassWord", "PassWord");
			tempHash.put("GUID", "GUID");
			list.add(tempHash);
			for (int j = 0; j < crrayList.size(); j += 3) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("LoginID", crrayList.get(j));
				hashMap.put("PassWord", crrayList.get(j + 1));
				hashMap.put("GUID", crrayList.get(j + 2));
				list.add(hashMap);
			}
		}
		return list;
	}

	public List<HashMap<String, String>> getWareInfo() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList = Soap.GetWebServre("selectWares", arrayList, brrayList);
		if (crrayList != null) {
			HashMap<String, String> tempHash = new HashMap<String, String>();
			tempHash.put("GUID", "GUID");
			tempHash.put("CustomerID", "CustomerID");
			tempHash.put("Name", "Name");
			tempHash.put("Num", "Num");
			list.add(tempHash);
			for (int j = 0; j < crrayList.size(); j += 4) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("GUID", crrayList.get(j));
				hashMap.put("CustomerID", crrayList.get(j + 1));
				hashMap.put("Name", crrayList.get(j + 2));
				hashMap.put("Num", crrayList.get(j + 3));
				list.add(hashMap);
			}
		}
		return list;
	}

	public List<HashMap<String, String>> getWareHouse() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList = Soap.GetWebServre("selectWareHouseInfo", arrayList,brrayList);
		if (crrayList != null) {
			HashMap<String, String> tempHash = new HashMap<String, String>();
			tempHash.put("GUID", "GUID");
			tempHash.put("Name", "Name");
			list.add(tempHash);
			for (int j = 0; j < crrayList.size(); j += 2) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("GUID", crrayList.get(j));
				hashMap.put("Name", crrayList.get(j + 1));
				list.add(hashMap);
			}
		}
		return list;
	}

	public List<HashMap<String, String>> getTruckInfo() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		arrayList.clear();
		brrayList.clear();
		crrayList = Soap.GetWebServre("selectTrucks", arrayList, brrayList);
		if (crrayList != null) {
			/*
			HashMap<String, String> tempHash = new HashMap<String, String>();
			tempHash.put("TruckID", "TruckID");
			list.add(tempHash);
			HashMap<String, String> tempLicense = new HashMap<String, String>();
			tempLicense.put("License", "License");
			list.add(tempHash);
			*/
			for (int j = 0; j < crrayList.size(); j+=2) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put("TruckID", crrayList.get(j));
				list.add(hashMap);
				HashMap<String, String> hashL = new HashMap<String, String>();
				hashL.put("License", crrayList.get(j+1));
				list.add(hashL);
			}
		}
		return list;
	}

	/**
	 * 增加一条货物信息
	 * 
	 * @return
	 */
	
	public String insertCargoInfo(ArrayList<String> listToInsert,
			String imagePath) {
		arrayList.clear();
		brrayList.clear();
		arrayList.add("wareInfo");
		String strToinsert = "";
		for (int i = 0; i < listToInsert.size(); i++) {
			if (i == (listToInsert.size() - 1))
				strToinsert += listToInsert.get(i);
			else
				strToinsert += listToInsert.get(i) + ",";
		}
		strToinsert += ";" + testUpload(imagePath);
		
		brrayList.add(strToinsert);
		crrayList = Soap.GetWebServre("insertCargoInfo", arrayList, brrayList);
		if (crrayList != null) {
			String str = crrayList.get(0);
			return str;
		} else
			return "false";
	}
	
	
	public String testUpload(String imagePath) {		
		String strImage = "";	
		FileInputStream fis;	
		try {
			if (imagePath.length() == 0) {
				fis = new FileInputStream("/sdcard/myImage/xu.jpg");
			} else
				fis = new FileInputStream(imagePath);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = fis.read(buffer)) >= 0) {
				baos.write(buffer, 0, count);
			}
			byte[] dataToUpload = baos.toByteArray();
			String uploadBuffer = new String(Base64.encode(dataToUpload, 0)); // 进行Base64编码
			strImage = uploadBuffer;
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strImage;
	}

	private Bitmap getBitMapFromPath(String imageFilePath) {

		FileInputStream fis;
		try {
			fis = new FileInputStream(imageFilePath);
			Bitmap bitmap = BitmapFactory.decodeStream(fis);
			return bitmap;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void UpdateWareStatue(String value) {
		arrayList.clear();
		brrayList.clear();
		arrayList.add("wareGUID");
		brrayList.add(value);
		Soap.GetWebServre("updateWareStatue", arrayList, brrayList);
	}

	/**
	 * 删除一条货物信息
	 * 
	 * @return
	 */
	public void deleteCargoInfo(String Cno) {

		arrayList.clear();
		brrayList.clear();

		arrayList.add("Cno");
		brrayList.add(Cno);

		Soap.GetWebServre("deleteCargoInfo", arrayList, brrayList);
	}
}

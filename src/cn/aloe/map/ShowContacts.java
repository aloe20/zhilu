package cn.aloe.map;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import cn.aloe.zhilu.R;

public class ShowContacts extends Activity {
	private ListView lv;
	private ArrayList<String> mContactsName = new ArrayList<String>();
	private static final String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER};
	private String strNumber = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_contacts);
		lv = (ListView) findViewById(R.id.lv);
		getPhoneContacts();
		ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mContactsName);
		lv.setAdapter(mArrayAdapter);
//		System.out.println(mContactsName+"-------------mContactsName---------");
		lv.setOnItemClickListener(new MyOnItemClickListener());
	}

	private void getPhoneContacts() {
		ContentResolver resolver = getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(1);
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				String contactName = phoneCursor
						.getString(0);
				mContactsName.add(contactName+"\n"+phoneNumber);
			}
			phoneCursor.close();
		}
	}
	
	class MyOnItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent1 = getIntent();
			strNumber = mContactsName.get(position);
			final String ipAddress = intent1.getStringExtra("ipAddress");
			final String phoneNumber = strNumber.substring(strNumber.lastIndexOf("\n")+1);
			final SmsManager sm = SmsManager.getDefault();
			AlertDialog.Builder builder = new AlertDialog.Builder(ShowContacts.this);
			builder.setTitle("短信发送");
			builder.setMessage("对方号码:"+phoneNumber+"\n短信内容:我的IP是 "+ipAddress);
			builder.setPositiveButton("发送", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					sm.sendTextMessage(phoneNumber, null, "我的IP地址是："+ipAddress, null, null);
					Intent intent = new Intent(ShowContacts.this, MyMap.class);
					startActivity(intent);
					ShowContacts.this.finish();
				}
				
			});
			builder.setNegativeButton("取消", null);
			builder.show();
			
//			Toast.makeText(ShowContacts.this, ipAddress+"\n"+strNumber.substring(strNumber.lastIndexOf("\n")+1), 0).show();
//			sm.sendTextMessage(phoneNumber, null, "我的IP地址是："+ipAddress, null, null);
		}
		
	}
}

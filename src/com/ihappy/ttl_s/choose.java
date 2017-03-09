package com.ihappy.ttl_s;

import java.util.ArrayList;

import com.ihappy.ttl_s.MyAdapter.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class choose extends Activity implements OnClickListener {
	private ListView lv;
	private MyAdapter mAdapter;
	private ArrayList<String> list;
	private ArrayList<String> listResult;
	private Button btAll;
	private Button ok;
	private Button btCancel;
	private Button btDeAll;
	private int checkNum; // 记录选中的条目数量
	private TextView tv_show;// 用于显示选中的条目数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose);
		lv = (ListView) findViewById(R.id.lv);
		btAll = (Button) findViewById(R.id.all);
		btDeAll = (Button) findViewById(R.id.deall);
		btCancel = (Button) findViewById(R.id.cancel);
		ok = (Button) findViewById(R.id.ok);
		tv_show = (TextView) findViewById(R.id.tv);
		btAll.setOnClickListener(this);
		btDeAll.setOnClickListener(this);
		ok.setOnClickListener(this);
		btCancel.setOnClickListener(this);
		Intent intent = getIntent();
		list=intent.getStringArrayListExtra("list");
		listResult = new ArrayList<String>();
		mAdapter = new MyAdapter(list, this);
		lv.setAdapter(mAdapter);
	       lv.setOnItemClickListener(new OnItemClickListener() {  
	            @Override  
	            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
	                    long arg3) {  
	                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤  
	                ViewHolder holder = (ViewHolder) arg1.getTag();  
	                // 改变CheckBox的状态  
	                holder.cb.toggle();  
	                // 将CheckBox的选中状况记录下来  
	                MyAdapter.getIsSelected().put(arg2, holder.cb.isChecked());  
	                // 调整选定条目  
	                if (holder.cb.isChecked() == true) {
	                	listResult.add(holder.tv.getText().toString());
	         	                    checkNum++;  
	                } else {  
	                	listResult.remove(holder.tv.getText().toString());
	                    checkNum--;  
	                }  
	                // 用TextView显示  
	                tv_show.setText("已选中" + checkNum + "项");  
	            }  
	        });  
	    }  

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok:
			Intent intent = new Intent();
			intent.putStringArrayListExtra("listResult",listResult);
			setResult(1001, intent);
			finish();
			break;
		case R.id.cancel:
			finish();
			break;
		case R.id.all:
			for (int i = 0; i < list.size(); i++) {  
                MyAdapter.getIsSelected().put(i, true);
                if (!listResult.contains(list.get(i))){
                	listResult.add(list.get(i));
                }
                
            }  
            // 数量设为list的长度  
            checkNum = list.size();  
            // 刷新listview和TextView的显示  
            dataChanged();  
			break;
		case R.id.deall:
			 // 遍历list的长度，将已选的按钮设为未选  
            for (int i = 0; i < list.size(); i++) {  
                if (MyAdapter.getIsSelected().get(i)) {  
                    MyAdapter.getIsSelected().put(i, false);  
                    listResult.remove(list.get(i));
                    checkNum--;// 数量减1  
                }  
            }  
            // 刷新listview和TextView的显示  
            dataChanged();  
			break;
		default:
			break;
		}

	}

	private void dataChanged() {
		// TODO Auto-generated method stub
		mAdapter.notifyDataSetChanged();  
        // TextView显示最新的选中数目  
        tv_show.setText("已选中" + checkNum + "项"); 
	}

}

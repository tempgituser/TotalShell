package com.example.totalshell;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private static ListView listView;
	private PackageManager pm;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		
		pm = this.getPackageManager();
//		listView = (ListView) findViewById(R.id.list_view);
//		LoadList(this);// 加载listview
		
		mDrawerToggle = new ActionBarDrawerToggle(this, (DrawerLayout) findViewById(R.id.drawer_layout), R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//				Log.wtf("zzz", "onDrawerClosed" + view.getId());
				
//				if(selectNumber == 1){
//					listView = (ListView) findViewById(R.id.list_view);
//					MainActivity.this.LoadList(MainActivity.this);
//				}
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//				Log.wtf("zzz", "onDrawerOpened" + drawerView.getId());
			}
		};
		// Set the drawer toggle as the DrawerListener
		((DrawerLayout) findViewById(R.id.drawer_layout)).setDrawerListener(mDrawerToggle);
	}

	Handler handler = new Handler(){
	    @Override
	    public void handleMessage(Message msg) {
			listView = (ListView) findViewById(R.id.list_view);
			MainActivity.this.LoadList(MainActivity.this);
//			View container = findViewById(R.id.container);
//			View list = container.findViewById(R.id.list_view);
	    }
	};
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	List<String> loadedPackageName = new ArrayList<String>();
	private void LoadList(Context context) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		selfList.clear();
		loadedPackageName.clear();
		boolean isIncludeSystem = SettingState.isIncludeSystem(context);
		boolean includeService = SettingState.isIncludeService(context);
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningAppProcessInfo> runningTasks = am.getRunningAppProcesses();
			List<RunningServiceInfo> services = am.getRunningServices(150);
			MyPackageInfo pInfo = new MyPackageInfo(context);

			for (int i = 0; i < runningTasks.size(); i++) {
				String appName = runningTasks.get(i).processName;
				if (appName == null || appName.length() < 1 || loadedPackageName.contains(appName)) {
					continue;
				}
				if (getPackageName().equals(appName)) {
					continue;
				}
				ApplicationInfo ai = pInfo.getInfo(appName);
				if (ai == null) {
					continue;
				}
				if (/*null==null ||*/ isIncludeSystem || (ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0
						/*|| (runningTasks.get(i).processName).equals("com.android.contacts") || (runningTasks.get(i).processName).equals("com.android.email")
						|| (runningTasks.get(i).processName).equals("com.android.settings") || (runningTasks.get(i).processName).equals("com.android.music")
						|| (runningTasks.get(i).processName).equals("com.android.calendar")
						|| (runningTasks.get(i).processName).equals("com.android.calculator2")
						|| (runningTasks.get(i).processName).equals("com.android.browser") || (runningTasks.get(i).processName).equals("com.android.camera")
						|| (runningTasks.get(i).processName).equals("com.cooliris.media") || (runningTasks.get(i).processName).equals("com.android.bluetooth")
						|| (runningTasks.get(i).processName).equals("com.android.mms")*/) {
					String dir = ai.publicSourceDir;
					Float size = Float.valueOf((float) ((new File(dir).length() * 1.0)));// 获得应用程序的大小如果size大于一M就用M为单位，否则用KB
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("icon", ai.loadIcon(pm));
					map.put("name", ai.loadLabel(pm));
//					if (size > 1024 * 1024)
//						map.put("info", size / 1024 / 1024 + " MB");
//					else
//						map.put("info", size / 1024 + " KB");
					map.put("info", appName);
					map.put("packagename", appName);// 获得包名给后面用
					selfList.add(map);
					loadedPackageName.add(appName);
				}
			}

			if(includeService){
				for (int i = 0; i < services.size(); i++) {
					String appName = services.get(i).process;
					if (appName == null || appName.length() < 1) {
						continue;
					}
					appName = appName.split(":")[0];
					if (loadedPackageName.contains(appName)) {
						continue;
					}
					if (getPackageName().equals(appName)) {
						continue;
					}
					ApplicationInfo ai = pInfo.getInfo(appName);
					if (ai == null) {
						continue;
					}
					if (/*null == null || */(ai.flags & ApplicationInfo.FLAG_SYSTEM) == 0
							/*|| (services.get(i).process).equals("com.android.contacts") || (services.get(i).process).equals("com.android.email")
							|| (services.get(i).process).equals("com.android.settings") || (services.get(i).process).equals("com.android.music")
							|| (services.get(i).process).equals("com.android.calendar")
							|| (services.get(i).process).equals("com.android.calculator2")
							|| (services.get(i).process).equals("com.android.browser") || (services.get(i).process).equals("com.android.camera")
							|| (services.get(i).process).equals("com.cooliris.media") || (services.get(i).process).equals("com.android.bluetooth")
							|| (services.get(i).process).equals("com.android.mms")*/) {
						String dir = ai.publicSourceDir;
						Float size = Float.valueOf((float) ((new File(dir).length() * 1.0)));// 获得应用程序的大小如果size大于一M就用M为单位，否则用KB
						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("icon", ai.loadIcon(pm));
						map.put("name", ai.loadLabel(pm));
//						if (size > 1024 * 1024)
//							map.put("info", size / 1024 / 1024 + " MB");
//						else
//							map.put("info", size / 1024 + " KB");
						map.put("info", appName);
						map.put("packagename", appName);// 获得包名给后面用
	//					Log.wtf("zzz", appName + "  " + ai.loadLabel(pm));
						selfList.add(map);
						loadedPackageName.add(appName);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		selfList.size();

//		MyPackageInfo pInfo = new MyPackageInfo(context);// 获得PackageInfo对象
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		map.put("icon", pInfo.getInfo("com.whatsapp").loadIcon(pm));
//		map.put("name", pInfo.getInfo("com.whatsapp").loadLabel(pm));
//		list
//		selfList.clear();
//		addSelf(context, "com.whatsapp");
//		addSelf(context, "com.github.shadowsocks");
//		addSelf(context, "org.proxydroid");
		
		SimpleAdapter listadapter = new SimpleAdapter(this, selfList, R.layout.package_list, new String[] { "icon", "name", "info" }, new int[] { R.id.icon,
				R.id.name, R.id.info });
		listView.setAdapter(listadapter);
//		listadapter.notifyDataSetChanged();

		// 下面这个方法主要是用来刷新图片，因为pInfo.getInfo(runningTasks.get(i).processName).loadIcon(pm)获得图片不能被显示出
		listadapter.setViewBinder(new ViewBinder() {
			@Override
			public boolean setViewValue(View view, Object data, String textRepresentation) {
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable) data);
					return true;
				} else
					return false;
			}
		}); // 为listView添加item的点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<?, ?> map = (HashMap<?, ?>) parent.getItemAtPosition(position);
				String packageName = (String) map.get("packagename");
//				Intent intent = new Intent();
//				intent = pm.getLaunchIntentForPackage(packageName);
//				startActivity(intent);
				
				selfList.remove(position);
				((SimpleAdapter)parent.getAdapter()).notifyDataSetChanged();
//				boolean isUseRoot = SettingState.isUseRoot(MainActivity.this);
//				if(!isUseRoot){
//					execShellCmd("am force-stop " + packageName);
//				}
				execShellCmdRoot("am force-stop " + packageName);
				
			}
		}); // 为listview的item添加长按事件
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//				final HashMap<?, ?> long_map = (HashMap<?, ?>) parent.getItemAtPosition(position);
//				new AlertDialog.Builder(MainActivity.this).setTitle("Are you sure close").setPositiveButton("sure", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//						String packageName = (String) long_map.get("packagename");
//						// 根据包名杀死应用程序
////						am.killBackgroundProcesses(packageName);
//						Log.wtf("zzz", packageName);
//						// refash list
//						// 刷新listview
//						LoadList(MainActivity.this);
//					}
//				}).setNegativeButton("cancle", new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						
//					}
//				}).show();
//				return false;
//			}
//		});
	}

	ArrayList<HashMap<String, Object>> selfList = new ArrayList<HashMap<String, Object>>();
	public void addSelf(Context t, String packageName){
		MyPackageInfo pInfo = new MyPackageInfo(t);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("icon", pInfo.getInfo(packageName).loadIcon(pm));
		map.put("name", pInfo.getInfo(packageName).loadLabel(pm));
		String dir = pInfo.getInfo(packageName).publicSourceDir;
		Float size = Float.valueOf((float) ((new File(dir).length() * 1.0)));
//		if (size > 1024 * 1024)
//			map.put("info", size / 1024 / 1024 + " MB");
//		else
//			map.put("info", size / 1024 + " KB");
		map.put("info", packageName);
		map.put("packagename", packageName);
		selfList.add(map);
	}
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();
		
		if (position == 0) {
//			handler.sendEmptyMessageDelayed(0, 500);
		}
		
//		if
		Log.e("zzz", "onNavigationDrawerItemSelected" + String.valueOf(position));
		
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			selectNumber = 1;
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			selectNumber = 2;
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			selectNumber = 3;
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
//		Log.wtf("zzz", "onOptionsItemSelected:" + id);
		if (id == R.id.action_settings) {
			OnCancelListener listener = new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
//					handler.sendEmptyMessage(0);
					listView = (ListView) findViewById(R.id.list_view);
					MainActivity.this.LoadList(MainActivity.this);
				}
			};
			SettingDialog dialog = new SettingDialog(MainActivity.this, listener);
			dialog.setTitle("设置");
			dialog.show();
			return true;
		}
		
		if (id == R.id.action_refresh) {
			listView = (ListView) findViewById(R.id.list_view);
			MainActivity.this.LoadList(MainActivity.this);
			return true;
		}

		if (item.getItemId() == R.id.action_example) {
			Toast.makeText(MainActivity.this, "Example action.", Toast.LENGTH_SHORT).show();
			for(HashMap<?, ?> map : selfList){
				String packageName = (String) map.get("packagename");
				execShellCmdRoot("am force-stop " + packageName);
			}
			selfList.clear();
			((SimpleAdapter)listView.getAdapter()).notifyDataSetChanged();
			finish();
			System.exit(0);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new MyFragment(sectionNumber);
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	int selectNumber = 0;
	@SuppressLint("ValidFragment")
	public static class MyFragment extends PlaceholderFragment {

		int sectionNumber = 0;
		public MyFragment(int sectionNumber){
			this.sectionNumber = sectionNumber;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView;
			switch(sectionNumber){
			case 1:
				rootView = inflater.inflate(R.layout.list_fragment_main, container, false);
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fragment_main, container, false);
				break;
			case 3:
				rootView = inflater.inflate(R.layout.fragment_main, container, false);
				break;
			default:
				rootView = super.onCreateView(inflater, container, savedInstanceState);
				break;
			}
			return rootView;
			//return super.onCreateView(inflater, container, savedInstanceState);
		}
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

//			PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view
//					.findViewById(R.id.tabs);
//			ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
//			MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
//			pager.setAdapter(adapter);
//			tabs.setViewPager(pager);
			if(sectionNumber == 1){
//				View list = view.findViewById(R.id.list_view);
//				View c = view.findViewById(R.id.container);
//				View ll = c.findViewById(R.id.list_view);
//				int a = 1;
				listView = (ListView) view.findViewById(R.id.list_view);
				((MainActivity)view.getContext()).LoadList(view.getContext());// 加载listview
				
			}
		}
	}
	
	class MyPackageInfo {
		private final List<ApplicationInfo> appList;

		public MyPackageInfo(Context context) {
			// get all package data
			PackageManager pm = context.getApplicationContext().getPackageManager();
			appList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		}

		public ApplicationInfo getInfo(String name) {
			if (name == null) {
				return null;
			}
			for (ApplicationInfo appInfo : appList) {
				if (name.equals(appInfo.processName)) {
					return appInfo;
				}
			}
			return null;
		}
		public ApplicationInfo getServiceInfo(String name) {
			if (name == null) {
				return null;
			}
			for (ApplicationInfo appInfo : appList) {
				if (name.contains(appInfo.processName)) {
					return appInfo;
				}
			}
			return null;
		}
	}
	private void execShellCmdRoot(String cmd) {
		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	private void execShellCmd(String cmd) {
		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeBytes(cmd);
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private void execShellCmds(String[] cmds) {
		try {
			// 申请获取root权限，这一步很重要，不然会没有作用
			Process process = Runtime.getRuntime().exec("su");
			// 获取输出流
			OutputStream outputStream = process.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			for (String s : cmds) {
				dataOutputStream.writeBytes(s);
			}
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}

package niceutility.hoa.owedebtmanager.android;

import java.util.List;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.data.Debt;
import niceutility.hoa.owedebtmanager.database.DatabaseHelper;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

public class MainActivity extends ActionBarActivity {

	public static final int CREATE_NEW_DEBT_REQUEST_CODE = 1;
	
	private ActionBar actionBar;
	
	public static DatabaseHelper databaseHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//set up actionBar with tab mode
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		//create tab my debt contains which I owe people
		 Tab tab = actionBar.newTab()
				.setTag("my_debt")
                .setText(R.string.tab_my_debt_title)
                .setTabListener(new MyTabListener<MyDebtFragment>(this, 
                						getString(R.string.tab_my_debt_title), MyDebtFragment.class));
		 //add and set this tab as default
		 actionBar.addTab(tab, true);
		 
		 
		 //create tab shows which debt people owe me
		 tab = actionBar.newTab()
				 .setTag("their_debt")
				 .setText(R.string.tab_their_debt_title)
				 .setTabListener(new MyTabListener<TheirDebtToMeFragment>(this, 
						 			getString(R.string.tab_people_title), TheirDebtToMeFragment.class));

		 //add this tab to action bar
		 actionBar.addTab(tab);
		 
		 //create tab shows people
		 tab = actionBar.newTab()
				 .setTag("people")
				 .setText(R.string.tab_people_title)
				 .setTabListener(new MyTabListener<PeopleFragments>(this, 
						 getString(R.string.tab_people_title), PeopleFragments.class));
		 
		 actionBar.addTab(tab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.action_create_new_debt){
			Intent intent = new Intent(this, CreateNewDebt.class);
			startActivityForResult(intent, CREATE_NEW_DEBT_REQUEST_CODE);
		}
		return super.onOptionsItemSelected(item);
	}

	
	//get DatabaseHelper to communicate with Ormlite
	public DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
		}
		return databaseHelper;
	}
	
	
	@Override
	protected void onDestroy() {
		if (databaseHelper != null){
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		super.onDestroy();
	}
	
	/**
	 * A placeholder fragment for listing my debt
	 */
	public static class MyDebtFragment extends Fragment {

		private List<Debt> listMyDebts;
		private ListView myDebtListView;
		public MyDebtFragment() {
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_my_debt, container,
					false);
			return rootView;
		}
	}

	
	
	/**
	 * A placeholder fragment for listing my debt
	 */
	public static class TheirDebtToMeFragment extends Fragment {

		public TheirDebtToMeFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_their_debt_to_me, container,
					false);
			return rootView;
		}
	}
	

	/**
	 * A placeholder fragment for listing my debt
	 */
	public static class PeopleFragments extends Fragment {

		public PeopleFragments() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_people, container,
					false);
			return rootView;
		}
	}
	
	private class MyTabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;

		/**
		 * Constructor used each time a new tab is created. * 
		 * @param activity
		 * The host Activity, used to instantiate the fragment
		 * @param tag
		 * The identifier tag for the fragment
		 * @param clz
		 *   The fragment's Class, used to instantiate the fragment
		 */
		public MyTabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// Check if the fragment is already initialized
			if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} else {
				// If it exists, simply attach it in order to show it
				ft.attach(mFragment);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				// Detach the fragment, because another one is being attached
				ft.detach(mFragment);
			}
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// User selected the already selected tab. Usually do nothing.
		}
	}
	
	
	public class DebtAdapter extends BaseAdapter {
		private Activity activity;
		private List<Debt> listDebts;
		public DebtAdapter (Activity activity, List<Debt> listDebts){
			this.activity = activity;
			this.listDebts = listDebts;
		}
		@Override
		public int getCount() {
			return listDebts.size();
		}

		@Override
		public Debt getItem(int position) {	
			return listDebts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			View view = convertView;
			if (view == null){
//				Layou
			}
			
			return view;
		}
		
		
		public class DebtViewHolder {
			
		}
		
	}
	
}

package niceutility.hoa.owedebtmanager.android;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.data.Debt;
import niceutility.hoa.owedebtmanager.data.Person;
import niceutility.hoa.owedebtmanager.database.DatabaseHelper;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

public class MainActivity extends ActionBarActivity {

	public static final int CREATE_NEW_DEBT_REQUEST_CODE = 1;
	public static final int REQUEST_VIEW_DEBT = 2;
	public static final int REQUEST_SETTINGS = 3;
	public static final String DEBT_ID_KEY = "debt_id";
	public static final String LOG_TAG = "OWE_DEBT_MainActivity";
	private ActionBar actionBar;

	public static DatabaseHelper databaseHelper;
	private static Dao<Debt, Integer> debtDao;
	private static Dao<Person, Integer> personDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get database connection
		databaseHelper = getHelper();
		try {
			debtDao = databaseHelper.getDebtDao();
			personDao = databaseHelper.getPersonDao();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// set up actionBar with tab mode
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// create tab my debt contains which I owe people
		Tab tab = actionBar
				.newTab()
				.setTag("my_debt")
				.setText(R.string.tab_my_debt_title)
				.setTabListener(
						new MyTabListener<MyDebtFragment>(this,
								getString(R.string.tab_my_debt_title),
								MyDebtFragment.class, true));
		// add and set this tab as default
		actionBar.addTab(tab, true);

		// create tab shows which debt people owe me
		tab = actionBar
				.newTab()
				.setTag("their_debt")
				.setText(R.string.tab_their_debt_title)
				.setTabListener(
						new MyTabListener<MyDebtFragment>(this,
								getString(R.string.tab_their_debt_title),
								MyDebtFragment.class, false));

		// add this tab to action bar
		actionBar.addTab(tab);

		// create tab shows people
		tab = actionBar
				.newTab()
				.setTag("people")
				.setText(R.string.tab_people_title)
				.setTabListener(
						new MyTabListener<PeopleFragments>(this,
								getString(R.string.tab_people_title),
								PeopleFragments.class, false));

		actionBar.addTab(tab);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		
		getMenuInflater().inflate(R.menu.main, menu);
		
		if (getActionBar().getSelectedNavigationIndex() == 2)
			menu.findItem(R.id.action_create_new_debt).setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivityForResult(intent, REQUEST_SETTINGS);
			return true;
		} else if (id == R.id.action_create_new_debt) {
			Intent intent = new Intent(this, CreateNewDebt.class);
			if (getActionBar().getSelectedNavigationIndex() == 0)
				intent.putExtra(MyDebtFragment.FRAGMENT_TYPE_KEY, true);
			else
				intent.putExtra(MyDebtFragment.FRAGMENT_TYPE_KEY, false);
			
			startActivityForResult(intent, CREATE_NEW_DEBT_REQUEST_CODE);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CREATE_NEW_DEBT_REQUEST_CODE
				&& resultCode == RESULT_OK) {
			Fragment frg = null;
			frg = getFragmentManager().findFragmentByTag(
					getString(R.string.tab_my_debt_title));
			if (frg.isVisible()){
				((Refreshable) frg).refresh();
				return;
			}
			else { // check their debt as well :D
				frg = getFragmentManager().findFragmentByTag(
						getString(R.string.tab_their_debt_title));
				((Refreshable) frg).refresh();
			}

		}else if(requestCode == REQUEST_VIEW_DEBT){
			Fragment frg = null;
			frg = getFragmentManager().findFragmentByTag(
					getString(R.string.tab_my_debt_title));
			if (frg.isVisible()){
				((Refreshable) frg).refresh();
				return;
			}else { // check their debt as well :D
				frg = getFragmentManager().findFragmentByTag(
						getString(R.string.tab_their_debt_title));
				((Refreshable) frg).refresh();
			}
			
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// get DatabaseHelper to communicate with Ormlite
	public DatabaseHelper getHelper() {
		if (databaseHelper == null) {
			databaseHelper = OpenHelperManager.getHelper(this,
					DatabaseHelper.class);
		}
		return databaseHelper;
	}

	@Override
	protected void onDestroy() {
		if (databaseHelper != null) {
			OpenHelperManager.releaseHelper();
			databaseHelper = null;
		}
		super.onDestroy();
	}

	/**
	 * A placeholder fragment for listing my debt
	 */
	public static class MyDebtFragment extends Fragment implements Refreshable {

		public static String FRAGMENT_TYPE_KEY = "type_key";
		private List<Debt> listMyDebts;
		private ListView myDebtListView;
		private DebtAdapter myDebtAdapter;;
		private boolean isMyDebt;
		
		
		/*
		 * public MyDebtFragment() {
		 * 
		 * }
		 */
		public static MyDebtFragment instantiate (boolean isMyDebt){
			Bundle bundle = new Bundle();
			bundle.putBoolean(FRAGMENT_TYPE_KEY, isMyDebt);
			MyDebtFragment fragment = new MyDebtFragment();
			fragment.setArguments(bundle);
			return fragment;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Bundle args = getArguments();
			isMyDebt = args.getBoolean(FRAGMENT_TYPE_KEY);
			
				
			View rootView = inflater.inflate(R.layout.fragment_my_debt,
					container, false);
			if (isMyDebt) {
				rootView.setBackgroundColor(getResources().getColor(R.color.they_owe_me_background));
			}else {
				rootView.setBackgroundColor(getResources().getColor(R.color.mydebt_list_background));
			}
			myDebtListView = (ListView) rootView
					.findViewById(R.id.my_debts_list_view);
			listMyDebts = new ArrayList<Debt>();
			myDebtAdapter = new DebtAdapter(getActivity(), listMyDebts);
			myDebtListView.setAdapter(myDebtAdapter);
			myDebtListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adpterView, View viewItem,
						int position, long id) {
					Intent intent = new Intent(getActivity(), ViewDebtActivity.class);
					Debt debt = listMyDebts.get(position);
					intent.putExtra(DEBT_ID_KEY, debt.getDebtId());
					startActivityForResult(intent, REQUEST_VIEW_DEBT);
					
				}
			});
			
			myDebtListView.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View view,
						final int position, long id) {
					
					final DebtLongClickDialogFragment dialog = new DebtLongClickDialogFragment();
					dialog.setOnOptionClickListener(new OnOptionClickListener() {
						
						@Override
						public void onOptionClick(int which) {
							dialog.dismiss();
							switch (which) {
							case 0:
								Intent intent = new Intent(getActivity(), ViewDebtActivity.class);
								Debt debt = listMyDebts.get(position);
								intent.putExtra(DEBT_ID_KEY, debt.getDebtId());
								startActivityForResult(intent, REQUEST_VIEW_DEBT);
								break;
							
							case 1:
								 
								DeleteDebtTask deleteTask = new DeleteDebtTask();
								deleteTask.execute(listMyDebts.get(position).getDebtId(), position);
								
								break;
							default:
								break;
							}
							
						}
					});
					
					dialog.show(getFragmentManager(), "long_click_options_dialog");
					return false;
				}
			});
			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			AsyncTask<Void, Void, Void> loadDebtTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						SimpleDateFormat sameMonthFormat = new SimpleDateFormat(
								"MM yyyy", Locale.US);
						listMyDebts.clear();
						DebtType type = null;
						if (isMyDebt)
							type = DebtType.my_debt;
						else 
							type = DebtType.owe_me;
						List<Debt> tmpList = debtDao.query(debtDao.queryBuilder().where().eq("type", type).prepare());
						Collections.sort(tmpList, new Comparator<Debt>() {

							@Override
							public int compare(Debt lhs, Debt rhs) {
								long difference = lhs.getOweDate()
										- rhs.getOweDate();
								return difference > 0 ? -1 : 1;
							}
						});
						if (tmpList.size() > 0) {
							listMyDebts.add(new Debt(tmpList.get(0)
									.getOweDate()));
							listMyDebts.add(tmpList.get(0));
							for (int i = 1; i < tmpList.size(); i++) {
								Debt thisDebt = tmpList.get(i);
								Date thisDebtOweDate = new Date(
										thisDebt.getOweDate());
								Debt lastDebt = tmpList.get(i - 1);
								if (sameMonthFormat.format(thisDebtOweDate)
										.equals(sameMonthFormat
												.format(new Date(lastDebt
														.getOweDate())))) {
									listMyDebts.add(thisDebt);
								} else { // add separator before add debt
									listMyDebts.add(new Debt(thisDebt
											.getOweDate()));
									listMyDebts.add(thisDebt);
								}
							}
						}
						// listMyDebts.addAll(tmpList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					myDebtAdapter.notifyDataSetChanged();
					Log.v(LOG_TAG, "my owe size: " + listMyDebts.size());
					super.onPostExecute(result);
				}
			};
			loadDebtTask.execute();
		}

		public void refresh() {
			AsyncTask<Void, Void, Void> loadDebtTask = new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						SimpleDateFormat sameMonthFormat = new SimpleDateFormat(
								"MM yyyy", Locale.US);
						listMyDebts.clear();
						DebtType type = null;
						if (isMyDebt)
							type = DebtType.my_debt;
						else 
							type = DebtType.owe_me;
						List<Debt> tmpList = debtDao.query(debtDao.queryBuilder().where().eq("type", type).prepare());
						Collections.sort(tmpList, new Comparator<Debt>() {

							@Override
							public int compare(Debt lhs, Debt rhs) {
								long difference = lhs.getOweDate()
										- rhs.getOweDate();
								return difference > 0 ? -1 : 1;
							}
						});
						if (tmpList.size() > 0) {
							listMyDebts.add(new Debt(tmpList.get(0)
									.getOweDate()));
							listMyDebts.add(tmpList.get(0));
							for (int i = 1; i < tmpList.size(); i++) {
								Debt thisDebt = tmpList.get(i);
								Date thisDebtOweDate = new Date(
										thisDebt.getOweDate());
								Debt lastDebt = tmpList.get(i - 1);
								if (sameMonthFormat.format(thisDebtOweDate)
										.equals(sameMonthFormat
												.format(new Date(lastDebt
														.getOweDate())))) {
									listMyDebts.add(thisDebt);
								} else { // add separator before add debt
									listMyDebts.add(new Debt(thisDebt
											.getOweDate()));
									listMyDebts.add(thisDebt);
								}
							}
						}
						// listMyDebts.addAll(tmpList);
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					myDebtAdapter.notifyDataSetChanged();
					Log.v(LOG_TAG, "my owe size: " + listMyDebts.size());
					super.onPostExecute(result);
				}
			};
			loadDebtTask.execute();

		}
		
		
		
		public class DeleteDebtTask extends AsyncTask<Integer, Void, Void> {

			@Override
			protected Void doInBackground(Integer ... params) {
				int debtId = params[0];
				int position = params[1];

				try {
					// query all for now :D
					Person owner =  listMyDebts.get(position).getPerson();
					
					owner.decreaseBalance(listMyDebts.get(position).getDebtAmount());
					debtDao.deleteById(debtId);
					personDao.update(owner);
					listMyDebts.remove(position);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}
			@Override
			protected void onPostExecute(Void result) {
				
				myDebtAdapter.notifyDataSetChanged();
				super.onPostExecute(result);
			}

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
			View rootView = inflater.inflate(
					R.layout.fragment_their_debt_to_me, container, false);
			return rootView;
		}
		
		
		
		
		
		
		
		
		
		
	}

	/**
	 * A placeholder fragment for listing my debt
	 */
	public static class PeopleFragments extends Fragment {

		private ListView personListView;
		private PersonAdapter personAdapter;
		private List<Person> people;

		public PeopleFragments() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_people,
					container, false);
			personListView = (ListView) rootView
					.findViewById(R.id.people_listview);
			people = new ArrayList<Person>();
			personAdapter = new PersonAdapter(people, getActivity());
			personListView.setAdapter(personAdapter);

			return rootView;
		}
		
		
		
		
		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			
			AsyncTask<Void, Void, Void> getPeopleTask = new AsyncTask<Void, Void, Void>() {
				
				@Override
				protected Void doInBackground(Void... params) {
					
					try {
						people.clear();
						people.addAll(personDao.queryForAll());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
				
				@Override
				protected void onPostExecute(Void result) {
					personAdapter.notifyDataSetChanged();
					super.onPostExecute(result);
				}
			};
			
			getPeopleTask.execute();
			super.onViewCreated(view, savedInstanceState);
		}
		
		
		
	}

	private class MyTabListener<T extends Fragment> implements
			ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		private final boolean isMyDebt;

		/**
		 * Constructor used each time a new tab is created. *
		 * 
		 * @param activity
		 *            The host Activity, used to instantiate the fragment
		 * @param tag
		 *            The identifier tag for the fragment
		 * @param clz
		 *            The fragment's Class, used to instantiate the fragment
		 */
		public MyTabListener(Activity activity, String tag, Class<T> clz, boolean isMyDebt) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
			this.isMyDebt = isMyDebt;
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			MainActivity.this.invalidateOptionsMenu();
			// Check if the fragment is already initialized
			if (mFragment == null && mClass == MyDebtFragment.class) {
				// If not, instantiate and add it to the activity
				mFragment = MyDebtFragment.instantiate(isMyDebt);
				ft.add(android.R.id.content, mFragment, mTag);
			}else if (mFragment == null) {
				// If not, instantiate and add it to the activity
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(android.R.id.content, mFragment, mTag);
			} 
			
			else {
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

	


	public interface Refreshable {
		public void refresh();
	}
	
	
	public static class DebtLongClickDialogFragment extends DialogFragment {
		private OnOptionClickListener optionClickListener;
		
		public void setOnOptionClickListener(OnOptionClickListener optionClickListener){
			this.optionClickListener = optionClickListener;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		    builder.setTitle("Owe-Debt Manager")
		           .setItems(R.array.long_click_options, new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog, int which) {
		            	   optionClickListener.onOptionClick(which);
		               }
		    });
		    return builder.create();
		}
	}
	public interface OnOptionClickListener{
		public void onOptionClick(int which);
	}

}

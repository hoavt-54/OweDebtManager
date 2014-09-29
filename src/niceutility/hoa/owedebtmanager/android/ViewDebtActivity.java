package niceutility.hoa.owedebtmanager.android;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Locale;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.data.Debt;
import niceutility.hoa.owedebtmanager.data.InterestType;
import niceutility.hoa.owedebtmanager.data.Person;
import niceutility.hoa.owedebtmanager.database.DatabaseHelper;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;

public class ViewDebtActivity extends ActionBarActivity{
	
	public static final int REQUEST_EDIT_DEBT = 1;
	public static final String DEBT_ID_KEY = "debt_id";
	
	private Debt debt;
	private int debtId;
	BigDecimal interestPaid;
	private boolean isEdited;
	
//	private TextView debtContent;
	private TextView itemNametv;
	private TextView debtAmountTv;
	private TextView totalTimeTv;
	private TextView interestPaidTv;
	private TextView totalPaidTv;
	private TextView invalidProportion;
	private TextView validProportion;
	private TextView oweDateTv;
	private TextView expiredDateTv;
	private TextView contactTitileTv;
	private QuickContactBadge contactBadge;
	private TextView contactNametv;
	private LinearLayout amountAreaLayout;
	private TextView interestType;
	private TextView interestRateTv;
	private TextView reminder;
	private ActionBar actionBar;
	
	public static DatabaseHelper databaseHelper;
	private Dao<Debt, Integer> debtDao;
	private Dao<Person, Integer> personDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_debt_layout);
		debtId = getIntent().getIntExtra(MainActivity.DEBT_ID_KEY, -1);
		
		if (savedInstanceState != null) {
			savedInstanceState.getInt(MainActivity.DEBT_ID_KEY);
		}
		// init database connection
		getHelper();
		// find view reference 
		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
//		debtContent = (TextView) findViewById(R.id.view_amount_txtview);
		itemNametv = (TextView) findViewById(R.id.view_item_name_tv);
		debtAmountTv = (TextView) findViewById(R.id.view_amount_txtview_value);
		totalTimeTv = (TextView) findViewById(R.id.view_total_time_value);
		interestPaidTv = (TextView) findViewById(R.id.view_interest_paid_value);
		totalPaidTv = (TextView) findViewById(R.id.view_total_paid_value);
		invalidProportion = (TextView) findViewById(R.id.view_invalid_proportion);
		validProportion = (TextView) findViewById(R.id.view_valid_proportion);
		oweDateTv = (TextView) findViewById(R.id.view_owe_date_value_txview);
		expiredDateTv = (TextView) findViewById(R.id.view_expired_date_value_txview);
		contactTitileTv = (TextView) findViewById(R.id.view_contact_title);
		contactBadge = (QuickContactBadge) findViewById(R.id.view_contact_badge);
		contactNametv = (TextView) findViewById(R.id.view_contact_name);
		amountAreaLayout = (LinearLayout) findViewById(R.id.amount_area_layout);
		interestType = (TextView) findViewById(R.id.view_interest_type);
		interestRateTv = (TextView) findViewById(R.id.view_interest_value);
		reminder = (TextView) findViewById(R.id.view_reminder_content);

		// get details in background thread
		
		GetDebtDetails detailsTask = new GetDebtDetails();
		detailsTask.execute();
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(MainActivity.DEBT_ID_KEY, debtId);
		super.onSaveInstanceState(outState);
	}
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.view_debt_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home){
			
			if (isEdited)
				setResult(RESULT_OK);
			else
				setResult(RESULT_CANCELED);
			
			finish();
		}
		else if (item.getItemId() == R.id.action_edit){
			isEdited = true;
			Intent intent = new Intent(ViewDebtActivity.this, CreateNewDebt.class);
			intent.putExtra(DEBT_ID_KEY, debtId);
			startActivityForResult(intent, REQUEST_EDIT_DEBT);
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		GetDebtDetails detailsTask = new GetDebtDetails();
		detailsTask.execute();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	public DatabaseHelper getHelper (){
		if (databaseHelper == null){
			databaseHelper = OpenHelperManager.getHelper(ViewDebtActivity.this, DatabaseHelper.class);
		}
		return databaseHelper;
	}
	
	public class GetDebtDetails extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				debtDao = databaseHelper.getDebtDao();
				personDao = databaseHelper.getPersonDao();
				debt = debtDao.queryForId(debtId);
				debt.setPerson(personDao.queryForId(debt.getPerson().getpId()));
				Log.v("VIEWDEBTACITVIIID", debt.getPerson().getProfileUri() + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result){
				// actionbar titile
				String actionBarTititle = "";
				if (debt.getPerson() != null){
					if (debt.isMoney())
						actionBarTititle = debt.getPerson().getName() + " - " + debt.getDebtAmount().longValue();
					else
						actionBarTititle = debt.getPerson().getName() + " - " + debt.getItemName();
				}
					
				actionBar.setTitle(actionBarTititle);
				
				
				// set Date
				SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
				
				LocalDate currentTime = new LocalDate();
				LocalDate oweDate = new LocalDate(debt.getOweDate());
				LocalDate experiedDate = new LocalDate(debt.getExpiredDate());
				int dayDifference = Days.daysBetween(oweDate.toDateTimeAtStartOfDay(), currentTime.toDateTimeAtStartOfDay()).getDays();
				int dayTotal =  Days.daysBetween(oweDate.toDateTimeAtCurrentTime(), experiedDate.toDateTimeAtCurrentTime()).getDays() + 1;
				
				float invalidProportTion = ((float) dayDifference) / dayTotal;
				LinearLayout.LayoutParams lp = new LayoutParams(0, LayoutParams.MATCH_PARENT);
				lp.weight = invalidProportTion;
				invalidProportion.setLayoutParams(lp);
				
				LinearLayout.LayoutParams lp2 = new LayoutParams(0, LayoutParams.MATCH_PARENT);
				lp2.weight = 1 - invalidProportTion;		
				validProportion.setLayoutParams(lp2);
				
				if (debt.isMoney()){
					if (debt.getInterestType() == InterestType.daily){
						totalTimeTv.setText(dayDifference + " " + getString(R.string.days));
						interestPaid = new BigDecimal(dayDifference * debt.getInterest() / 100).multiply(debt.getDebtAmount());
						interestPaidTv.setText( interestPaid.setScale(2, RoundingMode.HALF_UP) + "");
						totalPaidTv.setText (interestPaid.add(debt.getDebtAmount()).setScale(2, RoundingMode.HALF_UP) + "");
					}
					else if (debt.getInterestType() == InterestType.monthly){
						int monthDifference = (dayDifference + 1) / 30;
						int dayRemainder = (dayDifference + 1) % 30;
						
						
						totalTimeTv.setText(monthDifference + "" + getString(R.string.months) + " and " + dayRemainder + ""+getString(R.string.days));
						interestPaid = new BigDecimal(monthDifference * debt.getInterest() / 100 + (dayRemainder * debt.getInterest()/3000)).multiply(debt.getDebtAmount());
						interestPaidTv.setText( interestPaid.setScale(2, RoundingMode.HALF_UP) + "");
						totalPaidTv.setText (interestPaid.add(debt.getDebtAmount()).setScale(2, RoundingMode.HALF_UP) + "");
						
					}
					
					else if (debt.getInterestType() == InterestType.no_interest){
						interestPaidTv.setText("0" + "");
						totalPaidTv.setText(debt.getDebtAmount() + "");
					}
				}
				
				oweDateTv.setText(dateFormat1.format(debt.getOweDate()) + "");
				expiredDateTv.setText(dateFormat1.format(debt.getExpiredDate()) + "");
				
				
				
				
				// set contact infomation
				if (debt.getPerson().getContactId() != 0){
					Uri contactUri = Contacts.getLookupUri(debt.getPerson().getContactId(), debt.getPerson().getContactKey());
					contactBadge.assignContactUri(contactUri);
					if (debt.getPerson().getProfileUri() != null)
						Picasso.with(getApplicationContext())
							.load(debt.getPerson().getProfileUri())
							.fit()
							.placeholder(R.drawable.user_placeholder)
							.into(contactBadge);
				}
				contactNametv.setText(debt.getPerson().getName() + "");
				if (debt.getType() == DebtType.my_debt)
					contactTitileTv.setText(R.string.contact_field_name_title);
				
				// value
				if (debt.isMoney()){
//					debtContent.setText(debt.getDebtAmount().longValue() + "");
					debtAmountTv.setText(debt.getDebtAmount().longValue() + "");
					amountAreaLayout.setVisibility(View.VISIBLE);
					
					switch (debt.getInterestType()) {
					case daily:
						interestType.setText(R.string.daily);
						interestRateTv.setText(debt.getInterest() + "%");
						break;
					case monthly:
						interestType.setText(R.string.monthly);
						interestRateTv.setText(debt.getInterest() + "%");
						break;
					case no_interest:
						interestType.setText(R.string.no_interest);
					default:
						break;
					}
					
					
				} // things debt 
				else {
//					debtContent.setText(debt.getItemName() + "");
					itemNametv.setVisibility(View.VISIBLE);
					itemNametv.setText(debt.getItemName());
					amountAreaLayout.setVisibility(View.GONE);
				}
				if (debt.getComments() != null)
					reminder.setText(debt.getComments() + "");
				else
					reminder.setText(R.string.no_comments);
			}
			
			super.onPostExecute(result);
		}
		
	}
}

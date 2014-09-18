package niceutility.hoa.owedebtmanager.android;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.android.ContactPickerActivity.ContactsQuery;
import niceutility.hoa.owedebtmanager.data.Debt;
import niceutility.hoa.owedebtmanager.data.InterestType;
import niceutility.hoa.owedebtmanager.data.Person;
import niceutility.hoa.owedebtmanager.database.DatabaseHelper;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.squareup.picasso.Picasso;

public class CreateNewDebt extends ActionBarActivity implements OnItemSelectedListener{

	public static final String TIME_LONG_KEY = "time_key";
	public static final int  REQUEST_ADD_CONTACT = 1;
	private EditText debtNameEdt;
	private EditText debtAmountEdt;
	private EditText contactNameEdt;
	private Button addContactButton;
	private LinearLayout contactPreviewLayout;
	private QuickContactBadge contactPreviewImage;
	private Button removeContactPreviewBtn;
	private String contactKey;
	private TextView oweDateTextview;
	private TextView expiredDateTextView;
//	private Spinner interestTypeSinr;
	private EditText interestEdt;
	private EditText itemNameEdt;
	private TextView itemTitleTxtView;
	private TextView amountTitleTextview;
	private LinearLayout interestAreaLayout;
	
	
	private Debt newDebt;
	private long contactId;
//	private String debtName;
	private BigDecimal debtAmount;
	private String contactName;
	private Date oweDate;
	private Date expiredDate;
	private InterestType interestType;
	private String reminderComment;
	private double interest;
	private boolean isMoney;
	private String profileImageUri;
	private boolean askSetDate;
	private boolean resetDate;
	
	public static DatabaseHelper databaseHelper;
	private Dao<Debt, Integer> debtDao;
	private Dao<Person, Integer> personDao;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		// get database connection
		databaseHelper = getHelper();
		
		
		setContentView(R.layout.activity_create_new_debt);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//get view reference
//		debtNameEdt = (EditText) findViewById(R.id.debt_name_edit_text);
		debtAmountEdt = (EditText) findViewById(R.id.amount_edit_text);
		contactNameEdt = (EditText) findViewById(R.id.contact_name_edit_text);
//		selectContactSpnr = (Spinner) findViewById(R.id.contact_spinner);
		addContactButton = (Button) findViewById(R.id.add_contact_button);
		contactPreviewLayout = (LinearLayout) findViewById(R.id.contact_area_wrapper);
		contactPreviewImage = (QuickContactBadge) findViewById(R.id.contact_preview_profile_image);
		removeContactPreviewBtn = (Button) findViewById(R.id.remove_selected_contact_button);
		oweDateTextview = (TextView) findViewById(R.id.owe_date_value_txview);
		expiredDateTextView = (TextView) findViewById(R.id.expired_date_value_txview);
//		expiredDateSpinr = (Spinner) findViewById(R.id.expired_date_spinner);
//		interestTypeSinr = (Spinner) findViewById(R.id.interest_type_spinner);
		interestEdt = (EditText) findViewById(R.id.interest_editext);
		itemNameEdt = (EditText) findViewById(R.id.item_name_edit_text);
		itemTitleTxtView = (TextView) findViewById(R.id.item_title_txtView);
		amountTitleTextview = (TextView) findViewById(R.id.amount_title_txtView);
		interestAreaLayout = (LinearLayout) findViewById(R.id.interest_area_layout);
		
		// init boolean variable 
		isMoney = true;
		
		
		//config contact spinner
		
		addContactButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateNewDebt.this, ContactPickerActivity.class);
				startActivityForResult(intent, REQUEST_ADD_CONTACT);
				
			}
		});
		
		removeContactPreviewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				contactPreviewLayout.setVisibility(View.INVISIBLE);
				//contactName = "";
				contactId = 0;
				contactKey = "";
			}
		});
		
		//initiate owe date by current day
		oweDate = new Date();
		oweDateTextview.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(oweDate));
		oweDateTextview.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 DialogFragment newFragment = new DatePickerFragment() {
					
					@Override
					public void onDateSet(DatePicker picker,  int year, int monthOfYear, int dayOfMonth) {
						askSetDate = false;
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
						SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
						String datePlaceHolder = "%02d %02d %4d";
						String pickedDateString = String.format(datePlaceHolder, dayOfMonth, monthOfYear + 1, year);
						try {
							oweDate = dateFormat.parse(pickedDateString);
							oweDateTextview.setText(dateFormat1.format(oweDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				};
				if (oweDate != null){
					Bundle bundle = new Bundle();
					bundle.putLong(TIME_LONG_KEY, oweDate.getTime());
					newFragment.setArguments(bundle);
				}
				((DatePickerFragment) newFragment).setPermanentTitle("Set owe date");
			    newFragment.show(getSupportFragmentManager(), "oweDatePicker");
				
			}
		});
		
		//initiate owe date by current day
		expiredDate = new Date();
		expiredDateTextView.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(expiredDate));
		expiredDateTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 DialogFragment newFragment = new DatePickerFragment() {
					
					@Override
					public void onDateSet(DatePicker picker,  int year, int monthOfYear, int dayOfMonth) {
						askSetDate = false;
						SimpleDateFormat dateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
						SimpleDateFormat dateFormat1 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
						String datePlaceHolder = "%02d %02d %4d";
						String pickedDateString = String.format(datePlaceHolder, dayOfMonth, monthOfYear + 1, year);
						try {
							expiredDate = dateFormat.parse(pickedDateString);
							expiredDateTextView.setText(dateFormat1.format(expiredDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				};
				if (expiredDate != null){
					Bundle bundle = new Bundle();
					bundle.putLong(TIME_LONG_KEY, expiredDate.getTime());
					newFragment.setArguments(bundle);
				}
				((DatePickerFragment) newFragment).setPermanentTitle("Set expired date");
			    newFragment.show(getSupportFragmentManager(), "expiredDatePicker");
				
			}
		});
		
		
		//set up interest type spinner 
		ArrayAdapter<CharSequence> interestTypeAdapter = ArrayAdapter.createFromResource(this, 
										R.array.interest_type_array, android.R.layout.simple_spinner_item);
		interestTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		interestTypeSinr.setAdapter(interestTypeAdapter);
//		interestTypeSinr.setOnItemSelectedListener(this);
		//default value for interest type
		interestType = InterestType.no_interest;
		
		interestEdt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String text = s.toString();
				if (text.length() == 1){
					
				}
				
			}
		});
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
		// let OpenHelperManager close connection to database
				if (databaseHelper != null) {
					OpenHelperManager.releaseHelper();
					databaseHelper = null;
				}
		super.onDestroy();
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_new_debt, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			return true;
		case R.id.action_done:
			// handle action done
			if (checkValidateInputFields()){
				createDebtInLocalDatabase();
				setResult(RESULT_OK);
				finish();
			}
			
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ADD_CONTACT){
			if (resultCode == RESULT_OK){
				contactPreviewLayout.setVisibility(View.VISIBLE);
				contactId = data.getLongExtra(ContactPickerActivity.CONTACT_ID, 0);
				contactKey = data.getStringExtra(ContactPickerActivity.CONTACT_KEY);
				contactName = data.getStringExtra(ContactPickerActivity.CONTACT_NAME);
				
				final Uri contactUri = Contacts.getLookupUri(contactId, contactKey);
				contactNameEdt.setText(contactName);
				
				// get contact in background thread
				AsyncTask< Uri, Void, Cursor> loadContacttask = new AsyncTask<Uri, Void, Cursor>() {

					@Override
					protected Cursor doInBackground(Uri... uri) {
						String[] projection = ContactsQuery.PROJECTION;
						
						Cursor cursor = getContentResolver()
			                    .query(uri[0], projection, null, null, null);
			            cursor.moveToFirst();
			            return cursor;
					}
					
					@Override
					protected void onPostExecute(Cursor cursor) {
						String profileUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);
						Picasso.with(getBaseContext()).load(profileUri)
							.placeholder(R.drawable.user_placeholder)
							.error(R.drawable.user_placeholder)
							.fit().into(contactPreviewImage);
						profileImageUri = profileUri;
						contactPreviewImage.assignContactUri(contactUri);
						super.onPostExecute(cursor);
					}
				};
				
				loadContacttask.execute(contactUri);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	public boolean checkValidateInputFields(){
		
		// check debt name 
		/*debtName = debtNameEdt.getText().toString();
		if (debtName == null || debtName == "" || debtName.length() == 0){
			Toast.makeText(getBaseContext(), getString(R.string.debt_name_empty_error), Toast.LENGTH_SHORT).show();
			debtNameEdt.requestFocus();
			return false;
		}*/
		
		// check amount
		String debtAmountString = debtAmountEdt.getText().toString();
		if (debtAmountString != null &&  debtAmountString.length() > 0)
			debtAmount = new BigDecimal(debtAmountEdt.getText().toString());
		else {
			Toast.makeText(getBaseContext(), getString(R.string.debt_amount_empty_error), Toast.LENGTH_SHORT).show();
			debtAmountEdt.requestFocus();
			return false;
		}
		//check contact
		if (contactId != 0){
//			Toast.makeText(getBaseContext(), contactName + "  " + contactKey,  Toast.LENGTH_SHORT).show();
		}
		else{
			contactName = contactNameEdt.getText().toString();
			if (contactName == null || contactName.length() == 0){
				Toast.makeText(getBaseContext(), getString(R.string.contact_empty_error), Toast.LENGTH_SHORT).show();
				contactNameEdt.requestFocus();
				return false;
				
			}
		}
		
		// check date 
		SimpleDateFormat sameDateFormat = new SimpleDateFormat("ddMMyyyy", Locale.US);
		if (sameDateFormat.format(oweDate).equals(sameDateFormat.format(expiredDate)) && !askSetDate){
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			//set message
			builder.setMessage("You haven't set the expired date");
			builder.setTitle("Owe Debt Manager");
			// Add the buttons
			builder.setPositiveButton(R.string.save_anway, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               askSetDate = true;
			           }
			       });
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   resetDate = true;
			           }
			       });
			
			builder.create().show();
			
//			if (resetDate)
				return false;
//			Toast.makeText(getBaseContext(), "you haven't set the expired date", Toast.LENGTH_SHORT).show();
			
		}else if (oweDate.compareTo(expiredDate) > 0){
			Toast.makeText(getBaseContext(), "Expired date cannot be earlier than owe date!", Toast.LENGTH_SHORT).show();
			
			expiredDateTextView.requestFocus();
			return false;
		}
		else if (expiredDate.compareTo(new Date()) < 0 && !sameDateFormat.format(expiredDate).equals(sameDateFormat.format(new Date()))){
			Toast.makeText(getBaseContext(), "Expired date is over!", Toast.LENGTH_SHORT).show();
			expiredDateTextView.requestFocus();
			return false;
		}
		
		
		// check interest
//		interestType = //
		String interestValueText = interestEdt.getText().toString();
		if (interestValueText != null && interestValueText.length() > 0){
			interest = Double.parseDouble(interestValueText);
			if (interest > 0 && interestType == InterestType.no_interest){
				Toast.makeText(this, getString(R.string.interest_type_error), Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		else if (interestType != InterestType.no_interest ){
			interest = 0;
			Toast.makeText(this, getString(R.string.interest_value_error), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//Toast.makeText(this,"type: " + interestType + "    "  +interest + "", Toast.LENGTH_SHORT).show();
		
			
		
		
		return true; // if all input fields is validated
	}
	
	public abstract class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		private CharSequence title;
		public void setPermanentTitle(CharSequence title) {
	        this.title = title;
	        setTitle(title);
	    }
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Bundle data = getArguments();
			long time = data.getLong(TIME_LONG_KEY, 0);
			
			// set default time for 
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			DatePickerDialog dialog =  new DatePickerDialog(getActivity(), this, year, month, day){
				@Override
				public void onDateChanged(DatePicker view, int year, int month,
						int day) {
					
					super.onDateChanged(view, year, month, day);
					setTitle(title);
				}
			};
			dialog.setTitle(title);
			return dialog;
		}
		
		
		


		
	}

	@Override
	public void onItemSelected(AdapterView<?> adapter, View view, int position,
			long arg3) {
		interestType = InterestType.values()[position];
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean createDebtInLocalDatabase (){
		
		newDebt = new Debt(true, isMoney, DebtType.my_debt, debtAmount, 
					reminderComment, interest, interestType, oweDate.getTime(), expiredDate.getTime());
		newDebt.setType(DebtType.my_debt);
		AsyncTask< Void, Void, Void> saveTask =  new AsyncTask<Void, Void, Void>() {
			
			private ProgressDialog dialog = new ProgressDialog(CreateNewDebt.this);
			@Override
			protected void onPreExecute() {
				dialog.setMessage(getString(R.string.dialog_creating_new_debt_message));
				dialog.show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					debtDao = databaseHelper.getDebtDao();
					personDao = databaseHelper.getPersonDao();
					Person person = null;
					// find whether this new debt belongs to any person in database
					// we first find by contact information, then we find by name
					// if two ways above does not work, we simply create a new Person
				
					List<Person> listPotentialPeople = null;
					if (contactKey != null && contactId != 0)
						listPotentialPeople =	personDao.query(personDao.queryBuilder()
							.where().eq(Person.COLUMN_CONTACT_ID, contactId).and()
							.eq(Person.COlUMN_CONTACT_KEY, contactKey).prepare());
					
					if (listPotentialPeople != null && listPotentialPeople.size() == 1){
						person = listPotentialPeople.get(0);
					}
					else{
						// find by name
						listPotentialPeople = personDao.query(personDao.queryBuilder().where().like(Person.COLUMN_NAME, contactName).prepare());
						if (listPotentialPeople != null && listPotentialPeople.size() == 1){
							person = listPotentialPeople.get(0);
						}
						else { //create new person
							person = new Person(contactName, contactKey, contactId, profileImageUri);
							personDao.create(person);
							
						}
					}
					// finally we will have a valid person anyway
					newDebt.setPerson(person);
					debtDao.create(newDebt);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			
			
			
			@Override
			protected void onPostExecute(Void result) {
				dialog.dismiss();
				super.onPostExecute(result);
			}
		}; 
		
		
		saveTask.execute();
		return true;
	}
	public void onRadioButtonClicked(View view) {
	    // Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_money_debt:
	            if (checked){
	            	isMoney = true;
	            	if(itemNameEdt.getVisibility() != View.GONE){
	            		itemNameEdt.setVisibility(View.GONE);
	            		itemTitleTxtView.setVisibility(View.GONE);
	            	}
	            	if (amountTitleTextview.getVisibility() != View.VISIBLE){
	            		amountTitleTextview.setVisibility(View.VISIBLE);
	            		debtAmountEdt.setVisibility(View.VISIBLE);
	            		debtAmountEdt.requestFocus();
	            		interestAreaLayout.setVisibility(View.VISIBLE);
	            		
	            	}
	            }
	            break;
	        case R.id.radio_item:
	            if (checked){
	               isMoney = false;
	               
	               if (amountTitleTextview.getVisibility() != View.GONE){
	            		amountTitleTextview.setVisibility(View.GONE);
	            		debtAmountEdt.setVisibility(View.GONE);
	            		interestAreaLayout.setVisibility(View.GONE);
	            	}
	               
	               if(itemNameEdt.getVisibility() != View.VISIBLE){
	            		itemNameEdt.setVisibility(View.VISIBLE);
	            		itemNameEdt.requestFocus();
	            		itemTitleTxtView.setVisibility(View.VISIBLE);
	            		
	            	}
	            	
	               
	            }
	            break;
	        case R.id.radio_no_interest:
	            if (checked)
	            	interestType = InterestType.no_interest;
	            break;
	        case R.id.radio_daily:
	            if (checked)
	               interestType = InterestType.daily;
	            break;
	        case R.id.radio_monthly:
	            if (checked)
	               interestType = InterestType.monthly;
	            break;
	    }
	}
	
	


	
}

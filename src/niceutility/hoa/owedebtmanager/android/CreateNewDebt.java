package niceutility.hoa.owedebtmanager.android;

import java.math.BigDecimal;
import java.util.Date;

import com.squareup.picasso.Picasso;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.android.ContactPickerActivity.ContactsQuery;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class CreateNewDebt extends ActionBarActivity {

	public static final int  REQUEST_ADD_CONTACT = 1;
	private EditText debtNameEdt;
	private EditText debtAmountEdt;
	private EditText contactNameEdt;
	private Spinner selectContactSpnr;
	private Button addContactButton;
	private LinearLayout contactPreviewLayout;
	private ImageView contactPreviewImage;
	private TextView contactPreviewName;
	private Button removeContactPreviewBtn;
	private long contactId;
	private String contactKey;
	private Spinner oweDateSpinr;
	private Spinner expiredDateSpinr;
	private Spinner interestTypeSinr;
	private EditText interestEdt;
	
	private String debtName;
	private BigDecimal debtAmount;
	private String contactName;
	private Date oweDate;
	private Date expiredDate;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_new_debt);

		//get view reference
		debtNameEdt = (EditText) findViewById(R.id.debt_name_edit_text);
		debtAmountEdt = (EditText) findViewById(R.id.amount_edit_text);
		contactNameEdt = (EditText) findViewById(R.id.contact_name_edit_text);
//		selectContactSpnr = (Spinner) findViewById(R.id.contact_spinner);
		addContactButton = (Button) findViewById(R.id.add_contact_button);
		contactPreviewLayout = (LinearLayout) findViewById(R.id.contact_area_wrapper);
		contactPreviewImage = (ImageView) findViewById(R.id.contact_preview_profile_image);
		removeContactPreviewBtn = (Button) findViewById(R.id.remove_selected_contact_button);
		oweDateSpinr = (Spinner) findViewById(R.id.owe_date_spinner);
		expiredDateSpinr = (Spinner) findViewById(R.id.expired_date_spinner);
		interestTypeSinr = (Spinner) findViewById(R.id.interest_type_spinner);
		interestEdt = (EditText) findViewById(R.id.interest_editext);
		
		//config contact spinner
		
		addContactButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CreateNewDebt.this, ContactPickerActivity.class);
				startActivityForResult(intent, REQUEST_ADD_CONTACT);
				
			}
		});
		
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
				
				Uri contactUri = Contacts.getLookupUri(contactId, contactKey);
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
						
						super.onPostExecute(cursor);
					}
				};
				
				loadContacttask.execute(contactUri);
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	public int checkValidateInputFields(){
		
		// check debt name 
		debtName = debtNameEdt.getText().toString();
		if (debtName == null || debtName == "")
			return R.id.debt_name_edit_text;
		
		// check amount
		if (debtAmountEdt.getText().toString() != null)
			debtAmount = new BigDecimal(debtAmountEdt.getText().toString());
		else 
			return R.id.amount_edit_text;
		
		//check contact
		contactName = contactNameEdt.getText().toString();
		if (contactName == null || contactName == "")
			return R.id.contact_name_edit_text;
		
		
		
		return 0; // if all input fields is validated
	}

}

package niceutility.hoa.owedebtmanager.android;

import java.math.BigDecimal;
import java.util.Date;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.R.id;
import niceutility.hoa.owedebtmanager.R.layout;
import niceutility.hoa.owedebtmanager.R.menu;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.os.Build;

public class CreateNewDebt extends ActionBarActivity {

	private EditText debtNameEdt;
	private EditText debtAmountEdt;
	private EditText contactNameEdt;
	private Spinner selectContactSpnr;
	private Spinner oweDateSpinr;
	private Spinner expiredDateSpinr;
	private Spinner interestTypeSinr;
	private EditText interestEdt;
	
	private String debtName;
	private BigDecimal debtAmount;
	private String contactId;
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
		selectContactSpnr = (Spinner) findViewById(R.id.contact_spinner);
		oweDateSpinr = (Spinner) findViewById(R.id.owe_date_spinner);
		expiredDateSpinr = (Spinner) findViewById(R.id.expired_date_spinner);
		interestTypeSinr = (Spinner) findViewById(R.id.interest_type_spinner);
		interestEdt = (EditText) findViewById(R.id.interest_editext);
		
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
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}

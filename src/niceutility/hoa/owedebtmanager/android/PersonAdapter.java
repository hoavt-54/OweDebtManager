package niceutility.hoa.owedebtmanager.android;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.data.Person;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import android.app.Activity;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PersonAdapter extends BaseAdapter {
	private List<Person> people;
	private Activity activity;
	private SimpleDateFormat longAgoFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
	public PersonAdapter (List<Person> people, Activity activity){
		this.people = people;
		this.activity = activity;
	}
	
	@Override
	public int getCount() {
		return people.size();
		
	}

	@Override
	public Person getItem(int position) {
		
		return people.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		
		View view = convertView;
		PersonViewHolder holder = null;
		if (view == null){
			view = LayoutInflater.from(activity).inflate(R.layout.person_in_listview, null);
			holder = new PersonViewHolder();
			holder.personImage = (QuickContactBadge) view.findViewById(R.id.person_image);
			holder.personName = (TextView) view.findViewById(R.id.person_name);
			holder.personBalance = (TextView) view.findViewById(R.id.person_balance);
			holder.personLastTime = (TextView) view.findViewById(R.id.person_last_time);
			view.setTag(holder);
		}
		else holder = (PersonViewHolder) view.getTag();
		
		//set data
		Person person = people.get(position);
		if (person.getName() != null)
		holder.personName.setText(person.getName() + "");
		
		if (person.getContactKey() != null && person.getContactKey().length() > 0){
			Uri contactUri = Contacts.getLookupUri(person.getContactId(), person.getContactKey());
			holder.personImage.assignContactUri(contactUri);
			
				Picasso.with(activity).load(person.getProfileUri())
						.fit()
						.placeholder(R.drawable.user_placeholder)
						.error(R.drawable.user_placeholder)
						.into(holder.personImage);
			
		}
		
		if (person.getBalance() != null && person.getBalance().compareTo(BigDecimal.ZERO) > 0){
			holder.personBalance.setText(activity.getString(R.string.i_owe) + " " + person.getBalance().doubleValue());
		}
		else if (person.getBalance() != null) {
			holder.personBalance.setText(activity.getString(R.string.owe_me) + " " + person.getBalance().abs().doubleValue());
		}
		
		
		
		// set last day

		LocalDate currentTime = new LocalDate();
		LocalDate oweDate = new LocalDate(person.getLastDay());
		if (person.getLastDay() == 0){
			holder.personLastTime.setText(R.string.no_record);
			return view;
		}
		
		int dayDifference = Days.daysBetween(oweDate.toDateTimeAtStartOfDay(), currentTime.toDateTimeAtStartOfDay()).getDays();
		if (dayDifference == 0)
			holder.personLastTime.setText(activity.getString(R.string.today));
		else{
			if (dayDifference == 1)
				holder.personLastTime.setText(activity.getString(R.string.yesterday));
			else if (dayDifference <= 7) 
				holder.personLastTime.setText(dayDifference + " " + activity.getString(R.string.day_ago));
			else
				holder.personLastTime.setText(longAgoFormat.format(person.getLastDay()));
		}
		
		return view;
		
	}
	
	private class PersonViewHolder {
		public QuickContactBadge personImage;
		public TextView personName;
		public TextView personBalance;
		public TextView personLastTime;
	}
}

package niceutility.hoa.owedebtmanager.android;

import java.math.BigDecimal;
import java.util.List;

import com.squareup.picasso.Picasso;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.data.Person;
import android.app.Activity;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

public class PersonAdapter extends BaseAdapter {
	private List<Person> people;
	private Activity activity;
	
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
			if (person.getProfileUri() != null){
				Picasso.with(activity).load(person.getProfileUri())
						.fit()
						.placeholder(R.drawable.user_placeholder)
						.error(R.drawable.user_placeholder)
						.into(holder.personImage);
			}
		}
		
		if (person.getBalance() != null && person.getBalance().compareTo(BigDecimal.ZERO) > 0){
			holder.personBalance.setText("owe her " + person.getBalance().doubleValue());
		}
		else if (person.getBalance() != null) {
			holder.personBalance.setText("she owe " + person.getBalance().abs().doubleValue());
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

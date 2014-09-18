package niceutility.hoa.owedebtmanager.android;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.data.Debt;
import niceutility.hoa.owedebtmanager.data.Person;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DebtAdapter extends BaseAdapter {
	public static int DEBT_MY_DEBT_TYPE = 0;
	public static int DEBT_OWE_ME_TYPE = 1;
	public static int SEPARATOR = 2;
	public static int TOTAL_TYPES = 3;
	private Activity activity;
	private List<Debt> listDebts;
	private SimpleDateFormat longAgoFormat;
	private SimpleDateFormat sameMonthFormat;
	public DebtAdapter (Activity activity, List<Debt> listDebts){
		this.activity = activity;
		this.listDebts = listDebts;
		longAgoFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
		sameMonthFormat = new SimpleDateFormat("MMM yyyy", Locale.US);
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
	public int getViewTypeCount() {
		
		return TOTAL_TYPES;
	}
	
	@Override
	public int getItemViewType(int position) {
		
		switch (getItem(position).getType()) {
		case separator:
			return SEPARATOR;
		case my_debt:
			return DEBT_MY_DEBT_TYPE;
		case owe_me:
			return DEBT_OWE_ME_TYPE;

		default:
			return 0;
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		View view = convertView;
		DebtViewHolder holder = new DebtViewHolder();
		int viewType = getItemViewType(position);
		if (view == null){
			if (viewType == DEBT_MY_DEBT_TYPE){
				view = LayoutInflater.from(activity).inflate(R.layout.debt_item_in_list_view_layout, null);
				holder.mainTitle = (TextView) view.findViewById(R.id.debt_main_title);
				holder.content = (TextView) view.findViewById(R.id.debt_content);
				holder.date = (TextView) view.findViewById(R.id.debt_date);
			}
			else if (viewType == SEPARATOR){
				view = LayoutInflater.from(activity).inflate(R.layout.seperator_item_in_list_debt, null);
				holder.content = (TextView) view.findViewById(R.id.separator_date_content_txtView);
			}
			view.setTag(holder);
		}
		else
			holder = (DebtViewHolder) view.getTag();
		
		// setdata
		Debt debt = getItem(position);
		Date currentTime = new Date();
		if (viewType == DEBT_MY_DEBT_TYPE){
			Person  person = debt.getPerson();
			String personName = "";
			if (person.getName().split(" ").length > 3)
				personName = person.getName().split(" ")[0] + " " + person.getName().split(" ")[1] + " " 
							+ person.getName().split(" ")[2] + " " + person.getName().split(" ")[3];
			else
				personName = person.getName();
			holder.mainTitle.setText(personName + " - " + debt.getDebtAmount());
			holder.content.setText(" smthing here");
			
			
			// set date
			
			long timeDifference = currentTime.getTime() - debt.getOweDate();
			if (longAgoFormat.format(currentTime).equals(longAgoFormat.format(new Date(debt.getOweDate()))))
				holder.date.setText(activity.getString(R.string.today));
			else{
				int dayDifference = (int) (Math.round( (timeDifference) / 86400000D ) - 1);
				if (dayDifference == 1)
					holder.date.setText(activity.getString(R.string.yesterday));
				else if (dayDifference <= 7) 
					holder.date.setText(dayDifference + " " + activity.getString(R.string.day_ago));
				else
					holder.date.setText(longAgoFormat.format(debt.getOweDate()));
				
				
			}
		}
		else if (viewType == SEPARATOR){
			Date thisSeparatorDate = new Date(debt.getOweDate());
			if (sameMonthFormat.format(currentTime).equals(sameMonthFormat.format(thisSeparatorDate))){
				holder.content.setText(activity.getString(R.string.this_month));
			}
			else 
				holder.content.setText(sameMonthFormat.format(thisSeparatorDate));
		}
		return view;
	}
	
	
	public class DebtViewHolder {
		TextView mainTitle;
		TextView content;
		TextView date;
	}
	
}
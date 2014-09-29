

package niceutility.hoa.owedebtmanager.data;

import java.math.BigDecimal;

import niceutility.hoa.owedebtmanager.android.DebtType;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/*debtId
name
isMyDebt
amount
comments
interest
typeOfInterest*/

@DatabaseTable
public class Debt {
	public static final String PERSON_NAME_FIELD = "person_id";
	@DatabaseField(generatedId=true)
	private int debtId;
	
	@DatabaseField
	private DebtType type;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private BigDecimal debtAmount;
	
	@DatabaseField
	private String itemName; 
	
	@DatabaseField
	private String comments;
	
	@DatabaseField
	private double interest;
	
	@DatabaseField
	private InterestType interestType;
	
	//we may change these two attributes to long, instead of Date
	@DatabaseField
	private long oweDate;
	
	@DatabaseField
	private long expiredDate;
	
	@DatabaseField
	private boolean isMoney;
	
	@DatabaseField(columnName=PERSON_NAME_FIELD, foreign=true, foreignAutoRefresh= true)
	private Person person;
	
	
	public Debt (){
		super();
	}
	
	public Debt (long oweDate){
		super();
		this.oweDate = oweDate;
		this.type = DebtType.separator;
	}
	
	public Debt(boolean isMoney,  DebtType type,
			BigDecimal debtAmount, String comments, double interest,
			InterestType interestType, long oweDate, long expiredDate) {
		super();
//		this.name = name;
		this.debtAmount = debtAmount;
		this.comments = comments;
		this.interest = interest;
		this.interestType = interestType;
		this.oweDate = oweDate;
		this.expiredDate = expiredDate;
		this.isMoney = isMoney;
		this.type = type;
	}
	
	
	
	

	public Debt(DebtType type, String name, String itemName, String comments,
			long oweDate, long expiredDate, boolean isMoney, Person person) {
		super();
		this.type = type;
		this.name = name;
		this.itemName = itemName;
		this.comments = comments;
		this.oweDate = oweDate;
		this.expiredDate = expiredDate;
		this.isMoney = isMoney;
		this.person = person;
	}

	public int getDebtId() {
		return debtId;
	}

	public void setDebtId(int debtId) {
		this.debtId = debtId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getDebtAmount() {
		return debtAmount;
	}

	public void setDebtAmount(BigDecimal debtAmount) {
		this.debtAmount = debtAmount;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public InterestType getInterestType() {
		return interestType;
	}

	public void setInterestType(InterestType interestType) {
		this.interestType = interestType;
	}

	public long getOweDate() {
		return oweDate;
	}

	public void setOweDate(long oweDate) {
		this.oweDate = oweDate;
	}

	public long getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(long expiredDate) {
		this.expiredDate = expiredDate;
	}

	public Person getPerson() {
		return person;
	}



	public void setPerson(Person person) {
		this.person = person;
	}



	public DebtType getType() {
		return type;
	}



	public void setType(DebtType type) {
		this.type = type;
	}

	public boolean isMoney() {
		return isMoney;
	}

	public void setMoney(boolean isMoney) {
		this.isMoney = isMoney;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

}

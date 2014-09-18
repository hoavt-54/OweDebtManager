package niceutility.hoa.owedebtmanager.data;

import java.math.BigDecimal;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Person {
	public static final String COLUMN_NAME = "person_name";
	public static final String COlUMN_CONTACT_KEY = "person_contact_key";
	public static final String COLUMN_CONTACT_ID = "person_contact_id";
	
	@DatabaseField(generatedId=true)
	private int pId;
	
	@DatabaseField (columnName=COLUMN_NAME)
	private String name;
	
	@DatabaseField
	private String msidsn;
	
	@DatabaseField(columnName=COlUMN_CONTACT_KEY)
	private String contactKey;
	
	@DatabaseField(columnName=COLUMN_CONTACT_ID)
	private long contactId;
	
	@DatabaseField
	private String profileUri;
	
	@DatabaseField
	private BigDecimal balance;
	
	public Person(){
		super();
	}
	

	public Person(String name, String contactKey, long contactId, String profileUri) {
		super();
		this.name = name;
		this.contactId = contactId;
		this.contactKey = contactKey;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsidsn() {
		return msidsn;
	}

	public void setMsidsn(String msidsn) {
		this.msidsn = msidsn;
	}

	public String getContactKey() {
		return contactKey;
	}

	public void setContactKey(String contactKey) {
		this.contactKey = contactKey;
	}

	public long getContactId() {
		return contactId;
	}

	public void setContactId(long contactId) {
		this.contactId = contactId;
	}


	public String getProfileUri() {
		return profileUri;
	}


	public void setProfileUri(String profileUri) {
		this.profileUri = profileUri;
	}


	public BigDecimal getBalance() {
		return balance;
	}


	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	
}

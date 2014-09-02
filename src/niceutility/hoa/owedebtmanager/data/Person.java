package niceutility.hoa.owedebtmanager.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Person {
	
	@DatabaseField(generatedId=true)
	private int pId;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private String msidsn;
	
	

	public Person(int pId, String name, String msidsn) {
		super();
		this.pId = pId;
		this.name = name;
		this.msidsn = msidsn;
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
	
	
}

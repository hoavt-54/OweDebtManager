

package niceutility.hoa.owedebtmanager.data;

import java.math.BigDecimal;
import java.util.Date;

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
	@DatabaseField(generatedId=true)
	private int debtId;
	
	@DatabaseField
	private boolean isMyDebt;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private BigDecimal debtAmount;
	
	@DatabaseField
	private String comments;
	
	@DatabaseField
	private double interest;
	
	@DatabaseField
	private TypeOfInterest interestType;
	
	//we may change these two attributes to long, instead of Date
	@DatabaseField
	private Date oweDate;
	
	@DatabaseField
	private Date expiredDate;
	
	public int getDebtId() {
		return debtId;
	}

	public void setDebtId(int debtId) {
		this.debtId = debtId;
	}

	public boolean isMyDebt() {
		return isMyDebt;
	}

	public void setMyDebt(boolean isMyDebt) {
		this.isMyDebt = isMyDebt;
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

	public TypeOfInterest getInterestType() {
		return interestType;
	}

	public void setInterestType(TypeOfInterest interestType) {
		this.interestType = interestType;
	}

	public Date getOweDate() {
		return oweDate;
	}

	public void setOweDate(Date oweDate) {
		this.oweDate = oweDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public enum TypeOfInterest {
		daily, monthly
	}
}

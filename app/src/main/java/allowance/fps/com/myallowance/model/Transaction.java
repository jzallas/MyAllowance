package allowance.fps.com.myallowance.model;

import java.util.Date;

public class Transaction {

  private String mTitle;
  private String mDescription;
  private Double mAmountUSD;
  private Date mDate;

  public Transaction(String title, String description, Double amountUSD, Date date) {
    mTitle = title;
    mDescription = description;
    mAmountUSD = amountUSD;
    mDate = date;
  }

  public String getTitle() {
    return mTitle;
  }

  public String getDescription() {
    return mDescription;
  }

  public Double getAmountUSD() {
    return mAmountUSD;
  }

  public Date getDate() {
    return mDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Transaction that = (Transaction) o;

    if (mTitle != null ? !mTitle.equals(that.mTitle) : that.mTitle != null) {
      return false;
    }
    if (mDescription != null ? !mDescription.equals(that.mDescription)
        : that.mDescription != null) {
      return false;
    }
    if (mAmountUSD != null ? !mAmountUSD.equals(that.mAmountUSD) : that.mAmountUSD != null) {
      return false;
    }
    return !(mDate != null ? !mDate.equals(that.mDate) : that.mDate != null);

  }

  @Override
  public int hashCode() {
    int result = mTitle != null ? mTitle.hashCode() : 0;
    result = 31 * result + (mDescription != null ? mDescription.hashCode() : 0);
    result = 31 * result + (mAmountUSD != null ? mAmountUSD.hashCode() : 0);
    result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
    return result;
  }
}

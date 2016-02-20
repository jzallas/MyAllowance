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
}

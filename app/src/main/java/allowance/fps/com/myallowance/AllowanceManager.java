package allowance.fps.com.myallowance;

import java.util.Date;
import java.util.List;

import allowance.fps.com.myallowance.model.Transaction;

public interface AllowanceManager {

  void setStartDate(Date date);
  Date getStartDate();

  Date getEndDate();

  void setAllocatedHobbyAmount(Double amount);
  Double getAllocatedHobbyAmount();

  Double getTotalAllowedAmount();

  Double getTotalSaved();

  boolean performTransaction(Transaction transaction);

  boolean editTransaction(Transaction transaction);

  boolean deleteTransaction(Transaction transaction);

  List<Transaction> getLatestTenTransactions();


}

package allowance.fps.com.myallowance;

public class SavingManager {

  private double mTotalSaved = 0;

  public SavingManager() {
  }

  /**
   * get the total saved value
   */
  public Double getTotalSavedValue() {
    return mTotalSaved;
  }

  /**
   * Exchange your old allowance for a new allowance
   * @param allowanceWallet the old wallet
   * @return a new wallet
   */
  public AllowanceWallet getNewAllowance(AllowanceWallet allowanceWallet){
    if (allowanceWallet != null) {
      mTotalSaved += allowanceWallet.getRemainingAmount();
    }
    return new AllowanceWallet();
  }
}

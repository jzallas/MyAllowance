package allowance.fps.com.myallowance;

public class AllowanceWallet {

  private static final double ALLOWED_TOTAL = 250.0;

  private double mRemaining = 0;

  public AllowanceWallet() {
    mRemaining = ALLOWED_TOTAL;
  }

  public Double getTotalAllowedAmount() {
    return ALLOWED_TOTAL;
  }

  public Double getRemainingAmount() {
    return mRemaining;
  }

  public void withdraw(Double value) {
    mRemaining -= value;
  }

  public void deposit(Double value) {
    mRemaining += value;
  }
}

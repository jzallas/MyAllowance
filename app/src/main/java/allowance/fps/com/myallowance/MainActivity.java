package allowance.fps.com.myallowance;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import allowance.fps.com.myallowance.ui.AccountReviewFragment;
import allowance.fps.com.myallowance.ui.AccountSetupFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FinanceManager financeManager = new FinanceManager(this);
        if (financeManager.getStartDate() == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, AccountSetupFragment.newInstance())
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, AccountReviewFragment.newInstance())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

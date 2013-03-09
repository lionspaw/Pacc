package osobny.uctovnik.activities;

import java.util.List;

import osobny.uctovnik.async.AsyncGraphTask;
import osobny.uctovnik.async.AsyncGraphTask.OnPohybyForGraphSelected;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.fragments.GrafFragment;
import osobny.uctovnik.objects.PohybForGraphObject;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class GrafActivity extends SherlockFragmentActivity implements OnPohybyForGraphSelected {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			finish();
			return;
		}
		setContentView(R.layout.activity_graf);
		String where = (String) getIntent().getExtras().get(getResources().getString(R.string.intent_msg_list_pohyb));
		if (where != null) {
			AsyncGraphTask asyncGraphTask = new AsyncGraphTask(new PohybDataSource(getBaseContext()), this);
			asyncGraphTask.execute(where);
		}
		initActionBarSherlock();
	}
	
	private void initActionBarSherlock() {
    	getSupportActionBar().setHomeButtonEnabled(true);
    }
	
	@Override
	public void onPohybyForGraphSelected(List<PohybForGraphObject> result) {
		GrafFragment grafFragment = (GrafFragment) getSupportFragmentManager()
				.findFragmentById(R.id.fragment_graf);
		if (grafFragment != null && grafFragment.isAdded()) {
			grafFragment.setPohyby(result);
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			 Intent i = new Intent(this, MainMenuActivity.class);
			 i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 startActivity(i);
			 return true;
		default:
			break;
		}
		return true;
	}
}

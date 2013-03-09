package osobny.uctovnik.activities;

import osobny.uctovnik.fragments.HelpFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class HelpActivity extends SherlockFragmentActivity {

	public static final int HELP_PAGE_COUNT = 5;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		initActionBarSherlock();
		mPager = (ViewPager) findViewById(R.id.help_pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
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
	
	private void initActionBarSherlock() {
    	getSupportActionBar().setHomeButtonEnabled(true);
    }
	
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager manager) {
			super(manager);
		}
		
		@Override
		public Fragment getItem(int position) {
			return new HelpFragment(position);
		}
		
		@Override
		public int getCount() {
			return HELP_PAGE_COUNT;
		}
	}
}

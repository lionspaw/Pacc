package osobny.uctovnik.fragments;

import osobny.uctovnik.activities.HelpActivity;
import osobny.uctovnik.activities.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class HelpFragment extends SherlockFragment {
	
	private int pageNumber;
	
	public HelpFragment() {
	}
	
	public HelpFragment(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		ViewGroup  rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_help, container, false);
		
		TextView caption = (TextView) rootView.findViewById(R.id.help_caption);
		TextView text = (TextView) rootView.findViewById(R.id.help_text);
		TextView pageTxt = (TextView) rootView.findViewById(R.id.help_page_num);
		setHelp(caption, text, pageTxt);
		return rootView;
	}
	
	private void setHelp(TextView caption, TextView text, TextView pageTxt) {
		pageTxt.setText((pageNumber + 1) + "/" + HelpActivity.HELP_PAGE_COUNT);
		caption.setText(getResources().getString(getResources().getIdentifier("help_caption_" + pageNumber, "string", "osobny.uctovnik.activities")));
		text.setText(getResources().getString(getResources().getIdentifier("help_text_" + pageNumber, "string", "osobny.uctovnik.activities")));
	}
}

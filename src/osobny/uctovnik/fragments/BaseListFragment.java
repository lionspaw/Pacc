package osobny.uctovnik.fragments;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.dialogs.BaseDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;


public abstract class BaseListFragment extends SherlockListFragment {
	private ProgressBar progressBar = null;
	private TextView emptyText;
	private Integer listLayout;
	protected BaseDialog dialog;
	
	public BaseListFragment(Integer listLayout) {
		this.listLayout = listLayout;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	Bundle savedInstanceState) {
		View view = inflater.inflate(listLayout == null ? R.layout.fragment_list : listLayout, null);
		progressBar = (ProgressBar) view.findViewById(R.id.fragment_list_progress_bar);
		emptyText = (TextView) view.findViewById(android.R.id.empty);
		setHasOptionsMenu(true);		
		return view;
	}
		
	@Override
	public void onPause() {
		super.onPause();
		if (dialog != null && dialog.isAdded()) {
			dialog.dismiss();
		}
	}
	
	public void showProgressBar(boolean show) {
		 if (progressBar != null && this.isInLayout()) {
			 progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
			 emptyText.setText(show ? getString(R.string.selecting_data) : getString(R.string.empty_list));
		 }
	 }
	
	public abstract Object getListener();
	
	/*
	 * Volany pre update, popr. delete, insert aby adapter zobrazenych fragmentbol bol refreshnuty
	 * */
	public abstract void onItemUpdate();
	public abstract void onItemAdd();
	public abstract void onItemDelete();
}

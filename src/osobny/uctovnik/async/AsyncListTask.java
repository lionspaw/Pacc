package osobny.uctovnik.async;

import java.util.ArrayList;
import java.util.List;

import osobny.uctovnik.adapters.BaseAdapter;
import osobny.uctovnik.datasources.BaseDataSource;
import osobny.uctovnik.fragments.BaseListFragment;
import osobny.uctovnik.objects.IdObject;
import android.os.AsyncTask;


/*
 * Trieda na asynchronne selektovanie udajov do ListActivities.
 * Naplni activity adapter s udajmi podla where podmienky
 * Zobrazi progress bar na zaciatku doInBackground a schova v onPostExecute
 * */
public class AsyncListTask<O extends IdObject> extends AsyncTask<String, Void, List<O>> {

	private BaseDataSource<O> mDataSource;
	private BaseAdapter<O> mAdapter;
	private BaseListFragment fragment;
	
	public AsyncListTask(BaseListFragment fragment, BaseDataSource<O> dataSource, BaseAdapter<O> adapter) {
		this.mDataSource = dataSource;
		this.mAdapter = adapter;
		this.fragment = fragment;
	}
	//TODO ASYNC TASK BY NEMAL PRIAMO KOMUNIKOVAT S FRAGMENTOM ALE CEZ ACTIVITY
	
	@Override
	protected List<O> doInBackground(String... wheres) {
		if (fragment.isInLayout()) {
			fragment.getActivity().runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					fragment.showProgressBar(true); //zobrazenie progressBar-u				
				}
			});
		}
		List<O> retVal = new ArrayList<O>();
		this.mDataSource.open();
		for (String where : wheres) {
			 retVal.addAll(this.mDataSource.getAll(where)); //selektovanie udajov
		}
		this.mDataSource.close();
		
		return retVal;
	}
	
	@Override
    protected void onPostExecute(List<O> result) {
		mAdapter.clear();
		for (O item : result) { //Nie je mozne pouzivat mAdapter.addAll() -> je to od api 11.
			mAdapter.add(item);
		}
		//if (fragment.isAdded()) {
			fragment.showProgressBar(false);//schovanie progressBar-u
		//}
    }
}



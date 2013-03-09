package osobny.uctovnik.async;

import osobny.uctovnik.adapters.BaseAdapter;
import osobny.uctovnik.datasources.BaseDataSource;
import osobny.uctovnik.objects.IdObject;
import android.os.AsyncTask;


/*
 * Trieda na asynchronne insertovanie udajov
 * */
public class AsyncInsertTask<O extends IdObject> extends AsyncTask<O, O, Void> {

	private BaseDataSource<O> mDataSource;
	BaseAdapter<O> mAdapter;
	
	public AsyncInsertTask(BaseDataSource<O> dataSource, BaseAdapter<O> adapter) {
		this.mDataSource = dataSource;
		this.mAdapter = adapter;
	}
	
	@Override
	protected Void doInBackground(O... objects) {
		this.mDataSource.open();
		for (O object : objects) {
			object.setId(this.mDataSource.create(object));
			publishProgress(object);
		}
		this.mDataSource.close();
		return null;
	}
	
	@Override
	protected void onProgressUpdate(O... progress) {
		for (O object: progress) {
			mAdapter.add(object);
		}
	}
	
}

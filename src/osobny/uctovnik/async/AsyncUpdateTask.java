package osobny.uctovnik.async;

import osobny.uctovnik.adapters.BaseAdapter;
import osobny.uctovnik.datasources.BaseDataSource;
import osobny.uctovnik.objects.IdObject;
import android.os.AsyncTask;

public class AsyncUpdateTask<O extends IdObject> extends AsyncTask<O, O, Void> {

	private BaseDataSource<O> mDataSource;
	BaseAdapter<O> mAdapter;
	
	public AsyncUpdateTask(BaseDataSource<O> dataSource, BaseAdapter<O> adapter) {
		this.mDataSource = dataSource;
		this.mAdapter = adapter;
	}
	
	@Override
	protected Void doInBackground(O... objects) {
		this.mDataSource.open();
		for (O object : objects) {
			this.mDataSource.edit(object);
		}
		this.mDataSource.close();
		return null;
	}	
}

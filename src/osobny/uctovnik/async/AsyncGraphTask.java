package osobny.uctovnik.async;

import java.util.ArrayList;
import java.util.List;

import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.objects.PohybForGraphObject;
import android.os.AsyncTask;

public class AsyncGraphTask extends AsyncTask<String, Void, List<PohybForGraphObject>> {

	public interface OnPohybyForGraphSelected {
		public void onPohybyForGraphSelected(List<PohybForGraphObject> result);
	}
	
	private PohybDataSource pohybDataSource;
	private OnPohybyForGraphSelected activityForGraph;
	
	public AsyncGraphTask(PohybDataSource dataSource, OnPohybyForGraphSelected activityForGraph) {
		this.pohybDataSource = dataSource;
		this.activityForGraph = activityForGraph;
	}
	
	@Override
	protected List<PohybForGraphObject> doInBackground(String... wheres) {
		List<PohybForGraphObject> retVal = new ArrayList<PohybForGraphObject>();
		this.pohybDataSource.open();
		for (String where : wheres) {
			 retVal.addAll(this.pohybDataSource.getListForGraph(where)); //selektovanie udajov
		}
		this.pohybDataSource.close();
		return retVal;
	}
	
	@Override
    protected void onPostExecute(List<PohybForGraphObject> result) {
		activityForGraph.onPohybyForGraphSelected(result);
    }
}
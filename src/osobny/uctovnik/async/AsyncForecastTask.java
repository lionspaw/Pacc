package osobny.uctovnik.async;

import osobny.uctovnik.controllers.ForecastController;
import osobny.uctovnik.controllers.ForecastController.ForecastActivity;
import osobny.uctovnik.objects.ForecastObject;
import android.os.AsyncTask;

public class AsyncForecastTask extends AsyncTask<Integer, Void, ForecastObject> {
	
	private ForecastController controller;
	private ForecastActivity forecastActivity;
	private Long ucetId;
	
	public AsyncForecastTask(ForecastController controller, ForecastActivity forecastActivity, Long id) {
		this.controller = controller;
		this.forecastActivity = forecastActivity;
		this.ucetId = id;
	}		
	
	@Override
	protected ForecastObject doInBackground(Integer... dateIntervals) {
		ForecastObject data = null;
		for (Integer interval : dateIntervals) {
			data = controller.doForecastForInterval(interval, ucetId);			
		}
		return data;
	}
	
	@Override
    protected void onPostExecute(ForecastObject result) {
		if (result != null) {
			this.forecastActivity.repaintForecast(result.date, result.originalValues, result.forecastedValues);
		}
    }
}
package osobny.uctovnik.objects;

import java.util.Calendar;

public class ForecastObject {
	public Calendar date;
	public long[] originalValues;
	public long[] forecastedValues;
	
	public ForecastObject(Calendar date, long[] originalValues, long[] forecastedValues) {
		this.date = date;
		this.originalValues = originalValues;
		this.forecastedValues = forecastedValues;
	}
}
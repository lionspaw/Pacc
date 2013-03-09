package osobny.uctovnik.activities;

import java.util.Calendar;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import osobny.uctovnik.controllers.ForecastController;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.objects.UcetObject;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class ForecastActivity extends SherlockFragmentActivity
	implements ForecastController.ForecastActivity {
	
	private GraphicalView chartView = null;
	private LinearLayout linear;	
	ForecastController controller;
	Spinner spinnerUcty;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);
		initActionBarSherlock();
		controller = new ForecastController(this, getApplicationContext());
		
		spinnerUcty = (Spinner) findViewById(R.id.forecast_ucty);
		List<UcetObject> ucty = controller.getUcty();
		Button generuj = (Button) findViewById(R.id.forecast_btn_forecast);
		ArrayAdapter<UcetObject> adapter = new ArrayAdapter<UcetObject>(getBaseContext(), android.R.layout.simple_list_item_1, ucty);
		spinnerUcty.setAdapter(adapter);
		generuj.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ForecastActivity.this.forecast();
			}
		});
		linear = (LinearLayout) findViewById(R.id.forecast_graf);
		if (ucty.isEmpty()) {
			generuj.setEnabled(false);
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.forecast_err_no_ucet), Toast.LENGTH_LONG).show();
			return;
		} else {
			forecast();
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
	
	private void initActionBarSherlock() {
    	getSupportActionBar().setHomeButtonEnabled(true);
    }
	
	private void forecast() {
		UcetObject ucet = (UcetObject) spinnerUcty.getSelectedItem();
		//RadioButton rb1m = (RadioButton) findViewById(R.id.forecast_obdobie_1m);
		RadioButton rb3m = (RadioButton) findViewById(R.id.forecast_obdobie_3m);
		RadioButton rb6m = (RadioButton) findViewById(R.id.forecast_obdobie_6m);
		int pocetMesiacov = 1;
		if (rb3m.isChecked()) {
			pocetMesiacov = 3;
		} else if (rb6m.isChecked()) {
			pocetMesiacov = 6;
		}		
		controller.predpoved(ucet.getId(), pocetMesiacov);
	}
	
	public void repaintForecast(Calendar odDatum, long[] povodne, long[] predpovedane) {
		TimeSeries series_original = new TimeSeries(getResources().getString(R.string.text_original));
		TimeSeries series_forecasted = new TimeSeries(getResources().getString(R.string.text_forecasted));
		
		for (int i = 0; i < povodne.length; i++) {		
			series_original.add(i, Constants.sumaToDouble(povodne[i]));
		}
		
		for (int i = 0; i < predpovedane.length; i++) {
			series_forecasted.add(i + povodne.length - 1, Constants.sumaToDouble(predpovedane[i]));
		}
		
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(0, series_original);
		dataset.addSeries(1, series_forecasted);
		
		XYSeriesRenderer sRenderer_orig = new XYSeriesRenderer();
		sRenderer_orig.setColor(getResources().getColor(R.color.graf_orig));
		sRenderer_orig.setPointStyle(PointStyle.CIRCLE);
		sRenderer_orig.setFillPoints(true);
		sRenderer_orig.setLineWidth(3f);
		sRenderer_orig.setFillBelowLine(true);
		sRenderer_orig.setFillBelowLineColor(getResources().getColor(R.color.graf_orig_fill));
		
		XYSeriesRenderer sRenderer_forec = new XYSeriesRenderer();
		sRenderer_forec.setColor(getResources().getColor(R.color.graf_forec));
		sRenderer_forec.setPointStyle(PointStyle.CIRCLE);
		sRenderer_forec.setFillPoints(true);
		sRenderer_forec.setLineWidth(3f);
		sRenderer_forec.setFillBelowLine(true);
		sRenderer_forec.setFillBelowLineColor(getResources().getColor(R.color.graf_forec_fill));
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

		mRenderer.addSeriesRenderer(0, sRenderer_orig);
		mRenderer.addSeriesRenderer(1, sRenderer_forec);
		
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.GRAY);
		mRenderer.setMarginsColor(Color.WHITE);
		
		mRenderer.addXTextLabel(0, "'" + Constants.getDatum(odDatum).substring(2));
		odDatum.add(Calendar.DAY_OF_YEAR, povodne.length);
		mRenderer.addXTextLabel(povodne.length - 1, "'" + Constants.getDatum(odDatum).substring(2));
		odDatum.add(Calendar.DAY_OF_YEAR, predpovedane.length);
		mRenderer.addXTextLabel(povodne.length + predpovedane.length - 2, "'" + Constants.getDatum(odDatum).substring(2));
		
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setXLabels(0);
		mRenderer.setYLabelsColor(0, Color.WHITE);
		mRenderer.setYLabelsAlign(Align.LEFT);
		mRenderer.setLabelsTextSize(14);
		mRenderer.setLegendTextSize(20);
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setMargins(new int[] {25,30,0,30});//margin pre graf, kvoli title
		mRenderer.setLabelsColor(Color.BLACK);
		if (chartView != null) {
			linear.removeView(chartView);
		}
		chartView = ChartFactory.getLineChartView(getApplicationContext(), dataset, mRenderer);
		linear.addView(chartView);
	}
}

package osobny.uctovnik.fragments;

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.objects.PohybForGraphObject;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class GrafFragment extends Fragment {

	private GraphicalView chartView = null;
	private LinearLayout linear;
	private List<PohybForGraphObject> pohyby = new ArrayList<PohybForGraphObject>();

	private List<Integer> listX = new ArrayList<Integer>();
	private List<Double> listCr = new ArrayList<Double>();
	private List<Double> listDb = new ArrayList<Double>();
	private List<String> listDatum = new ArrayList<String>();
	private boolean emptyCr;
	private boolean emptyDb;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_graf, container, false);
		linear = (LinearLayout) view.findViewById(
				R.id.graf_layout);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	public void setPohyby(List<PohybForGraphObject> pohyby) {
		this.pohyby = pohyby;
		updateGraph();
	}

	private void parsePohybList() {
		listX.clear();
		listCr.clear();
		listDb.clear();
		listDatum.clear();
		emptyCr = true;
		emptyDb = true;
		for (int i = 0, j = 0; i < pohyby.size(); j++) { // i sa inkrementuje rucne!
			PohybForGraphObject pohyb = pohyby.get(i);
			String datum = Constants.getDatum(pohyb.getDatCas());
			listX.add(j); // pridame x hodnotu
			listDatum.add("'" + datum.substring(2)); // pridame popis k x + skracovanie z 2013 na '13
			PohybForGraphObject pohybNext = (i + 1) < pohyby.size() ? pohyby.get(i + 1)
					: null;

			if (pohybNext != null) { // existuje dalsi zaznam
				if (datum.equals(Constants.getDatum(pohybNext.getDatCas()))) {
					// dalsi zaznam ma rovnaky datum len iny typ (CR/DB)
					if (pohyb.isKredit()) {
						Double cr = Constants.sumaToDouble(pohyb.getSuma());
						if (emptyCr && !cr.equals(0.0d)) {
							emptyCr = false;
						}
						listCr.add(cr);
						
						Double db = Constants.sumaToDouble(pohybNext.getSuma());
						if (emptyDb && !db.equals(0.0d)) {
							emptyDb = false;
						}
						listDb.add(db);
					} else {
						Double db = Constants.sumaToDouble(pohyb.getSuma());
						if (emptyDb && !db.equals(0.0d)) {
							emptyDb = false;
						}
						listDb.add(db);
												
						Double cr = Constants.sumaToDouble(pohybNext.getSuma());
						if (emptyCr && !cr.equals(0.0d)) {
							emptyCr = false;
						}
						listCr.add(cr);
					}

				} else { // rozny datum
					pohybNext = null; // kvoli inkrementovanie i
					if (pohyb.isKredit()) {
						Double cr = Constants.sumaToDouble(pohyb.getSuma());
						if (emptyCr && !cr.equals(0.0d)) {
							emptyCr = false;
						}
						listCr.add(cr);
						listDb.add(0.0d);
					} else {
						Double db = Constants.sumaToDouble(pohyb.getSuma());
						if (emptyDb && !db.equals(0.0d)) {
							emptyDb = false;
						}
						listDb.add(db);
						listCr.add(0.0d);
					}
				}
			} else { // neexistuje dalsi zaznam
				if (pohyb.isKredit()) {
					Double cr = Constants.sumaToDouble(pohyb.getSuma());
					if (emptyCr && !cr.equals(0.0d)) {
						emptyCr = false;
					}
					listCr.add(cr);
					listDb.add(0.0d);
				} else {
					Double db = Constants.sumaToDouble(pohyb.getSuma());
					if (emptyDb && !db.equals(0.0d)) {
						emptyDb = false;
					}
					listDb.add(db);
					listCr.add(0.0d);
				}
			}			
			if (pohybNext == null) { // rucna inkrementacia i
				i++;
			} else {
				i += 2;
			}
		}
	}

	private void updateGraph() {	
		parsePohybList(); //title pre graf
		
		StringBuilder graphTitle = new StringBuilder();
		graphTitle.append(getResources().getString(R.string.graf_title));
		if (listDatum.size() > 0) {
			graphTitle.append(" " + getResources().getString(R.string.graf_title_od) + " " + listDatum.get(0));
			graphTitle.append(" " + getResources().getString(R.string.graf_title_do) + " " + listDatum.get(listDatum.size() -1));
		}
		
		TimeSeries series_kr = new TimeSeries(getResources().getString(R.string.text_cr));
		TimeSeries series_db = new TimeSeries(getResources().getString(R.string.text_db));
		TimeSeries series_sum = new TimeSeries(getResources().getString(R.string.text_suma));
		
		for (int i = 0; i < listX.size(); i++) {
			if (!emptyCr) {
				series_kr.add(listX.get(i), listCr.get(i));
			}
			if (!emptyDb) {
				series_db.add(listX.get(i), listDb.get(i));
			}
			if (!emptyCr && !emptyDb) {
				series_sum.add(listX.get(i), listCr.get(i) + listDb.get(i));
			}
		}
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		if (!emptyCr) {
			dataset.addSeries(0, series_kr);
		}
		if (!emptyDb) {
			dataset.addSeries(emptyCr ? 0 : 1, series_db);
		}
		if (!emptyCr && !emptyDb) {
			dataset.addSeries(2, series_sum);
		}

		String year = null;
		int pos = 0;
		/*Skracovanie nazvov, ak je rok rovnaky, tak to nevypisujem*/
		for (String datum : listDatum) {
			if (year == null) {
				year = datum.substring(0, datum.indexOf('.'));
			} else {
				if (datum.matches(year+"\\.[0-9]{2}\\.[0-9]{2}")) {
					listDatum.set(pos, datum.substring(datum.indexOf('.')+1));
				} else {
					year = datum.substring(0, datum.indexOf('.'));
				}
			}
			pos++;
		}
		
		XYSeriesRenderer sRenderer_kr = new XYSeriesRenderer();
		sRenderer_kr.setColor(getResources().getColor(R.color.graf_kredit));
		sRenderer_kr.setPointStyle(PointStyle.CIRCLE);
		sRenderer_kr.setFillPoints(true);
		sRenderer_kr.setLineWidth(3f);
		
		XYSeriesRenderer sRenderer_db = new XYSeriesRenderer();
		sRenderer_db.setColor(getResources().getColor(R.color.graf_debet));
		sRenderer_db.setPointStyle(PointStyle.CIRCLE);
		sRenderer_db.setFillPoints(true);
		sRenderer_db.setLineWidth(3f);
		
		XYSeriesRenderer sRenderer_sum = new XYSeriesRenderer();
		sRenderer_sum.setColor(getResources().getColor(R.color.graf_sum));
		sRenderer_sum.setPointStyle(PointStyle.CIRCLE);
		sRenderer_sum.setFillPoints(true);
		sRenderer_sum.setLineWidth(3f);
		
		XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

		if (!emptyCr) {
			mRenderer.addSeriesRenderer(0, sRenderer_kr);
		}
		if (!emptyDb) {
			mRenderer.addSeriesRenderer(emptyCr ? 0 : 1, sRenderer_db);
		}
		if (!emptyCr && !emptyDb) {
			mRenderer.addSeriesRenderer(2, sRenderer_sum);
		}
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.GRAY);
		mRenderer.setMarginsColor(Color.WHITE);
		
		for (int i = 0; i < listX.size(); i++) {
			mRenderer.addXTextLabel(listX.get(i), listDatum.get(i)); //TODO ritkitani ha tul sok
		}
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setXLabelsAlign(Align.CENTER);
		mRenderer.setXLabels(0);
		mRenderer.setYLabelsColor(0, Color.WHITE);
		mRenderer.setYLabelsAlign(Align.LEFT);
		mRenderer.setLabelsTextSize(14);
		mRenderer.setLegendTextSize(20);
		mRenderer.setChartTitle(graphTitle.toString());
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setMargins(new int[] {25,30,0,30});//margin pre graf, kvoli title
		mRenderer.setLabelsColor(Color.BLACK);
		if (isInLayout() && (!emptyCr || !emptyDb)) {
			if (chartView != null) {
				linear.removeView(chartView);
			}
			chartView = ChartFactory.getLineChartView(getActivity(), dataset, mRenderer);
			linear.addView(chartView);
		}
	}
}

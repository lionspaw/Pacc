package osobny.uctovnik.controllers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.adapters.PohybAdapter;
import osobny.uctovnik.async.AsyncGraphTask;
import osobny.uctovnik.async.AsyncGraphTask.OnPohybyForGraphSelected;
import osobny.uctovnik.async.AsyncInsertTask;
import osobny.uctovnik.async.AsyncListTask;
import osobny.uctovnik.async.AsyncSelectTask;
import osobny.uctovnik.async.AsyncSelectTask.SELECT_TYPE;
import osobny.uctovnik.async.AsyncSelectTask.SelectType;
import osobny.uctovnik.async.AsyncUpdateTask;
import osobny.uctovnik.datasources.KategoriaDataSource;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.datasources.UcetDataSource;
import osobny.uctovnik.fragments.PohybListFragment;
import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.helpers.GlobalParams;
import osobny.uctovnik.objects.KategoriaObject;
import osobny.uctovnik.objects.PohybForGraphObject;
import osobny.uctovnik.objects.PohybObject;
import osobny.uctovnik.objects.UcetObject;
import android.app.Activity;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class PohybController extends BaseController<PohybListFragment, PohybObject> {

	public Activity context;
	private PohybAdapter adapter;
	private PohybDataSource dataSource;	
	
	public PohybController(PohybListFragment fragment) {
		super(fragment);
		this.context = fragment.getActivity();
		this.adapter = new PohybAdapter(this, context,
				new ArrayList<PohybObject>());
		this.dataSource = new PohybDataSource(context);
	}
	
	@Override
	public PohybAdapter getAdapter() {
		return this.adapter;
	}
	
	@Override
	public PohybDataSource getDataSource() {
		return this.dataSource;
	}

	public PohybAdapter fillDataToList(String where) {
		this.where = where;	
		refreshAdapter();		
		fillSum();
		return adapter;
	}
	
	public void refreshAdapter() {
		if (GlobalParams.isAsync) {
			AsyncListTask<PohybObject> myAsyncTask = new AsyncListTask<PohybObject>(
					fragment, dataSource, adapter);
			myAsyncTask.execute(where);
		} else {
			syncFillList(where);
		}
	}
	
	/*
	 * Na vyplnenie sumy CR a DB
	 * */
	public void fillSum() {
		TextView sumCr = (TextView) fragment.getView().findViewById(R.id.fragment_list_pohyb_sum_kredit);
		TextView sumDb = (TextView) fragment.getView().findViewById(R.id.fragment_list_pohyb_sum_debet);
		
		if (GlobalParams.isAsync) {
			AsyncSelectTask myAsyncSelect = new AsyncSelectTask(adapter);
			SelectType cr = myAsyncSelect.new SelectType(SELECT_TYPE.POHYB_SUM_CR, where, sumCr);
			SelectType db = myAsyncSelect.new SelectType(SELECT_TYPE.POHYB_SUM_DB, where, sumDb); 
			myAsyncSelect.execute(cr, db);
		} else {
			dataSource.open();
			final String pohyb_sum_cr;
			final String pohyb_sum_db;
			pohyb_sum_cr = Constants.sumaToString(Long.valueOf(dataSource.getSumPohyb(where, true)));
			pohyb_sum_db = Constants.sumaToString(Long.valueOf(dataSource.getSumPohyb(where, false)));
			dataSource.close();
			sumCr.setText(fragment.getResources().getString(R.string.suma) + " " + pohyb_sum_cr);
			sumDb.setText(fragment.getResources().getString(R.string.suma) + " " + pohyb_sum_db);
		}
	}
	
	public void setText(TextView view, String text) {
		view.setText(text);
	}

	public List<KategoriaObject> getKategorie() {
		KategoriaDataSource kategoriaDataSource = new KategoriaDataSource(
				context);
		kategoriaDataSource.open();
		List<KategoriaObject> kategoriaObjects = kategoriaDataSource
				.getAll(null);
		kategoriaDataSource.close();
		return kategoriaObjects;
	}
	
	public List<UcetObject> getUcty() {
		UcetDataSource ucetDataSource = new UcetDataSource(context);
		ucetDataSource.open();
		List<UcetObject> ucetObjects = ucetDataSource.getAll(null);
		ucetDataSource.close();
		return ucetObjects;
	}

	public void insert(DatePicker date, TimePicker time, String suma,
			String poznamka, Boolean isKredit, KategoriaObject kat,
			UcetObject ucet) {
		try {
			PohybObject object = getObjectFromInput(null, date, time, suma, poznamka, isKredit, kat, ucet);
			if (GlobalParams.isAsync) {
				AsyncInsertTask<PohybObject> myAsyncTask = new AsyncInsertTask<PohybObject>(
						dataSource, adapter);
				myAsyncTask.execute(object);
			} else {
				syncInsert(object);
			}
			fragment.onItemAdd();
		} catch (ParseException ex) {
			Log.d("HIBA_POHYBC", ex.getLocalizedMessage());
		}
	}
	
	public void insertPrevod(String suma, UcetObject ucetZ, UcetObject ucetNa, KategoriaObject kategoria) {
		Calendar datCas = Calendar.getInstance();
		PohybObject pohybZ = new PohybObject(null, datCas, "".equals(suma) ? 0L 	: Constants.sumaToLong(suma), 
				context.getResources().getString(R.string.add_prevod_poznamka_na) + " " + ucetNa.getNazov(),
				false, kategoria, ucetZ);
		PohybObject pohybNa = new PohybObject(null, datCas, "".equals(suma) ? 0L : Constants.sumaToLong(suma),
				context.getResources().getString(R.string.add_prevod_poznamka_z) + " " + ucetZ.getNazov(),
				true, kategoria, ucetNa);

		if (GlobalParams.isAsync) {
			AsyncInsertTask<PohybObject> myAsyncTask = new AsyncInsertTask<PohybObject>(
					dataSource, adapter);
			myAsyncTask.execute(pohybZ, pohybNa);
		} else {
			syncInsert(pohybZ);
			syncInsert(pohybNa);
		}
		fragment.onItemAdd();

	}
	
	private PohybObject getObjectFromInput(String sId, DatePicker date, TimePicker time, String suma,
			String poznamka, Boolean isKredit, KategoriaObject kat,
			UcetObject ucet) throws ParseException {
		Long id = sId == null ? null : Long.decode(sId);
		Calendar datCas = Constants.getCalendar(date.getYear(),
				(date.getMonth()), date.getDayOfMonth(),
				time.getCurrentHour(), time.getCurrentMinute());
		Long pohybSuma = "".equals(suma) ? 0 : Constants.sumaToLong(suma);
		return new PohybObject(id, datCas, pohybSuma, poznamka, isKredit, kat, ucet);
	}
	
	public void fillGraph(OnPohybyForGraphSelected activityForGraph, String where) {
		if (GlobalParams.isAsync) {
			AsyncGraphTask asyncGraphTask = new AsyncGraphTask(dataSource, activityForGraph);
			asyncGraphTask.execute(where);
		} else {
			List<PohybForGraphObject> result = new ArrayList<PohybForGraphObject>();
			dataSource.open();
			result.addAll(dataSource.getListForGraph(where)); //selektovanie udajov
			dataSource.close();
			activityForGraph.onPohybyForGraphSelected(result);
		}
	}
	
	public void update(String sId, DatePicker date, TimePicker time, String suma,
			String poznamka, Boolean isKredit, KategoriaObject kat,
			UcetObject ucet) {
		try {
			PohybObject object = getObjectFromInput(sId, date, time, suma, poznamka, isKredit, kat, ucet);
			if (GlobalParams.isAsync) {
				AsyncUpdateTask<PohybObject> myAsynchTask = new AsyncUpdateTask<PohybObject>(dataSource, adapter);
				myAsynchTask.execute(object);
			} else {
				syncUpdate(object);
			}
			int itemIndex = this.adapter.getIndexById(object.getId());
			this.adapter.remove(this.adapter.getById(object.getId()));
			this.adapter.insert(object, itemIndex);
			fragment.onItemUpdate();
		} catch (ParseException ex) {
			Log.d("HIBA_POHYBC", ex.getLocalizedMessage());
		}
	}

	@Override
	public void delete() {
		String sId = ((TextView) this.swipedItem
				.findViewById(R.id.pohyb_row_id)).getText().toString();
		Long id = Long.decode(sId);
		PohybObject pohyb = adapter.getById(id);
		if (pohyb == null) {
			// ERROR NEEXISTUJE POHYB
			return;
		}
		dataSource.open();
		dataSource.remove(pohyb);
		dataSource.close();
		adapter.remove(pohyb);
		swipedItem = null;
		fragment.onItemDelete();
	}

	@Override
	public void edit() {
		String sId = ((TextView) swipedItem.findViewById(R.id.pohyb_row_id)).getText().toString();
		Long id = Long.decode(sId);
		PohybObject pohyb = adapter.getById(id);
		fragment.showEdit(fragment.getFragmentManager(), pohyb);
	}

	@Override
	public void clicked() {
		this.fragment.getListener().onPohybSelected();
	}
}

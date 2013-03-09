package osobny.uctovnik.controllers;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import osobny.uctovnik.activities.R;
import osobny.uctovnik.datasources.BankaDataSource;
import osobny.uctovnik.datasources.KategoriaDataSource;
import osobny.uctovnik.datasources.PohybDataSource;
import osobny.uctovnik.datasources.UcetDataSource;
import osobny.uctovnik.helpers.GlobalParams;
import osobny.uctovnik.helpers.MySQLiteHelper;
import osobny.uctovnik.objects.BankaObject;
import osobny.uctovnik.objects.ImpExpObject;
import osobny.uctovnik.objects.KategoriaObject;
import osobny.uctovnik.objects.PohybObject;
import osobny.uctovnik.objects.UcetObject;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class ImportExportController {

	public interface ImportExportActivityInteface {
		public void showMessage(String msg);
	}
	
	private Context context;
	private ImportExportActivityInteface activity;
	private MySQLiteHelper sqlHelper;
	private SQLiteDatabase database;
	
	public ImportExportController(Context context, ImportExportActivityInteface activity) {
		this.context = context;
		this.activity = activity;
	}
	
	public ImpExpObject getDataToExport() {
		ImpExpObject retVal = new ImpExpObject();
		BankaDataSource bds = new BankaDataSource(context);
		bds.open();
		retVal.setBanky(bds.getAll(null));
		bds.close();
		
		UcetDataSource uds = new UcetDataSource(context);
		uds.open();
		retVal.setUcty(uds.getAll(null));
		uds.close();
		
		KategoriaDataSource kds = new KategoriaDataSource(context);
		kds.open();
		retVal.setKategorie(kds.getAll(null));
		kds.close();
		
		PohybDataSource pds = new PohybDataSource(context);
		pds.open();
		retVal.setPohyby(pds.getAll(null));
		pds.close();
		
		if (retVal.getBanky().size() == 0 && retVal.getKategorie().size() == 0 &&
				retVal.getPohyby().size() ==0 && retVal.getUcty().size() ==0) {
			//nie su data na exportovanie
			return null;
		}
		
		return retVal;
	}
	
	public void export(File file) {
		if (GlobalParams.isAsync) {
			AsyncExportTask exportTask = new AsyncExportTask(context, activity);
			exportTask.execute(file);
		} else {
			try {
				Serializer serializer = new Persister();
				ImpExpObject impExpOb = getDataToExport();
				if (impExpOb == null) {
					activity.showMessage(context.getResources().getString(R.string.export_no_data));
					return;
				}
				serializer.write(impExpOb, file);
				activity.showMessage(context.getResources().getString(R.string.export_success) + " " + file.getAbsolutePath());
			} catch (Exception e) {
				activity.showMessage(context.getResources().getString(R.string.export_error));
			}
		}
	}
	
	public void importData(ImpExpObject importedData, boolean clearTables) {
		if (GlobalParams.isAsync) {
			AsyncImportTask importTask = new AsyncImportTask(activity, clearTables);
			importTask.execute(importedData);
		} else {
			activity.showMessage(doImport(importedData, clearTables));
		}
	}
	
	private String doImport(ImpExpObject importedData, boolean clearTables) {
		String retVal = null;
		this.sqlHelper = new MySQLiteHelper(context);
		this.database = sqlHelper.getWritableDatabase();
		this.database.beginTransaction();
		try {
			if (clearTables) {
				this.sqlHelper.clearTables(this.database);
			}
			
			BankaDataSource bds = new BankaDataSource(this.database);
			for (BankaObject banka : importedData.getBanky()) {
				bds.create(banka);
			}
			
			UcetDataSource uds = new UcetDataSource(this.database);
			for (UcetObject ucet : importedData.getUcty()) {
				uds.createFromImport(ucet);
			}
			
			KategoriaDataSource kds = new KategoriaDataSource(this.database);
			for (KategoriaObject kategoria : importedData.getKategorie()) {
				kds.create(kategoria);
			}
			
			PohybDataSource pds = new PohybDataSource(this.database);
			for (PohybObject pohyb : importedData.getPohyby()) {
				pds.createFromImport(pohyb);
			}
			this.database.setTransactionSuccessful();
			retVal = context.getResources().getString(R.string.import_success);
		} catch (SQLiteConstraintException e) {
			retVal = context.getResources().getString(R.string.import_error_constraint);
		} catch (Exception e) {
			retVal = context.getResources().getString(R.string.import_error);
		} finally {
			this.database.endTransaction();
			this.database.close();
			this.sqlHelper.close();
		}
		return retVal;
	}
	
	class AsyncExportTask extends AsyncTask<File, Void, String> {

		private Context impExpContext;
		private ImportExportActivityInteface impExpActivity;
		
		public AsyncExportTask(Context context, ImportExportActivityInteface activity) {
			this.impExpContext = context;
			this.impExpActivity = activity;
		}
		
		@Override
		protected String doInBackground(File... files) {
			for (File file : files) {
				try {
					Serializer serializer = new Persister();
					ImpExpObject impExp = getDataToExport();
					if (impExp == null) {
						return impExpContext.getResources().getString(R.string.export_no_data);
					}
					serializer.write(impExp, file);
					return impExpContext.getResources().getString(R.string.export_success) + " " + file.getAbsolutePath();
				} catch (Exception e) {
					//TODO
					return impExpContext.getResources().getString(R.string.export_error);
				}
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			impExpActivity.showMessage(result);
	    }
	}
	
	class AsyncImportTask extends AsyncTask<ImpExpObject, Void, String> {

		private ImportExportActivityInteface activity;
		private boolean clearTables;
		
		public AsyncImportTask(ImportExportActivityInteface activity, boolean clearTables) {
			this.activity = activity;
			this.clearTables = clearTables;
		}
		
		@Override
		protected String doInBackground(ImpExpObject... importedDatas) {
			for (ImpExpObject importedData : importedDatas) {
				return doImport(importedData, clearTables);
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(String result) {
			activity.showMessage(result);
	    }
	}
}

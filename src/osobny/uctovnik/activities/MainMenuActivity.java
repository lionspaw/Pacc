package osobny.uctovnik.activities;

import java.io.File;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import osobny.uctovnik.controllers.ImportExportController;
import osobny.uctovnik.controllers.ImportExportController.ImportExportActivityInteface;
import osobny.uctovnik.objects.ImpExpObject;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;

public class MainMenuActivity extends SherlockActivity implements
		ImportExportActivityInteface {

	boolean externalStorageAvailable = false;
	boolean externalStorageWriteable = false;

	private ImpExpObject importedData;
	private File resultFile;
	private static final int FILE_SELECT_RESULT = 1;
	private ImportExportController controller;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		controller = new ImportExportController(getApplicationContext(), this);
		Button btnKat = (Button) findViewById(R.id.main_btn_kategoria);
		btnKat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						KategoriaListActivity.class);
				startActivity(intent);
			}
		});

		Button btnBanka = (Button) findViewById(R.id.main_btn_banka);
		btnBanka.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						BankaListActivity.class);
				startActivity(intent);
			}
		});

		Button btnPohyb = (Button) findViewById(R.id.main_btn_pohyb);
		btnPohyb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						PohybListActivity.class);
				startActivity(intent);
			}
		});

		Button btnUcet = (Button) findViewById(R.id.main_btn_ucet);
		btnUcet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(),
						UcetListActivity.class);
				startActivity(intent);
			}
		});

		Button btnImport = (Button) findViewById(R.id.main_btn_import);
		btnImport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				selectFileForImport();
			}
		});

		Button btnExport = (Button) findViewById(R.id.main_btn_export);
		btnExport.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				exportuj(0);
			}
		});

		Button btnPrognoza = (Button) findViewById(R.id.main_btn_prognoza);
		btnPrognoza.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//Toast.makeText(getBaseContext(), getResources().getString(R.string.activity_main_forecast_not_ready),Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getBaseContext(), ForecastActivity.class);
				startActivity(intent);
			}
		});
		
		Button btnHelp = (Button) findViewById(R.id.main_btn_help);
		btnHelp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getBaseContext(), HelpActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	public void exportuj(Integer poradie) {
		checkStorageAvailability();
		if (!externalStorageAvailable || !externalStorageWriteable) {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.export_no_external_storage), Toast.LENGTH_LONG).show();
			return;
		}
		Integer val = poradie;
		resultFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), 
				val == 0 ? "pacc_data.xml" : "pacc_data" + String.valueOf(val) + ".xml");
		if (resultFile.exists()) {
			if (val < 100) { //maximalny nazov bude pacc_data100.xml
				exportuj(++val);
			} else {
				showAlertFileExists().show();
			}
		} else {
			export();
		}
	}
	
	private void checkStorageAvailability() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			externalStorageAvailable = externalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			externalStorageAvailable = true;
			externalStorageWriteable = false;
		} else {
			externalStorageAvailable = externalStorageWriteable = false;
		}
	}
	
	public void selectFileForImport() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("file/*");
		
		if (getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES).size() > 0) {
			startActivityForResult(intent, FILE_SELECT_RESULT);
		} else {
			Toast.makeText(getBaseContext(), getResources().getString(R.string.import_no_app_for_open), Toast.LENGTH_LONG).show();
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case FILE_SELECT_RESULT:
			if (resultCode == RESULT_OK) {
				importuj(data.getData().getPath());
			}
			break;
		}
	}
	
	public void importuj(String file) {
		Serializer serializer = new Persister();
		File source = new File(file);
		try {
			//TODO async?
			importedData = serializer.read(ImpExpObject.class, source);
			showAlertClearDb().show();
		} catch (Exception exp) {
			//TODO
		}
	}
	
	private AlertDialog showAlertClearDb() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.import_dialog_cleardb_text))
			.setTitle(getResources().getString(R.string.import_dialog_cleardb_title));
		
		builder.setPositiveButton(getResources().getString(R.string.clear), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						importData(true);
					}
				});
		builder.setNegativeButton(getResources().getString(R.string.keep), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						importData(false);
					}
				});
		return builder.create();
	}
	
	private AlertDialog showAlertFileExists() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.export_dialog_file_exists_text))
			.setTitle(getResources().getString(R.string.export_dialog_file_exists_title));
		builder.setCancelable(false);
		builder.setPositiveButton(getResources().getString(R.string.overwrite), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						export();
					}
				});
		builder.setNegativeButton(getResources().getString(R.string.no), 
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				});
		return builder.create();
	}
	
	public void importData(boolean clearTables) {
		this.controller.importData(importedData, clearTables);
	}
	
	private void export() {
		this.controller.export(resultFile);
	}
	
	@Override
	public void showMessage(String msg) {
		if (msg != null) {
			Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
		}
	}
}

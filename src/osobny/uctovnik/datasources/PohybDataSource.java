package osobny.uctovnik.datasources;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import osobny.uctovnik.helpers.Constants;
import osobny.uctovnik.objects.PohybForForecastObject;
import osobny.uctovnik.objects.PohybForGraphObject;
import osobny.uctovnik.objects.PohybObject;
import osobny.uctovnik.objects.UcetObject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/*
 * DataSource objekt - obsahuje nazov tabulky a nazvy stlpcov, CREATE statement a 
 * premapovanie databazoveho objetku na POJO
 * */
public class PohybDataSource extends BaseDataSource<PohybObject> {
	public static final String TABLE_NAME = "pohyb";
	
	public static final String DATUM = "datum";
	public static final String CAS = "cas";
	public static final String SUMA = "suma";
	public static final String POZNAMKA = "poznamka";
	public static final String KREDIT = "kredit";
	public static final String UCET_ID = "ucet_id";
	public static final String KATEGORIA_ID = "kategoria_id";
	public static final String[] ALL = {ID, DATUM, CAS, SUMA, POZNAMKA, KREDIT, UCET_ID, KATEGORIA_ID};
	
	public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ DATUM + " TEXT,"
			+ CAS + " TEXT,"
			+ SUMA + " INTEGER,"
			+ POZNAMKA + " TEXT,"
			+ KREDIT + " INTEGER,"
			+ UCET_ID + " INTEGER,"
			+ KATEGORIA_ID + " INTEGER,"
			+ " FOREIGN KEY(" + UCET_ID + ") REFERENCES " + UcetDataSource.TABLE_NAME + "(" + UcetDataSource.ID + ")"
			+ " FOREIGN KEY(" + KATEGORIA_ID + ") REFERENCES " + KategoriaDataSource.TABLE_NAME + "(" + KategoriaDataSource.ID + ")"
			+ ")";
	
	public PohybDataSource(Context context) {
		super(context);
	}
	
	public PohybDataSource(SQLiteDatabase database) {
		super(database);
	}	
	
	@Override
	public Long create(PohybObject oPohyb) {
		ContentValues values = new ContentValues();
		values.put(DATUM, Constants.getDatum(oPohyb.getDatCas()));
		values.put(CAS, Constants.getCas(oPohyb.getDatCas()));
		values.put(SUMA, oPohyb.getSuma());
		values.put(POZNAMKA, oPohyb.getPoznamka());
		values.put(KREDIT, oPohyb.isKredit() ? 1 : 0);
		values.put(UCET_ID, oPohyb.getUcet().getId());
		values.put(KATEGORIA_ID, oPohyb.getKategoria().getId());
		long id = database.insert(TABLE_NAME, null, values);

		UcetDataSource ucetDataSource = new UcetDataSource(database);
		UcetObject ucet = ucetDataSource.get(oPohyb.getUcet().getId());
		ucet.setZostatok(oPohyb.isKredit() ? ucet.getZostatok() + oPohyb.getSuma() : ucet.getZostatok() - oPohyb.getSuma());
		ucet.setDispZostatok(oPohyb.isKredit() ? ucet.getDispZostatok() + oPohyb.getSuma() : ucet.getDispZostatok() - oPohyb.getSuma());
		ucetDataSource.edit(ucet);
		
		return id;
	}
	
	public Long createFromImport(PohybObject oPohyb) throws SQLiteConstraintException {
		ContentValues values = new ContentValues();
		values.put(DATUM, Constants.getDatum(oPohyb.getDatCas()));
		values.put(CAS, Constants.getCas(oPohyb.getDatCas()));
		values.put(SUMA, oPohyb.getSuma());
		values.put(POZNAMKA, oPohyb.getPoznamka());
		values.put(KREDIT, oPohyb.isKredit() ? 1 : 0);
		values.put(UCET_ID, oPohyb.getUcetId());
		values.put(KATEGORIA_ID, oPohyb.getKategoriaId());
		long id = database.insertOrThrow(TABLE_NAME, null, values);

		return id;
	}

	@Override
	public void edit(PohybObject oPohyb) {
		ContentValues values = new ContentValues();
		values.put(DATUM, Constants.getDatum(oPohyb.getDatCas()));
		values.put(CAS, Constants.getCas(oPohyb.getDatCas()));
		values.put(SUMA, oPohyb.getSuma());
		values.put(POZNAMKA, oPohyb.getPoznamka());
		values.put(KREDIT, oPohyb.isKredit() ? 1 : 0);
		values.put(UCET_ID, oPohyb.getUcet().getId());
		values.put(KATEGORIA_ID, oPohyb.getKategoria().getId());

		PohybObject pohybPredZmenou = get(oPohyb.getId());
		UcetDataSource ucetDataSource = new UcetDataSource(database);

		// zmena povodneho uctu
		UcetObject ucetPovodny = ucetDataSource.get(pohybPredZmenou.getUcet()
				.getId());
		ucetPovodny.setZostatok(ucetPovodny.getZostatok()
				+ (pohybPredZmenou.isKredit() ? -pohybPredZmenou.getSuma()
						: pohybPredZmenou.getSuma()));
		ucetPovodny.setDispZostatok(ucetPovodny.getDispZostatok()
				+ (pohybPredZmenou.isKredit() ? -pohybPredZmenou.getSuma()
						: pohybPredZmenou.getSuma()));
		ucetDataSource.edit(ucetPovodny);

		UcetObject ucetNovy = ucetDataSource.get(oPohyb.getUcet().getId());
		ucetNovy.setZostatok(ucetNovy.getZostatok()
				+ (oPohyb.isKredit() ? oPohyb.getSuma() : -oPohyb.getSuma()));
		ucetNovy.setDispZostatok(ucetNovy.getDispZostatok()
				+ (oPohyb.isKredit() ? oPohyb.getSuma() : -oPohyb.getSuma()));
		ucetDataSource.edit(ucetNovy);

		database.update(TABLE_NAME, values, ID + "=" + oPohyb.getId(), null);
	}
	
	public void remove(PohybObject oPohyb) {
		database.delete(getTableName(), ID + "=" + String.valueOf(oPohyb.getId()), null);
		UcetDataSource ucetDataSource = new UcetDataSource(database);
		UcetObject ucet = ucetDataSource.get(oPohyb.getUcet().getId());
		ucet.setZostatok(oPohyb.isKredit() ? ucet.getZostatok() - oPohyb.getSuma() : ucet.getZostatok() + oPohyb.getSuma());
		ucet.setDispZostatok(oPohyb.isKredit() ? ucet.getDispZostatok() - oPohyb.getSuma() : ucet.getDispZostatok() + oPohyb.getSuma());
		ucetDataSource.edit(ucet);
	}
	
	
	@Override
	public String[] getAllColumns() {
		return ALL;
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	public int getSumKat(String idKat, boolean isKredit) {
		Cursor cursor = database.rawQuery("SELECT sum(suma) as suma FROM " + TABLE_NAME + " WHERE " 
				+ KATEGORIA_ID + "=" + idKat + " and " + KREDIT + "=" + (isKredit ? "1" : "0"), null);
		cursor.moveToFirst();
		int pocet = cursor.getInt(cursor.getColumnIndex("suma"));
		cursor.close();
		return pocet;
	}
	
	public int getSumPohyb(String where, boolean isKredit) {
		Cursor cursor = database.rawQuery("SELECT sum(suma) as suma FROM " + TABLE_NAME + " WHERE " + KREDIT + "=" + (isKredit ? "1" : "0")
				+ (where == null || "".equals(where) ? "" : " and " + where), null);
		cursor.moveToFirst();
		int pocet = cursor.getInt(cursor.getColumnIndex("suma"));
		cursor.close();
		return pocet;
	}
	
	
	/*
	 * Udaje pre graf:
	 * SELECT datum, kredit, SUM(suma) as suma FROM pohyb 
	 * WHERE datum BETWEEN 'datumA' AND 'datumB' GROUP BY DATUM, kredit ORDER BY datum ASC;
	 */
	public List<PohybForGraphObject> getListForGraph(String where) {
		List<PohybForGraphObject> retVal = new ArrayList<PohybForGraphObject>();
		Cursor cursor = database.rawQuery("SELECT " + DATUM + "," + KREDIT + ", SUM(suma) as " + SUMA + " FROM " + TABLE_NAME
				+ ("".equals(where) ? "" : " WHERE " + where) + " GROUP BY " + DATUM + "," + KREDIT + " ORDER BY " + DATUM + " ASC", null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				PohybForGraphObject pohyb = new PohybForGraphObject(
						Constants.getDateFromString(cursor.getString(cursor.getColumnIndex(DATUM))),
						cursor.getLong(cursor.getColumnIndex(SUMA)), 
						cursor.getInt(cursor.getColumnIndex(KREDIT)) == 1 ? true : false);
				retVal.add(pohyb);
				cursor.moveToNext();
			} catch (ParseException pex){
				//TODO
			}
		}
		cursor.close();

		return retVal;
	}
	
	/*
	 * Select pre predpoved
	 * select datum, sum((case kredit when 1 then 1 when 0 then -1 end) * suma) as suma from pohyb where ucet_id = #id#
	 * and datum >= #datum# group by datum
	 * */
	public List<PohybForForecastObject> getListForForecast(Long ucetId, Calendar datomOd) {
		List<PohybForForecastObject> retVal = new ArrayList<PohybForForecastObject>();
				
		Cursor cursor = database.rawQuery("SELECT " + DATUM + "," 
				+ " SUM((case " + KREDIT + " WHEN 1 THEN 1 WHEN 0 THEN -1 END) * " + SUMA +") as " + SUMA + " FROM "
				+ TABLE_NAME + " WHERE " + UCET_ID + " = " + ucetId + " AND " + DATUM + " >= \"" + Constants.getDatum(datomOd)
				+ "\" GROUP BY " + DATUM, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				PohybForForecastObject pohyb = new PohybForForecastObject(
						Constants.getDateFromString(cursor.getString(cursor.getColumnIndex(DATUM))),
						cursor.getLong(cursor.getColumnIndex(SUMA))
				);
				
				retVal.add(pohyb);
				cursor.moveToNext();
			} catch (ParseException pex){
				//TODO
			}
		}
		cursor.close();

		return retVal;
	}
	
	@Override
	public PohybObject cursorToObject(Cursor cursor) {
		Long ucetId = cursor.getLong(cursor.getColumnIndex(UCET_ID));
		Long kategoriaId = cursor.getLong(cursor.getColumnIndex(KATEGORIA_ID));
		UcetDataSource ucetDataSource = new UcetDataSource(database);
		KategoriaDataSource kategoriaDataSource = new KategoriaDataSource(database);
		
		Calendar datCas = Calendar.getInstance();
		try {
			datCas = Constants.getCalendarFromString(cursor.getString(cursor.getColumnIndex(DATUM)) + " " + 
					cursor.getString(cursor.getColumnIndex(CAS)));
		} catch (ParseException ex) {
			//TODO toto treba riesit!!!
			Log.d("HIBA_POHYBDS", ex.getLocalizedMessage());
		}
		
		PohybObject object = new PohybObject(
					cursor.getLong(cursor.getColumnIndex(ID)),
					datCas,
					cursor.getLong(cursor.getColumnIndex(SUMA)),
					cursor.getString(cursor.getColumnIndex(POZNAMKA)),
					cursor.getInt(cursor.getColumnIndex(KREDIT)) == 1 ? true : false,
							kategoriaDataSource.get(kategoriaId),
					ucetDataSource.get(ucetId));
		return object;
	}
	
	@Override
	public String getOrder() {
		return DATUM + " DESC, " + CAS + " DESC";
	}
}

package osobny.uctovnik.datasources;

import osobny.uctovnik.objects.UcetObject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

/*
 * DataSource objekt - obsahuje nazov tabulky a nazvy stlpcov, CREATE statement a 
 * premapovanie databazoveho objetku na POJO
 * */
public class UcetDataSource extends BaseDataSource<UcetObject> {
	public static final String TABLE_NAME = "ucet";
	
	public static final String ID = "_id";
	public static final String NAZOV = "nazov";
	public static final String CISLO = "cislo";
	public static final String ZOSTATOK = "zostatok";
	public static final String DISP_ZOSTATOK = "disp_zostatok";
	public static final String MENA = "mena";
	public static final String BANKA_ID = "banka_id";
	public static final String[] ALL = {ID, NAZOV, CISLO, ZOSTATOK, DISP_ZOSTATOK, MENA, BANKA_ID};
	
	public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ NAZOV + " TEXT,"
			+ CISLO + " TEXT,"
			+ ZOSTATOK + " INTEGER,"
			+ DISP_ZOSTATOK + " INTEGER,"
			+ MENA + " TEXT,"
			+ BANKA_ID + " INTEGER,"
			+ "FOREIGN KEY(" + BANKA_ID + ") REFERENCES " + BankaDataSource.TABLE_NAME + "(" + BankaDataSource.ID + ")"
			+ ")";

	
	public UcetDataSource(Context context) {
		super(context);
	}
	
	public UcetDataSource(SQLiteDatabase database) {
		super(database);
	}
	
	@Override
	public Long create(UcetObject oUcet) {
		ContentValues values = new ContentValues();
		values.put(NAZOV, oUcet.getNazov());
		values.put(CISLO, oUcet.getCisloUctu());
		values.put(ZOSTATOK, oUcet.getZostatok());
		values.put(DISP_ZOSTATOK, oUcet.getDispZostatok());
		values.put(MENA, oUcet.getMena());
		values.put(BANKA_ID, oUcet.getBanka().getId());
		long id = database.insert(TABLE_NAME, null, values);
		return id;
	}
	
	public Long createFromImport(UcetObject oUcet) throws SQLiteConstraintException {
		ContentValues values = new ContentValues();
		values.put(NAZOV, oUcet.getNazov());
		values.put(CISLO, oUcet.getCisloUctu());
		values.put(ZOSTATOK, oUcet.getZostatok());
		values.put(DISP_ZOSTATOK, oUcet.getDispZostatok());
		values.put(MENA, oUcet.getMena());
		values.put(BANKA_ID, oUcet.getBankaId());
		values.put(ID, oUcet.getId());
		long id = database.insertOrThrow(TABLE_NAME, null, values);
		return id;
	}
	
	@Override
	public void edit(UcetObject oUcet) {
		ContentValues values = new ContentValues();
		values.put(NAZOV, oUcet.getNazov());
		values.put(CISLO, oUcet.getCisloUctu());
		values.put(ZOSTATOK, oUcet.getZostatok());
		values.put(DISP_ZOSTATOK, oUcet.getDispZostatok());
		values.put(MENA, oUcet.getMena());
		values.put(BANKA_ID, oUcet.getBanka().getId());
		database.update(TABLE_NAME, values, ID + "=" + oUcet.getId(), null);
	}
	
	@Override
	public String[] getAllColumns() {
		return ALL;
	}
	
	@Override
	public String getTableName() {
		return TABLE_NAME;
	}
	
	@Override
	public UcetObject cursorToObject(Cursor cursor) {
		Long bankaId = cursor.getLong(cursor.getColumnIndex(BANKA_ID));
		BankaDataSource bankaDataSource = new BankaDataSource(database);
		
		UcetObject object = new UcetObject(
					cursor.getLong(cursor.getColumnIndex(ID)),
					cursor.getString(cursor.getColumnIndex(NAZOV)),
					cursor.getString(cursor.getColumnIndex(CISLO)),
					cursor.getLong(cursor.getColumnIndex(ZOSTATOK)),
					cursor.getLong(cursor.getColumnIndex(DISP_ZOSTATOK)),
					cursor.getString(cursor.getColumnIndex(MENA)),
					bankaDataSource.get(bankaId));
		
		return object;
	}
}

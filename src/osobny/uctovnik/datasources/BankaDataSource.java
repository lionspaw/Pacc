package osobny.uctovnik.datasources;

import osobny.uctovnik.objects.BankaObject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

/*
 * DataSource objekt - obsahuje nazov tabulky a nazvy stlpcov, CREATE statement a 
 * premapovanie databazoveho objetku na POJO
 * */
public class BankaDataSource extends BaseDataSource<BankaObject> {
	public static final String TABLE_NAME = "banka";
	
	public static final String CISLO = "cislo";
	public static final String NAZOV = "nazov";
	public static final String[] ALL = {ID, CISLO, NAZOV};
	
	public static final String CREATE_STATEMENT = "CREATE TABLE "+ TABLE_NAME + "("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ CISLO + " TEXT,"
			+ NAZOV + " TEXT"
			+ ")";
	
	public BankaDataSource(Context context) {
		super(context);
	}
	
	public BankaDataSource(SQLiteDatabase database) {
		super(database);
	}
	
	@Override
	public Long create(BankaObject oBanka) throws SQLiteConstraintException {
		ContentValues values = new ContentValues();
		values.put(CISLO, oBanka.getCislo());
		values.put(NAZOV, oBanka.getNazov());
		if (oBanka.getId() != null) {
			values.put(ID, oBanka.getId());
		}
		long id = database.insertOrThrow(TABLE_NAME, null, values);
		return id;
	}
	
	@Override
	public void edit(BankaObject oBanka) {
		ContentValues values = new ContentValues();
		values.put(CISLO, oBanka.getCislo());
		values.put(NAZOV, oBanka.getNazov());
		database.update(TABLE_NAME, values, ID + "=" + oBanka.getId(), null);
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
	public BankaObject cursorToObject(Cursor cursor) {
		BankaObject object = new BankaObject(
					cursor.getLong(cursor.getColumnIndex(ID)),
					cursor.getString(cursor.getColumnIndex(CISLO)),
					cursor.getString(cursor.getColumnIndex(NAZOV)));
		return object;
	}
}

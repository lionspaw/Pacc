package osobny.uctovnik.datasources;

import osobny.uctovnik.objects.KategoriaObject;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

/*
 * DataSource objekt - obsahuje nazov tabulky a nazvy stlpcov, CREATE statement a 
 * premapovanie databazoveho objetku na POJO
 * */
public class KategoriaDataSource extends BaseDataSource<KategoriaObject> {
	public static final String TABLE_NAME = "kategoria";
	public static final String NAZOV = "nazov";
	public static final String FARBA = "farba";
	public static final String[] ALL = {ID, NAZOV, FARBA};
	
	public static final String CREATE_STATEMENT = "CREATE TABLE " + TABLE_NAME + "("
			+ ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ NAZOV + " TEXT,"
			+ FARBA + " INTEGER"
			+ ")";
	
	
	public KategoriaDataSource(Context context) {
		super(context);
	}
	
	public KategoriaDataSource(SQLiteDatabase database) {
		super(database);
	}	
	
	@Override
	public Long create(KategoriaObject oKategoria) throws SQLiteConstraintException {
		ContentValues values = new ContentValues();
		values.put(NAZOV, oKategoria.getNazov());
		values.put(FARBA, oKategoria.getFarba());
		if (oKategoria.getId() != null) {
			values.put(ID, oKategoria.getId());
		}
		long id = database.insertOrThrow(TABLE_NAME, null, values);
		return id;
	}
	
	@Override
	public void edit(KategoriaObject oKategoria) {
		ContentValues values = new ContentValues();
		values.put(NAZOV, oKategoria.getNazov());
		values.put(FARBA, oKategoria.getFarba());
		database.update(TABLE_NAME, values, ID + "=" + oKategoria.getId(), null);
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
	public KategoriaObject cursorToObject(Cursor cursor) {
		KategoriaObject object = new KategoriaObject(
					cursor.getLong(cursor.getColumnIndex(ID)),
					cursor.getString(cursor.getColumnIndex(NAZOV)),
					Integer.valueOf(cursor.getString(cursor.getColumnIndex(FARBA))));
		return object;
	}
}

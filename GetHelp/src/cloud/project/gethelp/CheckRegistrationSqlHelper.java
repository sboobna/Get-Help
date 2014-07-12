package cloud.project.gethelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class CheckRegistrationSqlHelper extends SQLiteOpenHelper{
	public static final String REGISTRATION_TABLE = "registrationTable";
	public static final String PHONE_NO = "phoneNo";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final int DATABASE_VER = 1;
	public static final String DB_NAME = "Registration.db";
	
	private String DATABASE_CREATE = "CREATE TABLE "
			+ REGISTRATION_TABLE + "(" + PHONE_NO
			+ " TEXT PRIMARY KEY , " + FIRST_NAME
			+ " TEXT NOT NULL, " + LAST_NAME+ " TEXT NOT NULL);";
	
	public CheckRegistrationSqlHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(CheckRegistrationSqlHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + REGISTRATION_TABLE);
		onCreate(database);
	}
}

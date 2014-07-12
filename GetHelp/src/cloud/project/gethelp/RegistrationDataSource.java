package cloud.project.gethelp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RegistrationDataSource {

	private SQLiteDatabase registrationDb;
	private CheckRegistrationSqlHelper checkRegistrationSqlHelper;
	private String[] allColumns = { CheckRegistrationSqlHelper.PHONE_NO,
			CheckRegistrationSqlHelper.FIRST_NAME, CheckRegistrationSqlHelper.LAST_NAME};
	
	
	public RegistrationDataSource(Context context) {
		checkRegistrationSqlHelper = new CheckRegistrationSqlHelper(context, CheckRegistrationSqlHelper.DB_NAME , null,
				CheckRegistrationSqlHelper.DATABASE_VER);
		open();
	}

	public void open() throws SQLException {
		registrationDb = checkRegistrationSqlHelper.getWritableDatabase();
	}

	public void close() {
		registrationDb.close();
	}

	/**This method will add the given battery level in the database with a timestamp<br/>
	 * This will also return the batteryLevel which was added to the database
	 * @param batteryLevel
	 * @return
	 */
	public RegistrationBean addRegistrationDetails(RegistrationBean registrationBean) {
		ContentValues values = new ContentValues();
		values.put(CheckRegistrationSqlHelper.PHONE_NO, registrationBean.getPhnoneNo());
		values.put(CheckRegistrationSqlHelper.FIRST_NAME, registrationBean.getFirstName());
		values.put(CheckRegistrationSqlHelper.LAST_NAME, registrationBean.getLastName());
		registrationDb.insert(CheckRegistrationSqlHelper.REGISTRATION_TABLE, null, values);
		Cursor cursor = registrationDb.query(CheckRegistrationSqlHelper.REGISTRATION_TABLE, allColumns,
				CheckRegistrationSqlHelper.PHONE_NO+ " = " + registrationBean.getPhnoneNo(), null, null, null, null);
		cursor.moveToFirst();
		RegistrationBean returnVal = cursorToLevel(cursor);
		cursor.close();
		return returnVal;
	}

	public void deleteEntryCorrespondingToPhoneNo(RegistrationBean registrationBean) {
		
		Log.v("GETHELP!","Record with phone no : " + registrationBean.getPhnoneNo()+" is deleted");
		registrationDb.delete(CheckRegistrationSqlHelper.REGISTRATION_TABLE, CheckRegistrationSqlHelper.PHONE_NO+ " = "
				+ registrationBean.getPhnoneNo(), null);
	}	

	public List<RegistrationBean> getAllRegistrations() {
		List<RegistrationBean> batteryLevelList = new ArrayList<RegistrationBean>();

		Cursor cursor = registrationDb.query(CheckRegistrationSqlHelper.REGISTRATION_TABLE, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			RegistrationBean battLevel= cursorToLevel(cursor);
			batteryLevelList.add(battLevel);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return batteryLevelList;
	}

	/**
	 * @param cursor
	 * @return
	 */
	private RegistrationBean cursorToLevel(Cursor cursor) {
		RegistrationBean registrationBean = new RegistrationBean();
		registrationBean.setPhnoneNo(cursor.getString(0));
		registrationBean.setFirstName(cursor.getString(1));
		registrationBean.setLastName(cursor.getString(2));
		return registrationBean;
	}
	
	public void deleteDatabase(){
		this.open();
		registrationDb.delete(CheckRegistrationSqlHelper.DB_NAME, null, null);
		this.close();
	}
	
	public void finalize(){
		close();
	}

}

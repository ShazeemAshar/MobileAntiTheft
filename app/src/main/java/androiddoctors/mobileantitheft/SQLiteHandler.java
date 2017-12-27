package androiddoctors.mobileantitheft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

class SQLiteHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "UserDB";
    private static final String TABLE_NAME = "UserInfo";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PIN = "pin";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EC1 = "emergencyContact1";
    private static final String KEY_EC2 = "emergencyContact2";
    private static final String KEY_EC3 = "emergencyContact3";

    Context context;
    private SQLiteDatabase readableDb,writeableDb;
    private ContentValues values;

    SQLiteHandler(Context reference) {
        super(reference, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = reference;
        readableDb = getReadableDatabase();
        writeableDb = getWritableDatabase();
        values = new ContentValues();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME+ "("+KEY_ID+" integer primary key,"+KEY_NAME+" text,"+KEY_EMAIL+" text,"+KEY_PASSWORD+" text,"+KEY_PIN+","+KEY_PHONE+" text,"+KEY_EC1+" text,"+KEY_EC2+" text,"+KEY_EC3+" text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    void insertWebAccountInfo(String name, String email, String password){

        values.put(KEY_NAME,name);
        values.put(KEY_EMAIL,email);
        values.put(KEY_PASSWORD,password);

        long result = writeableDb.update(TABLE_NAME,values,KEY_ID +"=1",null);

        if (result!=-1){
            Toast.makeText(context,"Account Created Successfully",Toast.LENGTH_LONG).show();

        }
    }
    void insertPhoneNum(String phone){

        values.put(KEY_PHONE,phone);

        long result = writeableDb.insert(TABLE_NAME,null,values);

        if (result!=-1){
            //Toast.makeText(context,"Record Inserted",Toast.LENGTH_LONG).show();

        }
    }
    void insertPin(String pin){

        values.put(KEY_PIN,pin);

        long result = writeableDb.update(TABLE_NAME,values,KEY_ID +"=1",null);

        if (result!=-1){
            Toast.makeText(context,"PIN Registered",Toast.LENGTH_LONG).show();

        }
    }
    void insertEmergencyContacts(String ec1, String ec2, String ec3){

        values.put(KEY_EC1,ec1);
        values.put(KEY_EC2,ec2);
        values.put(KEY_EC3,ec3);

      long result = writeableDb.update(TABLE_NAME,values,KEY_ID +"=1",null);

        if (result!=-1){
            Toast.makeText(context,"Phone Numbers Registered",Toast.LENGTH_LONG).show();

        }
    }
    Cursor getUserData(){
        return readableDb.rawQuery("select * from "+TABLE_NAME,null);
    }

}

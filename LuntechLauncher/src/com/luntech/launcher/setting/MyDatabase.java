package com.luntech.launcher.setting;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;


public class MyDatabase extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "city";
	private static final int DATABASE_VERSION = 1;

	public MyDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public Cursor getProvinces() {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "id", "name" };
		String sqlTables = "gb2260";
		String sqlSelection=" id%10000=0";
		String sArgs[]={};
		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, sqlSelection, sArgs, null, null, null);

		c.moveToFirst();
		db.close();
		return c;

	}

	public Cursor getCities(String province) {

		SQLiteDatabase db = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String sqlSelection=" id % 100 = 0 and id-?<10000 and id-?>0 ";
		String[] sqlSelect = { "id", "name"};
		String[] sArgs={province,province};
		String sqlTables = "gb2260";
		qb.setTables(sqlTables);
		Cursor c = qb.query(db, sqlSelect, sqlSelection, sArgs, null, null, null);

		c.moveToFirst();
		db.close();
		return c;

	}
	
	
}
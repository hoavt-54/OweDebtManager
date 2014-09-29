/*
 * Copyright (c) 2014 Pendulab Inc.
 * 111 N Chestnut St, Suite 200, Winston Salem, NC, 27101, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Pendulab Inc. ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with Pendulab.
 */

package niceutility.hoa.owedebtmanager.database;

import java.sql.SQLException;

import niceutility.hoa.owedebtmanager.data.Debt;
import niceutility.hoa.owedebtmanager.data.Person;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{
	// name of the database file for Moomee application
	private static final String DATABASE_NAME = "OweDebtManager.db";
	//database version
	private static final int DATABASE_VERSION = 1;
	
	
	
	//DAO object to access friends table
	private Dao<Debt, Integer> debtDao = null;
	private Dao<Person, Integer> personDao = null;
	
	public Dao<Debt, Integer> getDebtDao() throws SQLException {
		if (debtDao == null) {
			TableUtils.dropTable(connectionSource, Debt.class, true);
			TableUtils.dropTable(connectionSource, Person.class, true);
			TableUtils.createTableIfNotExists(connectionSource, Debt.class);
			TableUtils.createTableIfNotExists(connectionSource, Person.class);

			debtDao = getDao(Debt.class);
		}
		
		return debtDao;
	}
	
	public Dao<Person, Integer> getPersonDao() throws SQLException {
		if (personDao == null) {
//			TableUtils.dropTable(connectionSource, WhoIsPlaying.class, true);
			personDao = getDao(Person.class);
		}
		
		return personDao;
	}
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
	}

	/**
	 * This is called when the database is first created. In this method, all the table will be created.
	 */
	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource arg1) {
		try {
			// here we create tables
			TableUtils.createTableIfNotExists(connectionSource, Debt.class);
			TableUtils.createTableIfNotExists(connectionSource, Person.class);
			
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connection, int arg2,
			int arg3) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Debt.class, true);
			TableUtils.dropTable(connectionSource, Person.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		debtDao = null;
	}

}

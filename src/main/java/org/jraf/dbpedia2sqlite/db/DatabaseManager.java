/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.dbpedia2sqlite.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.jraf.dbpedia2sqlite.Config;
import org.jraf.dbpedia2sqlite.Constants;
import org.jraf.dbpedia2sqlite.util.Log;

public class DatabaseManager {
    private static final String TAG = Constants.TAG + DatabaseManager.class.getSimpleName();

    //@formatter:off
    private static final String SQL_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS resource (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL, " +
            "abstract TEXT NOT NULL" +
            ")";
    
    private static final String SQL_INSERT = "INSERT INTO resource " +
    		"(name, abstract)" +
    		" VALUES " +
    		"(?, ?)";

    private static final String SQL_SELECT_FROM_NAME = "SELECT " +
            "_id, name, abstract" +
            " FROM " +
            "resource" +
            " WHERE " +
            "name=?";
    //@formatter:on

    private Connection mConnection;

    public DatabaseManager(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "DbManager Could not intialize jdbc driver", e);
        }

        boolean dbExists = new File(dbPath).exists();
        if (Config.LOGD) Log.d(TAG, "DbManager dbExists=" + dbExists);

        try {
            mConnection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            if (!dbExists) {
                Statement statement = mConnection.createStatement();
                statement.execute(SQL_CREATE_TABLE);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Could not initialize connection", e);
        }
    }

    public long insert(String name, String _abstract) {
        if (Config.LOGD) Log.d(TAG, "insert name=" + name + " _abstract=" + _abstract);

        PreparedStatement statement = null;
        try {
            statement = mConnection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);
            statement.setString(2, _abstract);

            int rows = statement.executeUpdate();
            if (Config.LOGD) Log.d(TAG, "insert rows=" + rows);

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                long res = resultSet.getLong(1);
                if (Config.LOGD) Log.d(TAG, "insert res=" + res);
                return res;
            }
        } catch (SQLException e) {
            Log.e(TAG, "insert Could not insert", e);
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                Log.w(TAG, "insert", e);
            }
        }
        return -1;
    }

    public Resource getFromName(String name) {
        if (Config.LOGD) Log.d(TAG, "getFromName name=" + name);
        PreparedStatement statement = null;
        try {
            statement = mConnection.prepareStatement(SQL_SELECT_FROM_NAME);
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            Resource res = new Resource();
            res.id = resultSet.getLong(1);
            res.name = resultSet.getString(2);
            res._abstract = resultSet.getString(3);
            if (Config.LOGD) Log.d(TAG, "getFromName res=" + res);
            return res;
        } catch (SQLException e) {
            Log.e(TAG, "Could not get resource", e);
        } finally {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                Log.w(TAG, "isExistingQuote", e);
            }
        }
        return null;
    }
}

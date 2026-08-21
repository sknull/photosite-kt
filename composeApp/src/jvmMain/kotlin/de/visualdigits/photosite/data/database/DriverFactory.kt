package de.visualdigits.photosite.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import de.visualdigits.photosite.SettingsDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(basePath: String): SqlDriver {
        val dbDirectory = File(basePath)
        if (!dbDirectory.exists()) {
            dbDirectory.mkdirs()
        }
        val dbPath = File(dbDirectory, "settings.db").canonicalPath
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:$dbPath",
            schema = SettingsDatabase.Schema
        )
        driver.execute(null, "PRAGMA foreign_keys = ON;", 0)
        return driver
    }
}

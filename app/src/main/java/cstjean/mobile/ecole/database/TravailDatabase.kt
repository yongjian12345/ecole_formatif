package cstjean.mobile.ecole.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cstjean.mobile.ecole.travail.Travail

@Database(entities = [Travail::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3)
    ]
)
@TypeConverters(TravailTypeConverters::class)
abstract class TravailDatabase : RoomDatabase() {
    abstract fun travailDao(): TravailDao
}
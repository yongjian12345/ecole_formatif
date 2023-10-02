package cstjean.mobile.ecole.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import cstjean.mobile.ecole.travail.Travail
import java.util.UUID
import kotlinx.coroutines.flow.Flow


@Dao
interface TravailDao {
    @Query("SELECT * FROM travail")
    fun getTravaux(): Flow<List<Travail>>

    @Query("SELECT * FROM travail WHERE id=(:id)")
    suspend fun getTravail(id: UUID): Travail

    @Insert
    suspend fun addTravail(travail: Travail)

    @Update
    suspend fun updateTravail(travail: Travail)
}
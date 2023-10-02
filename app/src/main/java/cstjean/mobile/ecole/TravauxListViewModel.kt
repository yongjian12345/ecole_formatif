package cstjean.mobile.ecole

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cstjean.mobile.ecole.travail.Travail
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "TravauxListViewModel"

/**
 * ViewModel pour la liste des travaux.
 *
 * @property travaux La liste des travaux.
 *
 * @author Gabriel T. St-Hilaire
 */
class TravauxListViewModel : ViewModel() {
    private val travailRepository = TravailRepository.get()

    private val _travaux: MutableStateFlow<List<Travail>> = MutableStateFlow(emptyList())
    val travaux: StateFlow<List<Travail>> = _travaux

    init {
        viewModelScope.launch {
            // loadTravaux()
            travailRepository.getTravaux().collect {
                _travaux.value = it
            }
        }
    }

    suspend fun loadTravaux() {
        val travaux = mutableListOf<Travail>()
        delay(5000)
        // Données de tests
        /*for (i in 0 until 100) {
            val travail = Travail(
                UUID.randomUUID(),
                "Travail #$i",
                Date(),
                i % 2 == 0
            )
            addTravail(travail)
        }*/

    }

    suspend fun addTravail(travail: Travail) {
        travailRepository.addTravail(travail)
    }
}

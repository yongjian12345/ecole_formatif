package cstjean.mobile.ecole

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/**
 * Activity qui contient un seul Fragment.
 *
 * @author Gabriel T. St-Hilaire
 */
class MainActivity : AppCompatActivity() {

    /**
     * Initialisation de l'Activity.
     *
     * @param savedInstanceState Les données conservées au changement d'état.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
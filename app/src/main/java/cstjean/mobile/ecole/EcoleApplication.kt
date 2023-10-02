package cstjean.mobile.ecole

import android.app.Application
class EcoleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TravailRepository.initialize(this)
    }
}
package cstjean.mobile.ecole

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.*


class DatePickerFragment : DialogFragment() {

    private val args: DatePickerFragmentArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dateListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                val resultDate = GregorianCalendar(year, month, dayOfMonth).time
                setFragmentResult(
                    REQUEST_KEY_DATE,
                    bundleOf(BUNDLE_KEY_DATE to resultDate)
                )
            }

        val calendar = Calendar.getInstance()
        calendar.time = args.travailDateRemise
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        const val REQUEST_KEY_DATE = "REQUEST_KEY_DATE" // La clé de la requête
        const val BUNDLE_KEY_DATE = "BUNDLE_KEY_DATE" // La clé du résultat
    }
}
package cstjean.mobile.ecole

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import cstjean.mobile.ecole.databinding.FragmentTravailBinding
import cstjean.mobile.ecole.travail.Travail
import kotlinx.coroutines.launch
import java.util.Date
import android.text.format.DateFormat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import java.io.File
import java.util.UUID

/**
 * Fragment pour la gestion d'un travail.
 *
 * @author Gabriel T. St-Hilaire
 */
class TravailFragment : Fragment() {
    private var _binding: FragmentTravailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Binding est null. La vue est visible ??"
        }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager: PackageManager = requireActivity().packageManager
        return intent.resolveActivity(packageManager) != null
    }

    private val args: TravailFragmentArgs by navArgs()
    private val travailViewModel: TravailViewModel by viewModels {
        TravailViewModelFactory(args.travailId)
    }

    private val getContact =
        registerForActivityResult(ActivityResultContracts.PickContact()) { uri: Uri? ->
            uri?.let { parseSelectionContact(it) }
        }
    private val prendrePhoto =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { photoPrise: Boolean ->
            if (photoPrise && photoFilename != null) {
                travailViewModel.updateTravail { oldTravail ->
                    oldTravail.copy(photoFilename = photoFilename)
                }
            }
        }
    private var photoFilename: String? = null



    /**
     * Initialisation du Fragment.
     *
     * @param savedInstanceState Les données conservées au changement d'état.
     */
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        travail = Travail(UUID.randomUUID(), "Travail 1", Date(),false)
    }*/

    /**
     * Instanciation de l'interface.
     *
     * @param inflater Pour instancier l'interface.
     * @param container Le parent qui contiendra notre interface.
     * @param savedInstanceState Les données conservées au changement d'état.
     *
     * @return La vue instanciée.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTravailBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Lorsque la vue est créée.
     *
     * @param view La vue créée.
     * @param savedInstanceState Les données conservées au changement d'état.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
            val newDateRemise =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE, Date::class.java) as Date
                else
                    bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
            travailViewModel.updateTravail { it.copy(dateRemise = newDateRemise) }
        }
        binding.apply {
            travailCoequipier.setOnClickListener {
                getContact.launch(null)
            }

            val intent = getContact.contract.createIntent(requireContext(), null)
            intent.addCategory(Intent.CATEGORY_APP_CALCULATOR) // Pour tester !
            travailCoequipier.isEnabled = canResolveIntent(intent)

            travailNom.doOnTextChanged { text, _, _, _ ->
                travailViewModel.updateTravail { oldTravail ->
                    oldTravail.copy(nom = text.toString())
                }
            }

            travailTermine.setOnCheckedChangeListener { _, isChecked ->
                travailViewModel.updateTravail { oldTravail ->
                    oldTravail.copy(estTermine = isChecked)
                }
            }

            travailCamera.setOnClickListener {
                photoFilename = "IMG_${Date()}.JPG"
                val photoFichier = File(requireContext().applicationContext.filesDir, photoFilename)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "cstjean.mobile.fileprovider",
                    photoFichier
                )
                prendrePhoto.launch(photoUri)
            }

            val cameraIntent = prendrePhoto.contract.createIntent(
                requireContext(),
                Uri.parse("")
            )
            // cameraIntent.addCategory(Intent.CATEGORY_APP_CALCULATOR) // Pour tester !
            travailCamera.isEnabled = canResolveIntent(cameraIntent)

             /*travailDate.apply {
                isEnabled = false
            }*/
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                travailViewModel.travail.collect { travail ->
                    travail?.let { updateUi(it) }
                }
            }
        }
    }

    /**
     * Lorsque la vue est détruite.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseSelectionContact(contactUri: Uri) {
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor = requireActivity().contentResolver.query(contactUri, queryFields, null, null, null)
        queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) {
                val coequipier = cursor.getString(0)
                travailViewModel.updateTravail { oldTravail ->
                    oldTravail.copy(coequipier = coequipier)
                }
            }
        }
    }

    private fun updatePhoto(photoFilename: String?) {
        if (binding.travailPhoto.tag != photoFilename) {
            val photoFichier = photoFilename?.let {
                File(requireContext().applicationContext.filesDir, it)
            }
            if (photoFichier?.exists() == true) {
                binding.travailPhoto.doOnLayout { view ->
                    val scaledBitmap = getScaledBitmap(
                        photoFichier.path,
                        view.width,
                        view.height
                    )
                    binding.travailPhoto.setImageBitmap(scaledBitmap)
                    binding.travailPhoto.tag = photoFilename
                }
            } else {
                binding.travailPhoto.setImageBitmap(null)
                binding.travailPhoto.tag = null
            }
        }
    }

    private fun updateUi(travail: Travail) {
        binding.apply {
            // Pour éviter une loop infinie avec le update
            if (travailNom.text.toString() != travail.nom) {
                travailNom.setText(travail.nom)
            }

            travailCoequipier.text = travail.coequipier.ifEmpty {
                getString(R.string.travail_ajouter_coequipier)
            }

            travailDate.text = DateFormat.format("EEEE, dd MMMM yyyy", travail.dateRemise).toString()

            travailDate.setOnClickListener {
                findNavController().navigate(
                    TravailFragmentDirections.selectDateRemise(travail.dateRemise)
                )
            }
            travailTermine.isChecked = travail.estTermine

            travailContacterCoequipier.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getMessageCoequipier(travail))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.travail_contacter_coequipier_sujet))
                }

                val chooserIntent = Intent.createChooser(
                    intent,
                    getString(R.string.travail_contacter_coequipier)
                )
                startActivity(chooserIntent)
            }
        }
        updatePhoto(travail.photoFilename)
    }

    private fun getMessageCoequipier(travail: Travail): String {
        val estTermine = if (travail.estTermine) {
            getString(R.string.travail_contacter_coequipier_termine)
        } else {
            getString(R.string.travail_contacter_coequipier_non_termine)
        }
        return getString(
            R.string.travail_contacter_coequipier_message,
            travail.nom, travail.dateRemiseFormatee, estTermine
        )
    }
}



package cstjean.mobile.ecole

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cstjean.mobile.ecole.databinding.FragmentTravauxListBinding
import cstjean.mobile.ecole.travail.Travail
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

private const val TAG = "TravauxListFragment"

/**
 * Fragment pour la liste des travaux.
 *
 * @author Gabriel T. St-Hilaire
 */
class TravauxListFragment : Fragment() {
    private var _binding: FragmentTravauxListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Binding est null. La vue est visible ??"
        }

    private val travauxListViewModel: TravauxListViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                travauxListViewModel.travaux.collect {travaux ->
                    binding.travauxRecyclerView.adapter = TravauxListAdapter(travaux) { travailId ->
                        Log.d(TAG, travailId.toString())
                        findNavController().navigate(TravauxListFragmentDirections.showTravailDetail(travailId))
                    }
                }
            }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_travaux_list, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.nouveau_travail -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            val nouveauTravail = Travail(
                                UUID.randomUUID(),
                                "",
                                Date(),
                                false
                            )
                            travauxListViewModel.addTravail(nouveauTravail)
                            findNavController().navigate(
                                TravauxListFragmentDirections.showTravailDetail(nouveauTravail.id)
                            )
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTravauxListBinding.inflate(inflater, container, false)

        binding.travauxRecyclerView.layoutManager = LinearLayoutManager(context)



        return binding.root
    }

    /**
     * Lorsque la vue est détruite.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package ru.nilsolk.contactapp.ui

import ContactViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.nilsolk.contactapp.databinding.FragmentContactBinding

class ContactFragment : Fragment() {
    private lateinit var binding: FragmentContactBinding
    private val viewModel: ContactViewModel by viewModels()
    private lateinit var contactAdapter: ContactsAdapter
    private lateinit var dialogFragment: ContactsResultDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.bindService(requireContext())
        binding = FragmentContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupRefresh()

        binding.duplicatesButton.setOnClickListener {
            viewModel.removeDuplicates()
        }
    }

    private fun setupRefresh(){
        val swipeLayout  = binding.swipeLayout
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            swipeLayout.isEnabled = binding.scrollView.scrollY == 0
        }
        swipeLayout.setOnRefreshListener {
            Runnable {
                viewModel.loadContacts()
                swipeLayout.isRefreshing = false
            }.run()
        }

    }
    private fun setupRecyclerView() {
        contactAdapter = ContactsAdapter()
        binding.contactsRecyclerView.apply {
            adapter = contactAdapter
        }
    }

    private fun setupObservers() {
        viewModel.contacts.observe(viewLifecycleOwner) { contacts ->
            contactAdapter.setList(contacts)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            dialogFragment = ContactsResultDialog(error)
            dialogFragment.show(childFragmentManager, "")
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            val mes = when (status) {
                STATUS_REMOVED -> "Success removed!"
                STATUS_NOT_FOUND -> " Duplicates not found!"
                else -> "Something went wrong!"
            }
            dialogFragment = ContactsResultDialog(mes)
            dialogFragment.show(childFragmentManager, "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unbindService(requireContext())
    }

    companion object {
        private const val STATUS_NOT_FOUND = "nf"
        private const val STATUS_REMOVED = "removed"
    }


}

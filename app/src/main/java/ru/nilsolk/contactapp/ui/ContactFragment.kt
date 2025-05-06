package ru.nilsolk.contactapp.ui

import ContactViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import ru.nilsolk.contactapp.R
import ru.nilsolk.contactapp.data.ContactState
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

    private fun setupRefresh() {
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            viewModel.loadContacts()
            swipeLayout.isRefreshing = false
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
            dialogFragment.show(childFragmentManager,ContactsResultDialog::class.toString())
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            val mes = when (status) {
                ContactState.REMOVED.toString() -> getString(R.string.success_removed)
                ContactState.NOT_FOUND.toString() -> getString(R.string.duplicates_not_found)
                else -> getString(R.string.something_went_wrong)
            }
            dialogFragment = ContactsResultDialog(mes)
            dialogFragment.show(childFragmentManager, ContactsResultDialog::class.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unbindService(requireContext())
    }

}

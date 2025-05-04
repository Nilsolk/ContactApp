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

        binding.duplicatesButton.setOnClickListener {
            viewModel.removeDuplicates()
            contactAdapter.notifyDataSetChanged()
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
            contactAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Success removed duplicates", Toast.LENGTH_SHORT)
                .show()
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        viewModel.status.observe(viewLifecycleOwner) { mes ->
            Toast.makeText(requireContext(), mes, Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.unbindService(requireContext())
    }

}

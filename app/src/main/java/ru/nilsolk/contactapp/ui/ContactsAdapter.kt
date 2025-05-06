package ru.nilsolk.contactapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.nilsolk.contactapp.data.ContactModel
import ru.nilsolk.contactapp.databinding.ContactItemBinding

class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactsHolder>() {
    private val contactList = mutableListOf<ContactModel>()

    fun setList(list: List<ContactModel>) {
        val diffCallback = ContactDiffUtil(contactList, list.sortedBy { it.name })
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        contactList.clear()
        contactList.addAll(list.sortedBy { it.name })
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactItemBinding.inflate(inflater, parent, false)
        return ContactsHolder(binding)
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: ContactsHolder, position: Int) {
        val item = contactList[position]
        with(holder.viewBinding) {
            number.text = item.number
            name.text = item.name
            if (item.photoUri.isNotEmpty()) {
                Glide.with(root)
                    .load(item.photoUri)
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .circleCrop()
                    .into(contactImage)
            } else {
                Glide.with(root)
                    .load(android.R.drawable.ic_menu_myplaces)
                    .circleCrop()
                    .into(contactImage)
            }
        }
    }

    class ContactsHolder(val viewBinding: ContactItemBinding) :
        RecyclerView.ViewHolder(viewBinding.root)
}

class ContactDiffUtil(
    private val oldList: List<ContactModel>,
    private val newList: List<ContactModel>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldContact = oldList[oldItemPosition]
        val newContact = newList[newItemPosition]
        return oldContact.number == newContact.number
                && oldContact.name == newContact.name
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldContact = oldList[oldItemPosition]
        val newContact = newList[newItemPosition]
        return oldContact == newContact
    }

}
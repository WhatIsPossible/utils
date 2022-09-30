package lex.utils.utils

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract

object ContactsUtil {

    /**
     * Insert contact
     * 调用系统的联系人保存页面
     * @param context
     * @param phone
     * @param name
     * @param email
     * @param company
     * @param notes
     * @param postal
     */
    fun insertContact(
        context: Context,
        phone: String,
        name: String? = null,
        email: String? = null,
        company: String? = null,
        notes: String? = null,
        postal: String? = null
    ) {
        // Creates a new Intent to insert a contact
        val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
            // Sets the MIME type to match the Contacts Provider
            type = ContactsContract.RawContacts.CONTENT_TYPE

            putExtra(ContactsContract.Intents.Insert.NAME, name)
            putExtra(ContactsContract.Intents.Insert.COMPANY, company)
            putExtra(ContactsContract.Intents.Insert.NOTES, notes)
            putExtra(ContactsContract.Intents.Insert.POSTAL, postal)

            // Inserts an email address
            putExtra(ContactsContract.Intents.Insert.EMAIL, email)
            /*
             * In this example, sets the email type to be a work email.
             * You can set other email types as necessary.
             */
            putExtra(
                ContactsContract.Intents.Insert.EMAIL_TYPE,
                ContactsContract.CommonDataKinds.Email.TYPE_WORK
            )
            // Inserts a phone number
            putExtra(ContactsContract.Intents.Insert.PHONE, phone)
            /*
             * In this example, sets the phone type to be a work phone.
             * You can set other phone types as necessary.
             */
            putExtra(
                ContactsContract.Intents.Insert.PHONE_TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_WORK
            )
        }
        context.startActivity(intent)
    }
}
package com.marekulip.smsextractor;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marekulip.smsextractor.listItems.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Contacts.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactsFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    public ContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation());
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(new MyContactsRecyclerViewAdapter(getContacts(), mListener));
        }
        return view;
    }

    /**
     *  Loads all available contacts in phone. Note that even contacts with no messages will be displayed
     *  @return list with loaded contact names and their numbers
     */
    private List<Contact> getContacts(){
        ContentResolver cr = getContext().getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        List<Contact> alContacts = null;
        if(cursor != null && cursor.moveToFirst())
        {
            alContacts = new ArrayList<>();
            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor contactsCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    String contactNumber;
                    String contactName;
                    if(contactsCursor != null && contactsCursor.moveToFirst()) {
                        contactNumber = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactName = contactsCursor.getString(contactsCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        alContacts.add(new Contact(contactNumber,contactName, contactName + " "+contactNumber));
                        contactsCursor.close();
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return alContacts;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        /**
         * Indicates click on contact from contact list.
         */
        void onListFragmentInteraction(String contactName, String phoneNumber);
    }
}

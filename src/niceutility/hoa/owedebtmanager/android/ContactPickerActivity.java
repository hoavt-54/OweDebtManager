package niceutility.hoa.owedebtmanager.android;



import java.util.Locale;

import niceutility.hoa.owedebtmanager.R;
import niceutility.hoa.owedebtmanager.util.Utils;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.QuickContactBadge;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

@SuppressLint("NewApi") public class ContactPickerActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public static final String CONTACT_NAME = "contact_name";
	public static final String CONTACT_ID= "contact_id";
	public static final String CONTACT_KEY = "contact_key";
	
	private static final String TAG = "ContactPickerActivity";
	private IndexableListView contactsListView;
	private ContactAdapter mAdapter;
	private String mSearchTerm;
	
	// Whether or not this is a search result view of this fragment, only used on pre-honeycomb
    // OS versions as search results are shown in-line via Action Bar search from honeycomb onward
    private boolean mIsSearchResultView = false;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_picker_layout);
		
		// modify action bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Select contact");
		
		contactsListView = (IndexableListView) findViewById(R.id.contact_list_view);
		mAdapter = new ContactAdapter(this);
		contactsListView.setAdapter(mAdapter);
		contactsListView.setFastScrollEnabled(true);
		contactsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long arg3) {
				
				Intent intent = new Intent(ContactPickerActivity.this, CreateNewDebt.class);
				Cursor cursor = (Cursor) adapter.getItemAtPosition(position);
				 // Generates the contact lookup Uri
	            /*final Uri contactUri = Contacts.getLookupUri(
	                    cursor.getLong(ContactsQuery.ID),
	                    cursor.getString(ContactsQuery.LOOKUP_KEY));*/
				
				String name = ((Cursor)adapter.getItemAtPosition(position)).getString(ContactsQuery.DISPLAY_NAME);
				long contactId = cursor.getLong(ContactsQuery.ID);
				String contact_lookup_key = cursor.getString(ContactsQuery.LOOKUP_KEY);
				
				intent.putExtra(CONTACT_NAME, name);
				intent.putExtra(CONTACT_ID, contactId);
				intent.putExtra(CONTACT_KEY, contact_lookup_key);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		 if (savedInstanceState != null) {
	            // If we're restoring state after this fragment was recreated then
	            // retrieve previous search term and previously selected search
	            // result.
	            mSearchTerm = savedInstanceState.getString(SearchManager.QUERY);
	        }
		
		// If there's a previously selected search item from a saved state then don't bother
        // initializing the loader as it will be restarted later when the query is populated into
        // the action bar search view (see onQueryTextChange() in onCreateOptionsMenu()).

            // Initialize the loader, and create a loader identified by ContactsQuery.QUERY_ID
			getSupportLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
            
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.contact_picker, menu);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    // Configure the search info and add any event listeners
	    
	    // In versions prior to Android 3.0, hides the search item to prevent additional
        // searches. In Android 3.0 and later, searching is done via a SearchView in the ActionBar.
        // Since the search doesn't create a new Activity to do the searching, the menu item
        // doesn't need to be turned off.
        if (mIsSearchResultView) {
            searchItem.setVisible(false);
        }

        // In version 3.0 and later, sets up and configures the ActionBar SearchView
        if (Utils.hasHoneycomb()) {

            // Retrieves the system search manager service
            final SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            // Retrieves the SearchView from the search menu item
            final SearchView searchView = (SearchView) searchItem.getActionView();

            // Assign searchable info to SearchView
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

            // Set listeners for SearchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String queryText) {
                    // Nothing needs to happen when the user submits the search string
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Called when the action bar search text has changed.  Updates
                    // the search filter, and restarts the loader to do a new query
                    // using the new search string.
                    String newFilter = !TextUtils.isEmpty(newText) ? newText : null;

                    // Don't do anything if the filter is empty
                    if (mSearchTerm == null && newFilter == null) {
                        return true;
                    }

                    // Don't do anything if the new filter is the same as the current filter
                    if (mSearchTerm != null && mSearchTerm.equals(newFilter)) {
                        return true;
                    }

                    // Updates current filter to new filter
                    mSearchTerm = newFilter;

                    getSupportLoaderManager().restartLoader(
                            ContactsQuery.QUERY_ID, null, ContactPickerActivity.this);
                    return true;
                }
            });

            if (Utils.hasICS()) {
                // This listener added in ICS
                searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) {
                        // Nothing to do when the action item is expanded
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                        // When the user collapses the SearchView the current search string is
                        // cleared and the loader restarted.
                        /*if (!TextUtils.isEmpty(mSearchTerm)) {
                            onSelectionCleared();
                        }*/
                        mSearchTerm = null;
                        getSupportLoaderManager().restartLoader(
                                ContactsQuery.QUERY_ID, null, ContactPickerActivity.this);
                        return true;
                    }
                });
            }

            if (mSearchTerm != null) {
                // If search term is already set here then this fragment is
                // being restored from a saved state and the search menu item
                // needs to be expanded and populated again.

                // Stores the search term (as it will be wiped out by
                // onQueryTextChange() when the menu item is expanded).
                final String savedSearchTerm = mSearchTerm;

                // Expands the search menu item
                if (Utils.hasICS()) {
                    searchItem.expandActionView();
                }

                // Sets the SearchView to the previous search string
                searchView.setQuery(savedSearchTerm, false);
            }
        }
	    
	    
	    
	    
	    
	    
	    
	    // When using the support library, the setOnActionExpandListener() method is
	    // static and accepts the MenuItem object as an argument
	    MenuItemCompat.setOnActionExpandListener(searchItem, new OnActionExpandListener() {
	        @Override
	        public boolean onMenuItemActionCollapse(MenuItem item) {
	            // Do something when collapsed
	            return true;  // Return true to collapse action view
	        }

	        @Override
	        public boolean onMenuItemActionExpand(MenuItem item) {
	            // Do something when expanded
	            return true;  // Return true to expand action view
	        }
	    });
	    
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.action_settings:
			return true;
		case android.R.id.home:
			setResult(RESULT_CANCELED);
			finish();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {
		
		 // If this is the loader for finding contacts in the Contacts Provider
        // (the only one supported)
        if (id == ContactsQuery.QUERY_ID) {
            Uri contentUri;

            // There are two types of searches, one which displays all contacts and
            // one which filters contacts by a search query. If mSearchTerm is set
            // then a search query has been entered and the latter should be used.

            if (mSearchTerm == null) {
                // Since there's no search string, use the content URI that searches the entire
                // Contacts table
                contentUri = ContactsQuery.CONTENT_URI;
            } else {
                // Since there's a search string, use the special content Uri that searches the
                // Contacts table. The URI consists of a base Uri and the search string.
                contentUri =
                        Uri.withAppendedPath(ContactsQuery.FILTER_URI, Uri.encode(mSearchTerm));
            }

            // Returns a new CursorLoader for querying the Contacts table. No arguments are used
            // for the selection clause. The search string is either encoded onto the content URI,
            // or no contacts search string is used. The other search criteria are constants. See
            // the ContactsQuery interface.
            return new CursorLoader(this,
                    contentUri,
                    ContactsQuery.PROJECTION,
                    ContactsQuery.SELECTION,
                    null,
                    ContactsQuery.SORT_ORDER);
        }

        Log.e(TAG, "onCreateLoader - incorrect ID provided (" + id + ")");
        return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// This swaps the new cursor into the adapter.
        if (loader.getId() == ContactsQuery.QUERY_ID) {
            mAdapter.swapCursor(data);
        }
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		
		if (loader.getId() == ContactsQuery.QUERY_ID) {
            // When the loader is being reset, clear the cursor from the adapter. This allows the
            // cursor resources to be freed.
            mAdapter.swapCursor(null);
        }
		
	}
	
	   @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        if (!TextUtils.isEmpty(mSearchTerm)) {
	            // Saves the current search string
	            outState.putString(SearchManager.QUERY, mSearchTerm);

	        }
	    }
	
	
	public class ContactAdapter extends CursorAdapter implements SectionIndexer{
		private LayoutInflater mInflater;
		private AlphabetIndexer mAlphabetIndexer; // Stores the AlphabetIndexer instance	
		private TextAppearanceSpan highlightTextSpan;
	     /**
         * Instantiates a new Contacts Adapter.
         * @param context A context that has access to the app's layout.
         */
        public ContactAdapter(Context context) {
            super(context, null, 0);

            // Stores inflater for use later
            mInflater = LayoutInflater.from(context);

            // Loads a string containing the English alphabet. To fully localize the app, provide a
            // strings.xml file in res/values-<x> directories, where <x> is a locale. In the file,
            // define a string with android:name="alphabet" and contents set to all of the
            // alphabetic characters in the language in their proper sort order, in upper case if
            // applicable.
            final String alphabet = context.getString(R.string.alphabet);

            // Instantiates a new AlphabetIndexer bound to the column used to sort contact names.
            // The cursor is left null, because it has not yet been retrieved.
            mAlphabetIndexer = new AlphabetIndexer(null, ContactsQuery.SORT_KEY, alphabet);

            // Defines a span for highlighting the part of a display name that matches the search
            // string
            highlightTextSpan = new TextAppearanceSpan(getApplicationContext(), R.style.searchTextHiglight);
        }

        
        
        /**
         * Identifies the start of the search string in the display name column of a Cursor row.
         * E.g. If displayName was "Adam" and search query (mSearchTerm) was "da" this would
         * return 1.
         *
         * @param displayName The contact display name.
         * @return The starting position of the search string in the display name, 0-based. The
         * method returns -1 if the string is not found in the display name, or if the search
         * string is empty or null.
         */
        private int indexOfSearchQuery(String displayName) {
            if (!TextUtils.isEmpty(mSearchTerm)) {
                return displayName.toLowerCase(Locale.getDefault()).indexOf(
                        mSearchTerm.toLowerCase(Locale.getDefault()));
            }
            return -1;
        }
        
        @Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
        	final View itemLayout =
                    mInflater.inflate(R.layout.contact_list_item, parent, false);
        	
        	 final ViewHolder holder = new ViewHolder();
             holder.text1 = (TextView) itemLayout.findViewById(android.R.id.text1);
             holder.text2 = (TextView) itemLayout.findViewById(android.R.id.text2);
             holder.icon = (QuickContactBadge) itemLayout.findViewById(android.R.id.icon);

             // Stores the resourceHolder instance in itemLayout. This makes resourceHolder
             // available to bindView and other methods that receive a handle to the item view.
             itemLayout.setTag(holder);
        	
        	
			return itemLayout;
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			final ViewHolder holder = (ViewHolder) view.getTag();

            // For Android 3.0 and later, gets the thumbnail image Uri from the current Cursor row.
            // For platforms earlier than 3.0, this isn't necessary, because the thumbnail is
            // generated from the other fields in the row.
            final String photoUri = cursor.getString(ContactsQuery.PHOTO_THUMBNAIL_DATA);

            final String displayName = cursor.getString(ContactsQuery.DISPLAY_NAME);

            final int startIndex = indexOfSearchQuery(displayName);

            if (startIndex == -1) {
                // If the user didn't do a search, or the search string didn't match a display
                // name, show the display name without highlighting
                holder.text1.setText(displayName);

                if (TextUtils.isEmpty(mSearchTerm)) {
                    // If the search search is empty, hide the second line of text
                    holder.text2.setVisibility(View.GONE);
                } else {
                    // Shows a second line of text that indicates the search string matched
                    // something other than the display name
                    holder.text2.setVisibility(View.VISIBLE);
                }
            } else {
                // If the search string matched the display name, applies a SpannableString to
                // highlight the search string with the displayed display name

                // Wraps the display name in the SpannableString
                final SpannableString highlightedName = new SpannableString(displayName);

                // Sets the span to start at the starting point of the match and end at "length"
                // characters beyond the starting point
                highlightedName.setSpan(highlightTextSpan, startIndex,
                        startIndex + mSearchTerm.length(), 0);

                // Binds the SpannableString to the display name View object
                holder.text1.setText(highlightedName);

                // Since the search string matched the name, this hides the secondary message
                holder.text2.setVisibility(View.GONE);
            }

            

            // Generates the contact lookup Uri
            final Uri contactUri = Contacts.getLookupUri(
                    cursor.getLong(ContactsQuery.ID),
                    cursor.getString(ContactsQuery.LOOKUP_KEY));

            // Binds the contact's lookup Uri to the QuickContactBadge
            holder.icon.assignContactUri(contactUri);
           
            // Loads the thumbnail image pointed to by photoUri into the QuickContactBadge in a
            // background worker thread
            Picasso.with(getApplicationContext())
            	.load(photoUri)
            	.placeholder(R.drawable.user_placeholder)
            	.error(R.drawable.user_placeholder)
            	.into(holder.icon);
		}
		
		

        /**
         * Overrides swapCursor to move the new Cursor into the AlphabetIndex as well as the
         * CursorAdapter.
         */
        @Override
        public Cursor swapCursor(Cursor newCursor) {
        	// Update the AlphabetIndexer with new cursor as well
            mAlphabetIndexer.setCursor(newCursor);
            return super.swapCursor(newCursor);
        }

        /**
         * An override of getCount that simplifies accessing the Cursor. If the Cursor is null,
         * getCount returns zero. As a result, no test for Cursor == null is needed.
         */
        @Override
        public int getCount() {
            if (getCursor() == null) {
                return 0;
            }
            return super.getCount();
        }

		
		 /**
         * A class that defines fields for each resource ID in the list item layout. This allows
         * ContactsAdapter.newView() to store the IDs once, when it inflates the layout, instead of
         * calling findViewById in each iteration of bindView.
         */
        private class ViewHolder {
            TextView text1;
            TextView text2;
            QuickContactBadge icon;
        }


		@Override
		public int getPositionForSection(int i) {
			 if (getCursor() == null) {
	                return 0;
	            }
	            return mAlphabetIndexer.getPositionForSection(i);
		}


		@Override
		public int getSectionForPosition(int i) {
			 if (getCursor() == null) {
	                return 0;
	            }
	            return mAlphabetIndexer.getSectionForPosition(i);
		}


		@Override
		public Object[] getSections() {
			 return mAlphabetIndexer.getSections();
		}
	}

	
	  /**
     * This interface defines constants for the Cursor and CursorLoader, based on constants defined
     * in the {@link android.provider.ContactsContract.Contacts} class.
     */
    public interface ContactsQuery {

        // An identifier for the loader
        final static int QUERY_ID = 1;

        // A content URI for the Contacts table
        final static Uri CONTENT_URI = Contacts.CONTENT_URI;

        // The search/filter query Uri
        final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;

        // The selection clause for the CursorLoader query. The search criteria defined here
        // restrict results to contacts that have a display name and are linked to visible groups.
        // Notice that the search on the string provided by the user is implemented by appending
        // the search string to CONTENT_FILTER_URI.
        @SuppressLint("InlinedApi")
        final static String SELECTION =
                (Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) +
                "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

        // The desired sort order for the returned Cursor. In Android 3.0 and later, the primary
        // sort key allows for localization. In earlier versions. use the display name as the sort
        // key.
        @SuppressLint("InlinedApi")
        final static String SORT_ORDER =
                Utils.hasHoneycomb() ? Contacts.SORT_KEY_PRIMARY : Contacts.DISPLAY_NAME;

        // The projection for the CursorLoader query. This is a list of columns that the Contacts
        // Provider should return in the Cursor.
        @SuppressLint("InlinedApi")
        final static String[] PROJECTION = {

                // The contact's row id
                Contacts._ID,

                // A pointer to the contact that is guaranteed to be more permanent than _ID. Given
                // a contact's current _ID value and LOOKUP_KEY, the Contacts Provider can generate
                // a "permanent" contact URI.
                Contacts.LOOKUP_KEY,

                // In platform version 3.0 and later, the Contacts table contains
                // DISPLAY_NAME_PRIMARY, which either contains the contact's displayable name or
                // some other useful identifier such as an email address. This column isn't
                // available in earlier versions of Android, so you must use Contacts.DISPLAY_NAME
                // instead.
                Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,

                // In Android 3.0 and later, the thumbnail image is pointed to by
                // PHOTO_THUMBNAIL_URI. In earlier versions, there is no direct pointer; instead,
                // you generate the pointer from the contact's ID value and constants defined in
                // android.provider.ContactsContract.Contacts.
                Utils.hasHoneycomb() ? Contacts.PHOTO_THUMBNAIL_URI : Contacts._ID,

                // The sort order column for the returned Cursor, used by the AlphabetIndexer
                SORT_ORDER,
        };

        // The query column numbers which map to each value in the projection
        final static int ID = 0;
        final static int LOOKUP_KEY = 1;
        final static int DISPLAY_NAME = 2;
        final static int PHOTO_THUMBNAIL_DATA = 3;
        final static int SORT_KEY = 4;
    }
	
	
	
}

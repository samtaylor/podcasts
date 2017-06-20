package samtaylor.podcasts.search

import android.app.SearchManager
import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import samtaylor.podcasts.R

class SearchActivity : LifecycleActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_search )

        this.handleIntent( intent )
    }

    private fun handleIntent( intent: Intent? )
    {
        intent?.let {
            if ( it.action == Intent.ACTION_SEARCH )
            {
                val query = it.extras[ SearchManager.QUERY ] as String
                Log.d( "XXX", "Search query is $query" )
            }
        }
    }
}

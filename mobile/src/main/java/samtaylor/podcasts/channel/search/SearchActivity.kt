package samtaylor.podcasts.channel.search

import android.app.SearchManager
import android.arch.lifecycle.LifecycleActivity
import android.os.Bundle
import samtaylor.podcasts.R

class SearchActivity : LifecycleActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_search )

        val query = this.intent.extras[ SearchManager.QUERY ] as String

        val searchFragment = SearchFragment.newInstance( query )
        this.supportFragmentManager.beginTransaction().replace( R.id.search_fragment_container, searchFragment ).commit()
    }
}

package samtaylor.podcasts.channel.search

import android.arch.lifecycle.ViewModel

class SearchViewModel : ViewModel()
{
    private var searchLiveData: SearchLiveData? = null

    fun getSearch( query: String ): SearchLiveData
    {
        if ( this.searchLiveData == null )
        {
            this.searchLiveData = SearchLiveData( query )
        }

        return this.searchLiveData!!
    }
}
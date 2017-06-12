package samtaylor.podcasts.shows

import android.arch.lifecycle.LiveData
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import samtaylor.podcasts.dataModel.Show
import samtaylor.podcasts.fetcher.CachedJsonFetcher

class ShowLiveData( private val showId: Int ): LiveData<Show>()
{
    private val fetcher = CachedJsonFetcher {
        Gson().fromJson<Show>( it.getJSONObject( "response" ).getJSONObject( "show" ).toString() )
    }

    override fun onActive()
    {
        this.fetcher.fetch( "https://api.spreaker.com/v2/shows/$showId" ) {
            this.value = it
        }
    }

}
package samtaylor.podcasts.channel

import android.arch.lifecycle.LiveData
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import samtaylor.podcasts.dataModel.Show
import samtaylor.podcasts.fetcher.CachedJsonFetcher

class ChannelLiveData( private val list_id: Int ): LiveData<List<Show>>()
{
    private val fetcher = CachedJsonFetcher { json ->
        Gson().fromJson<List<Show>>( json.getJSONObject( "response" ).getJSONArray( "items" ).toString() )
    }

    override fun onActive()
    {
        this.fetcher.fetch( "https://api.spreaker.com/v2/explore/lists/$list_id/items" ) { shows ->
            this.value = shows
        }
    }

}

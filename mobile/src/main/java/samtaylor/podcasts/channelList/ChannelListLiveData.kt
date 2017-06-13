package samtaylor.podcasts.channelList

import android.arch.lifecycle.LiveData
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import samtaylor.podcasts.dataModel.Channel
import samtaylor.podcasts.fetcher.CachedJsonFetcher

class ChannelListLiveData : LiveData<List<Channel>>()
{
    private val fetcher = CachedJsonFetcher {
        Gson().fromJson<List<Channel>>( it.getJSONObject( "response" ).getJSONArray( "items" ).toString() )
    }

    override fun onActive()
    {
        this.fetcher.fetch( "https://api.spreaker.com/v2/explore/lists?country=GB&limit=3" ) {
            this.value = it
        }
    }

}
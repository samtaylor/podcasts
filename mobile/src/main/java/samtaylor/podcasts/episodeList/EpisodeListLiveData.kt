package samtaylor.podcasts.episodeList

import android.arch.lifecycle.LiveData
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import samtaylor.podcasts.dataModel.Episode
import samtaylor.podcasts.fetcher.CachedJsonFetcher

class EpisodeListLiveData( private val showId: Int ): LiveData<List<Episode>>()
{
    private val fetcher = CachedJsonFetcher {
        Gson().fromJson<List<Episode>>( it.getJSONObject( "response" ).getJSONArray( "items" ).toString() )
    }

    override fun onActive()
    {
        this.fetcher.fetch( "https://api.spreaker.com/v2/shows/$showId/episodes" ) {
            this.value = it
        }
    }
}


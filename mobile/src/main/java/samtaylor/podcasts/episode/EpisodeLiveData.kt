package samtaylor.podcasts.episode

import android.arch.lifecycle.LiveData
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import samtaylor.podcasts.dataModel.Episode
import samtaylor.podcasts.fetcher.CachedJsonFetcher

class EpisodeLiveData( private val episodeId: Int ) : LiveData<Episode>()
{
    private val fetcher = CachedJsonFetcher {
        Gson().fromJson<Episode>( it.getJSONObject( "response" ).getJSONObject( "episode" ).toString() )
    }

    override fun onActive()
    {
        this.fetcher.fetch( "https://api.spreaker.com/v2/episodes/$episodeId" ) {
            this.value = it
        }
    }
}
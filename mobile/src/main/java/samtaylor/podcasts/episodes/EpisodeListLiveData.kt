package samtaylor.podcasts.episodes

import android.arch.lifecycle.LiveData
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

class EpisodeListLiveData(private val show_id: Int ): LiveData<List<Episode>>()
{
    override fun onActive()
    {
        "https://api.spreaker.com/v2/shows/$show_id/episodes".httpGet().responseJson { _, _, result ->
            when ( result )
            {
                is Result.Success -> {
                    val json = result.value.obj()

                    this.value = Gson().fromJson<List<Episode>>( json.getJSONObject( "response" ).getJSONArray( "items" ).toString() )
                }
            }
        }
    }
}


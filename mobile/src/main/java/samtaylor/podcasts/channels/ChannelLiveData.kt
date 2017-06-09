package samtaylor.podcasts.channels

import android.arch.lifecycle.LiveData
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import samtaylor.podcasts.shows.Show

class ChannelLiveData( private val list_id: Int ): LiveData<List<Show>>()
{
    override fun onActive()
    {
        "https://api.spreaker.com/v2/explore/lists/$list_id/items".httpGet().responseJson { _, _, result ->
            when ( result )
            {
                is Result.Success -> {
                    val json = result.value.obj()

                    this.value = Gson().fromJson<List<Show>>( json.getJSONObject( "response" ).getJSONArray( "items" ).toString() )
                }
            }
        }
    }

}

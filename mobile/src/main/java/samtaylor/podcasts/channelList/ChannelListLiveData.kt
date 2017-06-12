package samtaylor.podcasts.channelList

import android.arch.lifecycle.LiveData
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.salomonbrys.kotson.fromJson

class ChannelListLiveData : LiveData<List<ChannelListLiveData.Channel>>()
{
    private var cacheValue: String? = null

    override fun onActive()
    {
        "https://api.spreaker.com/v2/explore/lists?country=GB&limit=3".httpGet().responseJson { _, _, result ->
            when ( result )
            {
                is Result.Success ->
                {
                    val json = result.value.obj()

                    if ( this.cacheValue != json.toString() )
                    {
                        this.value = com.google.gson.Gson().fromJson<List<Channel>>( json.getJSONObject( "response" ).getJSONArray( "items" ).toString() )
                        this.cacheValue = json.toString()
                    }
                }
            }
        }
    }

    inner class Channel( val list_id: Int, val name: String )
}
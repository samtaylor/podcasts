package samtaylor.podcasts.shows

import android.arch.lifecycle.LiveData
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

class ShowLiveData( private val show_id: Int ): LiveData<Show>()
{
    override fun onActive()
    {
        "https://api.spreaker.com/v2/shows/$show_id".httpGet().responseJson { _, _, result ->
            when ( result )
            {
                is Result.Success -> {
                    val json = result.value.obj()

                    this.value = Gson().fromJson<Show>( json.getJSONObject( "response" ).getJSONObject( "show" ).toString() )
                }
            }
        }
    }
}
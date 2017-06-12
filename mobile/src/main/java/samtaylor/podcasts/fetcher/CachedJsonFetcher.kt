package samtaylor.podcasts.fetcher

import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.json.JSONObject

class CachedJsonFetcher<out T>( private val parser: (JSONObject) -> T )
{
    private var cache: String? = null

    fun fetch( url: String, callback: (T) -> Unit  )
    {
        url.httpGet().responseJson { _, _, result ->
            when ( result )
            {
                is Result.Success -> {
                    val json = result.value.obj()

                    if ( json.toString() != cache )
                    {
                        cache = json.toString()

                        callback( parser( json ) )
                    }
                }
            }
        }
    }
}
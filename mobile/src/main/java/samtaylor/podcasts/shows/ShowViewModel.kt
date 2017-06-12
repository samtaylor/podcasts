package samtaylor.podcasts.shows

import android.arch.lifecycle.ViewModel

class ShowViewModel : ViewModel()
{
    val showLiveDataTable: HashMap<Int, ShowLiveData> = HashMap()

    fun getShow( showId: Int ) : ShowLiveData
    {
        if ( showLiveDataTable[ showId ] == null )
        {
            showLiveDataTable[ showId ] = ShowLiveData( showId )
        }

        return showLiveDataTable[ showId ]!!
    }
}
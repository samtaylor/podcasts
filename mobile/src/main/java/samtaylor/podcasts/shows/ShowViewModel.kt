package samtaylor.podcasts.shows

import android.arch.lifecycle.ViewModel

class ShowViewModel : ViewModel()
{
    private var showLiveData: ShowLiveData? = null

    fun getShow( showId: Int ) : ShowLiveData
    {
        if ( this.showLiveData == null )
        {
            this.showLiveData = ShowLiveData( showId )
        }

        return this.showLiveData!!
    }
}
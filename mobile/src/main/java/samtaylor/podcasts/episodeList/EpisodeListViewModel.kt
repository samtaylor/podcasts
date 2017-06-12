package samtaylor.podcasts.episodeList

import android.arch.lifecycle.ViewModel

class EpisodeListViewModel : ViewModel()
{
    private val episodeListLiveDataTable: HashMap<Int, EpisodeListLiveData> = HashMap()

    operator fun get( showId: Int ): EpisodeListLiveData
    {
        if ( this.episodeListLiveDataTable[ showId ] == null )
        {
            this.episodeListLiveDataTable[ showId ]= EpisodeListLiveData( showId )
        }
        return this.episodeListLiveDataTable[ showId ]!!
    }
}
package samtaylor.podcasts.episodeList

import android.arch.lifecycle.ViewModel

class EpisodeListViewModel : ViewModel()
{
    private var episodeListLiveData: EpisodeListLiveData? = null

    fun getEpisodeList( showId: Int ): EpisodeListLiveData
    {
        if ( this.episodeListLiveData == null )
        {
            this.episodeListLiveData = EpisodeListLiveData( showId )
        }
        return this.episodeListLiveData!!
    }
}
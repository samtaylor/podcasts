package samtaylor.podcasts.episodes

import android.arch.lifecycle.ViewModel

class EpisodeListViewModel : ViewModel()
{
    private var episodeListLiveData: EpisodeListLiveData? = null

    fun getEpisodeListLiveData( show_id: Int ): EpisodeListLiveData
    {
        if ( episodeListLiveData == null )
        {
            episodeListLiveData = EpisodeListLiveData( show_id )
        }
        return episodeListLiveData!!
    }

}
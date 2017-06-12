package samtaylor.podcasts.episodeList

import android.arch.lifecycle.ViewModel

class EpisodeListViewModel : ViewModel()
{
    private val episodeListLiveDataTable: HashMap<Int, EpisodeListLiveData> = HashMap()

    fun getEpisodeListLiveData( show_id: Int ): EpisodeListLiveData
    {
        if ( this.episodeListLiveDataTable[ show_id ] == null )
        {
            this.episodeListLiveDataTable[ show_id ]= EpisodeListLiveData( show_id )
        }
        return this.episodeListLiveDataTable[ show_id ]!!
    }
}
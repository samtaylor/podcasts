package samtaylor.podcasts.episode

import android.arch.lifecycle.ViewModel

class EpisodeViewModel: ViewModel()
{
    private val episodeLiveDataTable: HashMap<Int, EpisodeLiveData> = HashMap()

    operator fun get( episodeId: Int ): EpisodeLiveData
    {
        if ( this.episodeLiveDataTable[ episodeId ] == null )
        {
            this.episodeLiveDataTable[ episodeId ] = EpisodeLiveData( episodeId )
        }

        return this.episodeLiveDataTable[ episodeId ]!!
    }
}
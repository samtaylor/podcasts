package samtaylor.podcasts.episode

import android.arch.lifecycle.ViewModel

class EpisodeViewModel: ViewModel()
{
    private var episodeLiveData: EpisodeLiveData? = null

    fun getEpisode( episodeId: Int ): EpisodeLiveData
    {
        if ( this.episodeLiveData == null )
        {
            this.episodeLiveData = EpisodeLiveData( episodeId )
        }

        return this.episodeLiveData!!
    }
}
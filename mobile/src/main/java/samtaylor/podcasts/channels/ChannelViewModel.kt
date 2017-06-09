package samtaylor.podcasts.channels

import android.arch.lifecycle.ViewModel

class ChannelViewModel: ViewModel()
{
    private var channelLiveData: ChannelLiveData? = null

    fun getChannel( item_id: Int ): ChannelLiveData
    {
        if ( this.channelLiveData == null )
        {
            this.channelLiveData = ChannelLiveData( item_id )
        }
        return this.channelLiveData!!
    }
}
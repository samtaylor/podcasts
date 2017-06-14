package samtaylor.podcasts.channel

import android.arch.lifecycle.ViewModel

class ChannelViewModel: ViewModel()
{
    private var channelLiveData: ChannelLiveData? = null

    fun getChannel( itemId: Int ): ChannelLiveData
    {
        if ( this.channelLiveData == null )
        {
            this.channelLiveData = ChannelLiveData( itemId )
        }
        return this.channelLiveData!!
    }
}
package samtaylor.podcasts.channel

import android.arch.lifecycle.ViewModel

class ChannelViewModel: ViewModel()
{
    private val channelLiveDataTable: HashMap<Int, ChannelLiveData> = HashMap()

    operator fun get( itemId: Int ): ChannelLiveData
    {
        if ( this.channelLiveDataTable[ itemId ] == null )
        {
            this.channelLiveDataTable[ itemId ] = ChannelLiveData( itemId )
        }
        return this.channelLiveDataTable[ itemId ]!!
    }
}
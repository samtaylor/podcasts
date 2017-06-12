package samtaylor.podcasts.channel

import android.arch.lifecycle.ViewModel

class ChannelViewModel: ViewModel()
{
    private val channelLiveDataTable: HashMap<Int, ChannelLiveData> = HashMap()

    fun getChannel( item_id: Int ): ChannelLiveData
    {
        if ( this.channelLiveDataTable[ item_id ] == null )
        {
            this.channelLiveDataTable[ item_id ] = ChannelLiveData( item_id )
        }
        return this.channelLiveDataTable[ item_id ]!!
    }
}
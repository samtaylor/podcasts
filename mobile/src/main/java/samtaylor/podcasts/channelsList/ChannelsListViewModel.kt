package samtaylor.podcasts.channelsList

class ChannelsListViewModel : android.arch.lifecycle.ViewModel()
{
    private var channelsList: ChannelsListLiveData? = null

    fun getChannelsList(): ChannelsListLiveData
    {
        if ( channelsList == null )
        {
            channelsList = ChannelsListLiveData()
        }
        return channelsList!!
    }
}

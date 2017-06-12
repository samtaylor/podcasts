package samtaylor.podcasts.channelList

class ChannelListViewModel : android.arch.lifecycle.ViewModel()
{
    private var channelList: ChannelListLiveData? = null

    fun getChannelList(): ChannelListLiveData
    {
        if ( channelList == null )
        {
            channelList = ChannelListLiveData()
        }
        return channelList!!
    }
}

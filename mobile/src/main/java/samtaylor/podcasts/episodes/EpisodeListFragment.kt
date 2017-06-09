package samtaylor.podcasts.episodes

import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import samtaylor.podcasts.R

class EpisodeListFragment: LifecycleFragment()
{
    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_episode_list, container, false )

//        val index = arguments.getInt( ARG_SECTION_INDEX )
//
//        val channelListViewModel = ViewModelProviders.of( this.activity ).get( ChannelsListViewModel::class.java )
//        val channelViewModel = ViewModelProviders.of( this ).get( ChannelViewModel::class.java )
//
//        val channel = channelListViewModel.getChannelsList().value!![ index ]
//        val channelLiveData = channelViewModel.getChannel( channel.list_id )
//
//        channelLiveData.observe( this, Observer { shows ->
//            val recyclerView = rootView.findViewById( R.id.channel_list ) as RecyclerView
//            recyclerView.setHasFixedSize( true )
//
//            recyclerView.layoutManager = LinearLayoutManager( this.context )
//
//            recyclerView.adapter = ShowsAdapter( shows )
//
//        } )

        return rootView
    }

    companion object
    {
        val ARG_SHOW_ID = "show_id"
    }
}
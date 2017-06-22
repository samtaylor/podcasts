package samtaylor.podcasts.channel

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import samtaylor.podcasts.R
import samtaylor.podcasts.channelList.ChannelListViewModel

class ChannelFragment : LifecycleFragment()
{
    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_channel, container, false )

        val index = arguments[ ARG_SECTION_INDEX ] as Int

        val channelListViewModel = ViewModelProviders.of( this.activity )[ ChannelListViewModel::class.java ]
        val channelViewModel = ViewModelProviders.of( this )[ ChannelViewModel::class.java ]

        val channel = channelListViewModel.getChannelList().value!![ index ]
        val channelLiveData = channelViewModel.getChannel( channel.list_id )

        channelLiveData.observe( this, Observer { shows ->
            val recyclerView = rootView.findViewById( R.id.channel_list ) as RecyclerView
            recyclerView.setHasFixedSize( true )

            recyclerView.layoutManager = GridLayoutManager( this.context, 2 )

            recyclerView.adapter = ShowsAdapter( shows )

        } )

        return rootView
    }

    companion object
    {
        private val ARG_SECTION_INDEX = "section_index"

        fun newInstance( index: Int ): ChannelFragment
        {
            val fragment = ChannelFragment()
            val args = Bundle()
            args.putInt( ARG_SECTION_INDEX, index )
            fragment.arguments = args
            return fragment
        }
    }

}
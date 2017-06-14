package samtaylor.podcasts.channel

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import samtaylor.podcasts.R
import samtaylor.podcasts.channelList.ChannelListViewModel
import samtaylor.podcasts.dataModel.Show
import samtaylor.podcasts.shows.ShowActivity

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

            recyclerView.layoutManager = LinearLayoutManager( this.context )

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

    inner class ShowViewHolder( val view: View ): RecyclerView.ViewHolder( view )

    inner class ShowsAdapter( val shows: List<Show>? ) : RecyclerView.Adapter<ShowViewHolder>()
    {
        override fun onCreateViewHolder(viewGroup: ViewGroup?, viewType: Int ): ShowViewHolder
        {
            val textView = LayoutInflater.from( viewGroup?.context ).inflate( android.R.layout.simple_list_item_1, viewGroup, false )

            return ShowViewHolder( textView )
        }

        override fun onBindViewHolder( viewHolder: ShowViewHolder?, position: Int )
        {
            val textView = viewHolder?.view as TextView
            val show = shows?.get( position )

            textView.text = show!!.title
            textView.setOnClickListener { _ ->

                val intent = Intent( textView.context, ShowActivity::class.java )
                intent.putExtra( ShowActivity.EXTRA_SHOW_ID, show.show_id )
                textView.context.startActivity( intent )
            }
        }

        override fun getItemCount(): Int
        {
            return shows?.size ?: 0
        }
    }
}
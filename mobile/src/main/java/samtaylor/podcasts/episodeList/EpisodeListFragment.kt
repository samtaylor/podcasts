package samtaylor.podcasts.episodeList

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
import samtaylor.podcasts.dataModel.Episode
import samtaylor.podcasts.episode.EpisodeActivity

class EpisodeListFragment: LifecycleFragment()
{
    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_episode_list, container, false )

        val showId = arguments[ ARG_SHOW_ID ] as Int

        val episodeListViewModel = ViewModelProviders.of( this.activity )[ EpisodeListViewModel::class.java ]

        val episodeListLiveData = episodeListViewModel.getEpisodeList( showId )
        episodeListLiveData.observe( this, Observer { episodes ->
            val recyclerView = rootView.findViewById( R.id.episode_list ) as RecyclerView
            recyclerView.setHasFixedSize( true )

            recyclerView.layoutManager = LinearLayoutManager( this.context )

            recyclerView.adapter = EpisodesAdapter( episodes )
        } )

        return rootView
    }

    companion object
    {
        val ARG_SHOW_ID = "show_id"

        fun newInstance( showId: Int ): EpisodeListFragment
        {
            val fragment = EpisodeListFragment()
            val args = Bundle()
            args.putInt( ARG_SHOW_ID, showId )
            fragment.arguments = args
            return fragment
        }
    }

    inner class EpisodeViewHolder( val view: View ): RecyclerView.ViewHolder( view )

    inner class EpisodesAdapter( val episodes: List<Episode>? ): RecyclerView.Adapter<EpisodeViewHolder>()
    {
        override fun getItemCount(): Int
        {
            return episodes?.size ?: 0
        }

        override fun onCreateViewHolder( viewGroup: ViewGroup?, position: Int ): EpisodeViewHolder
        {
            val textView = LayoutInflater.from( viewGroup?.context ).inflate( android.R.layout.simple_list_item_1, viewGroup, false )

            return EpisodeViewHolder( textView )
        }

        override fun onBindViewHolder( viewHolder: EpisodeViewHolder?, position: Int )
        {
            val textView = viewHolder?.view as TextView
            val episode = episodes?.get( position )

            textView.text = episode?.title
            textView.setOnClickListener { _ ->

                val intent = Intent( textView.context, EpisodeActivity::class.java )
                intent.putExtra( EpisodeActivity.EXTRA_EPISODE_ID, episode?.episode_id )
                textView.context.startActivity( intent )
            }
        }
    }
}
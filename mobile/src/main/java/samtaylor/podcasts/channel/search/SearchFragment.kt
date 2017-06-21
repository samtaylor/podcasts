package samtaylor.podcasts.channel.search

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import samtaylor.podcasts.R
import samtaylor.podcasts.channel.ShowsAdapter

class SearchFragment : LifecycleFragment()
{
    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_search, container, false )

        val viewModel = ViewModelProviders.of( this.activity )[ SearchViewModel::class.java ]
        viewModel.getSearch( this.arguments[ ARG_SEARCH_QUERY ] as String ).observe( this, Observer { shows ->

            val recyclerView = rootView.findViewById( R.id.search_list ) as RecyclerView
            recyclerView.setHasFixedSize( true )

            recyclerView.layoutManager = LinearLayoutManager( this.context )

            recyclerView.adapter = ShowsAdapter( shows )
        })

        return rootView
    }

    companion object
    {
        val ARG_SEARCH_QUERY = "search_query"

        fun newInstance( query: String ): SearchFragment
        {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString( ARG_SEARCH_QUERY, query )
            fragment.arguments = args
            return fragment
        }
    }
}
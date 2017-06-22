package samtaylor.podcasts.channelList

import android.app.SearchManager
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.Menu
import android.widget.SearchView
import android.widget.Toolbar
import samtaylor.podcasts.R
import samtaylor.podcasts.channel.ChannelFragment
import samtaylor.podcasts.dataModel.Channel
import samtaylor.podcasts.channel.search.SearchActivity
import samtaylor.podcasts.playback.PlaybackFragment


class MainActivity : LifecycleActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_main )

        val toolbar = findViewById( R.id.toolbar ) as Toolbar
        setActionBar( toolbar )

        val tabLayout = findViewById( R.id.tabs ) as TabLayout
        val channelsViewPager = findViewById( R.id.channels ) as ViewPager
        val listsData = ViewModelProviders.of( this )[ ChannelListViewModel::class.java ]

        listsData.getChannelList().observe( this, Observer { channelList ->

            channelsViewPager.adapter = ChannelsAdapter( supportFragmentManager, channelList!! )

            for ( channel in channelList )
            {
                val tab = tabLayout.newTab()
                tab.text = channel.name
                tabLayout.addTab( tab )
            }
        } )

        channelsViewPager.addOnPageChangeListener( TabLayout.TabLayoutOnPageChangeListener( tabLayout ) )
        tabLayout.addOnTabSelectedListener( TabLayout.ViewPagerOnTabSelectedListener( channelsViewPager ) )

        val playbackFragment = this.supportFragmentManager.findFragmentById( R.id.playback_fragment ) as PlaybackFragment
        playbackFragment.paddingAdjustment = {
            val view = this.findViewById( R.id.channels )
            view.setPadding( 0, 0, 0, it )
        }
    }

    override fun onCreateOptionsMenu( menu: Menu? ): Boolean
    {
        this.menuInflater.inflate( R.menu.menu_main, menu )

        val searchManager = getSystemService( Context.SEARCH_SERVICE ) as SearchManager
        val searchView = menu?.findItem( R.id.search )?.actionView as SearchView
        searchView.setSearchableInfo( searchManager.getSearchableInfo( ComponentName( this, SearchActivity::class.java ) ) )

        return true
    }

    inner class ChannelsAdapter( fm: FragmentManager, val channels: List<Channel> ) : FragmentPagerAdapter( fm )
    {
        override fun getItem( index: Int ): Fragment
        {
            return ChannelFragment.newInstance( index )
        }

        override fun getCount(): Int
        {
            return this.channels.size
        }
    }
}

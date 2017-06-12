package samtaylor.podcasts.channelList

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.widget.Toolbar
import samtaylor.podcasts.R
import samtaylor.podcasts.channel.ChannelFragment

class MainActivity : LifecycleActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_main )

        val toolbar = findViewById( R.id.toolbar ) as Toolbar
        setActionBar( toolbar )

        val listsData = ViewModelProviders.of( this ).get( ChannelListViewModel::class.java )
        listsData.getChannelsList().observe( this, Observer { channelsList ->
            val channelsAdapter = ChannelsAdapter( supportFragmentManager, channelsList!! )

            val channelsViewPager = findViewById( R.id.channels ) as ViewPager
            channelsViewPager.adapter = channelsAdapter

            val tabLayout = findViewById( R.id.tabs ) as TabLayout
            tabLayout.removeAllTabs()
            for ( podcastList in channelsList )
            {
                val tab = tabLayout.newTab()
                tab.text = podcastList.name
                tabLayout.addTab( tab )
            }

            channelsViewPager.addOnPageChangeListener( TabLayout.TabLayoutOnPageChangeListener( tabLayout ) )
            tabLayout.addOnTabSelectedListener( TabLayout.ViewPagerOnTabSelectedListener(channelsViewPager) )
        } )
    }

    inner class ChannelsAdapter(fm: FragmentManager, val channels: List<ChannelListLiveData.Channel> ) : FragmentPagerAdapter( fm )
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
package samtaylor.podcasts.shows

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toolbar
import samtaylor.podcasts.R
import samtaylor.podcasts.episodeList.EpisodeListFragment

class ShowActivity : AppCompatActivity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_show )

        val showId = this.intent.extras[ EXTRA_SHOW_ID ] as Int
        val showViewModel = ViewModelProviders.of( this )[ ShowViewModel::class.java ]

        val toolbar = findViewById( R.id.toolbar ) as Toolbar
        setActionBar( toolbar )

        showViewModel.getShow( showId ).observe( this, Observer { show ->

            val title = findViewById( R.id.show_title ) as TextView
            val author = findViewById( R.id.show_author ) as TextView
            val description = findViewById( R.id.show_description ) as TextView

            show?.let {
                this.title = show.title

                title.text = show.title
                author.text = show.author.fullname
                description.text = show.description

                val fragmentTransaction = this.supportFragmentManager.beginTransaction()
                val episodeFragment = EpisodeListFragment.newInstance( show.show_id )

                fragmentTransaction.add( R.id.episode_list_container, episodeFragment )
                fragmentTransaction.commit()
            }
        } )
    }

    companion object
    {
        val EXTRA_SHOW_ID = "show_id"
    }
}

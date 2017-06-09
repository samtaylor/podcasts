package samtaylor.podcasts.shows

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import samtaylor.podcasts.R

class ShowActivity : LifecycleActivity()
{


    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_show )

        val show_id = this.intent.extras[ EXTRA_SHOW_ID ] as Int
        ShowLiveData( show_id ).observe( this, Observer { show ->

            val title = findViewById( R.id.show_title ) as TextView
            val author = findViewById( R.id.show_author ) as TextView
            val description = findViewById( R.id.show_description ) as TextView

            title.text = show?.title
            author.text = show?.author?.fullname
            description.text = show?.description
        } )
    }

    companion object
    {
        val EXTRA_SHOW_ID = "show_id"
    }
}

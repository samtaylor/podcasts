package samtaylor.podcasts.episode

import android.app.Activity
import android.os.Bundle
import samtaylor.podcasts.R

class EpisodeActivity : Activity()
{
    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )
        setContentView( R.layout.activity_episode )
    }

    companion object
    {
        val EXTRA_EPISODE_ID = "episode_id"
    }
}

package samtaylor.podcasts.playback.play

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import samtaylor.podcasts.R
import samtaylor.podcasts.playback.PlaybackService
import samtaylor.podcasts.playback.PlaybackServiceConnection

class PlayButtonFragment : Fragment()
{
    private val serviceConnection = PlaybackServiceConnection { serviceConnection, state ->
        when ( state )
        {
            PlaybackServiceConnection.ConnectionState.CONNECTED -> {
                if ( serviceConnection.currentEpisode == this@PlayButtonFragment.arguments[ ARG_EPISODE_ID ] as Int )
                {
                    val button = this.view?.findViewById( R.id.play_button ) as ImageButton
                    if ( serviceConnection.playbackState == PlaybackService.PlaybackState.PLAYING )
                    {
                        button.setImageResource( android.R.drawable.ic_media_pause )
                    }
                    else
                    {
                        button.setImageResource( android.R.drawable.ic_media_play )
                    }
                }
            }
            else -> {}
        }
    }

    override fun onCreate( savedInstanceState: Bundle? )
    {
        super.onCreate( savedInstanceState )

        val intent = Intent( this.context, PlaybackService::class.java )
        this.context.bindService( intent, this.serviceConnection, Context.BIND_AUTO_CREATE )
    }

    override fun onCreateView( inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle? ): View?
    {
        val rootView = inflater!!.inflate( R.layout.fragment_play_button, container, false )

        val episodeId = this.arguments[ ARG_EPISODE_ID ] as Int

        val button = rootView.findViewById( R.id.play_button ) as ImageButton

        button.setOnClickListener {
            val playIntent = Intent( this.context, PlaybackService::class.java )
            playIntent.putExtra( PlaybackService.EXTRA_EPISODE_ID, episodeId )
            this.context.startService( playIntent )
            this.context.bindService( playIntent, this.serviceConnection, Context.BIND_AUTO_CREATE )
        }

        return rootView
    }

    companion object
    {
        val ARG_EPISODE_ID = "episode_id"

        fun newInstance( episodeId: Int ): PlayButtonFragment
        {
            val fragment = PlayButtonFragment()

            val args = Bundle()
            args.putInt( ARG_EPISODE_ID, episodeId )
            fragment.arguments = args

            return fragment
        }
    }
}
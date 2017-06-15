package samtaylor.podcasts.playback

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder

class PlaybackServiceConnection( private val callback: ( PlaybackServiceConnection, ConnectionState ) -> Unit ) : ServiceConnection
{
    enum class ConnectionState
    {
        CONNECTED, DISCONNECTED
    }
    private var playbackService: PlaybackService? = null

    private var serviceBound = false

    var currentEpisode: Int? = null
        get() = this.playbackService?.episodeId

    override fun onServiceConnected(name: ComponentName?, service: IBinder? )
    {
        val binder = service as PlaybackService.LocalBinder
        this.playbackService = binder.getService()

        this.serviceBound = true

        this.callback( this, ConnectionState.CONNECTED )
    }

    override fun onServiceDisconnected( name: ComponentName? )
    {
        this.serviceBound = false

        this.callback( this, ConnectionState.DISCONNECTED )
    }
}
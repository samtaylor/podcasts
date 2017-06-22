package samtaylor.podcasts.channel

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import samtaylor.podcasts.R

class ShowViewHolder( val view: View ): RecyclerView.ViewHolder( view )
{
    var showTitle: String
    set( value ) {
        ( this.view.findViewById( R.id.show_title ) as TextView ).text = value
    }
    get() {
        return ( this.view.findViewById( R.id.show_title ) as TextView ).text.toString()
    }

    var showAuthor: String
    set( value ) {
        ( this.view.findViewById( R.id.show_author ) as TextView ).text = value
    }
    get() {
        return ( this.view.findViewById( R.id.show_author ) as TextView ).text.toString()
    }
}
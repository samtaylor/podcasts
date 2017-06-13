package samtaylor.podcasts.dataModel

data class Show( val show_id: Int,
                 val title: String,
                 val site_url: String,
                 val image_url: String,
                 val image_original_url: String,
                 val description: String,
                 val author: Author)

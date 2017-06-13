package samtaylor.podcasts.dataModel

data class Episode( val episode_id: Int,
                    val title: String,
                    val duration: Int,
                    val explicit: Boolean,
                    val site_url: String,
                    val image_url: String,
                    val image_original_url: String,
                    val published_at: String,
                    val download_enabled: Boolean )

package com.optimus.iptv

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.SimpleExpandableListAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import java.net.HttpURLConnection
import java.net.URL

data class Channel(val name: String, val url: String, val group: String)

class MainActivity : AppCompatActivity() {

    private var allChannels: List<Channel> = emptyList()
    private var groupedChannels: List<Pair<String, List<Channel>>> = emptyList()
    private var miniPlayer: ExoPlayer? = null
    private var miniPlayingUrl: String? = null

    private lateinit var statusText: TextView
    private lateinit var listView: ExpandableListView
    private lateinit var miniPlayerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        listView = findViewById(R.id.channelList)
        miniPlayerView = findViewById(R.id.miniPlayerView)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        settingsButton.setOnClickListener {
            val allGroups = ArrayList(allChannels.map { it.group }.distinct().sorted())
            val intent = Intent(this, SettingsActivity::class.java)
            intent.putStringArrayListExtra("all_groups", allGroups)
            startActivity(intent)
        }

        val prefs = getSharedPreferences("optimus_prefs", MODE_PRIVATE)
        val host = prefs.getString("host", "") ?: ""
        val user = prefs.getString("username", "") ?: ""
        val pass = prefs.getString("password", "") ?: ""

        val m3uUrl = "http://$host/get.php?username=$user&password=$pass&type=m3u_plus&output=ts"

        Thread {
            try {
                val connection = URL(m3uUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                val text = connection.inputStream.bufferedReader().readText()
                val channels = parseM3U(text)

                runOnUiThread {
                    allChannels = channels
                    refreshDisplay()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    statusText.text = "Failed to load: ${e.message}"
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        if (allChannels.isNotEmpty()) {
            refreshDisplay()
        }
    }

    private fun refreshDisplay() {
        val prefs = getSharedPreferences("optimus_prefs", MODE_PRIVATE)
        val hiddenGroups = prefs.getStringSet("hidden_groups", emptySet()) ?: emptySet()

        val visibleChannels = allChannels.filter { !hiddenGroups.contains(it.group) }
        groupedChannels = visibleChannels.groupBy { it.group }.toList()

        statusText.text = "${visibleChannels.size} channels loaded"
        showChannels()
    }

    private fun parseM3U(text: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = text.lines()
        var currentName = ""
        var currentGroup = "Uncategorized"

        for (line in lines) {
            if (line.startsWith("#EXTINF")) {
                val groupMatch = Regex("group-title=\"(.*?)\"").find(line)
                currentGroup = groupMatch?.groupValues?.get(1)?.ifBlank { "Uncategorized" } ?: "Uncategorized"

                val nameParts = line.split(",")
                currentName = if (nameParts.size > 1) nameParts.last().trim() else "Unknown"
            } else if (line.startsWith("http")) {
                channels.add(Channel(currentName, line.trim(), currentGroup))
            }
        }
        return channels
    }

    private fun showChannels() {
        val groupData = groupedChannels.map { (groupName, channelsInGroup) ->
            mapOf("group" to "$groupName (${channelsInGroup.size})")
        }

        val childData = groupedChannels.map { (_, channelsInGroup) ->
            channelsInGroup.map { channel ->
                mapOf("child" to channel.name)
            }
        }

        val adapter = SimpleExpandableListAdapter(
            this,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("group"),
            intArrayOf(android.R.id.text1),
            childData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf("child"),
            intArrayOf(android.R.id.text1)
        )

        listView.setAdapter(adapter)

        listView.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
            val channel = groupedChannels[groupPosition].second[childPosition]

            if (miniPlayingUrl == channel.url) {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("stream_url", channel.url)
                startActivity(intent)
            } else {
                startMiniPreview(channel.url)
            }
            true
        }
    }

    private fun startMiniPreview(url: String) {
        miniPlayer?.release()

        val exoPlayer = ExoPlayer.Builder(this).build()
        miniPlayerView.player = exoPlayer
        miniPlayerView.visibility = android.view.View.VISIBLE

        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true

        miniPlayer = exoPlayer
        miniPlayingUrl = url
    }

    override fun onDestroy() {
        super.onDestroy()
        miniPlayer?.release()
        miniPlayer = null
    }
}

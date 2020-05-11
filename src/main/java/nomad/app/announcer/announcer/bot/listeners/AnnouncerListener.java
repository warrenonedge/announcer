package nomad.app.announcer.announcer.bot.listeners;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import nomad.app.announcer.announcer.bot.talk.AudioPlayerSendHandler;

@Slf4j
public class AnnouncerListener extends ListenerAdapter {
	private String googleTTS = "https://translate.google.com/translate_tts?ie=UTF-8&client=tw-ob&tl=en&q=";

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
		VoiceChannel botVChannel = getBotVoiceChannel(event.getJDA());

		if (botVChannel != null) {
			User user = event.getMember().getUser();
			if (user.isBot()) {
				return;
			}

			log.info("User joined: " + user.getName());
			talkInVoiceChannel(botVChannel, event.getGuild().getAudioManager(), user.getName());
		}
	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
		VoiceChannel botVChannel = getBotVoiceChannel(event.getJDA());

		if (botVChannel != null) {
			if (event.getChannelJoined().equals(botVChannel)) {
				User user = event.getMember().getUser();
				if (user.isBot()) {
					return;
				}

				log.info("User joined: " + user.getName());
				talkInVoiceChannel(botVChannel, event.getGuild().getAudioManager(), user.getName());
			}
		}

	}

	private VoiceChannel getBotVoiceChannel(JDA jda) {
		for (VoiceChannel voiceChannel : jda.getVoiceChannelCache()) {
			for (Member user : voiceChannel.getMembers()) {
				if (user.getId().equals(jda.getSelfUser().getId())) {
					return voiceChannel;
				}
			}
		}
		return null;
	}

	private void talkInVoiceChannel(VoiceChannel voiceChannel, AudioManager audioManager, String user) {

		AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(playerManager);
		AudioPlayer player = playerManager.createPlayer();
		AudioPlayerSendHandler handler = new AudioPlayerSendHandler(player);

		audioManager.setSendingHandler(handler);
		audioManager.openAudioConnection(voiceChannel);

		String toSay = user + " has entered the channel.".replaceAll(" ", "+");
		String url = googleTTS + toSay;
		log.info(url);
		playerManager.loadItem(url, new AudioLoadResultHandler() {

			@Override
			public void trackLoaded(AudioTrack track) {
				player.playTrack(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				// TODO Auto-generated method stub

			}

			@Override
			public void noMatches() {
				// TODO Auto-generated method stub

			}

			@Override
			public void loadFailed(FriendlyException exception) {
				// TODO Auto-generated method stub

			}
		});
		// handler.getQueue().add(output);
	}

}

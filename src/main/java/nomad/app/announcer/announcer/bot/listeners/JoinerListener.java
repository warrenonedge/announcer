package nomad.app.announcer.announcer.bot.listeners;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinerListener extends ListenerAdapter {
	// Only listening to guild messages.
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		// Good practise to ignore bots.
		if (event.getAuthor().isBot()) {
			return;
		}
		// Gets the raw message content and binds it to a local variable.
		String message = event.getMessage().getContentRaw().toLowerCase();
		// So we don't have to access event.getChannel() every time.
		TextChannel channel = event.getChannel();
		// Checks if the command is !join.
		if (message.equals(">join")) {
			// Checks if the bot has permissions.
			if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
				// The bot does not have permission to join any voice channel. Don't forget the
				// .queue()!
				channel.sendMessage("I do not have permissions to join a voice channel!").queue();
				return;
			}
			// Creates a variable equal to the channel that the user is in.
			VoiceChannel connectedChannel = event.getMember().getVoiceState().getChannel();
			// Checks if they are in a channel -- not being in a channel means that the
			// variable = null.
			if (connectedChannel == null) {
				// Don't forget to .queue()!
				channel.sendMessage("You are not connected to a voice channel!").queue();
				return;
			}
			// Gets the audio manager.
			AudioManager audioManager = event.getGuild().getAudioManager();
			// When somebody really needs to chill.
			if (audioManager.isAttemptingToConnect()) {
				channel.sendMessage("The bot is already trying to connect! Enter the chill zone!").queue();
				return;
			}
			// Connects to the channel.
			audioManager.openAudioConnection(connectedChannel);
			// Obviously people do not notice someone/something connecting.
			channel.sendMessage("Connected to the voice channel!").queue();
		} else if (message.equals(">leave")) { // Checks if the command is !leave.
			// Gets the channel in which the bot is currently connected.
			VoiceChannel connectedChannel = event.getGuild().getSelfMember().getVoiceState().getChannel();
			// Checks if the bot is connected to a voice channel.
			if (connectedChannel == null) {
				// Get slightly fed up at the user.
				channel.sendMessage("I am not connected to a voice channel!").queue();
				return;
			}
			// Disconnect from the channel.
			event.getGuild().getAudioManager().closeAudioConnection();
			// Notify the user.
			channel.sendMessage("Disconnected from the voice channel!").queue();
		} else if (message.equals(">help")) {
			channel.sendMessage("Type >join for me to join your voice channel.").queue();
			channel.sendMessage("Type >leave for me to leave your voice channel.").queue();
			return;
		}
	}
}
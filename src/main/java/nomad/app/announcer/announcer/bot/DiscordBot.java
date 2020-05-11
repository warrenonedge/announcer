package nomad.app.announcer.announcer.bot;

import javax.annotation.PreDestroy;
import javax.security.auth.login.LoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import nomad.app.announcer.announcer.bot.listeners.AnnouncerListener;
import nomad.app.announcer.announcer.bot.listeners.JoinerListener;

@Slf4j
@Service
public class DiscordBot {

	private JDA jda;

	@Autowired
	public DiscordBot(@Value("${secret}") String SECRET_TOKEN) {
		log.info("Secret: " + SECRET_TOKEN);
		try {
			jda = new JDABuilder(AccountType.BOT).setToken(SECRET_TOKEN).setStatus(OnlineStatus.DO_NOT_DISTURB)
					.setActivity(Activity.watching("For Activity"))
					.addEventListeners(new JoinerListener(), new AnnouncerListener()).build();
			log.info("Logged in as " + jda.getSelfUser().getName() + "#" + jda.getSelfUser().getDiscriminator() + "!");
		} catch (LoginException e) {
			e.printStackTrace();
		}

	}

	@PreDestroy
	public void destroy() {
		jda.shutdown();
	}
}

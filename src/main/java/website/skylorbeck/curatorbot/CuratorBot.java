package website.skylorbeck.curatorbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.EmoteManager;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import net.dv8tion.jda.internal.managers.EmoteManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CuratorBot extends ListenerAdapter {
    private static final Logger log = Logger.getLogger("CuratorBot");

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(args[0]);
        // Set activity (like "playing Something")
        builder.disableCache(CacheFlag.ACTIVITY);
        builder.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER));
        builder.setActivity(Activity.watching("everything"));
        builder.addEventListeners(new CuratorBot());
        builder.build();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        if (!event.getAuthor().isBot() && msg.startsWith("!")) {
            event.getMessage().delete().queue();

            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if (msg.contains("shutdown")) {
                    event.getJDA().shutdown();
                } else if (msg.contains("poll")) {
                    String[] choices = msg.substring(6).split(",");
                    StringBuilder message = new StringBuilder("Vote Below\n");
                    for (int i = 0; i < choices.length; i++) {
                        message.append(i + 1).append("\uFE0F\u20E3").append(" ").append(choices[i]).append("\n");
                    }
                    event.getChannel().sendMessage(message).queue(sentMessage -> {
                        for (int i = 0; i < choices.length; i++) {
                            sentMessage.addReaction((i + 1) + "\uFE0F\u20E3").queue();
                        }
                    });

                }
            }
            super.onMessageReceived(event);
        }
    }
}
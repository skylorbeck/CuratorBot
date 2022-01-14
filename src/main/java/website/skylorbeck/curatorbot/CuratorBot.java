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
import net.dv8tion.jda.api.utils.TimeUtil;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import net.dv8tion.jda.internal.managers.EmoteManagerImpl;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.dv8tion.jda.internal.requests.Route;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CuratorBot extends ListenerAdapter {
    private static final Logger log = Logger.getLogger("CuratorBot");
    private static final Random ran = new Random(System.currentTimeMillis());

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
        String msg = event.getMessage().getContentDisplay().toLowerCase(Locale.ROOT);
        if (!event.getAuthor().isBot()) {
            if (msg.startsWith("!")) {
                msg = msg.substring(1);
                event.getMessage().delete().queue();

                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

                    if (msg.startsWith("shutdown")) {
                        event.getJDA().shutdown();
                    } else if (msg.startsWith("poll")) {
                        String[] choices = msg.substring(4).split(",");
                        StringBuilder message = new StringBuilder("Vote by choosing a reaction!\n");
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
                StringBuilder message = new StringBuilder();
                if (msg.startsWith("random")) {
                    msg = msg.substring(6);
                        int max = 20;
                        try {
                            max = Integer.parseInt(msg);
                        } catch (Exception ignored) {
                            try {
                                max = Integer.parseInt(msg.substring(1));
                            }catch (Exception ignored2){}
                        }
                        message.append("Rolling a d").append(max).append(" for ").append(event.getMember().getEffectiveName()).append("\n");
                        int result = ran.nextInt(max) + 1;
                        String resultS = result + "";
                        String[] results = resultS.split("");
                        for (String s : results) {
                            message.append(s).append("\uFE0F\u20E3");
                        }
                    event.getChannel().sendMessage(message).queue();
                }
            }
        }
        super.onMessageReceived(event);
    }
}
package website.skylorbeck.curatorbot;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.EmoteManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
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
import java.util.ArrayList;
import java.util.List;
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
                MessageChannel channel = event.getChannel();
                msg = msg.substring(1);

                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    if (msg.startsWith("shutdown")) {
                        event.getJDA().shutdown();
                    } else if (msg.startsWith("poll")) {
                        String[] choices = msg.substring(4).split(",");
                        StringBuilder message = new StringBuilder("Vote by choosing a reaction!\n");
                        for (int i = 0; i < choices.length; i++) {
                            message.append(i + 1).append("\uFE0F\u20E3").append(" ").append(choices[i]).append("\n");
                        }
                        channel.sendMessage(message).queue(sentMessage -> {
                            for (int i = 0; i < choices.length; i++) {
                                sentMessage.addReaction((i + 1) + "\uFE0F\u20E3").queue();
                            }
                        });
                    } else if (msg.startsWith("rule ")) {
                        String[] rule = new String[]{
                                "Rule 1: Be Nice - This means no insulting others or their Artwork. Constructive Criticism is OK.",
                                "Rule 1a: Be Nice - No racial slurs, no derogatory remarks for any reason. This includes jokes.",
                                "Rule 2: NSFW only in NSFW channels. No porn. No Gore.",
                                "Rule 3: No Advertising or Spamming your stuff without permission directly from an Admin"
                        };
                        msg = msg.substring(5);
                        Message message = event.getMessage().getReferencedMessage();
                        if (msg.startsWith("1")) {
                            if (message != null) {
                                message.reply(rule[0]).queue();
                            } else {
                                channel.sendMessage(rule[0]).queue();
                            }
                        } else if (msg.startsWith("2")) {
                            if (message != null) {
                                message.reply(rule[1]).queue();
                            } else {
                                channel.sendMessage(rule[1]).queue();
                            }
                        } else if (msg.startsWith("3")) {
                            if (message != null) {
                                message.reply(rule[2]).queue();
                            } else {
                                channel.sendMessage(rule[2]).queue();
                            }
                        }
                    } else if(msg.startsWith("socials")) {
                        String[] socials = new String[]{
                                "CurseForge:https://www.curseforge.com/members/skylorbeck/projects",
                                "Patreon:https://www.patreon.com/SkylorBeck",
                                "YouTube:https://www.youtube.com/c/SkylorBeck",
                                "Twitter:https://twitter.com/BeckSkylor",
                                "GitHub:https://github.com/Skylortrexler",
                                "Personal Website:https://skylorbeck.website/"
                        };
                        StringBuilder message = new StringBuilder("My Social Links:\n");
                        for (String social : socials) {
                            message.append(social).append("\n");
                        }
                        channel.sendMessage(message).queue();
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
                        } catch (Exception ignored2) {
                        }
                    }
                    message.append("Rolling a d").append(max).append(" for ").append(event.getMember().getAsMention()).append("\n");
                    int result = ran.nextInt(max) + 1;
                    String resultS = result + "";
                    String[] results = resultS.split("");
                    for (String s : results) {
                        message.append(s).append("\uFE0F\u20E3");
                    }
                    channel.sendMessage(message).queue();
                }
                event.getMessage().delete().queue();
            }
        }
        super.onMessageReceived(event);
    }
}
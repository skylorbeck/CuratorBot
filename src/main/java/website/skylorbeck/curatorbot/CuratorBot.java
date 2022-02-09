package website.skylorbeck.curatorbot;

import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateRolesEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
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
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CuratorBot extends ListenerAdapter {
    private static final Logger log = Logger.getLogger("CuratorBot");
    private static final Random ran = new Random(System.currentTimeMillis());
    private String lastMessage;

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(args[0]);
        // Set activity (like "playing Something")
        builder.disableCache(CacheFlag.ACTIVITY);
        builder.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER));
        builder.setActivity(Activity.watching("everything"));
        builder.addEventListeners(new CuratorBot());
        builder.build();
    }

    enum Commands {
        Help("**!Help** Show this message"),
        Random("**!Random** Gets a random number between 1-20\n     **`(Amount)`** Get a random number with a max of **`(Amount)`**"),
        Damage("**!Damage `(Amount)`** Take damage.\n     **`(Type) (Amount)`** Take damage of type.\n     **`'type||types'`** Show all types."),
        Rules("**!Rules** Show the rules.\n     **`(Rule Number)`** optionally show a specific rule"),
        Socials("**!Socials** Show the socials"),
        ;

        private final String description;

        Commands(String s) {
            description = s;
        }

        public String getDescription() {
            return description;
        }
    }

    enum Socials {
        CurseForge("https://www.curseforge.com/members/skylorbeck/projects"),
        Patreon("https://www.patreon.com/SkylorBeck"),
        Twitter("https://twitter.com/BeckSkylor"),
        YouTube("https://www.youtube.com/c/SkylorBeck"),
        GitHub("https://github.com/Skylortrexler"),
        Website("https://skylorbeck.website/");

        private final String url;

        Socials(String s) {
            url = s;
        }

        public String getUrl() {
            return url;
        }
    }

    enum DamageTypes {
        Emotional,
        Physical,
        Spiritual,
        Mental,
        Social,
        Environmental,
        Electrical,
        Chemical,
        Kinetic,
        Radiation,
        Plasma,
        Nuclear,
        Quantum,
        Magic,
        Energy,
        Heat,
        Cold,
        Tachyon,
        Cosmic,
        Slashing,
        Piercing,
        Blunt,
        Acid,
        Corrosive,
        Fire,
        Ice,
        Lightning,
        Thunder,
        Wind,
        Earth,
        Water,
        Stone,
        Metal,
        Wood,
        Flesh,
        Nature,
        Skeleton,
        Celestial,
        Demonic,
        Necrotic
    }

    enum Rules {
        Rule1("`Rule 1:`\nNo racism, sexism, homophobia, or transphobia."),
        Rule2("`Rule 2:`\nNo hate speech, hate symbols, or hate music."),
        Rule3("`Rule 3:`\nNo Advertising, Spam, or Self-promotion without being approved by Skylor."),
        Rule4("`Rule 4:`\nNo porn, gore or death"),
        ;

        private final String rule;

        Rules(String s) {
            rule = s;
        }

        public String getRule() {
            return rule;
        }
    }
// R:Admin(836874169085460512),
// R:Patreon(914756962833539073),
// R:Curator(852401504156385301),
// R:Exalted(914626053769691136),
// R:Trusted Authority(916890181154508800),
// R:Internal Tester(853420931014852618),
// R:Noble Dog Knight(922273823095660595),
// R:Lizard Guard(914756479083495474),
// R:Frog Knight(914756341401255937),
// R:Server Booster(936898416020193331),
// R:Artist - Approved(936982957766361158),
// R:Artist(923362820555436042),
// R:Beta Tester(896981811270406214),
// R:Snowball Bot(920008550414180383),
// R:@everyone(836872336417357874)
    enum Roles{
        Artist("lower_left_paintbrush",923362820555436042L),
        BetaTester("WIP",896981811270406214L),
        ;

        private final long id;
        private final String emoji;

        Roles(String moji, long l) {
            emoji = moji;
            id = l;
        }

        public long getId() {
            return id;
        }

        public String getEmoji() {
            return emoji;
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        Channel channel = event.getChannel();
        if (channel.getIdLong() == 896980872656457788L) {
            String ID = event.getReactionEmote().isEmoji()? EmojiParser.parseToAliases(event.getReactionEmote().getEmoji()):event.getReactionEmote().getName();
            for (Roles role : Roles.values()) {
                if (ID.contains(role.getEmoji())) {
                    event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(role.getId())).queue();
                }
            }
        }
//        System.out.println(event.getGuild().getRoles());
        super.onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) return;
        Channel channel = event.getChannel();
        if (channel.getIdLong() == 896980872656457788L) {
            String ID = event.getReactionEmote().isEmoji()? EmojiParser.parseToAliases(event.getReactionEmote().getEmoji()):event.getReactionEmote().getName();
            for (Roles role : Roles.values()) {
                if (ID.contains(role.getEmoji())) {
                    event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(role.getId())).queue();
                }
            }
        }
        super.onMessageReactionRemove(event);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (lastMessage!=null && lastMessage.equals("")) {
            if (lastMessage.equals(event.getMessage().getContentDisplay())) {
                event.getMessage().delete().queue();
                event.getChannel().sendMessage("Please no spamming").queue();
            } else {
                lastMessage = event.getMessage().getContentDisplay();
            }
        } else {
            lastMessage = event.getMessage().getContentDisplay();
        }
        String msg = event.getMessage().getContentDisplay().toLowerCase(Locale.ROOT);
        if (msg.contains("discord.gg")){
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                return;
            }
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("Please no advertising other Discord Servers without permission").queue();
        }
        if (msg.startsWith("!")) {
            MessageChannel channel = event.getChannel();
            Message message = event.getMessage().getReferencedMessage();
            StringBuilder newMessage = new StringBuilder();

            msg = msg.substring(1);
            msg = msg.replaceAll("\\s", "");
            //admin only commands
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                if (msg.startsWith("shutdown")) {
                    event.getJDA().shutdown();
                } else if (msg.startsWith("poll")) {
                    String[] choices = msg.substring(4).split(",");
                    newMessage = new StringBuilder("Vote by choosing a reaction!\n");
                    for (int i = 0; i < choices.length; i++) {
                        newMessage.append(i + 1).append("\uFE0F\u20E3").append(" ").append(choices[i]).append("\n");
                    }
                    channel.sendMessage(newMessage).queue(sentMessage -> {
                        for (int i = 0; i < choices.length; i++) {
                            sentMessage.addReaction((i + 1) + "\uFE0F\u20E3").queue();
                        }
                    });
                }
            }
            //public commands
            if (msg.startsWith("random")) {
                msg = msg.substring(6);
                int max = 20;
                try {
                    max = Integer.parseInt(msg);
                } catch (Exception ignored) {
                }
                newMessage.append("Rolling a d").append(max).append(" for ").append(event.getMember().getAsMention()).append("\n");
                int result = ran.nextInt(max) + 1;
                String resultS = result + "";
                String[] results = resultS.split("");
                for (String s : results) {
                    newMessage.append(s).append("\uFE0F\u20E3");
                }
                channel.sendMessage(newMessage).queue();
            } else if (msg.startsWith("damage")) {
                int damage = 0;
                msg = msg.substring(6);
                try {
                    damage = Integer.parseInt(msg);
                    if (damage < 0) {
                        damage = -damage;
                        newMessage.append(event.getMember().getAsMention()).append(" was healed for ").append(damage).append(" health!");
                    } else {
                        newMessage.append(event.getMember().getAsMention()).append(" has taken ").append(damage).append(" damage!");
                    }
                    if (message != null && message.getMember() != null) {
                        message.reply(newMessage).queue();
                    } else {
                        channel.sendMessage(newMessage).queue();
                    }
                } catch (NumberFormatException ignored) {
                    if (msg.startsWith("types") || msg.startsWith("type")) {
                        newMessage.append("Damage Types:\n");
                        for (DamageTypes type : DamageTypes.values()) {
                            newMessage.append(type.toString()).append(", ");
                        }
                        channel.sendMessage(newMessage).queue();
                    } else for (DamageTypes damageType : DamageTypes.values()) {
                        String arg = damageType.name().toLowerCase(Locale.ROOT);
                        try {
                            if (msg.startsWith(arg)) {
                                msg = msg.substring(arg.length());
                                try {
                                    damage = Integer.parseInt(msg);
                                    if (damage < 0) {
                                        damage = -damage;
                                        newMessage.append(event.getMember().getAsMention()).append(" was healed for ").append(damage).append(" ").append(arg).append(" health!");
                                    } else {
                                        newMessage.append(event.getMember().getAsMention()).append(" has taken ").append(damage).append(" ").append(arg).append(" damage!");
                                    }
                                    if (message != null && message.getMember() != null) {
                                        message.reply(newMessage).queue();
                                    } else {
                                        channel.sendMessage(newMessage).queue();
                                    }
                                } catch (NumberFormatException ignored2) {
                                }
                            }
                        } catch (ArrayIndexOutOfBoundsException ignored3) {
                        }
                    }
                }
            } else if (msg.startsWith("rule")) {
                if (msg.startsWith("rules")) {
                    newMessage.append("Rules:\n");
                    for (Rules rule : Rules.values()) {
                        newMessage.append(rule.getRule()).append("\n");
                    }
                    channel.sendMessage(newMessage).queue();
                } else
                    for (Rules rule : Rules.values()) {
                        String ruleString = rule.getRule();
                        if (msg.startsWith(rule.toString().toLowerCase(Locale.ROOT))) {
                            newMessage.append(ruleString);
                            if (message != null) {
                                message.reply(newMessage.toString()).queue();
                            } else {
                                channel.sendMessage(newMessage.toString()).queue();
                            }
                        }
                    }
            } else if (msg.startsWith("socials") || msg.startsWith("social")) {
                newMessage = new StringBuilder("My Social Links:\n");
                ArrayList<MessageEmbed> embeds = new ArrayList<>();
                for (Socials social : Socials.values()) {
                    MessageEmbed embed = new EmbedBuilder().addField(new MessageEmbed.Field(social.name(), social.getUrl(), false)).build();
                    embeds.add(embed);
                }
                MessageAction action = channel.sendMessage(newMessage.toString());
                action.setEmbeds(embeds).queue();
            } else if (msg.startsWith("help")) {
                newMessage.append("Commands:\n");
                for (Commands command : Commands.values()) {
                    newMessage.append(command.getDescription()).append("\n");
                }
                channel.sendMessage(newMessage.toString()).queue();
            }
            event.getMessage().delete().queue();
        }
        super.onMessageReceived(event);
    }
}
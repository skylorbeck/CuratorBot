package website.skylorbeck.curatorbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public class CuratorBot extends ListenerAdapter {
    private static final Logger log = Logger.getLogger("CuratorBot");
    private static Random ran;

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(args[0]);
        // Set activity (like "playing Something")
        builder.disableCache(CacheFlag.ACTIVITY);
        builder.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER));
        builder.setActivity(Activity.watching("everything"));
        builder.addEventListeners(new CuratorBot());

        JDA jda = builder.build();

        CommandDataImpl damage = new CommandDataImpl("damage", "Read something horrible? Take some damage");
        damage.addOption(OptionType.INTEGER, "amount", "How much damage to take", true);
        damage.addOption(OptionType.STRING, "type", "What type of damage to take", false);

        jda.upsertCommand(damage).queue();

        CommandDataImpl randomNumber = new CommandDataImpl("random", "Get a random number");
        randomNumber.addOption(OptionType.INTEGER, "min", "The minimum number", true);
        randomNumber.addOption(OptionType.INTEGER, "max", "The maximum number", true);

        jda.upsertCommand(randomNumber).queue();

        CommandDataImpl roll = new CommandDataImpl("roll", "Roll some dice");
        roll.addOption(OptionType.INTEGER, "amount", "How many dice to roll", true);
        roll.addOption(OptionType.INTEGER, "sides", "How many sides the dice have", true);

        jda.upsertCommand(roll).queue();

        CommandDataImpl coinCount = new CommandDataImpl("coins", "Open your coin purse and count your coins");
        jda.upsertCommand(coinCount).queue();

        CommandDataImpl silverCount = new CommandDataImpl("silvers", "How many Silver Crowns do you have?");
        jda.upsertCommand(silverCount).queue();

        CommandDataImpl goldCount = new CommandDataImpl("golds", "How many Gold Crowns do you have?");
        jda.upsertCommand(goldCount).queue();

        ran = new Random(System.currentTimeMillis());
//        log.info("Initalized Random");
        Paths.get("users/").toFile().mkdirs();
//        log.info("Created users folder");
        log.info("CuratorBot is ready!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("damage")) {
            int amount = event.getOption("amount").getAsInt();
            OptionMapping type = event.getOption("type");
            if (type != null) {
                event.reply(event.getUser().getAsMention()+" took " + amount + " " + type.getAsString() + " damage!").queue();
            } else {
                event.reply(event.getUser().getAsMention()+" took " + amount + " damage!").queue();
            }
        } else if (event.getName().equals("random")) {
            int min = event.getOption("min").getAsInt();
            int max = event.getOption("max").getAsInt();
            event.reply("Your random number is " + ran.nextInt(max - min + 1) + min + "!").queue();
        } else if (event.getName().equals("roll")) {
            int amount = event.getOption("amount").getAsInt();
            int sides = event.getOption("sides").getAsInt();
            int total = 0;
            for (int i = 0; i < amount; i++) {
                total += ran.nextInt(sides) + 1;
            }
            event.reply(event.getUser().getAsMention()+" rolled " + amount + "d" + sides + " and got " + total + "!").queue();
        } else if (event.getName().equals("coins")) {
            event.reply("You have "+ checkEmote(event.getUser().getId(), "coin")+" coins").setEphemeral(true).queue();
        } else if (event.getName().equals("silvers")) {
            event.reply("You have "+ checkEmote(event.getUser().getId(), "silver")+" silver crowns").setEphemeral(true).queue();
        } else if (event.getName().equals("golds")) {
            event.reply("You have "+ checkEmote(event.getUser().getId(), "gold")+" gold crowns").setEphemeral(true).queue();
        }
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        Channel channel = event.getChannel();
        if (channel.getIdLong() == 896980872656457788L) {
            switch (event.getEmoji().getType()) {
                case UNICODE:
                    UnicodeEmoji emoji = event.getEmoji().asUnicode();
//                    log.info("Unicode Emoji as Codepoints: " + emoji.getAsCodepoints());
//                    log.info("Unicode Emoji Formatted: " + emoji.getFormatted());
//                    log.info("Unicode Emoji Name: " + emoji.getName());
                    if (emoji.getAsCodepoints().equals("U+1f58cU+fe0f")) {
                        event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(923362820555436042L))).queue();
                    }
                    break;
                case CUSTOM:
                    CustomEmoji customEmoji = event.getEmoji().asCustom();
//                    log.info("Custom Emoji: " + customEmoji.getName());
                    if (customEmoji.getName().equals("WIP")) {
                        event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(896981811270406214L))).queue();
                    }
                    break;
            }


        }

        if (event.isFromThread()) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            String authorID = message.getAuthor().getId();
            if (!event.getUserId().equals(authorID)) {
                switch (event.getEmoji().getType()) {
                    case UNICODE:
                        break;
                    case CUSTOM:
                        CustomEmoji customEmoji = event.getEmoji().asCustom();
//                log.info("Custom Emoji: " + customEmoji.getName());
                        if (customEmoji.getName().equals("bigcoin")) {
                            AddEmote(authorID, "coin");
                        } else if (customEmoji.getName().equals("silver")) {
                            AddEmote(authorID, "silver");
                        } else if (customEmoji.getName().equals("gold")) {
                            AddEmote(authorID, "gold");
                        }
                        break;
                }
            }
        }
        super.onMessageReactionAdd(event);
    }

    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {

        if (event.getUser().isBot()) return;
        Channel channel = event.getChannel();
        if (channel.getIdLong() == 896980872656457788L) {
            switch (event.getEmoji().getType()) {
                case UNICODE:
                    UnicodeEmoji emoji = event.getEmoji().asUnicode();
                    if (emoji.getAsCodepoints().equals("U+1f58cU+fe0f")) {
                        event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(923362820555436042L))).queue();
                    }
                    break;
                case CUSTOM:
                    CustomEmoji customEmoji = event.getEmoji().asCustom();
//                    log.info("Custom Emoji: " + customEmoji.getName());
                    if (customEmoji.getName().equals("WIP")) {
                        event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(896981811270406214L))).queue();
                    }
                    break;
            }
        }
        if (event.isFromThread()) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            String authorID = message.getAuthor().getId();
            switch (event.getEmoji().getType()) {
                case UNICODE:
                    break;
                case CUSTOM:
                    CustomEmoji customEmoji = event.getEmoji().asCustom();
                    if (customEmoji.getName().equals("bigcoin")) {
                        RemoveEmote(authorID, "coin");
                    } else if (customEmoji.getName().equals("silver")) {
                        RemoveEmote(authorID, "silver");
                    } else if (customEmoji.getName().equals("gold")) {
                        RemoveEmote(authorID, "gold");
                    }
                    break;
            }
        }
    }
    public int AddEmote(String authorID, String emoteName) {
        File userFile = CheckForUserFile(authorID);
        //read the file
        try {
            FileInputStream fis = new FileInputStream(userFile);
            byte[] data = new byte[(int) userFile.length()];
            fis.read(data);
            fis.close();
            String json = new String(data, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            int coins = jsonObject.get(emoteName).getAsInt();
            coins++;
            jsonObject.addProperty(emoteName, coins);
            FileOutputStream fos = new FileOutputStream(userFile);
            fos.write(gson.toJson(jsonObject).getBytes());
            fos.close();
            return coins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int RemoveEmote(String authorID, String emoteName){

        File userFile = CheckForUserFile(authorID);;
        //read the file
        try {
            FileInputStream fis = new FileInputStream(userFile);
            byte[] data = new byte[(int) userFile.length()];
            fis.read(data);
            fis.close();
            String json = new String(data, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            int coins = jsonObject.get(emoteName).getAsInt();
            coins--;
            if (coins < 0) {
                coins = 0;
            }
            jsonObject.addProperty(emoteName, coins);
            FileOutputStream fos = new FileOutputStream(userFile);
            fos.write(gson.toJson(jsonObject).getBytes());
            fos.close();
            return coins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int checkEmote(String authorID, String emoteName){
        File userFile = CheckForUserFile(authorID);
        //read the file
        try {
            FileInputStream fis = new FileInputStream(userFile);
            byte[] data = new byte[(int) userFile.length()];
            fis.read(data);
            fis.close();
            String json = new String(data, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            int coins = jsonObject.get(emoteName).getAsInt();
            return coins;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private File CheckForUserFile(String authorID) {
        File userFile = new File("users/"+ authorID +".json");
        if (!userFile.exists()){
            //create the file
            try {
                userFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(userFile);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("coin", 0);
                jsonObject.addProperty("silver", 0);
                jsonObject.addProperty("gold", 0);
                Gson gson = new Gson();
                fos.write(gson.toJson(jsonObject).getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userFile;
    }

}
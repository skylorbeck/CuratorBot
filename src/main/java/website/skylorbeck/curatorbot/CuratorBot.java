package website.skylorbeck.curatorbot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.UnicodeEmoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;


//blue 1023103829916516443
//purple 1023104468289605662
//green 1023104859844653067

public class CuratorBot extends ListenerAdapter {
    private static final Logger log = Logger.getLogger("CuratorBot");
    private static Random ran;

    public static final long SKYBLUE = 1023103829916516443L;
    public static final long ELECTRICPURPLE = 1023104468289605662L;
    public static final long BRIGHTGREEN = 1023104859844653067L;

    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault(args[0]);
        builder.disableCache(CacheFlag.ACTIVITY);
        builder.setMemberCachePolicy(MemberCachePolicy.ONLINE.or(MemberCachePolicy.OWNER));
        builder.setActivity(Activity.watching("you"));
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

        CommandDataImpl poll = new CommandDataImpl("poll", "Start a poll");
        poll.addOption(OptionType.STRING, "question", "The question to ask", true);
        poll.addOption(OptionType.STRING, "options", "The options to choose from. Split options with ','", true);
        poll.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
        jda.upsertCommand(poll).queue();

        CommandDataImpl patch = new CommandDataImpl("patch", "Publish a patch note");
        patch.addOption(OptionType.STRING, "title", "The name of the thing receiving the patch", true);
        patch.addOption(OptionType.STRING, "version", "The version of the patch", true);
        patch.addOption(OptionType.STRING, "changes", "The changes in the patch. Split changes with ','", true);
        patch.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
        jda.upsertCommand(patch).queue();

        CommandDataImpl coinCount = new CommandDataImpl("coins", "Open your coin purse and count your coins");
        jda.upsertCommand(coinCount).queue();

        CommandDataImpl silverCount = new CommandDataImpl("silvers", "How many Silver Crowns do you have?");
        jda.upsertCommand(silverCount).queue();

        CommandDataImpl goldCount = new CommandDataImpl("golds", "How many Gold Crowns do you have?");
        jda.upsertCommand(goldCount).queue();

        CommandDataImpl donateCoins = new CommandDataImpl("donate", "Donate some coins to your favorite team");
        donateCoins.addOption(OptionType.ROLE, "team", "The team to Donate to", true);
        donateCoins.addOption(OptionType.INTEGER, "amount", "How many coins to donate", true);
        jda.upsertCommand(donateCoins).queue();

        CommandDataImpl addCoins = new CommandDataImpl("addcoins", "Add some coins to a user");
        addCoins.addOption(OptionType.USER, "user", "The user to add coins to", true);
        addCoins.addOption(OptionType.INTEGER, "amount", "How many coins to add", true);
        addCoins.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
        jda.upsertCommand(addCoins).queue();
        
        CommandDataImpl removecoins = new CommandDataImpl("removecoins", "Add some coins to a user");
        removecoins.addOption(OptionType.USER, "user", "The user to add coins to", true);
        removecoins.addOption(OptionType.INTEGER, "amount", "How many coins to add", true);
        removecoins.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
        jda.upsertCommand(removecoins).queue();

        CommandDataImpl leaderboard = new CommandDataImpl("leaderboard", "See who has the most coins");
        jda.upsertCommand(leaderboard).queue();

        CommandDataImpl teamleaderboard = new CommandDataImpl("teamleaderboard", "See which team has the most coins");
        jda.upsertCommand(teamleaderboard).queue();

        CommandDataImpl quit = new CommandDataImpl("quit", "Shut down the bot");
        quit.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));
        jda.upsertCommand(quit).queue();



        ran = new Random(System.currentTimeMillis());
//        log.info("Initalized Random");
        Paths.get("users/").toFile().mkdirs();
//        log.info("Created users folder");
        log.info("CuratorBot is ready!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        if (command.equals("damage")) {
            int amount = event.getOption("amount").getAsInt();
            OptionMapping type = event.getOption("type");
            if (type != null) {
                event.reply(event.getUser().getAsMention() + " took " + amount + " " + type.getAsString() + " damage!").queue();
            } else {
                event.reply(event.getUser().getAsMention() + " took " + amount + " damage!").queue();
            }
        } else if (command.equals("random")) {
            int min = event.getOption("min").getAsInt();
            int max = event.getOption("max").getAsInt();
            event.reply("Your random number is " + ran.nextInt(max - min + 1) + min + "!").queue();
        } else if (command.equals("roll")) {
            int amount = event.getOption("amount").getAsInt();
            int sides = event.getOption("sides").getAsInt();
            int total = 0;
            for (int i = 0; i < amount; i++) {
                total += ran.nextInt(sides) + 1;
            }
            event.reply(event.getUser().getAsMention() + " rolled " + amount + "d" + sides + " and got " + total + "!").queue();
        } else if (command.equals("donate")) {
            int amount = event.getOption("amount").getAsInt();
            Role role = event.getOption("team").getAsRole();
            String team = role.getName();
            if (role.getId().equals("1023103829916516443") || role.getId().equals("1023104468289605662") || role.getId().equals("1023104859844653067")) {
                if (checkEmote(event.getUser().getId(), "coin") >= amount) {
                    RemoveEmote(event.getUser().getId(), "coin", amount);
                    AddEmote(role.getId(), "coin", amount);
                    ReplyCallbackAction reply = event.reply(event.getUser().getAsMention() + " donated " + amount + " coins to " + role.getName().toUpperCase(Locale.ROOT) + "! ");
                    if (role.getIdLong()==SKYBLUE&&!event.getGuild().getMembersWithRoles(role).contains(event.getMember())) {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        if (event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(BRIGHTGREEN)).contains(event.getMember())) {
                            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(BRIGHTGREEN)).queue();
                            reply.addContent(" "+event.getUser().getName().toUpperCase(Locale.ROOT) + " DEFECTED FROM " + event.getGuild().getRoleById(BRIGHTGREEN).getName().toUpperCase(Locale.ROOT)).queue();
                        } else if (event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(ELECTRICPURPLE)).contains(event.getMember())) {
                            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(ELECTRICPURPLE)).queue();
                            reply.addContent(" "+event.getUser().getName().toUpperCase(Locale.ROOT) + " DEFECTED FROM " + event.getGuild().getRoleById(ELECTRICPURPLE).getName().toUpperCase(Locale.ROOT)).queue();
                        }
                    } else if (role.getIdLong()==BRIGHTGREEN&&!event.getGuild().getMembersWithRoles(role).contains(event.getMember())) {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        if (event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(SKYBLUE)).contains(event.getMember())) {
                            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(SKYBLUE)).queue();
                            reply.addContent(" "+event.getUser().getName().toUpperCase(Locale.ROOT) + " DEFECTED FROM " + event.getGuild().getRoleById(SKYBLUE).getName().toUpperCase(Locale.ROOT)).queue();
                        } else if (event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(ELECTRICPURPLE)).contains(event.getMember())) {
                            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(ELECTRICPURPLE)).queue();
                            reply.addContent(" "+event.getUser().getName().toUpperCase(Locale.ROOT) + " DEFECTED FROM " + event.getGuild().getRoleById(ELECTRICPURPLE).getName().toUpperCase(Locale.ROOT)).queue();
                        }
                    } else if (role.getIdLong()==ELECTRICPURPLE&&!event.getGuild().getMembersWithRoles(role).contains(event.getMember())) {
                        event.getGuild().addRoleToMember(event.getMember(), role).queue();
                        if (event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(SKYBLUE)).contains(event.getMember())) {
                            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(SKYBLUE)).queue();
                            reply.addContent(" "+event.getUser().getName().toUpperCase(Locale.ROOT) + " DEFECTED FROM " + event.getGuild().getRoleById(SKYBLUE).getName().toUpperCase(Locale.ROOT)).queue();
                        } else if (event.getGuild().getMembersWithRoles(event.getGuild().getRoleById(BRIGHTGREEN)).contains(event.getMember())) {
                            event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(BRIGHTGREEN)).queue();
                            reply.addContent(" "+event.getUser().getName().toUpperCase(Locale.ROOT) + " DEFECTED FROM " + event.getGuild().getRoleById(BRIGHTGREEN).getName().toUpperCase(Locale.ROOT)).queue();
                        }
                    }
                    reply.queue();


                    File userFile = CheckForUserFile(event.getUser().getId());
                    try {
                        FileInputStream fis = new FileInputStream(userFile);
                        byte[] data = new byte[(int) userFile.length()];
                        fis.read(data);
                        fis.close();
                        String json = new String(data, StandardCharsets.UTF_8);
                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                        int coins = jsonObject.get("donated").getAsInt();
                        coins += amount;
                        jsonObject.addProperty("donated", coins);
                        FileOutputStream fos = new FileOutputStream(userFile);
                        fos.write(gson.toJson(jsonObject).getBytes());
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    event.reply(event.getUser().getAsMention() + " you don't have enough coins!").setEphemeral(true).queue();
                }

            } else {
                event.reply("You can only donate to " + event.getJDA().getRoleById("1023103829916516443").getAsMention() + ", " + event.getJDA().getRoleById("1023104468289605662").getAsMention() + ", or " + event.getJDA().getRoleById("1023104859844653067").getAsMention()).setEphemeral(true).queue();
            }
        } else if (command.equals("coins")) {
            event.reply("You have " + checkEmote(event.getUser().getId(), "coin") + " coins").setEphemeral(true).queue();
        } else if (command.equals("silvers")) {
            event.reply("You have " + checkEmote(event.getUser().getId(), "silver") + " silver crowns").setEphemeral(true).queue();
        } else if (command.equals("golds")) {
            event.reply("You have " + checkEmote(event.getUser().getId(), "gold") + " gold crowns").setEphemeral(true).queue();
        } else if (command.equals("addcoins")) {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                int amount = event.getOption("amount").getAsInt();
                User user = event.getOption("user").getAsUser();
                AddEmote(user.getId(), "coin", amount);
                event.reply("Added " + amount + " coins to " + user.getAsMention()).queue();
            } else {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            }
        } else if (command.equals("removecoins")) {
            if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                int amount = event.getOption("amount").getAsInt();
                User user = event.getOption("user").getAsUser();
                RemoveEmote(user.getId(), "coin", amount);
                event.reply("Removed " + amount + " coins from " + user.getAsMention()).queue();
            } else {
                event.reply("You do not have permission to use this command!").setEphemeral(true).queue();
            }
        } else if (command.equals("leaderboard")) {
            File[] files = Paths.get("users/").toFile().listFiles();
            HashMap<String, Integer> coins = new HashMap<>();
            for (File file : files) {
                if (file.getName().equals("1023103829916516443.json")||file.getName().equals("1023104468289605662.json")||file.getName().equals("1023104859844653067.json")) {
                    continue;
                }
                try {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fis.read(data);
                    fis.close();
                    String json = new String(data, StandardCharsets.UTF_8);
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                    coins.put(file.getName().replace(".json", ""), jsonObject.get("coin").getAsInt());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            coins = sortByValue(coins);
            StringBuilder leaderboard = new StringBuilder();
            int i = 1;
            for (String user : coins.keySet()) {
                User u = event.getJDA().getUserById(user);
                if (u == null) {
                    u = event.getJDA().retrieveUserById(user).complete();
                }
                leaderboard.append(i).append(". ").append(u.getAsMention()).append(" - ").append(coins.get(user)).append(" coins\n");
                i++;
                if (i == 11) {
                    break;
                }
            }
            event.reply(leaderboard.toString()).setEphemeral(true).queue();
        } else if (command.equals("teamleaderboard")) {
            File[] files = Paths.get("users/").toFile().listFiles();
            HashMap<String, Integer> coins = new HashMap<>();
            for (File file : files) {
                if (!file.getName().equals("1023103829916516443.json") && !file.getName().equals("1023104468289605662.json") && !file.getName().equals("1023104859844653067.json")) {
                    continue;
                }
                try {
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fis.read(data);
                    fis.close();
                    String json = new String(data, StandardCharsets.UTF_8);
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                    coins.put(file.getName().replace(".json", ""), jsonObject.get("coin").getAsInt());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            coins = sortByValue(coins);
            StringBuilder leaderboard = new StringBuilder();
            int i = 1;
            for (String user : coins.keySet()) {
                leaderboard.append(i).append(". ").append(event.getJDA().getRoleById(user).getAsMention()).append(" - ").append(coins.get(user)).append(" coins\n");
                i++;

            }
            event.reply(leaderboard.toString()).setEphemeral(true).queue();
        } else if (command.equals("poll")) {
            String question = event.getOption("question").getAsString();
            String[] options = event.getOption("options").getAsString().split(",");
            StringBuilder processedOptions = new StringBuilder();
            for (int i = 0; i < options.length; i++) {
                String option = options[i];
                option = option.trim();
                options[i] = option;

                processedOptions
                        //append emoji for i
                        .append(":regional_indicator_").append((char) (97 + i)).append(": ")
                        .append(option).append("\n");
            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(question)
                    .setDescription("React to vote!")
                    .setColor(new Color((int) (Math.random() * 0x1000000)))
                    .addField("Options", processedOptions.toString(), false)
                    .setFooter("Poll created by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());
            event.replyEmbeds(embed.build()).queue(
                    message -> {
                        message.retrieveOriginal().queue(
                                original -> {
                                    for (int i = 0; i < options.length; i++) {
                                        StringBuilder emoji = new StringBuilder();
                                        emoji.appendCodePoint(0x1F1E6 + i);
                                        original.addReaction(Emoji.fromUnicode(emoji.toString())).queue();
                                    }
                                },
                                failure -> {
                                    event.reply( failure.getMessage()).setEphemeral(true).queue();
                                }
                        );
                    },
                    failure -> {
                        event.reply( failure.getMessage()).setEphemeral(true).queue();
                    }
            );
        } else if (command.equals("patch")) {
            String title = event.getOption("title").getAsString();
            String version = "v" + event.getOption("version").getAsString();
            String[] changes = event.getOption("changes").getAsString().split(",");
            for (int i = 0; i < changes.length; i++) {
                String change = changes[i];
                change = change.trim();
                changes[i] = change;

            }

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(title)
                    .setDescription(version)
                    .setColor(new Color((int) (Math.random() * 0x1000000)))
                    .addField("ChangeLog:", String.join("\n", changes), false);
            event.replyEmbeds(embed.build()).queue();
        }

        else if (command.equals("quit")){
            event.getJDA().shutdown();
        }
    }

        @Override
        public void onMessageReactionAdd (@NotNull MessageReactionAddEvent event){
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
                        String name = customEmoji.getName();
                        if (name.equals("WIP")) {
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(896981811270406214L))).queue();
                        } else if (name.equals("sky_blue")){
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SKYBLUE))).queue();
                            event.retrieveMessage().complete().removeReaction(event.getJDA().getEmojisByName("electric_purple",true).get(0), event.getUser()).queue();
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(ELECTRICPURPLE))).queue();
                            event.retrieveMessage().complete().removeReaction(event.getJDA().getEmojisByName("bright_green",true).get(0), event.getUser()).queue();
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(BRIGHTGREEN))).queue();
                        } else if (name.equals("electric_purple")){
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(ELECTRICPURPLE))).queue();
                            event.retrieveMessage().complete().removeReaction(event.getJDA().getEmojisByName("sky_blue",true).get(0), event.getUser()).queue();
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SKYBLUE))).queue();
                            event.retrieveMessage().complete().removeReaction(event.getJDA().getEmojisByName("bright_green",true).get(0), event.getUser()).queue();
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(BRIGHTGREEN))).queue();
                        } else if (name.equals("bright_green")){
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(BRIGHTGREEN))).queue();
                            event.retrieveMessage().complete().removeReaction(event.getJDA().getEmojisByName("electric_purple",true).get(0), event.getUser()).queue();
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(ELECTRICPURPLE))).queue();
                            event.retrieveMessage().complete().removeReaction(event.getJDA().getEmojisByName("sky_blue",true).get(0), event.getUser()).queue();
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SKYBLUE))).queue();
                        }
                        break;
                }


            }

//        if (event.isFromThread()) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            String authorID = message.getAuthor().getId();
            if (!event.getUserId().equals(authorID)) {
                switch (event.getEmoji().getType()) {
                    case UNICODE:
                        break;
                    case CUSTOM:
                        CustomEmoji customEmoji = event.getEmoji().asCustom();
                        String name = customEmoji.getName();
                        if (name.equals("bigcoin")) {
                            AddEmote(authorID, "coin", 1);
                        } else if (name.equals("silver")) {
                            AddEmote(authorID, "silver", 1);
                        } else if (name.equals("gold")) {
                            AddEmote(authorID, "gold", 1);
                        }
                        break;
                }
            }
//        }
            super.onMessageReactionAdd(event);
        }

        @Override
        public void onMessageReactionRemove (@NotNull MessageReactionRemoveEvent event){

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
                      String name = customEmoji.getName();
                        if (name.equals("WIP")) {
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(896981811270406214L))).queue();
                        }/* else if (name.equals("sky_blue")){
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(SKYBLUE))).queue();
                        } else if (name.equals("electric_purple")){
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(ELECTRICPURPLE))).queue();
                        } else if (name.equals("bright_green")){
                            event.getGuild().removeRoleFromMember(Objects.requireNonNull(event.getMember()), Objects.requireNonNull(event.getGuild().getRoleById(BRIGHTGREEN))).queue();
                        }*/
                        break;
                }
            }
//        if (event.isFromThread()) {
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            String authorID = message.getAuthor().getId();
            switch (event.getEmoji().getType()) {
                case UNICODE:
                    break;
                case CUSTOM:
                    CustomEmoji customEmoji = event.getEmoji().asCustom();
                    String name = customEmoji.getName();
                    if (name.equals("bigcoin")) {
                        RemoveEmote(authorID, "coin", 1);
                    } else if (name.equals("silver")) {
                        RemoveEmote(authorID, "silver", 1);
                    } else if (name.equals("gold")) {
                        RemoveEmote(authorID, "gold", 1);
                    }
                    break;
            }
//        }
        }
        public int AddEmote (String authorID, String emoteName,int amount){
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
                coins += amount;
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

        public int RemoveEmote (String authorID, String emoteName,int amount){

            File userFile = CheckForUserFile(authorID);
            ;
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
                coins -= amount;
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

        public int checkEmote (String authorID, String emoteName){
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

        private File CheckForUserFile (String authorID){
            File userFile = new File("users/" + authorID + ".json");
            if (!userFile.exists()) {
                //create the file
                try {
                    userFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(userFile);
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("coin", 0);
                    jsonObject.addProperty("silver", 0);
                    jsonObject.addProperty("gold", 0);
                    jsonObject.addProperty("donated", 0);
                    Gson gson = new Gson();
                    fos.write(gson.toJson(jsonObject).getBytes());
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return userFile;
        }


        private HashMap<String, Integer> sortByValue (HashMap < String, Integer > coins){

            List<Map.Entry<String, Integer>> list = new LinkedList<>(coins.entrySet());
            list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
            HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : list) {
                sortedMap.put(entry.getKey(), entry.getValue());
            }
            return sortedMap;
        }
    }
package lol.athena.events;

import lol.athena.Athena;
import lol.athena.plugin.events.Cancellable;
import lol.athena.plugin.events.PluginEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.automod.AutoModExecutionEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleCreateEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleDeleteEvent;
import net.dv8tion.jda.api.events.automod.AutoModRuleUpdateEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagAddEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagRemoveEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateEmojiEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateModeratedEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateNameEvent;
import net.dv8tion.jda.api.events.emoji.update.EmojiUpdateRolesEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementCreateEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementDeleteEvent;
import net.dv8tion.jda.api.events.entitlement.EntitlementUpdateEvent;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.*;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventCreateEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventDeleteEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserAddEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.ScheduledEventUserRemoveEvent;
import net.dv8tion.jda.api.events.guild.scheduledevent.update.*;
import net.dv8tion.jda.api.events.guild.update.*;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteAddEvent;
import net.dv8tion.jda.api.events.message.poll.MessagePollVoteRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEmojiEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.*;
import net.dv8tion.jda.api.events.self.*;
import net.dv8tion.jda.api.events.session.*;
import net.dv8tion.jda.api.events.stage.StageInstanceCreateEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdateTopicEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerAddedEvent;
import net.dv8tion.jda.api.events.sticker.GuildStickerRemovedEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateAvailableEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateDescriptionEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateNameEvent;
import net.dv8tion.jda.api.events.sticker.update.GuildStickerUpdateTagsEvent;
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent;
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class NewAthenaEventListener extends ListenerAdapter {

    private Athena bot;

    private void invokeEvent(Class<? extends Event> eventClass, Event event) {
        if (event.getClass().getSimpleName().startsWith("Generic") && event.getClass().getName().startsWith("net.dv8tion.jda.api.events")) {
            return;
        }
        if (bot.getPluginManager().getRegisteredEvents() == null) {
            return;
        }

        List<EventRegistration> registrations = bot.getPluginManager().getRegisteredEvents().get(eventClass);
        Guild guild = null;

        try {
            Method guildMethod = eventClass.getMethod("getGuild");
            if (guildMethod != null) {
                guild = (Guild) guildMethod.invoke(event);
            }
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException ex) {
            // This is not a guild event.
        } catch (ClassCastException ex) {
            // Unknown method return type... Probably not a guild event..?
        }

        if (registrations != null) {
            for (EventRegistration registration : registrations) {
                if (!registration.getPlugin().isEnabled()) {
                    continue;
                }
                if (guild != null && !registration.getPlugin().isActive(guild.getId())) {
                    continue;
                }
                if (event instanceof Cancellable) {
                    if (((Cancellable) event).isCancelled() && bot.isSkipCancelledEventInvocations()) {
                        continue;
                    }
                }
                registration.call(event);
            }
        } else {
            bot.getPluginManager().getRegisteredEvents().put(eventClass, new ArrayList<>());
        }
    }

    public void callPluginEvent(PluginEvent pluginEvent) {
        invokeEvent(pluginEvent.getClass(), pluginEvent);
    }

    public void callEvent(Event event) {
        invokeEvent(event.getClass(), event);
    }


    public void onRawGateway(@Nonnull RawGatewayEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGatewayPing(@Nonnull GatewayPingEvent event) {
    }

    public void onReady(@Nonnull ReadyEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSessionInvalidate(@Nonnull SessionInvalidateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSessionDisconnect(@Nonnull SessionDisconnectEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSessionResume(@Nonnull SessionResumeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSessionRecreate(@Nonnull SessionRecreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onShutdown(@Nonnull ShutdownEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onStatusChange(@Nonnull StatusChangeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onException(@Nonnull ExceptionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserContextInteraction(@Nonnull UserContextInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageContextInteraction(@Nonnull MessageContextInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onCommandAutoCompleteInteraction(@Nonnull CommandAutoCompleteInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEntitySelectInteraction(@Nonnull EntitySelectInteractionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateName(@Nonnull UserUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateGlobalName(@Nonnull UserUpdateGlobalNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateDiscriminator(@Nonnull UserUpdateDiscriminatorEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateAvatar(@Nonnull UserUpdateAvatarEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateOnlineStatus(@Nonnull UserUpdateOnlineStatusEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateActivityOrder(@Nonnull UserUpdateActivityOrderEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateFlags(@Nonnull UserUpdateFlagsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserTyping(@Nonnull UserTypingEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUserUpdateActivities(@Nonnull UserUpdateActivitiesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSelfUpdateAvatar(@Nonnull SelfUpdateAvatarEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSelfUpdateMFA(@Nonnull SelfUpdateMFAEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSelfUpdateName(@Nonnull SelfUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSelfUpdateGlobalName(@Nonnull SelfUpdateGlobalNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onSelfUpdateVerified(@Nonnull SelfUpdateVerifiedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageUpdate(@Nonnull MessageUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageDelete(@Nonnull MessageDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageBulkDelete(@Nonnull MessageBulkDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageEmbed(@Nonnull MessageEmbedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageReactionRemove(@Nonnull MessageReactionRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageReactionRemoveAll(@Nonnull MessageReactionRemoveAllEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessageReactionRemoveEmoji(@Nonnull MessageReactionRemoveEmojiEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessagePollVoteAdd(@Nonnull MessagePollVoteAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onMessagePollVoteRemove(@Nonnull MessagePollVoteRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onPermissionOverrideDelete(@Nonnull PermissionOverrideDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onPermissionOverrideUpdate(@Nonnull PermissionOverrideUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onPermissionOverrideCreate(@Nonnull PermissionOverrideCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onStageInstanceDelete(@Nonnull StageInstanceDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onStageInstanceUpdateTopic(@Nonnull StageInstanceUpdateTopicEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onStageInstanceUpdatePrivacyLevel(@Nonnull StageInstanceUpdatePrivacyLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onStageInstanceCreate(@Nonnull StageInstanceCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelCreate(@Nonnull ChannelCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelDelete(@Nonnull ChannelDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateBitrate(@Nonnull ChannelUpdateBitrateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateName(@Nonnull ChannelUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateFlags(@Nonnull ChannelUpdateFlagsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateNSFW(@Nonnull ChannelUpdateNSFWEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateParent(@Nonnull ChannelUpdateParentEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdatePosition(@Nonnull ChannelUpdatePositionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateRegion(@Nonnull ChannelUpdateRegionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateSlowmode(@Nonnull ChannelUpdateSlowmodeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateDefaultThreadSlowmode(@Nonnull ChannelUpdateDefaultThreadSlowmodeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateDefaultReaction(@Nonnull ChannelUpdateDefaultReactionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateDefaultSortOrder(@Nonnull ChannelUpdateDefaultSortOrderEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateDefaultLayout(@Nonnull ChannelUpdateDefaultLayoutEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateTopic(@Nonnull ChannelUpdateTopicEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateVoiceStatus(@Nonnull ChannelUpdateVoiceStatusEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateType(@Nonnull ChannelUpdateTypeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateUserLimit(@Nonnull ChannelUpdateUserLimitEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateArchived(@Nonnull ChannelUpdateArchivedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateArchiveTimestamp(@Nonnull ChannelUpdateArchiveTimestampEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateAutoArchiveDuration(@Nonnull ChannelUpdateAutoArchiveDurationEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateLocked(@Nonnull ChannelUpdateLockedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateInvitable(@Nonnull ChannelUpdateInvitableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onChannelUpdateAppliedTags(@Nonnull ChannelUpdateAppliedTagsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onForumTagAdd(@Nonnull ForumTagAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onForumTagRemove(@Nonnull ForumTagRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onForumTagUpdateName(@Nonnull ForumTagUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onForumTagUpdateEmoji(@Nonnull ForumTagUpdateEmojiEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onForumTagUpdateModerated(@Nonnull ForumTagUpdateModeratedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onThreadRevealed(@Nonnull ThreadRevealedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onThreadHidden(@Nonnull ThreadHiddenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onThreadMemberJoin(@Nonnull ThreadMemberJoinEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onThreadMemberLeave(@Nonnull ThreadMemberLeaveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildReady(@Nonnull GuildReadyEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildTimeout(@Nonnull GuildTimeoutEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildJoin(@Nonnull GuildJoinEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildLeave(@Nonnull GuildLeaveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildAvailable(@Nonnull GuildAvailableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUnavailable(@Nonnull GuildUnavailableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUnavailableGuildJoined(@Nonnull UnavailableGuildJoinedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onUnavailableGuildLeave(@Nonnull UnavailableGuildLeaveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildBan(@Nonnull GuildBanEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUnban(@Nonnull GuildUnbanEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildAuditLogEntryCreate(@Nonnull GuildAuditLogEntryCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberRemove(@Nonnull GuildMemberRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateAfkChannel(@Nonnull GuildUpdateAfkChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateSystemChannel(@Nonnull GuildUpdateSystemChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateRulesChannel(@Nonnull GuildUpdateRulesChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateCommunityUpdatesChannel(@Nonnull GuildUpdateCommunityUpdatesChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateAfkTimeout(@Nonnull GuildUpdateAfkTimeoutEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateExplicitContentLevel(@Nonnull GuildUpdateExplicitContentLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateIcon(@Nonnull GuildUpdateIconEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateMFALevel(@Nonnull GuildUpdateMFALevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateName(@Nonnull GuildUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateNotificationLevel(@Nonnull GuildUpdateNotificationLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateOwner(@Nonnull GuildUpdateOwnerEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateSplash(@Nonnull GuildUpdateSplashEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateVerificationLevel(@Nonnull GuildUpdateVerificationLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateLocale(@Nonnull GuildUpdateLocaleEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateFeatures(@Nonnull GuildUpdateFeaturesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateVanityCode(@Nonnull GuildUpdateVanityCodeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateBanner(@Nonnull GuildUpdateBannerEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateDescription(@Nonnull GuildUpdateDescriptionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateBoostTier(@Nonnull GuildUpdateBoostTierEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateBoostCount(@Nonnull GuildUpdateBoostCountEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateMaxMembers(@Nonnull GuildUpdateMaxMembersEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateMaxPresences(@Nonnull GuildUpdateMaxPresencesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildUpdateNSFWLevel(@Nonnull GuildUpdateNSFWLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateDescription(@Nonnull ScheduledEventUpdateDescriptionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateEndTime(@Nonnull ScheduledEventUpdateEndTimeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateLocation(@Nonnull ScheduledEventUpdateLocationEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateName(@Nonnull ScheduledEventUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateStartTime(@Nonnull ScheduledEventUpdateStartTimeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateStatus(@Nonnull ScheduledEventUpdateStatusEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUpdateImage(@Nonnull ScheduledEventUpdateImageEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventCreate(@Nonnull ScheduledEventCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventDelete(@Nonnull ScheduledEventDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUserAdd(@Nonnull ScheduledEventUserAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onScheduledEventUserRemove(@Nonnull ScheduledEventUserRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildInviteCreate(@Nonnull GuildInviteCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberJoin(@Nonnull GuildMemberJoinEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberRoleAdd(@Nonnull GuildMemberRoleAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberRoleRemove(@Nonnull GuildMemberRoleRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdateNickname(@Nonnull GuildMemberUpdateNicknameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdateAvatar(@Nonnull GuildMemberUpdateAvatarEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdateBoostTime(@Nonnull GuildMemberUpdateBoostTimeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdatePending(@Nonnull GuildMemberUpdatePendingEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdateFlags(@Nonnull GuildMemberUpdateFlagsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildMemberUpdateTimeOut(@Nonnull GuildMemberUpdateTimeOutEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceUpdate(@Nonnull GuildVoiceUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceMute(@Nonnull GuildVoiceMuteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceDeafen(@Nonnull GuildVoiceDeafenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceGuildMute(@Nonnull GuildVoiceGuildMuteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceGuildDeafen(@Nonnull GuildVoiceGuildDeafenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceSelfMute(@Nonnull GuildVoiceSelfMuteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceSelfDeafen(@Nonnull GuildVoiceSelfDeafenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceSuppress(@Nonnull GuildVoiceSuppressEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceStream(@Nonnull GuildVoiceStreamEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceVideo(@Nonnull GuildVoiceVideoEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildVoiceRequestToSpeak(@Nonnull GuildVoiceRequestToSpeakEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onAutoModExecution(@Nonnull AutoModExecutionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onAutoModRuleCreate(@Nonnull AutoModRuleCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onAutoModRuleUpdate(@Nonnull AutoModRuleUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onAutoModRuleDelete(@Nonnull AutoModRuleDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleCreate(@Nonnull RoleCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleDelete(@Nonnull RoleDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdateColor(@Nonnull RoleUpdateColorEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdateHoisted(@Nonnull RoleUpdateHoistedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdateIcon(@Nonnull RoleUpdateIconEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdateMentionable(@Nonnull RoleUpdateMentionableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdateName(@Nonnull RoleUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdatePermissions(@Nonnull RoleUpdatePermissionsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onRoleUpdatePosition(@Nonnull RoleUpdatePositionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEmojiAdded(@Nonnull EmojiAddedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEmojiRemoved(@Nonnull EmojiRemovedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEmojiUpdateName(@Nonnull EmojiUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEmojiUpdateRoles(@Nonnull EmojiUpdateRolesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGenericPrivilegeUpdate(@Nonnull GenericPrivilegeUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onApplicationCommandUpdatePrivileges(@Nonnull ApplicationCommandUpdatePrivilegesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onApplicationUpdatePrivileges(@Nonnull ApplicationUpdatePrivilegesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildStickerAdded(@Nonnull GuildStickerAddedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildStickerRemoved(@Nonnull GuildStickerRemovedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildStickerUpdateName(@Nonnull GuildStickerUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildStickerUpdateTags(@Nonnull GuildStickerUpdateTagsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildStickerUpdateDescription(@Nonnull GuildStickerUpdateDescriptionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onGuildStickerUpdateAvailable(@Nonnull GuildStickerUpdateAvailableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEntitlementCreate(@Nonnull EntitlementCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEntitlementUpdate(@Nonnull EntitlementUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onEntitlementDelete(@Nonnull EntitlementDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    public void onHttpRequest(@Nonnull HttpRequestEvent event) {
        invokeEvent(event.getClass(), event);
    }
}

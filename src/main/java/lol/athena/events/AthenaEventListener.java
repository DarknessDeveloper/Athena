package lol.athena.events;

import lol.athena.Athena;
import lol.athena.plugin.events.Cancellable;
import lol.athena.plugin.events.PluginEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.annotations.ForRemoval;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.ExceptionEvent;
import net.dv8tion.jda.api.events.GatewayPingEvent;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.ChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.update.*;
import net.dv8tion.jda.api.events.guild.*;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdatePendingEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideCreateEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideDeleteEvent;
import net.dv8tion.jda.api.events.guild.override.PermissionOverrideUpdateEvent;
import net.dv8tion.jda.api.events.guild.update.*;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.events.http.HttpRequestEvent;
import net.dv8tion.jda.api.events.message.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.role.RoleCreateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.events.role.update.*;
import net.dv8tion.jda.api.events.self.SelfUpdateAvatarEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateMFAEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateNameEvent;
import net.dv8tion.jda.api.events.self.SelfUpdateVerifiedEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceCreateEvent;
import net.dv8tion.jda.api.events.stage.StageInstanceDeleteEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdatePrivacyLevelEvent;
import net.dv8tion.jda.api.events.stage.update.StageInstanceUpdateTopicEvent;
import net.dv8tion.jda.api.events.thread.ThreadHiddenEvent;
import net.dv8tion.jda.api.events.thread.ThreadRevealedEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberJoinEvent;
import net.dv8tion.jda.api.events.thread.member.ThreadMemberLeaveEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"deprecation"})
@AllArgsConstructor
@Deprecated
@ForRemoval
public class AthenaEventListener extends ListenerAdapter {

    @Getter private Athena bot;

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
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
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

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        invokeEvent(event.getClass(), event);
    }


    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelDelete(ChannelDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateArchived(ChannelUpdateArchivedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateArchiveTimestamp(ChannelUpdateArchiveTimestampEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateAutoArchiveDuration(ChannelUpdateAutoArchiveDurationEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateBitrate(ChannelUpdateBitrateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateInvitable(ChannelUpdateInvitableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateLocked(ChannelUpdateLockedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateName(ChannelUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateNSFW(ChannelUpdateNSFWEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateParent(ChannelUpdateParentEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdatePosition(ChannelUpdatePositionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateRegion(ChannelUpdateRegionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateSlowmode(ChannelUpdateSlowmodeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateTopic(ChannelUpdateTopicEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateType(ChannelUpdateTypeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onChannelUpdateUserLimit(ChannelUpdateUserLimitEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onException(ExceptionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGatewayPing(GatewayPingEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRawGateway(RawGatewayEvent event) {
        // invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateDiscriminator(UserUpdateDiscriminatorEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateAvatar(UserUpdateAvatarEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateOnlineStatus(UserUpdateOnlineStatusEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateActivityOrder(UserUpdateActivityOrderEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateFlags(UserUpdateFlagsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserTyping(UserTypingEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserActivityStart(UserActivityStartEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserActivityEnd(UserActivityEndEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUserUpdateActivities(UserUpdateActivitiesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onSelfUpdateAvatar(SelfUpdateAvatarEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onSelfUpdateMFA(SelfUpdateMFAEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onSelfUpdateName(SelfUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onSelfUpdateVerified(SelfUpdateVerifiedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageEmbed(MessageEmbedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onMessageReactionRemoveAll(MessageReactionRemoveAllEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onPermissionOverrideDelete(PermissionOverrideDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onPermissionOverrideUpdate(PermissionOverrideUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onPermissionOverrideCreate(PermissionOverrideCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onStageInstanceDelete(StageInstanceDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onStageInstanceUpdateTopic(StageInstanceUpdateTopicEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onStageInstanceUpdatePrivacyLevel(StageInstanceUpdatePrivacyLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onStageInstanceCreate(StageInstanceCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onThreadRevealed(ThreadRevealedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onThreadHidden(ThreadHiddenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onThreadMemberJoin(ThreadMemberJoinEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onThreadMemberLeave(ThreadMemberLeaveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildTimeout(GuildTimeoutEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildAvailable(GuildAvailableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUnavailable(GuildUnavailableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUnavailableGuildJoined(UnavailableGuildJoinedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onUnavailableGuildLeave(UnavailableGuildLeaveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateAfkChannel(GuildUpdateAfkChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateSystemChannel(GuildUpdateSystemChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateRulesChannel(GuildUpdateRulesChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateCommunityUpdatesChannel(GuildUpdateCommunityUpdatesChannelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateAfkTimeout(GuildUpdateAfkTimeoutEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateExplicitContentLevel(GuildUpdateExplicitContentLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateIcon(GuildUpdateIconEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateSplash(GuildUpdateSplashEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateLocale(GuildUpdateLocaleEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateFeatures(GuildUpdateFeaturesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateVanityCode(GuildUpdateVanityCodeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateBanner(GuildUpdateBannerEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateDescription(GuildUpdateDescriptionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateBoostTier(GuildUpdateBoostTierEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateBoostCount(GuildUpdateBoostCountEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateMaxMembers(GuildUpdateMaxMembersEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateMaxPresences(GuildUpdateMaxPresencesEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildUpdateNSFWLevel(GuildUpdateNSFWLevelEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildInviteDelete(GuildInviteDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberUpdate(GuildMemberUpdateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberUpdateNickname(GuildMemberUpdateNicknameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberUpdateAvatar(GuildMemberUpdateAvatarEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildMemberUpdatePending(GuildMemberUpdatePendingEvent event) {
        invokeEvent(event.getClass(), event);
    }


    @Override
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceStream(GuildVoiceStreamEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceVideo(GuildVoiceVideoEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onGuildVoiceRequestToSpeak(GuildVoiceRequestToSpeakEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleCreate(RoleCreateEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdateColor(RoleUpdateColorEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdateHoisted(RoleUpdateHoistedEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdateIcon(RoleUpdateIconEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdateMentionable(RoleUpdateMentionableEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdateName(RoleUpdateNameEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onRoleUpdatePosition(RoleUpdatePositionEvent event) {
        invokeEvent(event.getClass(), event);
    }

    @Override
    public void onHttpRequest(HttpRequestEvent event) {
        // invokeEvent(event.getClass(), event);
    }



}
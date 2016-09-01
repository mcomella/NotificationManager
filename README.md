# NTFY app
A notifications manager on Android.

## V0.5
Display a list of apps that the user has and provide a quick way to disable/enable their notifications.

(Alternatively, allow user to add an app to the manager. All added app will be muted until it’s removed.)

## V1
Allow the user to create basic categories/contexts (location, time, etc) for when to mute which notifications.

## V2 (for later)
Provide more detailed control over notifications, such as different cues (visual, sounds, vibrations, etc), priority (levels?), other contexts around them that we didn't explore yet (location, time, etc).

## Technical Notes
API 21 (L) is convenient so we support 21+. API 18 may be possible, however,
with some code branching on versions (e.g.
`NotificationListenerService.cancelNotification`).

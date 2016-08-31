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
API 18 is supported because `NotificationListenerService` was added in that
version. However, API 21 adds some additional features (e.g. interruption
filters) that could be useful.

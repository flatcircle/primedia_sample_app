# primedia_sample_app

The issue faced is that when attempting to retrieve the time position of the currently playing onDemand stream, by calling player.getPosition(), on the stream. This is once player.play() has been called but before it has started playing. The player propagates an error state through to the player stateChangeListener. The errors being:

E/MediaPlayer: Error (-38,0)
E/TdAndroidPlayerBkg: Native player error: Other / extra: 0


Seeing as our UI acts on state changes this causes a problem that locks our UI as the player then plays the onDemandStream, but the error state has already come through.

Steps to reproduce:
1. Press play on sample app
2. Press skip and start cycling through the sample podcast clips
3. Once an error has come through it should show that it's been propagated by the state change listener in the error text field below the play button.


If you're having trouble reproducing we've also noticed on our actual app that this happens most reliably using samsung devices, other devices this isn't as frequent or not at all.

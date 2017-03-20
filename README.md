## SettingsForcer
Application to force protected VR settings, primarily to prevent the setup wizard from launching. Also allows you to run VR content without certain required Oculus/Gear soft-dependencies. This prevents many issues caused by those soft-depends, such as obtrusive messages, etc.

**Note**: This application *must* be installed with adb, using the `-g` flag. **There is no way around this, period.**

```adb install -g app-release.apk```

*This grants the `android.permission.WRITE_SECURE_SETTINGS` permission that is needed to modify Global settings.*

The app is currently very simple, it just reads the value on launch and writes it when the user makes a new selection. There is also an optional service that runs constantly in the background, and monitors the value of the setting (reverting it whenever a change is made).

### Screenshots / demo video
**[Video demo](https://drive.google.com/open?id=0B4O23VCBmTAEekc2Z3hoNTBHM0k)**

Setup Wizard is incomplete, would prompt if VR content launched:
<p align="center">
  <img src="http://i.imgur.com/TGnZ1N5.png" alt="Incomplete state" style="float:middle" width="360" height="640"/>
</p>

User can select to "complete" the setup wizard, will no longer prompt:
<p align="center">
  <img src="http://i.imgur.com/8RqNyBU.png" alt="Complete state" style="float:middle" width="360" height="640"/>
</p>

*Screenshots/video above do not show background service option, as they were taken prior to its development. However, the background service is now enabled.**

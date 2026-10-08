# Custom Emoji Keyboard for Android

A starter Android IME that lets users turn pictures into reusable 256×256 PNG custom emojis/stickers and expose them from a system keyboard.

## What is included

- Real Android `InputMethodService` keyboard.
- QWERTY letter input.
- `My Emojis` panel.
- Built-in blue verified-style sample badge based on the supplied reference image.
- Pick an image from the device using Android's system content picker.
- Center-crop to a square and resize to 256×256 PNG.
- Store custom emoji files in the app's private storage.
- Insert PNGs into editors that advertise compatible image MIME types using `InputConnection.commitContent()`.
- No INTERNET permission and no broad photo/storage permission.
- GitHub Actions debug-build workflow.

## Requirements

Use a current Android Studio release with JDK 17. This project targets API 37 and uses Android Gradle Plugin 9.4.0 with Gradle 9.6.0.

## Open and run

1. Download/clone this repository.
2. Open the repository folder in Android Studio.
3. Let Gradle sync.
4. Install the app on an Android phone/emulator.
5. Open **Custom Emoji Keyboard**.
6. Tap **Enable keyboard** and enable it in Android settings.
7. Select **Custom Emoji Keyboard** as the current keyboard.
8. Open a messaging app.
9. Tap **⭐ My Emojis**.
10. Tap **＋ Add** to add an image. The app opens the system picker; after saving, return to the keyboard and the image appears in My Emojis.

## GitHub

Create an empty GitHub repository, then from this folder run:

```bash
git init
git add .
git commit -m "Initial custom emoji keyboard"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/custom-emoji-keyboard.git
git push -u origin main
```

Replace `YOUR_USERNAME` and the repository name with your own.

## Important limitation

A custom image cannot become a genuine Unicode emoji. In this project it is a keyboard-managed image/sticker. Android's `commitContent()` lets an IME send content such as PNG images to an editor, but the receiving app must advertise support for the relevant MIME type. Apps that do not support image insertion may reject the custom emoji.

## Next production features

- Background removal/cutout.
- Emoji borders and shapes.
- Drag/reorder custom emojis.
- Long-press delete.
- GIF/video sticker support.
- Search and favorites.
- Themes.
- Emoji packs/export/import.
- Better compatibility fallback for apps that do not accept `commitContent()`.
- Play Store privacy policy and production signing.

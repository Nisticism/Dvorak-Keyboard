package nistic.dvorakkeyboard;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.*;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;




public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView keyboardView;
    private Keyboard keyboard;
    //private Keyboard charKeyboard;

    private Integer caps = 0;

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keys_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    public View setSpecialChars() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.characters_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    public View setEmojisKeyboard() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.emoji_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Integer vibeLength = 3;
        InputConnection inputConnection = getCurrentInputConnection();
        //inputConnection.setComposingText("Nistic", 1);
        playClick(primaryCode);

        if (inputConnection != null) {
            switch(primaryCode) {
                case Keyboard.KEYCODE_DELETE :
                    CharSequence selectedText = inputConnection.getSelectedText(0);

                    if (TextUtils.isEmpty(selectedText)) {
                        inputConnection.deleteSurroundingText(1, 0);
                    } else {
                        inputConnection.commitText("", 1);
                    }
                    break;
                case Keyboard.KEYCODE_SHIFT:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(vibeLength, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Deprecated in API 26
                        v.vibrate(vibeLength);
                    }
                    if (caps == 0) {
                        caps += 1;
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow_shift);
                        keyboard.setShifted(true);
                        keyboardView.invalidateAllKeys();
                        break;
                    }
                    else if (caps == 1) {
                        caps += 1;
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow_caps);
                        keyboard.setShifted(true);
                        keyboardView.invalidateAllKeys();
                        break;
                    }
                    else if (caps == 2) {
                        caps = 0;
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow);
                        keyboard.setShifted(false);
                        keyboardView.invalidateAllKeys();
                        break;
                    }
                    break;

                case Keyboard.KEYCODE_DONE:
                    inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, 66));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(vibeLength, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Deprecated in API 26
                        v.vibrate(vibeLength);
                    }
                    break;
                case Keyboard.KEYCODE_ALT:
                    setInputView(setSpecialChars());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(vibeLength, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Deprecated in API 26
                        v.vibrate(vibeLength);
                    }
                    break;

                case Keyboard.KEYCODE_CANCEL:
                    setInputView(onCreateInputView());
                    if (caps == 0) {
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow);
                        keyboard.setShifted(false);
                        keyboardView.invalidateAllKeys();
                    }
                    else if (caps == 1) {
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow_shift);
                        keyboard.setShifted(true);
                        keyboardView.invalidateAllKeys();
                    }
                    else if (caps == 2) {
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow_caps);
                        keyboard.setShifted(true);
                        keyboardView.invalidateAllKeys();
                    }
//                    keyboard.setShifted(false);
//                    caps = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(vibeLength, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Deprecated in API 26
                        v.vibrate(vibeLength);
                    }
                    break;

                    //  emoji menu
                case Keyboard.KEYCODE_MODE_CHANGE:
                    setInputView(setEmojisKeyboard());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(vibeLength, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Deprecated in API 26
                        v.vibrate(vibeLength);
                    }
                    break;


                default :
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(vibeLength, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        // Deprecated in API 26
                        v.vibrate(vibeLength);
                    }
                    char code = (char) primaryCode;
                    if(Character.isLetter(code) && caps == 2){
                        code = Character.toUpperCase(code);
                    }
                    else if(Character.isLetter(code) && caps == 1){
                        code = Character.toUpperCase(code);
                        keyboard.setShifted(false);
                        keyboard.getKeys().get(32).icon = getDrawable(R.drawable.up_arrow);
                        keyboardView.invalidateAllKeys();
                        caps = 0;
                    }
                    if (code >= 400) {
                        inputConnection.commitText(emojis(code), 1);
                    }
                    else if (code < 400) {
                        inputConnection.commitText(String.valueOf(code), 1);
                    }

            }
        }
    }

    private String emojis(int code) {


        // Row 1
        if (code == 400) {
            return "\uD83D\uDE42";
        }
        if (code == 401) {
            return "\uD83D\uDE43";
        }
        if (code == 402) {
            return "\uD83D\uDE0A";
        }
        if (code == 403) {
            return "\uD83D\uDE04";
        }
        if (code == 404) {
            return "\uD83D\uDE05";
        }
        if (code == 405) {
            return "\uD83D\uDE1D";
        }
        if (code == 406) {
            return "\uD83D\uDE02";
        }
        if (code == 407) {
            return "\uD83E\uDD23";
        }
        if (code == 408) {
            return "\uD83D\uDE09";
        }
        if (code == 409) {
            return "\uD83D\uDE0B";
        }


        //  Row 2
        if (code == 410) {
            return "\uD83D\uDE07";
        }
        if (code == 411) {
            return "\uD83D\uDE44";
        }
        if (code == 412) {
            return "\uD83D\uDE0D";
        }
        if (code == 413) {
            return "\uD83D\uDE0C";
        }
        if (code == 414) {
            return "\uD83D\uDE1B";
        }
        if (code == 415) {
            return "\uD83E\uDD14";
        }
        if (code == 416) {
            return "\uD83D\uDE0E";
        }
        if (code == 417) {
            return "\uD83D\uDE0F";
        }
        if (code == 418) {
            return "\uD83E\uDD10";
        }
        if (code == 419) {
            return "\uD83D\uDE36";
        }
        if (code == 420) {
            return "\uD83D\uDE41";
        }
        if (code == 421) {
            return "\uD83D\uDE15";
        }
        if (code == 422) {
            return "\uD83D\uDE29";
        }
        if (code == 423) {
            return "\uD83D\uDE26";
        }
        if (code == 424) {
            return "\uD83D\uDE2E";
        }
        if (code == 425) {
            return "\uD83D\uDE2D";
        }
        if (code == 426) {
            return "\uD83D\uDE22";
        }
        if (code == 427) {
            return "\uD83D\uDE20";
        }
        if (code == 428) {
            return "\uD83D\uDE24";
        }
        if (code == 429) {
            return "\uD83E\uDD22";
        }
        if (code == 430) {
            return "\uD83D\uDE11";
        }
        if (code == 431) {
            return "\uD83E\uDD11";
        }
        if (code == 432) {
            return "\uD83D\uDE34";
        }
        return "";
    }

    private void playClick(int primaryCode) {
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(primaryCode){
            case 32: am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
            break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }


    public MyInputMethodService() {
        super();
    }

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}

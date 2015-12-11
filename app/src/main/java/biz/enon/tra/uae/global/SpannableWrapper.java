package biz.enon.tra.uae.global;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;

/**
 * Created by ak-buffalo on 26.11.15.
 */
public abstract class SpannableWrapper {
    private SpannableStringBuilder spannedText;
    private String originalText;

    public SpannableWrapper(String _text){
        originalText = _text;
    }

    public String getOriginalText() {
        return originalText;
    }

    public SpannableStringBuilder getSpannedText() {
        return spannedText != null ? spannedText : new SpannableStringBuilder(originalText);
    }

    public void setSpannedText(SpannableStringBuilder _spannedText){
        spannedText = _spannedText;
    }

    @Override
    public String toString() {
        return spannedText != null ? spannedText.toString() : originalText.toString();
    }

    public static final <T extends SpannableWrapper> T makeSelectedTextBold(CharSequence _constraint, T _wrapper){
        if(_wrapper != null){
            String originalText = _wrapper.getOriginalText();
            int startFrom = originalText.toString().toLowerCase().indexOf((String) _constraint);
            if(startFrom >= 0 && originalText.length() > startFrom + _constraint.length()){
                SpannableStringBuilder spannedText = new SpannableStringBuilder(originalText);
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                spannedText.setSpan(bss, startFrom, startFrom + _constraint.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                _wrapper.setSpannedText(spannedText);
            }
        }
        return _wrapper;
    }

    public static final SpannableStringBuilder makeSelectedTextBold(CharSequence _constraint, String _originalText){
        SpannableStringBuilder spannedText = new SpannableStringBuilder(_originalText);
        int startFrom = _originalText.toLowerCase().indexOf(((String) _constraint).toLowerCase());
        if(startFrom >= 0 && _originalText.length() >= startFrom + _constraint.length()){
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            spannedText.setSpan(bss, startFrom, startFrom + _constraint.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
        return spannedText;
    }
}
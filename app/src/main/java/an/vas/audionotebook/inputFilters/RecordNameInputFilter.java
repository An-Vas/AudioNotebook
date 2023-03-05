package an.vas.audionotebook.inputFilters;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * InputFilter that allows you to use only Russian and English letters, numbers, spaces and
 * underscores so that the file is accessible regardless of the file system. In AudioNotebook used
 * to control input names of the recordings.
 */
public class RecordNameInputFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            if (!((Character.isDigit(source.charAt(i))
                    ||(Character.isAlphabetic(source.charAt(i)))
                    ||(Character.UnicodeBlock.of(source.charAt(i)).equals(Character.UnicodeBlock.CYRILLIC))
                    ||(source.charAt(i) == ' ')
                    ||(source.charAt(i) == '_')))) {
                return "";
            }
        }
        return null;
    }
}

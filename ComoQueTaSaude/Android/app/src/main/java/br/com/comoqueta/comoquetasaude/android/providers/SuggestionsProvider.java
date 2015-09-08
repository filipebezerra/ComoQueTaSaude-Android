package br.com.comoqueta.comoquetasaude.android.providers;

import android.content.SearchRecentSuggestionsProvider;

/**
 * .
 *
 * @author Filipe Bezerra
 * @version #, 15/08/2015
 * @since #
 */
public class SuggestionsProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = SuggestionsProvider.class.getName();
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
package seng202.team4.repository;

import seng202.team4.models.ThemeSettings;

import java.util.Optional;

/**
 * An interface to access the saved theme in the database
 */
public interface ThemeRepository {

    /**
     * Loads the saved theme from the database
     * @return the saved theme, or empty if none has been saved yet
     */
    Optional<ThemeSettings> load();

    /**
     * Saves the theme to the database, replacing any previously saved theme
     * @param primaryColor the primary colour as a hex string
     * @param darkMode true if dark mode is enabled
     */
    void save(String primaryColor, boolean darkMode);
}
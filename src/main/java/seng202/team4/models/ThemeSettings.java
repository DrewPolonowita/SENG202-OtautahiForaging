package seng202.team4.models;

/**
 * Model representing the user's saved theme
 * @param primaryColor the primary colour as a hex string
 * @param darkMode true if dark mode is enabled
 */
public record ThemeSettings(String primaryColor, boolean darkMode) { }
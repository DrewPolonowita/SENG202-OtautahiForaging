package seng202.team4.gui;

import java.util.function.Consumer;

public class WindowManager {


    private final Consumer<WindowManager> mainLauncher;

    private final Runnable clearScreen;

    public WindowManager(
            Consumer<WindowManager> mainLauncher,
            Runnable clearScreen) {
        this.mainLauncher = mainLauncher;
        this.clearScreen = clearScreen;
        launchMainScreen();


    }

    public void launchMainScreen() { mainLauncher.accept(this);}

    public void closeMainScreen() {
        clearScreen.run();
    }
}

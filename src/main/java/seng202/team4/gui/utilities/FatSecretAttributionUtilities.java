package seng202.team4.gui.utilities;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.services.api.FoodApiClient;

import java.awt.Desktop;
import java.net.URI;

public final class FatSecretAttributionUtilities {
    private static final Logger log = LogManager.getLogger(FatSecretAttributionUtilities.class);
    private static final String FATSECRET_ATTRIBUTION_STANDARD_IMAGE_URL =
            "https://platform.fatsecret.com/api/static/images/powered_by_fatsecret_horizontal_brand.png";
    private static final String FATSECRET_ATTRIBUTION_DARK_IMAGE_URL =
            "https://platform.fatsecret.com/api/static/images/powered_by_fatsecret_horizontal_dark.png";
    private static final String FATSECRET_ATTRIBUTION_LINK_URL = "https://platform.fatsecret.com";

    private FatSecretAttributionUtilities() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }

    public static ImageView createStandardAttributionImage() {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setImage(new Image(FATSECRET_ATTRIBUTION_STANDARD_IMAGE_URL));


        imageView.setOnMouseClicked(event -> {
            try {
                Desktop.getDesktop().browse(new URI(FATSECRET_ATTRIBUTION_LINK_URL));
            } catch (Exception e) {
                log.error(e);
            }
        });
        return imageView;
    }
}

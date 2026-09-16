package seng202.team4.services;

import seng202.team4.gui.WindowManager;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.FoodDAO;
import seng202.team4.repository.RecipeDAO;
import seng202.team4.repository.RegionDAO;
import seng202.team4.repository.TreeDAO;
import seng202.team4.repository.TreeRepository;
import seng202.team4.services.api.ApiClient;
import seng202.team4.services.api.ApiClientInterface;
import seng202.team4.services.api.FoodApiClient;
import seng202.team4.services.api.RecipeApiClient;
import seng202.team4.services.treeData.RegionService;
import seng202.team4.services.treeData.TreeDataManager;
import seng202.team4.services.treeData.TreeService;


/**
 * The class which stores information related to the state of the entire app in a given instance
 * This class also has the ability to launch screens using the {@link WindowManager}
 * @author Team 4
 */
public class StateManager {

    private final ApiClientInterface apiClient;
    private final WindowManager windowManager;
    private final DatabaseManager databaseManager;
    private final TreeRepository treeRepository;
    private final TreeDataManager treeService;
    private final FoodService foodService;
    private final RecipeService recipeService;


    /**
     * A constructor which launches the mainScreen of the application
     * Initialises database manager passing in null, database manager will handle path
     * @param windowManager the window manager for this application
     *
     */
    public StateManager(WindowManager windowManager) {
        this.windowManager = windowManager;
        this.databaseManager = new DatabaseManager(null);
        this.apiClient = new ApiClient();
        this.treeRepository = new TreeDAO(databaseManager, getFoodService());
        this.foodService = new FoodService(new FoodDAO(databaseManager), new FoodApiClient(apiClient));
        this.treeService = new TreeService(treeRepository, apiClient, new RegionService(new RegionDAO(databaseManager)), foodService);
        this.recipeService = new RecipeService(new RecipeDAO(databaseManager), new RecipeApiClient(apiClient));
        this.windowManager.launchMapScreen(this);
        this.windowManager.launchNavScreen(this);

    }

    public WindowManager getWindowManager() {
        return windowManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public TreeDataManager getTreeService() {
        return treeService;
    }

    public FoodService getFoodService() {
        return foodService;
    }

    public RecipeService getRecipeService() { return recipeService; }
}

package seng202.team4.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import seng202.team4.models.TreeModel;
import seng202.team4.models.api.ApiResponse;
import seng202.team4.models.api.Tree;

import java.util.Arrays;
import java.util.List;

public class TreeService {
    private static final Logger log = LogManager.getLogger(TreeService.class);
    private final ApiHandler apiHandler;

    public TreeService()
    {
        this.apiHandler = new ApiHandler();
    }

    public TreeService(ApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    private TreeModel createTreeObject(Tree treeRecord)
    {
        return new TreeModel(
                treeRecord.properties().treeId(),
                treeRecord.geometry().coordinates(),
                treeRecord.properties().ownership(),
                treeRecord.properties().genus()
        );
    }
    public List<TreeModel> getAllTrees(){
        ApiResponse response = apiHandler.getTrees("", "").getApiResponse();

        if (response == null) {
            log.info("API Response null");
            return List.of();
        }


        List<Tree> treeRecords = Arrays.asList(
                response.featureTrees()
        );

        return treeRecords.stream()
                .limit(5)
                .map(this::createTreeObject)
                .toList();

    }
}

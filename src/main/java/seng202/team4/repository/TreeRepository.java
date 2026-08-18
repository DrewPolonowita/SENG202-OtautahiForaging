package seng202.team4.repository;

import seng202.team4.models.TreeModel;
import java.util.List;

public interface TreeRepository extends DAOInterface<TreeModel> {
    List<TreeModel> getTreesByCommonName(String type);
}
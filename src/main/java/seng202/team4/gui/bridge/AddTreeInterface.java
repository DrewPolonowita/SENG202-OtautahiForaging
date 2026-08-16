package seng202.team4.gui.bridge;

import seng202.team4.models.TreeModel;

/**
 * Interface to allow passing the tree lambda function between classes
 */
public interface AddTreeInterface {
    /**
     * Operation
     * @param tree to be passed to underlying operation
     */
    void operation(TreeModel tree);
}

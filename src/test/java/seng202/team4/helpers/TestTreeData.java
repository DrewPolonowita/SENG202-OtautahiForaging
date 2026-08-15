package seng202.team4.helpers;

import seng202.team4.models.TreeModel;
import seng202.team4.models.api.Geometry;
import seng202.team4.models.api.Properties;
import seng202.team4.models.api.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTreeData {

    public static Tree createFakeRecord(int x, int y, int id, String owner, String genus, String commonName) {
        int[] coordinates = {x, y};
        Geometry geometry = new Geometry("location", coordinates);
        Properties properties = new Properties(id, owner, genus, commonName);

        return new Tree(
                "A",
                123,
                geometry,
                properties
        );
    }
    public static List<TreeModel> createTreeModels() {
        List<TreeModel> testTrees = new ArrayList<>();
        testTrees.add(new TreeModel(2452, new int[]{5252, 3423}, "Council", "Malus", "Apple", "Apple"));
        testTrees.add(new TreeModel(2454, new int[]{2252, 5423}, "Council", "Malus", "Apple", "Apple"));
        testTrees.add(new TreeModel(2453, new int[]{5253, 8424}, "Council", "Pyrus", "Pear", "Pear"));
        testTrees.add(new TreeModel(724, new int[]{4313, 8591}, "Council", "Prunus", "Cherry", "Cherry"));
        testTrees.add(new TreeModel(2042, new int[]{4539, 7854}, "Council", "Prunus", "Apricot", "Apricot"));
        return testTrees;
    }



    public static Tree[] createFakeAppleRecords() {
        return new Tree[] {
                createFakeRecord(5252, 3423, 2452, "Council", "Malus", "Apple"),
                createFakeRecord(2252, 5423, 2454, "Council", "Malus", "Apple")
        };
    }

    public static Tree[] createFakePearRecords() {
        return new Tree[] {
                createFakeRecord(5253, 8424, 2453, "Council", "Pyrus", "Pear")
        };
    }

    public static Tree[] createFakeCherryRecords() {
        return new Tree[] {
                createFakeRecord(4313, 8591, 724, "Council", "Prunus", "Cherry")
        };
    }

    public static Tree[] createFakePeachRecords() {
        return new Tree[0];
    }

    public static Tree[] createFakeApricotRecords() {
        return new Tree[] {
                createFakeRecord(4539, 7854, 2042, "Council", "Prunus", "Apricot")
        };
    }

    public static Tree[] createFakeRecords() {
        List<Tree> fakeTreeList = new ArrayList<>();
        fakeTreeList.addAll(Arrays.asList(createFakeAppleRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakePearRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakeCherryRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakePeachRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakeApricotRecords()));
        return fakeTreeList.toArray(new Tree[0]);
    }

}

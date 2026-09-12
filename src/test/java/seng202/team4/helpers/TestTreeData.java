package seng202.team4.helpers;

import seng202.team4.models.TreeModel;
import seng202.team4.models.api.TreeApi.Geometry;
import seng202.team4.models.api.TreeApi.Properties;
import seng202.team4.models.api.TreeApi.TreeRecord;
import seng202.team4.models.api.TreeApi.TreeRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTreeData {

    public static TreeRecord createFakeRecord(int x, int y, int id, String owner, String genus, String commonName) {
        float[] coordinates = {x, y};
        Geometry geometry = new Geometry("location", coordinates);
        Properties properties = new Properties(id, owner, genus, commonName);

        return new TreeRecord(
                "A",
                123,
                geometry,
                properties
        );
    }
    public static List<TreeModel> createTreeModels() {
        List<TreeModel> testTrees = new ArrayList<>();
        testTrees.add(new TreeModel(2452, new float[]{5252, 3423}, "Council", "Malus", "Apple", "Apple", null, "serviced"));
        testTrees.add(new TreeModel(2454, new float[]{2252, 5423}, "Council", "Malus", "Apple", "Apple", null, "serviced"));
        testTrees.add(new TreeModel(2453, new float[]{5253, 8424}, "Council", "Pyrus", "Pear", "Pear", null, "serviced"));
        testTrees.add(new TreeModel(724, new float[]{4313, 8591}, "Council", "Prunus", "Cherry", "Cherry", null, "serviced"));
        testTrees.add(new TreeModel(2042, new float[]{4539, 7854}, "Council", "Prunus", "Apricot", "Apricot", null, "serviced"));
        return testTrees;
    }



    public static TreeRecord[] createFakeAppleRecords() {
        return new TreeRecord[] {
                createFakeRecord(5252, 3423, 2452, "Council", "Malus", "Apple"),
                createFakeRecord(2252, 5423, 2454, "Council", "Malus", "Apple")
        };
    }

    public static TreeRecord[] createFakePearRecords() {
        return new TreeRecord[] {
                createFakeRecord(5253, 8424, 2453, "Council", "Pyrus", "Pear")
        };
    }

    public static TreeRecord[] createFakeCherryRecords() {
        return new TreeRecord[] {
                createFakeRecord(4313, 8591, 724, "Council", "Prunus", "Cherry")
        };
    }

    public static TreeRecord[] createFakePeachRecords() {
        return new TreeRecord[0];
    }

    public static TreeRecord[] createFakeApricotRecords() {
        return new TreeRecord[] {
                createFakeRecord(4539, 7854, 2042, "Council", "Prunus", "Apricot")
        };
    }

    public static TreeRecord[] createFakeRecords() {
        List<TreeRecord> fakeTreeList = new ArrayList<>();
        fakeTreeList.addAll(Arrays.asList(createFakeAppleRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakePearRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakeCherryRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakePeachRecords()));
        fakeTreeList.addAll(Arrays.asList(createFakeApricotRecords()));
        return fakeTreeList.toArray(new TreeRecord[0]);
    }

}

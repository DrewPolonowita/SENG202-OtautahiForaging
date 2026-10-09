package seng202.team4.unittests.services.treeData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.models.TreeModel;
import seng202.team4.repository.TreeRepository;
import seng202.team4.services.FoodMapper;
import seng202.team4.services.api.TreeClientInterface;
import seng202.team4.services.dataCentre.treeData.RegionService;
import seng202.team4.services.dataCentre.treeData.TreeService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A test class for {@link TreeService}
 */
class TreeServiceTest {

    private TreeRepository treeRepository;
    private TreeClientInterface treeApiClient;
    private RegionService regionService;
    private FoodMapper foodMapper;
    private TreeService treeService;

    @BeforeEach
    void setUp() {
        treeRepository = mock(TreeRepository.class);
        treeApiClient = mock(TreeClientInterface.class);
        regionService = mock(RegionService.class);
        foodMapper = mock(FoodMapper.class);

        treeService = new TreeService(
                treeRepository,
                treeApiClient,
                regionService,
                foodMapper
        );
    }

    @Test
    void testReturnsCachedTrees() {
        TreeModel tree = mock(TreeModel.class);

        when(regionService.getOverlappingRegions(
                "Apple", 10.0, 10.0, 0.0, 0.0
        )).thenReturn(List.of());

        when(regionService.getQueryRegion(List.of())).thenReturn(null);

        when(treeRepository.getTreesByRegion(
                "Apple", 10.0, 10.0, 0.0, 0.0
        )).thenReturn(List.of(tree));

        List<TreeModel> result = treeService.getTreesByRegion(
                "Apple", 10.0, 10.0, 0.0, 0.0
        );

        assertEquals(1, result.size());
        assertSame(tree, result.getFirst());

        verify(treeRepository).getTreesByRegion(
                "Apple", 10.0, 10.0, 0.0, 0.0
        );
        verifyNoInteractions(treeApiClient);
    }

    @Test
    void testNoApiOrRepoGivesAnEmptyList() {
        when(regionService.getOverlappingRegions(
                "Apple", 10.0, 10.0, 0.0, 0.0
        )).thenReturn(List.of());

        when(regionService.getQueryRegion(List.of())).thenReturn(null);

        when(treeRepository.getTreesByRegion(
                "Apple", 10.0, 10.0, 0.0, 0.0
        )).thenReturn(List.of());

        List<TreeModel> result = treeService.getTreesByRegion(
                "Apple", 10.0, 10.0, 0.0, 0.0
        );

        assertTrue(result.isEmpty());
    }
}
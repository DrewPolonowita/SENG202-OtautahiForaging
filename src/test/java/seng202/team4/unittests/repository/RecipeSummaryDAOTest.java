package seng202.team4.unittests.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.exceptions.DuplicateEntryException;
import seng202.team4.helpers.TestRecipeData;
import seng202.team4.models.RecipeSummary;
import seng202.team4.repository.DatabaseManager;
import seng202.team4.repository.RecipeSummaryDAO;
import seng202.team4.repository.RecipeSummaryRepository;
import seng202.team4.unittests.TestSuite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeSummaryDAOTest extends TestSuite {

    RecipeSummaryRepository recipeSummaryDAO;
    DatabaseManager databaseManager;

    @BeforeEach
    void setUpTest() {
        databaseManager = new DatabaseManager(this.getTestDb());
        recipeSummaryDAO = new RecipeSummaryDAO(databaseManager);
    }

    @Test
    void addWhenRecipeSummaryExistsAlreadyThrowsDuplicateEntryException() throws DuplicateEntryException {
        RecipeSummary recipeSummary = TestRecipeData.createRecipeSummaries().getFirst();

        recipeSummaryDAO.add(recipeSummary);

        assertThrows(DuplicateEntryException.class, () -> recipeSummaryDAO.add(recipeSummary));
    }

    @Test
    void updateChangesRecipeSummary() throws DuplicateEntryException {
        RecipeSummary original = TestRecipeData.createRecipeSummaries().getFirst();
        recipeSummaryDAO.add(original);
        RecipeSummary updated = new RecipeSummary(original.getRecipeId(), "new name", "new image");

        recipeSummaryDAO.update(updated);
        recipeSummaryDAO.addRecipeUseFood("Apple", original.getRecipeId());


        RecipeSummary result = recipeSummaryDAO.getRecipeSummariesByFoodName("Apple").getFirst();
        assertEquals(original.getRecipeId(), result.getRecipeId());
        assertEquals("new name", result.getRecipeName());
        assertEquals("new image", result.getRecipeImageUrl());

    }

    @Test
    void getRecipeSummariesByFoodNameGivesRightSummaries() throws DuplicateEntryException {
        List<RecipeSummary> summaries = TestRecipeData.createRecipeSummaries();

        RecipeSummary summary1 = summaries.getFirst();
        RecipeSummary summary2 = summaries.get(1);

        recipeSummaryDAO.add(summary1);
        recipeSummaryDAO.add(summary2);

        recipeSummaryDAO.addRecipeUseFood("Apple", summary1.getRecipeId());
        recipeSummaryDAO.addRecipeUseFood("Apple", summary2.getRecipeId());

        List<RecipeSummary> results = recipeSummaryDAO.getRecipeSummariesByFoodName("Apple");

        assertTrue(results.stream().anyMatch(summary -> summary.getRecipeId() == summary1.getRecipeId()));
        assertTrue(results.stream().anyMatch(summary -> summary.getRecipeId() == summary2.getRecipeId()));
    }

    @Test
    void saveRecipeUpdatesBasedOnTime() throws Exception {
        RecipeSummary summary = TestRecipeData.createRecipeSummaries().getFirst();
        recipeSummaryDAO.add(summary);
        recipeSummaryDAO.addRecipeUseFood("Apple", summary.getRecipeId());

        String sql = """
            UPDATE recipe_uses_food
            SET retrieval_time = DATETIME('now', '-25 hours')
            WHERE common_name = ?
            AND recipe_id = ?
            """;

        try (Connection conn = databaseManager.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Apple");
            ps.setInt(2, summary.getRecipeId());
            ps.executeUpdate();
        }

        assertTrue(recipeSummaryDAO.getRecipeSummariesByFoodName("Apple").isEmpty());
    }
}

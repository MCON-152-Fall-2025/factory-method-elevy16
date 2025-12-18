package com.mcon152.recipeshare.web;

import com.mcon152.recipeshare.Recipe;
import com.mcon152.recipeshare.service.RecipeFactory;
import com.mcon152.recipeshare.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    // create Logger object
    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /**
     * Create a new recipe.
     * Returns 201 Created with Location header pointing to the new resource.
     */
    @PostMapping
    public ResponseEntity<Recipe> addRecipe(@RequestBody RecipeRequest recipeRequest) {

        logger.info("POST /api/recipes: request received");

        // DEBUG summary
        logger.debug("POST /api/recipes: body summary - title={}, type={}",
                recipeRequest.getTitle(), recipeRequest.getType());

        // MDC
        MDC.put("recipeTitle", recipeRequest.getTitle());

        try {
            Recipe toSave = RecipeFactory.createFromRequest(recipeRequest);
            Recipe saved = recipeService.addRecipe(toSave);
            logger.info("POST /api/recipes: created recipe with id={}", saved.getId());

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()           // /api/recipes
                    .path("/{id}")                  // /{id}
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(saved);
        } catch (Exception e) {
            logger.error("POST /api/recipes: unexpected error - {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
        finally {
            MDC.remove("recipeTitle");
        }
    }

    /**
     * Retrieve all recipes. 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        logger.info("GET /api/recipes: request received.");

        List<Recipe> recipes = recipeService.getAllRecipes();

        logger.info("GET /api/recipes: success, returned {} recipes.", recipes.size());

        return ResponseEntity.ok(recipes);
    }

    /**
     * Retrieve a recipe by id. 200 OK or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable long id) {
        logger.info("GET /api/recipes/{}: request received.", id);

        try {
            return recipeService.getRecipeById(id)
                    .map(recipe -> {
                        logger.info("GET /api/recipes/{}: success", id);
                        return ResponseEntity.ok(recipe);
                    })
                    .orElseGet(() -> {
                        logger.warn("GET /api/recipes/{}: not found", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("GET /api/recipes/{}: unexpected error - {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a recipe. 204 No Content if deleted, 404 Not Found otherwise.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable long id) {
        logger.info("DELETE /api/recipes/{}: request received.", id);
        try {
            boolean deleted = recipeService.deleteRecipe(id);

            if (deleted) {
                logger.info("DELETE /api/recipes/{}: deleted successfully", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("DELETE /api/recipes/{}: not deleted (not found)", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("DELETE /api/recipes/{}: unexpected error - {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Replace a recipe (full update). 200 OK with updated entity or 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable long id, @RequestBody RecipeRequest updatedRequest) {
        logger.info("PUT /api/recipes/{}: request received.", id);

        // DEBUG summary
        logger.debug("PUT /api/recipes/{}: body summary - title={}, type={}",
                id, updatedRequest.getTitle(), updatedRequest.getType());

        // MDC
        MDC.put("recipeTitle", updatedRequest.getTitle());


        try {
            Recipe updatedRecipe = RecipeFactory.createFromRequest(updatedRequest);

            return recipeService.updateRecipe(id, updatedRecipe)
                    .map( recipe -> {
                        logger.info("PUT /api/recipes/{}: success", id);
                        return ResponseEntity.ok(recipe);
                            })
                    .orElseGet(() -> {
                        logger.warn("PUT /api/recipes/{}: not found", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("PUT /api/recipes/{}: unexpected error - {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
        finally {
            MDC.remove("recipeTitle");
        }
    }

    /**
     * Partial update. 200 OK with updated entity or 404 Not Found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Recipe> patchRecipe(@PathVariable long id, @RequestBody RecipeRequest partialRequest) {
        logger.info("PATCH /api/recipes/{}: request received.", id);

        // DEBUG summary
        logger.debug("PATCH /api/recipes/{}: body summary - title={}, type={}",
                id, partialRequest.getTitle(), partialRequest.getType());

        // MDC
        MDC.put("recipeTitle", partialRequest.getTitle());

        try {
            Recipe partialRecipe = RecipeFactory.createFromRequest(partialRequest);

            return recipeService.patchRecipe(id, partialRecipe)
                    .map(recipe -> {
                        logger.info("PATCH /api/recipes/{}: success", id);
                        return ResponseEntity.ok(recipe);
                    })
                    .orElseGet(() -> {
                        logger.warn("PATCH /api/recipes/{}: not found", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("PATCH /api/recipes/{}: unexpected error - {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }

        // remove MDC
        finally {
            MDC.remove("recipeTitle");
        }
    }
}

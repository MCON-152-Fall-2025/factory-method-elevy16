package com.mcon152.recipeshare.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerSoupTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void postSoupRecipe_returns201AndLocation() throws Exception {
        String json = """
        {
          "type": "SOUP",
          "title": "Tomato Basil Soup",
          "servings": 3,
          "instructions": "Simmer 20 minutes.",
          "description": "Simple soup.",
          "ingredients": "tomatoes, basil, broth",
          "spiceLevel": 2
        }
        """;

        mockMvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/recipes/")));
    }
}
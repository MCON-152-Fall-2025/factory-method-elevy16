package com.mcon152.recipeshare;

import com.mcon152.recipeshare.Recipe;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SOUP")
public class SoupRecipe extends Recipe {

    // 0=mild, 1=medium, 2=hot (your choice
    private Integer spiceLevel;

    public Integer getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(Integer spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

}

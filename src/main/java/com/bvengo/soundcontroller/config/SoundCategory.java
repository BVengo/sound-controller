package com.bvengo.soundcontroller.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SoundCategory extends SoundConfig {
    final int MAXIMUM_DEPTH = 4;

    int depth;
    // icon

    HashMap<String, SoundCategory> childCategories = new HashMap<>();
    HashMap<String, SoundConfig> childSounds = new HashMap<>();

    public SoundCategory(int depth, String id, String name) {
        super(id, name);
        this.depth = depth;
    }

    /**
     * Add a child. Type is determined by the depth of the SoundCategory
     * @param wholeId The whole id for the child ([mod]:[category].[subcategory].[sound])
     */
    public List<SoundConfig> addChild(String wholeId) {
        List<SoundConfig> newChildren = new ArrayList<>();

        // Whole path without parent
        String childId = wholeId.substring(this.depth == 0 ? 0 : this.id.length()+1);
        int categoryEnd = childId.indexOf(getDelimiter(this.depth + 1));

        // Is a category (not at maximum depth, and a separator has been found)
        if(this.depth < MAXIMUM_DEPTH && categoryEnd != -1) {
            String childName = childId.substring(0, categoryEnd);
            String childPath = this.id + getDelimiter(this.depth) + childName;

            if(!childCategories.containsKey(childName)) {
                childCategories.put(childName, new SoundCategory(this.depth + 1, childPath, childName));
                newChildren.add(childCategories.get(childName));
            }
            newChildren.addAll(childCategories.get(childName).addChild(wholeId));

        } else {
            childSounds.put(childId, new SoundConfig(wholeId, childId));
            newChildren.add(childSounds.get(childId));
        }

        return newChildren;
    }

    public SoundConfig getChild(String wholeId) {
        if(wholeId.equals("")) {
            return this;
        }

        String childId = wholeId.substring(this.depth == 0 ? 0 : this.id.length()+1);
        int categoryEnd = childId.indexOf(getDelimiter(this.depth + 1));

        // Isn't found yet (not at maximum depth, and a separator has been found)
        if(this.depth < MAXIMUM_DEPTH && categoryEnd != -1) {
            String childName = childId.substring(0, categoryEnd);
            return childCategories.get(childName).getChild(wholeId);
        }

        return((childSounds.containsKey(childId) ? childSounds : childCategories).get(childId));
    }

    private static String getDelimiter(int depth) {
        return switch(depth) {
            case 0  -> {yield "";}      // After empty root string
            case 1  -> {yield ":";}     // After a mod
            default -> {yield ".";}     // After a generic category
        };
    }
}

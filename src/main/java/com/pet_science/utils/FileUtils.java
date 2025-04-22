package com.pet_science.utils;

import java.util.*;

public class FileUtils {
    public static String[] findImagesToDelete(String oldImages, String newImages) {
        if (oldImages == null || newImages == null) {
            return new String[0];  // 返回空数组而不是空列表
        }

        String[] oldUrls = Arrays.stream(oldImages.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        String[] newUrls = Arrays.stream(newImages.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        Set<String> newUrlSet = new HashSet<>(Arrays.asList(newUrls));
        List<String> toDelete = new ArrayList<>();

        for (String oldUrl : oldUrls) {
            if (!newUrlSet.contains(oldUrl)) {
                toDelete.add(oldUrl);
            }
        }

        // 将List转换为数组返回
        return toDelete.toArray(new String[0]);
    }
}

package de.codecrafter47.taboverlay.config;

import org.yaml.snakeyaml.error.Mark;

public interface SortingRulePreprocessor {

    String process(String sortingRule, ErrorHandler errorHandler, Mark mark);
}

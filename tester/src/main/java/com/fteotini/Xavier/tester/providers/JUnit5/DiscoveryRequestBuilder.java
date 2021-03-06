package com.fteotini.Xavier.tester.providers.JUnit5;

import com.fteotini.Xavier.commons.tester.TestExecutionMode;
import com.fteotini.Xavier.commons.util.ClassPathResolver;
import com.fteotini.Xavier.tester.TestDiscoveryOptions;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.Filter;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.PackageNameFilter;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClasspathRoots;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;

class DiscoveryRequestBuilder {
    private TestDiscoveryOptions options;
    private final List<Filter<?>> filters = new ArrayList<>();

    DiscoveryRequestBuilder(TestDiscoveryOptions options) {
        this.options = options;
    }

    LauncherDiscoveryRequest build() {
        return LauncherDiscoveryRequestBuilder.request()
                .selectors(buildSelectors())
                .filters(buildFilters())
                .build();
    }

    private Filter<?>[] buildFilters() {
        addFilterSet(options.getIncludedClassNamePatterns(), ClassNameFilter::includeClassNamePatterns);
        addFilterSet(options.getExcludedClassNamePatterns(), ClassNameFilter::excludeClassNamePatterns);

        addFilterSet(options.getIncludedPackageNames(), PackageNameFilter::includePackageNames);
        addFilterSet(options.getExcludedPackageNames(), PackageNameFilter::excludePackageNames);

        return filters.toArray(Filter[]::new);
    }

    private List<? extends DiscoverySelector> buildSelectors() {
        List<? extends DiscoverySelector> selectors = Collections.emptyList();

        if (options.getTestExecutionMode() == TestExecutionMode.ENTIRE_SUITE) {
            selectors = createClasspathRootSelectors();
        }

        if (!options.getSelectedMethods().isEmpty() && options.getTestExecutionMode() == TestExecutionMode.SINGLE_METHOD) {
            selectors = options.getSelectedMethods().stream()
                    .map(m -> selectMethod(m.getBelongingClass(), m.getMethod()))
                    .collect(Collectors.toList());
        }

        return selectors;
    }

    private List<? extends DiscoverySelector> createClasspathRootSelectors() {
        var classpath = new ClassPathResolver().getClassPaths().stream().filter(p -> p.toFile().isDirectory()).collect(Collectors.toCollection(HashSet::new));
        if (!options.getAdditionalClassPaths().isEmpty())
            classpath.addAll(options.getAdditionalClassPaths());

        return selectClasspathRoots(classpath);
    }

    private <T extends Filter<?>> void addFilterSet(Set<String> filterSet, Function<String[], T> filterFunction) {
        if (!filterSet.isEmpty()) {
            filters.add(filterFunction.apply(filterSet.toArray(String[]::new)));
        }
    }
}

package com.fteotini.amf.launcher.util;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class ClassPathResolverTest {
    @Test
    void It_should_return_the_currently_loaded_classPath() {
        var currentlyLoadedCP = System.getProperty("java.class.path").split(File.pathSeparator);

        var result = new ClassPathResolver().getClassPaths();

        assertThat(result).hasSameSizeAs(currentlyLoadedCP)
                .anyMatch(p -> p.toString().equals(currentlyLoadedCP[0]));
    }

    @Test
    void Given_a_non_existing_path_in_the_currently_loaded_classPath_then_it_should_filter_it_out() {
        var key = "java.class.path";
        var oldCP = System.getProperty(key);

        try {
            var currentlyLoadedCP = oldCP.split(File.pathSeparator);
            System.setProperty(key, oldCP + File.pathSeparator + "not_existing");

            var result = new ClassPathResolver().getClassPaths();

            assertThat(result).hasSameSizeAs(currentlyLoadedCP);
        } finally {
            System.setProperty(key, oldCP);
        }
    }
}
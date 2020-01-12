/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.eggs.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import javax.inject.Inject;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Model(adaptables = {Resource.class, SlingHttpServletRequest.class})
public class HelloWorldModel {

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @OSGiService
    private SlingSettingsService settings;
    @SlingObject
    private Resource currentResource;
    @SlingObject
    private ResourceResolver resourceResolver;

    @Inject
    @org.apache.sling.models.annotations.Optional
    Object htlParameter;

    private String message;

    @PostConstruct
    protected void init() {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        String currentPagePath = Optional.ofNullable(pageManager)
                .map(pm -> pm.getContainingPage(currentResource))
                .map(Page::getPath).orElse("");

        message = "\tHello World!\n"
            + "\tThis is instance: " + settings.getSlingId() + "\n"
            + "\tResource type is: " + resourceType + "\n"
            + "\tCurrent page is: " + currentPagePath + "\n"
            + "\thtlParameter page is: " + String.join(",", getHtlParameter()) + "\n";
    }

    public String getMessage() {
        return message;
    }

    /**
     * returns a set of classes to use for a link component
     * supports either a single class string that is split into multiple classes
     * or an array of strings
     *
     * @return set if class strings
     */
    public Set<String> getHtlParameter() {
        final Set<String> classes = new HashSet<>();

        if (htlParameter == null) {
            return classes;
        }

        if (htlParameter instanceof String) {
            // if linkClasses is a string we can directly add it
            addClassString(classes, (String) htlParameter);
        } else if (htlParameter instanceof Object[]) {
            // if we have an array we have to check each item
            for (Object o : (Object[]) htlParameter) {
                if (o instanceof String) {
                    classes.add(((String) o).trim());
                }
            }
        }

        return classes;
    }

    /**
     * Adds a string of classes to a set by splitting the string at whitespace
     *
     * @param classes set of classes
     * @param clazz   class string
     */
    private void addClassString(Set<String> classes, String clazz) {
        final String[] splitClasses = clazz.split(" ");
        for (String c : splitClasses) {
            classes.add(c.trim());
        }
    }

}

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

import com.adobe.acs.commons.i18n.I18nProvider;
import com.adobe.acs.commons.i18n.impl.I18nProviderImpl;
import com.adobe.acs.commons.models.injectors.annotation.impl.I18NAnnotationProcessorFactory;
import com.adobe.acs.commons.models.injectors.impl.I18nInjector;
import io.wcm.sling.models.injectors.impl.AemObjectInjector;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Simple JUnit test verifying the HelloWorldModel
 */
@ExtendWith(AemContextExtension.class)
class HelloWorldModelTest {

    private Resource resource;

    private MockSlingHttpServletRequest request;

    private String[] htlParameter = {"link"};

    @BeforeEach
    public void setup(AemContext context) throws Exception {

        //Load i18nInjector
        context.registerService(new AemObjectInjector());
        context.registerService(I18nProvider.class, new I18nProviderImpl());
        context.registerInjectActivateService(new I18nInjector());
        context.registerService(StaticInjectAnnotationProcessorFactory.class, new I18NAnnotationProcessorFactory());

        //Load sample-content
        context.load().json("/sample-content.json", "/content/mypage");
        context.currentResource("/content/mypage");

        resource = context.currentResource();

        //add htlParameter as Parameter
        context.request().setAttribute("htlParameter", htlParameter);

        request = context.request();
    }

    @Test
    void testGetMessage() throws Exception {
        // create sling model
        HelloWorldModel hello = request.adaptTo(HelloWorldModel.class);

        // some very basic junit tests
        String msg = hello.getMessage();
        assertNotNull(msg);
        assertTrue(StringUtils.contains(msg, resource.getResourceType()));
    }

    @Test
    @DisplayName("This tests the correct handover from htl to osgi model")
    void testGetHtlParameter () {
        // create sling model
        HelloWorldModel hello = request.adaptTo(HelloWorldModel.class);

        assertAll(
                () -> assertNotNull(hello),
                () -> assertNotNull(hello.getHtlParameter()),
                () -> assertArrayEquals(htlParameter, hello.getHtlParameter().toArray())
        );

    }


}

package com.ttnd.headless.api;

import org.apache.sling.api.resource.ResourceResolver;

public interface HeadlessResourceResolverService {

    ResourceResolver getResourceResolver();
}
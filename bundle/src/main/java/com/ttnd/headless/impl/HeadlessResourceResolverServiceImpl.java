package com.ttnd.headless.impl;

import com.ttnd.headless.api.HeadlessResourceResolverService;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(label = "Headless Connection Service" ,description = "Implementation of Headless Connection Service", metatype = true,immediate = true)
@Service(HeadlessResourceResolverService.class)

public class HeadlessResourceResolverServiceImpl implements HeadlessResourceResolverService {

    Logger logger = LoggerFactory.getLogger(HeadlessResourceResolverServiceImpl.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    ResourceResolver resourceResolver ;

    public ResourceResolverFactory getResourceResolverFactory() {
        return resourceResolverFactory;
    }

    public ResourceResolver getResourceResolver(){
        if(resourceResolver == null) {
            try {
                Map param = new HashMap();
                param.put(ResourceResolverFactory.SUBSERVICE, "getResourceResolver");
                resourceResolver = getResourceResolverFactory().getServiceResourceResolver(param);
            } catch (LoginException e) {
                logger.error("Login Exception");
            }
        }
        return resourceResolver;
    }
}

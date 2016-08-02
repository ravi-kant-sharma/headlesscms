package com.ttnd.headless.servlet;

import com.day.cq.dam.api.Asset;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample servlet which easily converts a Node as JSON to the PrintWriter.
 */
@SlingServlet(paths={"/bin/headless"})
public class ResourceToJSONServlet extends SlingAllMethodsServlet {

    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(ResourceToJSONServlet.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8080");
        response.addHeader("Access-Control-Allow-Credentials", "true");

        final ResourceResolver resolver = request.getResourceResolver();
        String path = request.getParameter("query");
        String property =  request.getParameter("property");

        final Resource resource = resolver.getResource(path);
        if(path.endsWith(".jpg")){
            InputStream stream = resource.adaptTo(Asset.class).getOriginal().getStream();
            byte[] bytes = org.apache.commons.io.IOUtils.toByteArray(stream);
            response.setContentType("image/jpeg");
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.setStatus(SlingHttpServletResponse.SC_NOT_MODIFIED);
        }else {
            final PrintWriter out = response.getWriter();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            final Node node = resource.adaptTo(Node.class);
            final Set<String> propertiesToIgnore = new HashSet<String>() {{
                add("jcr:created");
                add("jcr:createdBy");
                add("jcr:versionHistory");
                add("jcr:predecessors");
                add("jcr:baseVersion");
                add("jcr:uuid");
                add("jcr:primaryType");
            }};
            JsonItemWriter jsonWriter = new JsonItemWriter(propertiesToIgnore);
            try {
                jsonWriter.dump(node, out, 1, true);
                response.setStatus(SlingHttpServletResponse.SC_OK);
            } catch (RepositoryException | JSONException e) {
                logger.error("Could not get JSON", e);
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String path = request.getParameter("query");

        final PrintWriter out = response.getWriter();
        final ResourceResolver resolver = request.getResourceResolver();
        final Resource resource = resolver.getResource(path);
        final Node node = resource.adaptTo(Node.class);

        /* Node properties to exclude from the JSON object. */
        final Set<String> propertiesToIgnore = new HashSet<String>() {{
            add("jcr:created");
            add("jcr:createdBy");
            add("jcr:versionHistory");
            add("jcr:predecessors");
            add("jcr:baseVersion");
            add("jcr:uuid");
        }};

        JsonItemWriter jsonWriter = new JsonItemWriter(propertiesToIgnore);

        try {
            /* Write the JSON to the PrintWriter with max recursion of 1 level and tidy formatting. */
            jsonWriter.dump(node, out, 1, true);
            response.setStatus(SlingHttpServletResponse.SC_OK);
        } catch (RepositoryException | JSONException e) {
            logger.error("Could not get JSON", e);
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

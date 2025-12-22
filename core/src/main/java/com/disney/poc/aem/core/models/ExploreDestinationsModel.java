package com.disney.poc.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;

@Model(
    adaptables = Resource.class,
    resourceType = "poc-aem/components/exploreDestinations",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class ExploreDestinationsModel {

    @ValueMapValue
    private String heading;

    @ValueMapValue
    private String componentDescription;

    @ValueMapValue
    private String buttonText;

    @ValueMapValue
    private String ctaLink;

    public String getHeading() {
        return heading;
    }

    public String getComponentDescription() {
        return componentDescription;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getCtaLink() {
        return ctaLink;
    }
}

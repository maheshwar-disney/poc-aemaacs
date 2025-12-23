package com.disney.poc.aem.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.Exporter;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = "dvc-aem/components/herobanner"
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class HeroBannerModel {

    @ValueMapValue
    @JsonProperty("heading")
    private String heading;

    @ValueMapValue
    @JsonProperty("heroImage")
    private String heroImage;

    @ValueMapValue
    @JsonProperty("altText")
    private String altText;

    // Getters
    public String getHeading() {
        return heading;
    }

    public String getHeroImage() {
        return heroImage;
    }

    public String getAltText() {
        return altText;
    }
}

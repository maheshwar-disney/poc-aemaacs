package com.disney.poc.aem.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.export.json.ExporterConstants;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(
    adaptables = Resource.class,
    resourceType = "poc-aem/components/cardsDisney",
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class CardsDisneyModel {

    @ValueMapValue
    @JsonProperty("heading")
    private String heading;

    @ValueMapValue
    @JsonProperty("componentDescription")
    private String componentDescription;

    @ValueMapValue(name = "cardTitle")
    private String[] cardTitles;

    @ValueMapValue(name = "cardDescription")
    private String[] cardDescriptions;

    @ValueMapValue(name = "cardImage")
    private String[] cardImages;

    @ValueMapValue(name = "altText")
    private String[] altTexts;

    @ValueMapValue(name = "ctaLink")
    private String[] ctaLinks;

    @JsonProperty("cardsList")
    private List<Map<String, String>> cardsList;

    @PostConstruct
    protected void init() {
        cardsList = new ArrayList<>();

        if (cardTitles != null) {
            int length = cardTitles.length; // assume all arrays are same length
            for (int i = 0; i < length; i++) {
                Map<String, String> cardMap = new HashMap<>();
                cardMap.put("cardTitle", safeGet(cardTitles, i));
                cardMap.put("cardDescription", safeGet(cardDescriptions, i));
                cardMap.put("cardImage", safeGet(cardImages, i));
                cardMap.put("altText", safeGet(altTexts, i));
                cardMap.put("ctaLink", safeGet(ctaLinks, i));

                cardsList.add(cardMap);
            }
        }
    }

    private String safeGet(String[] array, int index) {
        return array != null && index < array.length ? array[index] : "";
    }

    // Getters
    public String getHeading() {
        return heading;
    }

    public String getComponentDescription() {
        return componentDescription;
    }

    public List<Map<String, String>> getCardsList() {
        return cardsList;
    }
}

package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.DESCRIPTION_LOG_KEY;

@Service
public class FilingHistoryDescriptionProviderService {

    private static final String FILING_HISTORY_DESCRIPTIONS_FILEPATH = "api-enumerations/filing_history_descriptions.yml";
    private static final String FILING_HISTORY_DESCRIPTION_KEY = "description";
    private static final String LOG_MESSAGE_FILE_KEY = "file";

    private final Map<String, String> filingHistoryDescriptions;

    private final LoggingUtils loggingUtils;

    @Autowired
    public FilingHistoryDescriptionProviderService(LoggingUtils loggingUtils) {
        this(FILING_HISTORY_DESCRIPTIONS_FILEPATH, loggingUtils);
    }

    public FilingHistoryDescriptionProviderService(final String filingHistoryDescriptionsFile, LoggingUtils loggingUtils) {
        this.loggingUtils = loggingUtils;
        filingHistoryDescriptions = loadFilingHistoryDescriptionsFromClasspath(filingHistoryDescriptionsFile);
    }

    private Map<String, String> loadFilingHistoryDescriptionsFromClasspath(final String filingHistoryDescriptionKey) {
        final InputStream resource = getClass().getClassLoader().getResourceAsStream(filingHistoryDescriptionKey);
        if (resource == null) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_FILE_KEY, filingHistoryDescriptionKey);
            loggingUtils.getLogger().error("Orders descriptions file not found", logMap);
            return null;
        }
        Map<String, String> filingHistoryDescriptionsLocal = new HashMap<>();
        try(final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filingHistoryDescriptionKey)) {
            final Yaml yaml = new Yaml();
            final Map<String, Object> filingHistoryDescriptionsRoot = yaml.load(inputStream);
            Map<?, ?> out = (Map<?, ?>)filingHistoryDescriptionsRoot.get(FILING_HISTORY_DESCRIPTION_KEY);
            if (out == null) {
                Map<String, Object> logMap = new HashMap<>();
                logMap.put(DESCRIPTION_LOG_KEY, FILING_HISTORY_DESCRIPTION_KEY);
                loggingUtils.getLogger().error("Filing History descriptions file not found", logMap);
                return null;
            } else {
                for(Map.Entry<?, ?> entry : out.entrySet()) {
                    filingHistoryDescriptionsLocal.put((String)entry.getKey(), (String)entry.getValue());
                }
            }
        } catch (IOException ioe) {
            // This is very unlikely to happen here given File.exists() check above,
            // and that it is not likely to encounter an error closing the stream either.
            loggingUtils.getLogger().error(ioe);
        }
        return filingHistoryDescriptionsLocal;
    }

    private String getFilingHistoryDescriptionWithKey(String descriptionKey) {
        return filingHistoryDescriptions.getOrDefault(descriptionKey, descriptionKey);
    }

    /**
     * Fetches the specified key from the filing history enumerations and replaces the variables
     * with the ones in the filingHistoryDescriptionValues. It strips out all asterisks in description.
     * If descriptionKey is null, null is returned. If the descriptionKey is not found in the filing history
     * enumeration, the provided descriptionKey is returned. If filingHistoryDescriptionValues is null the
     * description found in filing history enumeration is returned (if available).
     * @param descriptionKey the key to fetch from the filing history enumerations
     * @param filingHistoryDescriptionValues the map of the description values to replace in the description
     * @return the formatted filing history description
     */
    public String mapFilingHistoryDescription(String descriptionKey, Map<String, Object> filingHistoryDescriptionValues) {
        if(filingHistoryDescriptions == null) {
            return null;
        }
        String description = getFilingHistoryDescriptionWithKey(descriptionKey);
        if(filingHistoryDescriptionValues == null){
            return description == null ? null : description.replace("*", "");
        } else if(filingHistoryDescriptionValues.containsKey("description")) {
            return (String) filingHistoryDescriptionValues.get("description");
        } else {
            final StringBuilder sb = new StringBuilder(description);
            replaceAll(sb, "*", "");
            filingHistoryDescriptionValues.forEach((k,v)-> {
                if(v instanceof String) {
                    String value = k.contains("date") ? reformatActionDate((String) v) : (String) v;
                    replaceAll(sb, "{" + k + "}", value);
                }
            });
            return sb.toString();
        }
    }

    /**
     * Reformats an action date string such as "2009-08-23" as "23 August 2009".
     * @param actionDate the action date as reported from the filing history
     * @return the same date rendered for display purposes
     */
    public static String reformatActionDate(final String actionDate) {
        final LocalDate parsedDate = LocalDate.parse(actionDate);
        return parsedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")); // TODO: could be made constant or application property
    }

    /**
     * Replaces a specified String in a StringBuilder
     * @param builder the StringBuilder
     * @param from the String you want to replace
     * @param to the String you want it to be replaced by
     */
    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

}

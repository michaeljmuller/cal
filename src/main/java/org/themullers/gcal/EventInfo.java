package org.themullers.gcal;

import com.google.api.client.util.DateTime;

public record EventInfo(String name,
                        DateTime start) {
}

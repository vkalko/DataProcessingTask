package org.example.vkalko.dataprocessing.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class PubSubNotificationDTO {

    @Setter
    @Getter
    private Message message;

    @Setter
    @Getter
    @NoArgsConstructor
    public class Message {

        private String data;
        private String messageId;
        private String publishTime;
        private Attributes attributes;

        @Setter
        @Getter
        @NoArgsConstructor
        public class Attributes {
            private String bucketId;
            private String eventTime;
            private String eventType;
            private String notificationConfig;
            private String objectGeneration;
            private String objectId;
            private String overwroteGeneration;
            private String payloadFormat;
        }
    }
}

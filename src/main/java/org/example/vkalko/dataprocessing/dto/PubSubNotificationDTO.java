package org.example.vkalko.dataprocessing.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class PubSubNotificationDTO {

    @Setter
    @Getter
    private Message message;

    public PubSubNotificationDTO() {}

    @Setter
    @Getter
    public class Message {

        private String data;
        private String messageId;
        private String publishTime;
        private Attributes attributes;

        public Message() {}

        public Message(String data, String messageId) {
            this.data = data;
            this.messageId = messageId;
        }


        @Setter
        @Getter
        public class Attributes {
            private String bucketId;
            private String eventTime;
            private String eventType;
            private String notificationConfig;
            private String objectGeneration;
            private String objectId;
            private String overwroteGeneration;
            private String payloadFormat;

            public Attributes() {}


        }
    }
}

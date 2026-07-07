package Student_ride_sharing.demand.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class LocationTrackingController {

    // 1. MUST be marked final to ensure constructor enforcement
    private final SimpMessagingTemplate messagingTemplate;

    // 2. Ensure constructor matches the class name and assigns the parameter
    public LocationTrackingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/driver/move")
    public void broadcastDriverLocation(@Payload Map<String, Object> locationPayload) {
        Object rideId = locationPayload.get("rideId");

        if (rideId != null) {
            String destinationTopic = "/topic/ride/" + rideId.toString();

            // 🟢 This will no longer be null!
            // 🟢 Cast the second parameter to Object explicitly to clear compiler ambiguity
            messagingTemplate.convertAndSend(destinationTopic, (Object) locationPayload);
        }
    }
}
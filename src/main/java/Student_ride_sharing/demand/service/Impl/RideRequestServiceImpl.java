package Student_ride_sharing.demand.service.Impl;

import Student_ride_sharing.demand.dto.GroupedRequestDTO;
import Student_ride_sharing.demand.dto.RideRequestDetailedDto;
import Student_ride_sharing.demand.dto.RideRequestDto;
import Student_ride_sharing.demand.dto.RideResponseDto;
import Student_ride_sharing.demand.entity.RequestStatus;
import Student_ride_sharing.demand.entity.RideRequest;
import Student_ride_sharing.demand.entity.User;
import Student_ride_sharing.demand.repository.RideRequestRepository;
import Student_ride_sharing.demand.repository.UserRepository;
import Student_ride_sharing.demand.service.RideRequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {

    private final RideRequestRepository rideRequestRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private SimpMessagingTemplate messagingTemplate;




    @Override
    @Transactional
//    public RideRequestDto createRequest(RideRequestDto dto) {
//        // 1. Resolve logged-in student username from security context principal token session
//        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        User student = userRepository.findByUsername(currentUsername)
//                .orElseThrow(() -> new RuntimeException("Logged in student record not found"));
//
//        // 2. Map payload properties automatically via ModelMapper
//        RideRequest request = modelMapper.map(dto, RideRequest.class);
//
//        // 🟢 FIX: Explicitly strip any ID value ModelMapper might have misconfigured or cached!
//        // This guarantees Hibernate maps this transaction exclusively to a SQL INSERT operation.
//        request.setId(null);
//
//        // 3. Bind permanent state variables and student relationship pointers
//        request.setStudent(student);
//        request.setStatus(RequestStatus.PENDING);
//
//        // 🟢 FIX: Ensure price maps exactly to the matching flat price field on your Entity
//        request.setPrice(dto.getPrice());
//
//        // 4. Save cleanly into your database dashboard tables
//        RideRequest savedRequest = rideRequestRepository.save(request);
//
//        // 5. Build mapped transfer object response payload structure back to your React app client
//        return modelMapper.map(savedRequest, RideRequestDto.class);
//    }
    public RideRequestDto createRequest(RideRequestDto dto) {
        // 1. Resolve logged-in student username from security context principal token session
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        User student = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Logged in student record not found"));

        // 2. Map payload properties automatically via ModelMapper
        RideRequest request = modelMapper.map(dto, RideRequest.class);

        // 🟢 FIX: Explicitly strip any ID value ModelMapper might have misconfigured or cached!
        request.setId(null);

        // 3. Bind permanent state variables and student relationship pointers
        request.setStudent(student);
        request.setStatus(RequestStatus.PENDING);
        request.setPrice(dto.getPrice());

        // 4. Save cleanly into your database dashboard tables
        RideRequest savedRequest = rideRequestRepository.save(request);

        // =========================================================================
        // 🟢 REAL-TIME WEBSOCKET BROADCAST: Trigger grouping update for drivers!
        // =========================================================================
        try {
            // Fetch the freshly updated clustered demand list (where count >= 3)
            // This is the heavy native SQL aggregation query you built earlier
            List<GroupedRequestDTO> updatedClusters =rideRequestRepository.findGroupedPendingRequests();

            // Broadcast the data straight down the WebSocket pipeline to all drivers!
            messagingTemplate.convertAndSend("/topic/driver-demands", updatedClusters);

        } catch (Exception e) {
            // Fail-safe: Wrap in a try-catch so that if WebSockets experience an anomaly,
            // the student's primary database transaction isn't broken or rolled back.
            System.err.println("Failed to broadcast updated clusters via WebSocket: " + e.getMessage());
        }
        // =========================================================================

        // 5. Build mapped transfer object response payload structure back to your React app client
        return modelMapper.map(savedRequest, RideRequestDto.class);
    }

    @Override
    public List<GroupedRequestDTO> getGroupedDemandsForDrivers() {
        return rideRequestRepository.findGroupedPendingRequests();
    }
    @Override
    public List<RideRequestDetailedDto> searchManualClusterRequests(
            String source, String destination, String preferredVehicle, String timeSlot) {

        // 1. Run your native SQL clustering query
        List<RideRequest> rawRequests = rideRequestRepository.findDetailedRequestsInCluster(
                source, destination, preferredVehicle, timeSlot
        );

        // 2. Map entities to the new Detailed DTO context
        return rawRequests.stream()
                .map(request -> {
                    RideRequestDetailedDto dto = modelMapper.map(request, RideRequestDetailedDto.class);

                    // 🟢 EXPLICIT RELATION MAPPING: Extract fields from request.getStudent()
                    if (request.getStudent() != null) {
                        dto.setStudentUsername(request.getStudent().getUsername());
                        dto.setStudentName(request.getStudent().getName());
                    }

                    // Map vehicle type to string if modelMapper needs assistance matching types
                    if (request.getPreferredVehicle() != null) {
                        dto.setPreferredVehicle(request.getPreferredVehicle().name());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}